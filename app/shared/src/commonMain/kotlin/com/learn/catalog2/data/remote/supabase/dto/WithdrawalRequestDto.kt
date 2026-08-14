package com.learn.catalog2.data.remote.supabase.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WithdrawalRequestDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("amount_points") val amountPoints: Float,
    @SerialName("amount_cash") val amountCash: Float,
    @SerialName("status") val status: String = "pending",
    @SerialName("requested_at") val requestedAt: String? = null
)