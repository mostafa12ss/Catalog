package com.learn.catalog2.domain.repository // أو data.repository حسب تقسيم الحزم لديك

import com.learn.catalog2.data.remote.supabase.dto.WithdrawalRequestDto
import com.learn.catalog2.domain.models.WalletTransaction
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    fun getBalance(): Flow<Int>
    fun getTransactions(): Flow<List<WalletTransaction>>
    suspend fun syncWalletData()
    suspend fun addTransaction(
        title: String,
        amount: Int,
        isIncome: Boolean,
        relatedGuideId: String? = null
    )
    suspend fun requestWithdrawal(
        amountPoints: Float,
        amountCash: Float
    ): Result<Unit>

    // 👈 جلب طلبات السحب الخاصة بالمستخدم
    suspend fun getWithdrawalRequests(): Result<List<WithdrawalRequestDto>>
}