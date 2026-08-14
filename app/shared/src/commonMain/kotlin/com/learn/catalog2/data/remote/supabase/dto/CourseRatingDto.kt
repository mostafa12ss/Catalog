package com.learn.catalog2.data.remote.supabase.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseRatingDto(
    val id: String? = null,
    @SerialName("guide_id") val guideId: String,
    @SerialName("user_id") val userId: String,
    val rating: Double,
    @SerialName("created_at") val createdAt: String? = null
)