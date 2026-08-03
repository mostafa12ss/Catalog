// remote/supabase/dto/CourseDto.kt
package com.learn.catalog2.remote.supabase.dto

import com.learn.catalog2.domain.models.DataModels.Course
import kotlinx.serialization.Serializable

@Serializable
data class ProfileRef(
    val full_name: String? = null
)

@Serializable
data class CourseDto(
    val id: String,
    val author_id: String? = null,
    val title: String,
    val description: String? = null,
    val difficulty: Long = 1,
    val price_points: Int,
    val downloads: Int = 0,
    val rating: Float = 0f,
    val is_published: Boolean = false,
    val Profiles: ProfileRef? = null // بيتملى تلقائيًا عن طريق join مع جدول Profiles
) {
    fun toCourse() = Course(
        id = id,
        rank = 0,
        title = title,
        subtitle = description ?: "",
        author = Profiles?.full_name ?: "Eng. Unknown",
        level = difficultyToLevel(difficulty),
        points = price_points,
        downloads = downloads,
        rating = rating
    )
}

fun difficultyToLevel(difficulty: Long): String = when (difficulty) {
    1L -> "Beginner"
    2L -> "Intermediate"
    else -> "Advanced"
}