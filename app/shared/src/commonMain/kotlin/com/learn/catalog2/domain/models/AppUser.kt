// domain/models/AppUser.kt
package com.learn.catalog2.domain.models

data class AppUser(
    val id: String,
    val email: String? = null,
    val fullName: String = "User",
    val title: String = "",
    val experience: String = "",
    val rating: Float = 0f,
    val role: UserRole = UserRole.JUNIOR,
    val pointsBalance: Int = 0,
    val userType: String? = null,
    val avatarUrl: String? = null
) {
    // حساب الـ initial بأمان حتى لو كان الاسم يحتوي مسافات فارغة
    val initial: String
        get() = fullName.trim().firstOrNull()?.uppercase() ?: "U"

    companion object {
        fun getDemoUser() = AppUser(
            id = "user_001",
            fullName = "Eng. Rashid Al-Farsi",
            title = "Mechanical Engineer",
            experience = "14 yrs exp.",
            rating = 4.9f,
            role = UserRole.JUNIOR,
            pointsBalance = 1340,
            email = "rashid@example.com",
            userType = "JUNIOR"
        )
    }
}