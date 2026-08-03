// data/repository/UserRepository.kt
package com.learn.catalog2.data.repository

import com.learn.catalog2.domain.models.AppUser
import com.learn.catalog2.domain.models.UserRole

interface UserRepository {
    suspend fun getCurrentUser(): AppUser
    suspend fun updateUserRole(role: UserRole)
}