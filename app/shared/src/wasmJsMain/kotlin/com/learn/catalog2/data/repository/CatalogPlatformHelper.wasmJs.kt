package com.learn.catalog2.data.repository

import com.learn.catalog2.app.shared.data.remote.SupabaseClientProvider
import com.learn.catalog2.domain.models.DataModels.Course
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

actual fun getPlatformCatalogsFlow(
    localQueryFlow: () -> Flow<List<Course>>
): Flow<List<Course>> = flow {
    try {
        // جلب البيانات مباشرة من جدول Supabase للويب فقط
        val courses = SupabaseClientProvider.client.postgrest["guides"]
            .select()
            .decodeList<Course>() // يفترض أن الكلاس Course معلم بـ @Serializable

        emit(courses)
    } catch (e: Exception) {
        println("❌ Web Supabase Error: ${e.message}")
        emit(emptyList())
    }
}