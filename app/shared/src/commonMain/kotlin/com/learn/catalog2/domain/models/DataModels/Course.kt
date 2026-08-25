package com.learn.catalog2.domain.models.DataModels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Course(
    val id: String,
    val rank: Int = 0,
    val title: String = "",
    val subtitle: String = "",
    val author: String = "",
    val level: String = "Beginner",
    val points: Int = 0,
    val downloads: Int = 0,
    val rating: Float = 0.0f,

    @SerialName("is_saved")
    val isSaved: Boolean = false,

    @SerialName("is_downloaded")
    val isDownloaded: Boolean = false,

    @SerialName("file_urls")
    val fileUrls: List<String> = emptyList(),

    @SerialName("local_path")
    val localPath: String? = null,

    @SerialName("category_id")
    val categoryId: String = "",

    @SerialName("category_name")
    val categoryName: String = ""
)