package com.learn.catalog2.domain.models.DataModels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ProfileDto(
    val id: String,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val role: String? = "Junior",
    val title: String? = "Mechatronics Engineer",
    val points: Int = 0
)
