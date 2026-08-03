// domain/models/AppUser.kt
package com.learn.catalog2.domain.models

data class AppUser(
    val id: String,
    val email: String? = null,           // إضافة الحقل المطلوب
    val fullName: String = "User",       // قيمة افتراضية لتجنب أخطاء الـ Constructor
    val title: String = "",
    val experience: String = "",
    val rating: Float = 0f,
    val role: UserRole = UserRole.JUNIOR,
    val pointsBalance: Int = 0,
    val userType: String? = null,        // إضافة الحقل المطلوب
    val initial: String = if (fullName.isNotEmpty()) fullName.first().toString() else "U"
) {
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
