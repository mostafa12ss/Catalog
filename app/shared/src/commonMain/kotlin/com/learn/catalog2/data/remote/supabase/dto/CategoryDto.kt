package com.learn.catalog2.remote.supabase.dto

import com.learn.catalog2.domain.models.DataModels.Category
import kotlinx.serialization.Serializable

// remote/supabase/dto/CategoryDto.kt
@Serializable
data class CategoryDto(
    val id: String,
    val name: String,
    val count: Int
) {
    fun toCategory() = Category(id, name, count)
}