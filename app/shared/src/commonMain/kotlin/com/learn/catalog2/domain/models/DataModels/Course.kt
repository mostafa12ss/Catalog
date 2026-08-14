package com.learn.catalog2.domain.models.DataModels

data class Course(
    val id: String,
    val rank: Int,
    val title: String,
    val subtitle: String,
    val author: String = "",
    val level: String = "Beginner",
    val points: Int,
    val downloads: Int,
    val rating: Float=0.0f,
    val isSaved: Boolean = false,
    val isDownloaded: Boolean = false, // إضافة الخاصية المفقودة
    val fileUrls: List<String> = emptyList(),
    val localPath: String? = null,           // 👈 المسار المحلي بعد التنزيل (C:/Downloads/CV_Mahmoud.pdf)
    val categoryId: String = "",       // 👈 لحفظ ID الفئة
    val categoryName: String = ""
)
