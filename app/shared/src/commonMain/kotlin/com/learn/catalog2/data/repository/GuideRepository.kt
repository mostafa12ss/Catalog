package com.learn.catalog2.data.repository

import com.learn.catalog2.domain.models.DataModels.Category
import com.learn.catalog2.domain.models.DataModels.Course
import kotlinx.coroutines.flow.Flow

interface GuideRepository {
    fun getTrendingCourses(): Flow<List<Course>>
    fun getCategories(): Flow<List<Category>>
    suspend fun syncData()
    suspend fun searchGuides(query: String): List<Course>
    fun getFavorites(): Flow<List<Course>>
    suspend fun toggleFavorite(id: String, isFavorite: Boolean)

    // ميزات الـ Senior
    suspend fun uploadFile(bucket: String, path: String, byteArray: ByteArray): String
    suspend fun createCatalog(
        title: String,
        subtitle: String,
        categoryId: String,
        points: Int,
        fileUrls: List<String>
    ): Result<Unit>

    // ميزة التحميل والأوفلاين
    fun getDownloadedGuides(): Flow<List<Course>>
    suspend fun downloadGuideFile(guide: Course): Result<String> // يعيد المسار المحلي للملف
}
