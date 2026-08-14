package com.learn.catalog2.data.repository

import com.learn.catalog2.data.remote.supabase.dto.GuideStepDto
import com.learn.catalog2.domain.models.DataModels.Category
import com.learn.catalog2.domain.models.DataModels.Course
import kotlinx.coroutines.flow.Flow

data class UserProfileStats(
    val guidesOwned: Int = 0,
    val ptsSpent: Int = 0,
    val offlineGuides: Int = 0,
    val publishedGuidesCount: Int = 0,
    val totalDownloads: Int = 0,
    val totalEarnedPoints: Int = 0
)

interface GuideRepository {
    fun getTrendingCourses(): Flow<List<Course>>
    fun getCategories(): Flow<List<Category>>
    fun getCategoryCountsFlow(): Flow<Map<String, Long>>
    suspend fun syncData()
    suspend fun searchGuides(query: String): List<Course>
    fun getFavorites(): Flow<List<Course>>
    suspend fun toggleFavorite(id: String, isFavorite: Boolean)
    suspend fun rateGuide(guideId: String, newRating: Float): Result<Unit>
    fun getUserStatsFlow(): Flow<UserProfileStats>

    // ميزات الـ Senior / الكاتب
    suspend fun uploadFile(bucket: String, path: String, byteArray: ByteArray): Result<String>
    suspend fun createCatalog(
        title: String,
        subtitle: String,
        categoryId: String,
        points: Int,
        fileUrls: List<String>
    ): Result<Unit>

    // جلب خطوات الدليل
    suspend fun getGuideSteps(guideId: String): Result<List<GuideStepDto>>

    // ميزة التحميل والأوفلاين
    fun getDownloadedGuides(): Flow<List<Course>>
    suspend fun downloadGuideFile(guide: Course): Result<String>
    fun getCatalogsFlow(): Flow<List<Course>>
}