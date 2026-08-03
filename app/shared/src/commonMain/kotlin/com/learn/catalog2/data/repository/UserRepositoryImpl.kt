package com.learn.catalog2.data.repository

import com.learn.catalog2.domain.models.AppUser
import com.learn.catalog2.domain.models.UserRole

class UserRepositoryImpl : UserRepository {

    private var currentUser = AppUser.getDemoUser()

    override suspend fun getCurrentUser(): AppUser {
        return currentUser
    }

    override suspend fun updateUserRole(role: UserRole) {
        currentUser = currentUser.copy(role = role)
    }
}