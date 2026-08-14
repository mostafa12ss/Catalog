package com.learn.catalog2.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.learn.catalog2.data.local.CatalogEntity
import com.learn.catalog2.data.local.CategoryEntity
import com.learn.catalog2.data.remote.supabase.dto.CourseRatingDto
import com.learn.catalog2.data.remote.supabase.dto.GuideStepDto
import com.learn.catalog2.database.CatalogDatabase
import com.learn.catalog2.domain.models.DataModels.Category
import com.learn.catalog2.domain.models.DataModels.Course
import com.learn.catalog2.remote.supabase.dto.CategoryDto
import com.learn.catalog2.remote.supabase.dto.CourseDto
import com.learn.catalog2.remote.supabase.dto.difficultyToLevel
import com.learn.catalog2.data.util.StorageUtils
import com.learn.catalog2.presentation.utils.saveDownloadedFile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Clean, robust, production-ready implementation of [GuideRepository] using Supabase KMP.
 */
class GuideRepositoryImpl(
    private val supabase: SupabaseClient,
    private val database: CatalogDatabase
) : GuideRepository {

    private val catalogQueries = database.catalogEntityQueries
    private val categoryQueries = database.categoryEntityQueries

    private val _downloadedGuidesState = MutableStateFlow<List<Course>>(emptyList())

    override fun getTrendingCourses(): Flow<List<Course>> {
        return catalogQueries.selectAllCatalogs()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }
            .catch { e ->
                println("❌ Database Error (getTrendingCourses): ${e.message}")
                emit(emptyList())
            }
    }

    override fun getCategories(): Flow<List<Category>> {
        return categoryQueries.selectAllCategories()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { Category(it.id, it.name, it.count.toInt()) } }
            .catch { e ->
                println("❌ Database Error (getCategories): ${e.message}")
                emit(emptyList())
            }
    }

    override suspend fun syncData() {
        try {
            // 1. جلب الكتالوجات المرفوعة المعتمدة مع ربط بيانات المؤلف
            val response = supabase.from("guides")
                .select(Columns.raw("id, author_id, title, description, difficulty, price_points, downloads, rating, is_published, category_id, file_url, profiles(full_name)")) {
                    filter {
                        eq("is_published", true)
                    }
                    order("downloads", Order.DESCENDING)
                    limit(50)
                }

            val dtos = response.decodeList<CourseDto>()

            // 2. جلب الأقسام
            val catResponse = supabase.from("categories").select()
            val catDtos = catResponse.decodeList<CategoryDto>()

            val currentLocalItems = catalogQueries.selectAllCatalogs().executeAsList().associateBy { it.id }

            database.transaction {
                dtos.forEach { dto ->
                    val existing = currentLocalItems[dto.id]
                    catalogQueries.insertOrReplaceCatalog(
                        CatalogEntity(
                            id = dto.id,
                            title = dto.title,
                            description = dto.description ?: "",
                            author = dto.profiles?.full_name ?: dto.author_id ?: "Eng. Unknown",
                            level = difficultyToLevel(dto.difficulty),
                            downloads = dto.downloads.toLong(),
                            isDownloaded = existing?.isDownloaded ?: 0L,
                            isFavorite = existing?.isFavorite ?: 0L,
                            points = dto.price_points.toLong(),
                            rating = dto.rating.toDouble(),
                            category_id = dto.category_id,
                            file_url = dto.file_url,
                            local_path = existing?.local_path
                        )
                    )
                }

                catDtos.forEach { dto ->
                    categoryQueries.insertOrReplaceCategory(
                        CategoryEntity(
                            id = dto.id,
                            name = dto.name,
                            count = dto.count.toLong()
                        )
                    )
                }
            }
        } catch (e: Exception) {
            println("❌ Sync Error: ${e.message}")
        }
    }

    override suspend fun searchGuides(query: String): List<Course> {
        return try {
            catalogQueries.selectAllCatalogs().executeAsList()
                .filter { it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
                .map { it.toDomain() }
        } catch (e: Exception) {
            println("❌ Search Guides Error: ${e.message}")
            emptyList()
        }
    }

    override fun getFavorites(): Flow<List<Course>> {
        return catalogQueries.selectAllCatalogs()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list -> list.filter { it.isFavorite == 1L }.map { it.toDomain() } }
            .catch { emit(emptyList()) }
    }

    override suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
        try {
            val catalog = catalogQueries.selectAllCatalogs().executeAsList().find { it.id == id }
            catalog?.let {
                val updated = it.copy(isFavorite = if (isFavorite) 1L else 0L)
                catalogQueries.insertOrReplaceCatalog(updated)
            }
        } catch (e: Exception) {
            println("❌ Toggle Favorite Error: ${e.message}")
        }
    }

    override suspend fun rateGuide(
        guideId: String,
        newRating: Float
    ): Result<Unit> {
        return try {
            val user = supabase.auth.currentUserOrNull() ?: throw Exception("User not authenticated")

            // 1. إنشاء كائن الـ DTO النظيف
            val ratingDto = CourseRatingDto(
                guideId = guideId,
                userId = user.id,
                rating = newRating.toDouble()
            )

            // 2. إرسال الكائن مباشرة مع تحديد تعارض المفاتيح (Upsert)
            supabase.from("course_ratings").upsert(ratingDto) {
                onConflict = "guide_id, user_id"
            }

            // 3. تحديث التقييم محلياً في SQLDelight لسرعة العرض
            val catalog = catalogQueries.selectAllCatalogs().executeAsList().find { it.id == guideId }
            catalog?.let {
                val updated = it.copy(rating = newRating.toDouble())
                catalogQueries.insertOrReplaceCatalog(updated)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ Rate Guide Error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun uploadFile(bucket: String, path: String, byteArray: ByteArray): Result<String> = runCatching {
        val safeFileName = StorageUtils.sanitizeFileName(path.split("/").last())
        val folder = if (path.contains("/")) path.substringBeforeLast("/") + "/" else ""
        val fullPath = "$folder$safeFileName"

        val bucketRef = supabase.storage.from(bucket)
        bucketRef.upload(fullPath, byteArray) {
            upsert = true
        }
        bucketRef.publicUrl(fullPath)
    }

    override fun getCategoryCountsFlow(): Flow<Map<String, Long>> {
        return catalogQueries.selectCategoriesWithCount()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list ->
                list.filter { it.category_id != null }
                    .associate { it.category_id!! to it.catalogCount }
            }
            .catch { emit(emptyMap()) }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createCatalog(
        title: String,
        subtitle: String,
        categoryId: String,
        points: Int,
        fileUrls: List<String>
    ): Result<Unit> = runCatching {
        val user = supabase.auth.currentUserOrNull() ?: throw Exception("User session not found")
        val generatedId = Uuid.random().toString()
        val authorName = user.userMetadata?.get("full_name")?.toString()?.removeSurrounding("\"")
            ?: user.email?.substringBefore("@")
            ?: "Creator"

        val insertData = buildJsonObject {
            put("id", generatedId)
            put("author_id", user.id)
            put("title", title)
            put("description", subtitle)
            if (categoryId.isNotBlank()) {
                put("category_id", categoryId)
            }
            put("file_url", fileUrls.firstOrNull())
            put("difficulty", 1)
            put("price_points", points)
            put("is_published", true)
        }

        supabase.from("guides").insert(insertData)

        database.transaction {
            catalogQueries.insertOrReplaceCatalog(
                CatalogEntity(
                    id = generatedId,
                    title = title,
                    description = subtitle,
                    author = authorName,
                    level = "Beginner",
                    downloads = 0L,
                    isDownloaded = 0L,
                    isFavorite = 0L,
                    points = points.toLong(),
                    rating = 5.0,
                    category_id = categoryId.ifBlank { null },
                    file_url = fileUrls.firstOrNull(),
                    local_path = null
                )
            )
        }
        syncData()
    }

    override fun getDownloadedGuides(): Flow<List<Course>> {
        return catalogQueries.selectAllCatalogs()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list ->
                list.filter { it.isDownloaded == 1L }
                    .map { entity ->
                        entity.toDomain().copy(isDownloaded = true, isSaved = true)
                    }
            }
            .catch { emit(emptyList()) }
    }

    override suspend fun downloadGuideFile(guide: Course): Result<String> = runCatching {
        val rawUrl = guide.fileUrls.firstOrNull() ?: throw Exception("File URL not found")

        val relativePath = when {
            rawUrl.contains("/catalogs/") -> rawUrl.substringAfter("/catalogs/")
            rawUrl.startsWith("http://") || rawUrl.startsWith("https://") -> rawUrl.split("/").last()
            else -> rawUrl
        }

        // 1. تنزيل وحفظ الملف والحصول على المسار المحلي
        val bytes = supabase.storage.from("catalogs").downloadPublic(relativePath)
        val savedLocalPath: String = saveDownloadedFile(relativePath, bytes)

        // 2. استدعاء الـ RPC لزيادة التحميلات في Supabase
        try {
            supabase.postgrest.rpc(
                function = "increment_downloads",
                parameters = buildJsonObject { put("guide_id", guide.id) }
            )
        } catch (e: Exception) {
            println("⚠️ Supabase RPC failed: ${e.message}")
        }

        // 3. تحديث القيمة محلياً مع حفظ المسار المحلي local_path
        val catalog = catalogQueries.selectAllCatalogs().executeAsList().find { it.id == guide.id }
        val newDownloadsCount = (catalog?.downloads ?: guide.downloads.toLong()) + 1L

        catalog?.let {
            catalogQueries.insertOrReplaceCatalog(
                it.copy(
                    isDownloaded = 1L,
                    downloads = newDownloadsCount,
                    local_path = savedLocalPath
                )
            )
        }

        _downloadedGuidesState.update { current ->
            if (current.none { it.id == guide.id }) {
                current + guide.copy(
                    isSaved = true,
                    isDownloaded = true,
                    downloads = newDownloadsCount.toInt(),
                    localPath = savedLocalPath
                )
            } else current
        }

        guide.title
    }

    override fun getCatalogsFlow(): Flow<List<Course>> {
        return catalogQueries.selectAllCatalogs()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }
    override suspend fun getGuideSteps(guideId: String): Result<List<GuideStepDto>> = runCatching {
        supabase.from("guide_steps")
            .select {
                filter {
                    eq("guide_id", guideId)
                }
                order("step_order", Order.ASCENDING)
            }
            .decodeList<GuideStepDto>()
    }
    override fun getUserStatsFlow(): Flow<UserProfileStats> {
        val currentUserId = supabase.auth.currentUserOrNull()?.id
        val currentUserName = supabase.auth.currentUserOrNull()?.userMetadata?.get("full_name")?.toString()?.removeSurrounding("\"")

        return catalogQueries.selectAllCatalogs()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list ->
                // --- Junior Stats ---
                val downloadedList = list.filter { it.isDownloaded == 1L }
                val guidesOwned = downloadedList.size
                val ptsSpent = downloadedList.sumOf { it.points.toInt() }
                val offlineGuides = downloadedList.count { !it.local_path.isNullOrEmpty() }

                // --- Senior Stats ---
                val publishedList = list.filter {
                    (currentUserId != null && it.author == currentUserId) ||
                            (currentUserName != null && it.author.equals(currentUserName, ignoreCase = true))
                }

                val publishedGuidesCount = publishedList.size
                val totalDownloads = publishedList.sumOf { it.downloads.toInt() }
                val totalEarnedPoints = publishedList.sumOf {
                    it.downloads.toInt() * it.points.toInt()
                }

                UserProfileStats(
                    guidesOwned = guidesOwned,
                    ptsSpent = ptsSpent,
                    offlineGuides = offlineGuides,
                    publishedGuidesCount = publishedGuidesCount,
                    totalDownloads = totalDownloads,
                    totalEarnedPoints = totalEarnedPoints
                )
            }
            .catch { emit(UserProfileStats()) }
    }

    private fun CatalogEntity.toDomain() = Course(
        id = id,
        rank = 0,
        title = title,
        subtitle = description,
        author = author,
        level = level,
        points = points.toInt(),
        downloads = downloads.toInt(),
        rating = rating.toFloat(),
        isSaved = isFavorite == 1L,
        isDownloaded = isDownloaded == 1L,
        fileUrls = listOfNotNull(file_url),
        localPath = local_path,
        categoryId = category_id ?: ""
    )
}