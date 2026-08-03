package com.learn.catalog2.domain.models

data class WalletTransaction(
    val id: String,
    val title: String,
    val date: String,
    val amount: Int,       // موجب = دخل، سالب = مصروف
    val isIncome: Boolean,
    val timestamp: Long = 0
)
