package com.learn.catalog2.data.repository

import com.learn.catalog2.domain.models.AppUser
import com.learn.catalog2.domain.models.DataModels.ProfileDto
import com.learn.catalog2.domain.models.UserRole
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class UserRepositoryImpl(
    private val supabase: SupabaseClient // 👈 تم إضافة SupabaseClient في الـ Constructor
) : UserRepository {

    override suspend fun getCurrentUser(): AppUser? {
        // 1. جلب بيانات الجلسة الحالية من Supabase Auth
        val authUser = supabase.auth.currentUserOrNull() ?:return null
            ?: throw IllegalStateException("User not logged in")

        // 2. محاولة قراءة البروفايل من جدول Profiles
        val profile = try {
            supabase.from("profiles")
                .select {
                    filter { eq("id", authUser.id) }
                }
                .decodeSingleOrNull<ProfileDto>()
        } catch (e: Exception) {
            println("⚠️ Error fetching profile: ${e.message}")
            null
        }

        // 3. بناء كيان المستخدم الحقيقي (مع الاعتماد على بيانات Auth كخطة بديلة)
        val extractedName = profile?.fullName
            ?: authUser.userMetadata?.get("full_name")?.toString()?.removeSurrounding("\"")
            ?: authUser.userMetadata?.get("name")?.toString()?.removeSurrounding("\"")
            ?: authUser.email?.substringBefore("@")
            ?: "Engineer"

        val extractedAvatar = profile?.avatarUrl
            ?: authUser.userMetadata?.get("avatar_url")?.toString()?.removeSurrounding("\"")

        val currentRole = if (profile?.role?.uppercase() == "SENIOR") UserRole.SENIOR else UserRole.JUNIOR

        return AppUser(
            id = authUser.id,
            fullName = extractedName,
            email = authUser.email ?: "",
            role = currentRole,
            pointsBalance = profile?.points ?: 0,
            avatarUrl = extractedAvatar
        )
    }

    override suspend fun updateUserRole(newRole: UserRole) {
        val authUser = supabase.auth.currentUserOrNull() ?: return

        try {
            // تحديث عمود role في جدول Profiles
            supabase.from("profiles").update(buildJsonObject {
                put("role", newRole.name)
            }) {
                filter { eq("id", authUser.id) }
            }
        } catch (e: Exception) {
            println("❌ Error updating user role: ${e.message}")
            throw e
        }
    }
}