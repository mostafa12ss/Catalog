package com.learn.catalog2.data.remote.supabase.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("type") val type: String,
    @SerialName("amount") val amount: Double,
    @SerialName("related_guide_id") val relatedGuideId: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)