package com.learn.catalog2.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class WalletRepositoryImpl(
    private val supabase: SupabaseClient,
    private val database: CatalogDatabase
) : WalletRepository {

    private val queries = database.walletTransactionEntityQueries

    override fun getBalance(): Flow<Int> {
        return queries.getBalance()
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { (it?.SUM ?: 0L).toInt() }
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

        // 1. الحفظ المحلي أولاً (Offline-First)
        queries.insertOrReplaceTransaction(entity)

        // 2. الرفع إلى Supabase عند توفر الجلسة للمستخدم
        val user = supabase.auth.currentUserOrNull()
        if (user != null) {
            runCatching {
                val dto = TransactionDto(
                    id = newId,
                    userId = user.id,
                    type = if (isIncome) "INCOME" else "EXPENSE",
                    amount = finalAmount.toDouble(),
                    relatedGuideId = relatedGuideId
                )
                supabase.from("transactions").insert(dto)
            }
        }
    }

    @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
    override suspend fun syncWalletData() = withContext(Dispatchers.Default) {
        val user = supabase.auth.currentUserOrNull() ?: return@withContext
        try {
            // 1. جلب بيانات المحفظة من جدول wallets (إن احتجت مستقبلاً لحفظ نقاط أرباح الـ Creator)
            val remoteWallet = supabase.from("wallets")
                .select {
                    filter { eq("user_id", user.id) }
                }
                .decodeSingleOrNull<WalletDto>()

            // 2. جلب وتحديث سجل المعاملات
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
                        title = if (isIncome) "كسب نقاط" else "شراء دليل",
                        date = dto.createdAt ?: "Recorded",
                        amount = dto.amount.toLong(),
                        isIncome = if (isIncome) 1L else 0L,
                        timestamp = Clock.System.now().toEpochMilliseconds()
                    )
                    queries.insertOrReplaceTransaction(entity)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    @OptIn(ExperimentalTime::class)
    override suspend fun requestWithdrawal(
        amountPoints: Float,
        amountCash: Float
    ): Result<Unit> = runCatching {
        val user = supabase.auth.currentUserOrNull() ?: throw Exception("User not authenticated")

        val dto = WithdrawalRequestDto(
            userId = user.id,
            amountPoints = amountPoints,
            amountCash = amountCash,
            status = "pending",
            requestedAt =  Clock.System.now().toString()
        )

        supabase.from("withdrawal_requests").insert(dto)
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