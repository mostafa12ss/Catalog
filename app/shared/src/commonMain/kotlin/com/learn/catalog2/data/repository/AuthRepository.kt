package com.learn.catalog2.data.repository

import com.learn.catalog2.domain.models.AppUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<AppUser?>
    suspend fun signUp(email: String, password: String, fullName: String, userType: String): kotlin.Result<Unit>
    suspend fun signIn(email: String, password: String): kotlin.Result<Unit>

    suspend fun signInWithGoogle(): kotlin.Result<Unit>
    suspend fun signInWithGithub(): kotlin.Result<Unit>
    suspend fun signOut(): kotlin.Result<Unit>

}