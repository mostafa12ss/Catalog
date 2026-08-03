package com.learn.catalog2.data.repository

import com.learn.catalog2.domain.models.AppUser
import com.learn.catalog2.domain.models.UserRole
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Github
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepositoryImpl(
    private val client: SupabaseClient
) : AuthRepository {

    override val currentUser: Flow<AppUser?> =
        client.auth.sessionStatus.map { status ->
            when (status) {
                is SessionStatus.Authenticated -> {
                    val user = status.session.user
                    val metadata = user?.userMetadata
                    
                    val roleString = metadata?.get("user_type")?.toString()?.removeSurrounding("\"")
                    val role = if (roleString == "SENIOR") UserRole.SENIOR else UserRole.JUNIOR
                    
                    AppUser(
                        id = user?.id.orEmpty(),
                        email = user?.email,
                        fullName = metadata?.get("full_name")?.toString()?.removeSurrounding("\"") ?: "User",
                        role = role,
                        userType = roleString
                    )
                }
                else -> null
            }
        }

    override suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
        userType: String
    ): kotlin.Result<Unit> =
        runCatching {
            client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("full_name", fullName)
                    put("user_type", userType)
                }
            }
        }

    override suspend fun signIn(email: String, password: String): kotlin.Result<Unit> =
        runCatching {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
        }

    override suspend fun signInWithGoogle(): kotlin.Result<Unit> =
        runCatching {
            client.auth.signInWith(Google)
        }

    override suspend fun signInWithGithub(): kotlin.Result<Unit> =
        runCatching {
            client.auth.signInWith(Github)
        }

    override suspend fun signOut(): kotlin.Result<Unit> =
        runCatching { client.auth.signOut() }
}
