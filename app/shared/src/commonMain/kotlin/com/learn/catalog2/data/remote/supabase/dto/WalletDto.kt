package com.learn.catalog2.data.remote.supabase.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WalletDto(
    @SerialName("user_id") val userId: String,
    @SerialName("points_balance") val pointsBalance: Float = 0f,
    @SerialName("earnings_balance") val earningsBalance: Float = 0f
)