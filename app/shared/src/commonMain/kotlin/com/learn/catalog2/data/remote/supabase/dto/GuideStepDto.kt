package com.learn.catalog2.data.remote.supabase.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GuideStepDto(
    val id: String? = null,
    @SerialName("guide_id") val guideId: String,
    @SerialName("step_order") val stepOrder: Int,
    val text: String,
    @SerialName("image_url") val imageUrl: String? = null
)