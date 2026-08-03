package com.learn.catalog2.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.learn.catalog2.data.local.WalletTransactionEntity
import com.learn.catalog2.database.CatalogDatabase
import com.learn.catalog2.domain.models.WalletTransaction
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class WalletRepositoryImpl(
    private val supabase: SupabaseClient,
    private val database: CatalogDatabase
) : WalletRepository {

    // ✅ التعديل 1: استدعاء Queries المخصصة لـ WalletTransactionEntity
    private val queries = database.walletTransactionEntityQueries

    override fun getBalance(): Flow<Int> {
        return queries.getBalance()
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { (it?.SUM ?: 0L).toInt() } // ✅ SUM تأتي كـ Long nullable
    }

    override fun getTransactions(): Flow<List<WalletTransaction>> {
        // ✅ التعديل 2: استخدام selectAllTransactions المطابق لملف الـ SQ
        return queries.selectAllTransactions()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities ->
                entities.map { it.toDomain() }
            }.onStart {
                syncWalletData()
            }
    }

    override suspend fun syncWalletData() {
        try {
            // TODO: Fetch from Supabase "wallet_transactions" table
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ✅ التعديل 3: تحويل أنواع Long القادمة من SQLite إلى Int و Boolean المعتمدة في Domain
    private fun WalletTransactionEntity.toDomain() = WalletTransaction(
        id = id,
        title = title,
        date = date,
        amount = amount.toInt(), // SQLite يُرجع INTEGER كـ Long
        isIncome = isIncome == 1L, // تحويل 1L / 0L إلى Boolean
        timestamp = timestamp
    )
}