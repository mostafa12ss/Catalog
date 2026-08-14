package com.learn.catalog2.presentation.utils

import com.learn.catalog2.domain.UseCases.PurchaseGuideUseCase.PurchaseGuideUseCase
import com.learn.catalog2.domain.UseCases.PurchaseGuideUseCase.PurchaseResult
import com.learn.catalog2.domain.models.DataModels.Course
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PurchaseManager(
    private val purchaseGuideUseCase: PurchaseGuideUseCase
) {
    private val _purchaseResult = MutableSharedFlow<PurchaseResult>()
    val purchaseResult: SharedFlow<PurchaseResult> = _purchaseResult.asSharedFlow()

    suspend fun processPurchase(guide: Course) {
        val result = purchaseGuideUseCase(guide)
        _purchaseResult.emit(result)
    }
}