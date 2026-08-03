package com.learn.catalog2.data.repository

import com.learn.catalog2.domain.models.WalletTransaction
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    fun getBalance(): Flow<Int>
    fun getTransactions(): Flow<List<WalletTransaction>>
    suspend fun syncWalletData()
}
