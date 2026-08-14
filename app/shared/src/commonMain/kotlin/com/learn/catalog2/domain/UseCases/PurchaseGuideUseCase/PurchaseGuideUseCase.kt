package com.learn.catalog2.domain.UseCases.PurchaseGuideUseCase

import com.learn.catalog2.data.repository.GuideRepository

import com.learn.catalog2.domain.models.DataModels.Course
import com.learn.catalog2.domain.repository.WalletRepository
import kotlinx.coroutines.flow.first

sealed class PurchaseResult {
    object Success : PurchaseResult()
    data class InsufficientPoints(val required: Int, val available: Int) : PurchaseResult()
    data class Error(val message: String) : PurchaseResult()
}

class PurchaseGuideUseCase(
    private val walletRepository: WalletRepository,
    private val guideRepository: GuideRepository
) {
    suspend operator fun invoke(guide: Course): PurchaseResult {
        return try {
            // 1. جلب رصيد النقاط الحالي من المحفظة
            val currentBalance = walletRepository.getBalance().first()

            // 2. إذا كان مجاني أو محمل مسبقاً، تنزيل مباشر بدون خصم
            if (guide.points <= 0 || guide.isDownloaded) {
                guideRepository.downloadGuideFile(guide)
                return PurchaseResult.Success
            }

            // 3. التحقق من كفاية الرصيد
            if (currentBalance < guide.points) {
                return PurchaseResult.InsufficientPoints(
                    required = guide.points,
                    available = currentBalance
                )
            }

            // 4. خصم النقاط وتسجيل العملية في المحفظة
            walletRepository.addTransaction(
                title = "Unlock: ${guide.title}",
                amount = guide.points,
                isIncome = false
            )

            // 5. تحميل وحفظ الكتالوج
            guideRepository.downloadGuideFile(guide)
            PurchaseResult.Success

        } catch (e: Exception) {
            PurchaseResult.Error(e.message ?: "An unexpected error occurred")
        }
    }
}