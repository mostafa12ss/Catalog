package com.learn.catalog2.data.remote.supabase.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    val type: String,
    val amount: Double,
    @SerialName("related_guide_id") val relatedGuideId: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)