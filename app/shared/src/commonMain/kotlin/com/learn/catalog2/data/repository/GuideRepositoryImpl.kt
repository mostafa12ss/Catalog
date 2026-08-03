package com.learn.catalog2.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.learn.catalog2.data.local.CatalogEntity
import com.learn.catalog2.data.local.CategoryEntity
import com.learn.catalog2.database.CatalogDatabase
import com.learn.catalog2.domain.models.DataModels.Category
import com.learn.catalog2.domain.models.DataModels.Course
import com.learn.catalog2.remote.supabase.dto.CategoryDto
import com.learn.catalog2.remote.supabase.dto.CourseDto
import com.learn.catalog2.remote.supabase.dto.difficultyToLevel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlin.time.Clock

class GuideRepositoryImpl(
    private val supabase: SupabaseClient,
    private val database: CatalogDatabase
) : GuideRepository {

    private val catalogQueries = database.catalogEntityQueries
    private val categoryQueries = database.categoryEntityQueries

    override fun getTrendingCourses(): Flow<List<Course>> {
        return catalogQueries.selectAllCatalogs()
            .asFlow()
            // 💡 استخدام Dispatchers.Main ليتوافق مع Wasm/Web بدلاً من Default
            .mapToList(Dispatchers.Main)
            .map { entities -> entities.map { it.toDomain() } }
            .onStart {
                try {
                    syncData()
                } catch (e: Exception) {
                    println("Sync failed safely: ${e.message}")
                }
            }
            .catch { e ->
                // 💡 حماية الـ Flow من انهيار الـ ViewModel لو الـ DB فيها مشكلة على الـ Web
                println("Database query error: ${e.message}")
                emit(emptyList())
            }
    }

    override fun getCategories(): Flow<List<Category>> {
        return categoryQueries.selectAllCategories()
            .asFlow()
            .mapToList(Dispatchers.Main)
            .map { entities -> entities.map { Category(it.id, it.name, it.count.toInt()) } }
            .catch { e ->
                println("Database query error: ${e.message}")
                emit(emptyList())
            }
    }

    override suspend fun syncData() {
        try {
            val response = supabase.from("guides").select(
                columns = Columns.raw(
                    "id, author_id, title, description, difficulty, price_points, downloads, rating, is_published, Profiles(full_name)"
                )
            ) {
                filter { eq("is_published", true) }
                order("downloads", Order.DESCENDING)
                limit(50)
            }
            val dtos: List<CourseDto> = response.decodeList()

            database.transaction {
                dtos.forEach { dto ->
                    catalogQueries.insertOrReplaceCatalog(dto.toEntity())
                }
            }

            val catResponse = supabase.from("categories").select()
            val catDtos: List<CategoryDto> = catResponse.decodeList()

            database.transaction {
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
            println("Error during syncData: ${e.message}")
            e.printStackTrace()
        }
    }
    override suspend fun searchGuides(query: String): List<Course> {
        return try {
            catalogQueries.selectAllCatalogs().executeAsList()
                .filter { it.title.contains(query, ignoreCase = true) }
                .map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun getFavorites(): Flow<List<Course>> {
        return catalogQueries.selectAllCatalogs()
            .asFlow()
            .mapToList(Dispatchers.Main)
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
            e.printStackTrace()
        }
    }

    override fun getDownloadedGuides(): Flow<List<Course>> {
        return catalogQueries.selectAllCatalogs()
            .asFlow()
            .mapToList(Dispatchers.Main)
            .map { list -> list.filter { it.isDownloaded == 1L }.map { it.toDomain() } }
            .catch { emit(emptyList()) }
    }

    override suspend fun downloadGuideFile(guide: Course): Result<String> {
        return try {
            val catalog = catalogQueries.selectAllCatalogs().executeAsList().find { it.id == guide.id }
            catalog?.let {
                val updated = it.copy(isDownloaded = 1L)
                catalogQueries.insertOrReplaceCatalog(updated)
            }
            Result.success("downloads/${guide.id}_file")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadFile(bucket: String, path: String, byteArray: ByteArray): String {
        val bucketRef = supabase.storage.from(bucket)
        bucketRef.upload(path, byteArray) {
            upsert = true
        }
        return bucketRef.publicUrl(path)
    }

    override suspend fun createCatalog(
        title: String,
        subtitle: String,
        categoryId: String,
        points: Int,
        fileUrls: List<String>
    ): Result<Unit> {
        return try {
            val newCatalog = CourseDto(
                id = "",
                title = title,
                description = subtitle,
                price_points = points,
                downloads = 0,
                rating = 0f,
                is_published = false
            )
            supabase.from("guides").insert(newCatalog)
            syncData()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun CatalogEntity.toDomain() = Course(
        id = id,
        rank = 0,
        title = title,
        subtitle = description,
        points = points.toInt(),
        downloads = downloads.toInt(),
        rating = rating.toFloat(),
        isSaved = isFavorite == 1L,
        isDownloaded = isDownloaded == 1L
    )

    private fun CourseDto.toEntity() = CatalogEntity(
        id = id,
        title = title,
        description = description ?: "",
        author = Profiles?.full_name ?: "Eng. Unknown",
        level = difficultyToLevel(difficulty),
        downloads = downloads.toLong(),
        isDownloaded = 0L,
        isFavorite = 0L,
        points = price_points.toLong(),
        rating = rating.toDouble()
    )
}