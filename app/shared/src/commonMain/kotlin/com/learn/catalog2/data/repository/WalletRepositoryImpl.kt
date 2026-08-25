package com.learn.catalog2.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.learn.catalog2.data.local.WalletTransactionEntity
import com.learn.catalog2.data.remote.supabase.dto.TransactionDto
import com.learn.catalog2.data.remote.supabase.dto.WalletDto
import com.learn.catalog2.data.remote.supabase.dto.WithdrawalRequestDto
import com.learn.catalog2.database.CatalogDatabase
import com.learn.catalog2.domain.models.WalletTransaction
import com.learn.catalog2.domain.repository.WalletRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class WalletRepositoryImpl(
    private val supabase: SupabaseClient,
    private val database: CatalogDatabase
) : WalletRepository {
    private val claimMutex = Mutex()
    private val queries = database.walletTransactionEntityQueries

    // ⚡ Signal لإخطار الـ Balance بإعادة جلب القيمة فور حدوث معاملة جديدة
    private val balanceRefreshSignal = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /**
     * جلب الرصيد المباشر وإعادة تحديثه فوراً عند أي إضافة أو خصم
     */
    override fun getBalance(): Flow<Int> = flow {
        fetchAndEmitBalance()

        // الاستماع لإشارات التحديث المستمرة عند إضافة معاملات جديدة
        balanceRefreshSignal.collect {
            fetchAndEmitBalance()
        }
    }

    private suspend fun kotlinx.coroutines.flow.FlowCollector<Int>.fetchAndEmitBalance() {
        val user = supabase.auth.currentUserOrNull()
        if (user != null) {
            val remoteWallet = runCatching {
                supabase.from("wallets")
                    .select { filter { eq("user_id", user.id) } }
                    .decodeSingleOrNull<WalletDto>()
            }.getOrNull()

            if (remoteWallet != null) {
                emit(remoteWallet.points)
            } else {
                val localBalance = queries.getBalance().executeAsOneOrNull()?.SUM ?: 0L
                emit(localBalance.toInt())
            }
        } else {
            emit(0)
        }
    }

    override fun getTransactions(): Flow<List<WalletTransaction>> {
        return queries.selectAllTransactions()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities ->
                entities.sortedByDescending { it.timestamp }
                    .map { it.toDomain() }
            }
            .onStart {
                syncWalletData()
            }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun addTransaction(
        title: String,
        amount: Int,
        isIncome: Boolean,
        relatedGuideId: String?
    ) = withContext(Dispatchers.Default) {
        val finalAmount = if (isIncome) amount else -amount
        val currentTimestamp = Clock.System.now().toEpochMilliseconds()
        val formattedDate = "Today"
        val newId = Uuid.random().toString()

        val entity = WalletTransactionEntity(
            id = newId,
            title = title,
            date = formattedDate,
            amount = finalAmount.toLong(),
            isIncome = if (isIncome) 1L else 0L,
            timestamp = currentTimestamp
        )

        // 1. التحديث المحلي الفوري للمعاملات
        queries.insertOrReplaceTransaction(entity)

        // 2. الرفع إلى Supabase
        val user = supabase.auth.currentUserOrNull()
        if (user != null) {
            runCatching {
                val transactionJson = buildJsonObject {
                    put("user_id", user.id)
                    put("type", if (isIncome) "INCOME" else "EXPENSE")
                    put("amount", amount.toDouble())
                    if (!relatedGuideId.isNullOrBlank()) {
                        put("related_guide_id", relatedGuideId)
                    }
                }
                supabase.from("transactions").insert(transactionJson)
            }.onSuccess {
                // ⚡ إرسال إشارة لتحديث الـ Balance فور نجاح الرفع والخصم من الـ Trigger
                balanceRefreshSignal.tryEmit(Unit)
            }.onFailure { error ->
                println("⚠️ Transaction Sync Error: ${error.message}")
            }
        }
    }

    override suspend fun claimFreeRewardPoints(): Result<Unit> = withContext(Dispatchers.Default) {
        claimMutex.withLock {
            runCatching {
                val user = supabase.auth.currentUserOrNull()
                    ?: throw Exception("يجب تسجيل الدخول أولاً لشحن الرصيد المجاني.")

                val currentWallet = runCatching {
                    supabase.from("wallets")
                        .select { filter { eq("user_id", user.id) } }
                        .decodeSingleOrNull<WalletDto>()
                }.getOrNull()

                val currentClaims = currentWallet?.freeClaimCount ?: 0

                if (currentClaims >= 2) {
                    throw Exception("عذراً، لقد استنفدت الحد الأقصى للشحن المجاني (مرتان فقط).")
                }

                val updatedPoints = (currentWallet?.points ?: 0) + 50
                val updatedClaims = currentClaims + 1

                val walletDto = WalletDto(
                    userId = user.id,
                    points = updatedPoints,
                    freeClaimCount = updatedClaims
                )

                supabase.from("wallets").upsert(walletDto)

                addTransaction(
                    title = "مكافأة مجانية (50 نقطة)",
                    amount = 50,
                    isIncome = true,
                    relatedGuideId = null
                )

                syncWalletData()
            }
        }
    }

    @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
    override suspend fun syncWalletData() = withContext(Dispatchers.Default) {
        val user = supabase.auth.currentUserOrNull() ?: return@withContext
        try {
            val remoteTransactions = supabase.from("transactions")
                .select {
                    filter { eq("user_id", user.id) }
                }
                .decodeList<TransactionDto>()

            database.transaction {
                remoteTransactions.forEach { dto ->
                    val isIncome = dto.amount >= 0
                    val entity = WalletTransactionEntity(
                        id = dto.id ?: Uuid.random().toString(),
                        title = if (isIncome) "مكافأة مجانية (50 نقطة)" else "شراء دليل",
                        date = dto.createdAt ?: "Today",
                        amount = dto.amount.toLong(),
                        isIncome = if (isIncome) 1L else 0L,
                        timestamp = Clock.System.now().toEpochMilliseconds()
                    )
                    queries.insertOrReplaceTransaction(entity)
                }
            }
            // ⚡ تحديث الرصيد عند اكتمال التزامن
            balanceRefreshSignal.tryEmit(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun requestWithdrawal(
        amountPoints: Float,
        amountCash: Float
    ): Result<Unit> {
        return Result.failure(Exception("عمليات السحب غير متاحة حالياً، يرجى المحاولة لاحقاً."))
    }

    override suspend fun getWithdrawalRequests(): Result<List<WithdrawalRequestDto>> = runCatching {
        val user = supabase.auth.currentUserOrNull() ?: throw Exception("User not authenticated")

        supabase.from("withdrawal_requests")
            .select {
                filter { eq("user_id", user.id) }
            }
            .decodeList<WithdrawalRequestDto>()
    }

    private fun WalletTransactionEntity.toDomain() = WalletTransaction(
        id = id,
        title = title,
        date = date,
        amount = amount.toInt(),
        isIncome = isIncome == 1L,
        timestamp = timestamp
    )
}