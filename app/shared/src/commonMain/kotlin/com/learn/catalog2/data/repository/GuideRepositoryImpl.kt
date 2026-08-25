package com.learn.catalog2.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.learn.catalog2.data.local.CatalogEntity
import com.learn.catalog2.data.local.CategoryEntity
import com.learn.catalog2.data.remote.supabase.dto.CourseRatingDto
import com.learn.catalog2.data.remote.supabase.dto.GuideStepDto
import com.learn.catalog2.data.util.StorageUtils
import com.learn.catalog2.database.CatalogDatabase
import com.learn.catalog2.domain.models.DataModels.Category
import com.learn.catalog2.domain.models.DataModels.Course
import com.learn.catalog2.presentation.utils.saveDownloadedFile
import com.learn.catalog2.remote.supabase.dto.CategoryDto
import com.learn.catalog2.remote.supabase.dto.CourseDto
import com.learn.catalog2.remote.supabase.dto.difficultyToLevel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlin.random.Random
import kotlin.time.ExperimentalTime

class GuideRepositoryImpl(
    private val supabase: SupabaseClient,
    private val database: CatalogDatabase
) : GuideRepository {

    private val catalogQueries = database.catalogEntityQueries
    private val categoryQueries = database.categoryEntityQueries

    private val _downloadedGuidesState = MutableStateFlow<List<Course>>(emptyList())

    override fun getTrendingCourses(): Flow<List<Course>> {
        return getCatalogsFlow()
    }

    override fun getCategories(): Flow<List<Category>> {
        return categoryQueries.selectAllCategories()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { Category(it.id, it.name, it.count.toInt()) } }
            .flowOn(Dispatchers.Default)
            .catch { emit(emptyList()) }
    }

    override suspend fun syncData() = withContext(Dispatchers.Default) {
        try {
            println("🔄 Starting Supabase Sync...")

            val response = supabase.from("guides")
                .select(Columns.raw("id, author_id, title, description, difficulty, price_points, downloads, rating, is_published, category_id, file_url, profiles(full_name)")) {
                    filter { eq("is_published", true) }
                    order("downloads", Order.DESCENDING)
                    limit(50)
                }

            val dtos = response.decodeList<CourseDto>()
            println("✅ Retrieved ${dtos.size} guides from Supabase")

            val catResponse = supabase.from("categories").select()
            val catDtos = catResponse.decodeList<CategoryDto>()

            val currentLocalItems = catalogQueries.selectAllCatalogs().executeAsList().associateBy { it.id }

            catDtos.forEach { dto ->
                categoryQueries.insertOrReplaceCategory(
                    CategoryEntity(
                        id = dto.id,
                        name = dto.name,
                        count = dto.count.toLong()
                    )
                )
            }

            dtos.forEach { dto ->
                val existing = currentLocalItems[dto.id]
                catalogQueries.insertOrReplaceCatalog(
                    CatalogEntity(
                        id = dto.id,
                        title = dto.title.ifBlank { "Untitled Guide" },
                        description = dto.description ?: "",
                        author = dto.profiles?.full_name ?: dto.author_id ?: "Eng. Creator",
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

            println("💾 Sync completed successfully!")
        } catch (e: Exception) {
            println("❌ Sync Error Details: ${e.message}")
            e.printStackTrace()
        }
    }

    override suspend fun searchGuides(query: String): List<Course> = withContext(Dispatchers.Default) {
        runCatching {
            val categoriesMap = categoryQueries.selectAllCategories().executeAsList().associate { it.id to it.name }
            catalogQueries.selectAllCatalogs().executeAsList()
                .filter { it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
                .map { it.toDomain(categoriesMap) }
        }.getOrDefault(emptyList())
    }

    override fun getFavorites(): Flow<List<Course>> {
        return catalogQueries.selectAllCatalogs()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list ->
                val categoriesMap = categoryQueries.selectAllCategories().executeAsList().associate { it.id to it.name }
                list.filter { it.isFavorite == 1L }.map { it.toDomain(categoriesMap) }
            }
            .flowOn(Dispatchers.Default)
            .catch { emit(emptyList()) }
    }

    override suspend fun toggleFavorite(id: String, isFavorite: Boolean) = withContext(Dispatchers.Default) {
        runCatching {
            val catalog = catalogQueries.selectAllCatalogs().executeAsList().find { it.id == id }
            catalog?.let {
                val updated = it.copy(isFavorite = if (isFavorite) 1L else 0L)
                catalogQueries.insertOrReplaceCatalog(updated)
            }
        }
        Unit
    }

    override suspend fun rateGuide(
        guideId: String,
        newRating: Float
    ): Result<Unit> = withContext(Dispatchers.Default) {
        runCatching {
            val user = supabase.auth.currentUserOrNull() ?: throw Exception("User not authenticated")

            val ratingDto = CourseRatingDto(
                guideId = guideId,
                userId = user.id,
                rating = newRating.toDouble()
            )

            supabase.from("course_ratings").upsert(ratingDto) {
                onConflict = "guide_id, user_id"
            }

            val catalog = catalogQueries.selectAllCatalogs().executeAsList().find { it.id == guideId }
            catalog?.let {
                val updated = it.copy(rating = newRating.toDouble())
                catalogQueries.insertOrReplaceCatalog(updated)
            }
            Unit
        }
    }

    override suspend fun uploadFile(bucket: String, path: String, byteArray: ByteArray): Result<String> = withContext(Dispatchers.Default) {
        runCatching {
            val safeFileName = StorageUtils.sanitizeFileName(path.split("/").last())
            val folder = if (path.contains("/")) path.substringBeforeLast("/") + "/" else ""
            val fullPath = "$folder$safeFileName"

            val bucketRef = supabase.storage.from(bucket)
            bucketRef.upload(fullPath, byteArray) {
                upsert = true
            }
            bucketRef.publicUrl(fullPath)
        }
    }

    override fun getCategoryCountsFlow(): Flow<Map<String, Long>> {
        return catalogQueries.selectCategoriesWithCount()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list ->
                list.filter { it.category_id != null }
                    .associate { it.category_id!! to it.catalogCount }
            }
            .flowOn(Dispatchers.Default)
            .catch { emit(emptyMap()) }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun createCatalog(
        title: String,
        subtitle: String,
        categoryId: String,
        points: Int,
        fileUrls: List<String>
    ): Result<Unit> = withContext(Dispatchers.Default) {
        runCatching {
            val user = supabase.auth.currentUserOrNull() ?: throw Exception("User session not found")

            runCatching {
                val authorName = user.userMetadata?.get("full_name")?.jsonPrimitive?.content
                    ?: user.email?.substringBefore("@")
                    ?: "Creator"

                supabase.from("profiles").upsert(buildJsonObject {
                    put("id", user.id)
                    put("full_name", authorName)
                })
            }

            val generatedId = "guide_${Random.nextLong(100, Long.MAX_VALUE)}"
            val authorName = user.userMetadata?.get("full_name")?.jsonPrimitive?.content
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
            syncData()
            Unit
        }
    }

    override fun getDownloadedGuides(): Flow<List<Course>> {
        return catalogQueries.selectAllCatalogs()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list ->
                val categoriesMap = categoryQueries.selectAllCategories().executeAsList().associate { it.id to it.name }
                list.filter { it.isDownloaded == 1L }
                    .map { entity ->
                        entity.toDomain(categoriesMap).copy(isDownloaded = true, isSaved = true)
                    }
            }
            .flowOn(Dispatchers.Default)
            .catch { emit(emptyList()) }
    }

    override suspend fun downloadGuideFile(guide: Course): Result<String> = withContext(Dispatchers.Default) {
        runCatching {
            val rawUrl = guide.fileUrls.firstOrNull() ?: throw Exception("File URL not found")

            // 1. تنزيل الملف وحفظه محلياً
            val relativePath = when {
                rawUrl.contains("/catalogs/") -> rawUrl.substringAfter("/catalogs/")
                rawUrl.startsWith("http://") || rawUrl.startsWith("https://") -> rawUrl.split("/").last()
                else -> rawUrl
            }

            val bytes = supabase.storage.from("catalogs").downloadPublic(relativePath)
            val savedLocalPath: String = saveDownloadedFile(relativePath, bytes)

            // 2. زيادة عدد التحميلات في Supabase
            runCatching {
                supabase.postgrest.rpc(
                    function = "increment_downloads",
                    parameters = buildJsonObject { put("guide_id", guide.id) }
                )
            }

            // 3. تحديث قاعدة البيانات المحلية (SQLDelight)
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

            // 4. تحديث الـ State
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
    }

    override fun getCatalogsFlow(): Flow<List<Course>> = flow {
        val localItems = catalogQueries.selectAllCatalogs().executeAsList()

        if (localItems.isNotEmpty()) {
            val categoriesMap = categoryQueries.selectAllCategories().executeAsList().associate { it.id to it.name }
            emit(localItems.map { it.toDomain(categoriesMap) })
        } else {
            println("🌐 Local DB is empty (Web NoOp Driver), fetching directly from Supabase...")
            val response = supabase.from("guides")
                .select(Columns.raw("id, author_id, title, description, difficulty, price_points, downloads, rating, is_published, category_id, file_url, profiles(full_name)")) {
                    filter { eq("is_published", true) }
                    order("downloads", Order.DESCENDING)
                    limit(50)
                }
            val remoteDtos = response.decodeList<CourseDto>()
            emit(remoteDtos.map { it.toCourse() })
        }
    }.catch { error ->
        println("❌ Error loading catalogs flow: ${error.message}")
        emit(emptyList())
    }.flowOn(Dispatchers.Default)

    override suspend fun getGuideSteps(guideId: String): Result<List<GuideStepDto>> = withContext(Dispatchers.Default) {
        runCatching {
            supabase.from("guide_steps")
                .select {
                    filter { eq("guide_id", guideId) }
                    order("step_order", Order.ASCENDING)
                }
                .decodeList<GuideStepDto>()
        }
    }

    override fun getUserStatsFlow(): Flow<UserProfileStats> {
        val currentUserName = supabase.auth.currentUserOrNull()?.userMetadata?.get("full_name")?.jsonPrimitive?.content

        return catalogQueries.selectAllCatalogs()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list ->
                val downloadedList = list.filter { it.isDownloaded == 1L }
                val guidesOwned = downloadedList.size
                val ptsSpent = downloadedList.sumOf { it.points.toInt() }
                val offlineGuides = downloadedList.count { !it.local_path.isNullOrEmpty() }

                val publishedList = list.filter {
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
            .flowOn(Dispatchers.Default)
            .catch { emit(UserProfileStats()) }
    }

    private fun CatalogEntity.toDomain(categoriesMap: Map<String, String> = emptyMap()) = Course(
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
        categoryId = category_id ?: "",
        categoryName = categoriesMap[category_id] ?: ""
    )
}