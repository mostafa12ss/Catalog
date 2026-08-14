package com.learn.catalog2.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.catalog2.domain.models.WalletTransaction
import com.learn.catalog2.domain.repository.WalletRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WalletViewModel(
    private val walletRepository: WalletRepository
) : ViewModel() {

    // 💡 تم استخدام getBalance() بدلاً من getPointsBalance() ليطابق اسم الدالة في الـ Repository
    val pointsBalance: StateFlow<Int> = walletRepository.getBalance()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val transactions: StateFlow<List<WalletTransaction>> = walletRepository.getTransactions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // دالة شحن النقاط (تزيد الرصيد)
    fun topUpPoints(amount: Int, title: String = "Points Top Up") {
        viewModelScope.launch {
            walletRepository.addTransaction(title = title, amount = amount, isIncome = true)
        }
    }

    // دالة الخصم/الشراء (تنقص الرصيد)
    fun deductPoints(amount: Int, title: String = "Catalog Purchase"): Boolean {
        if (pointsBalance.value < amount) return false // رصيد غير كافٍ

        viewModelScope.launch {
            walletRepository.addTransaction(title = title, amount = amount, isIncome = false)
        }
        return true
    }
}