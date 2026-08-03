package com.learn.catalog2.di

import com.learn.catalog2.app.shared.data.remote.SupabaseClientProvider
import com.learn.catalog2.data.local.DatabaseDriverFactory
import com.learn.catalog2.data.local.createDatabase
import com.learn.catalog2.data.repository.*
import com.learn.catalog2.database.CatalogDatabase
import com.learn.catalog2.presentation.viewmodels.*
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    // Supabase Client
    single { SupabaseClientProvider.client }

    // Database Driver Factory - تم إضافة get() لتمثيل الـ Context أو المعامل المطلوب
    // Database - تحديد النوع صراحة داخل get() لتجنب خطأ Infer Type T
    single<CatalogDatabase?> {
        try {
            createDatabase(get<DatabaseDriverFactory>())
        } catch (e: Exception) {
            println("Database creation skipped on this platform: ${e.message}")
            null
        }
    }
    // Repositories
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
    singleOf(::GuideRepositoryImpl) { bind<GuideRepository>() }
    singleOf(::WalletRepositoryImpl) { bind<WalletRepository>() }

    // ViewModels
    viewModelOf(::AuthViewModel)
    viewModelOf(::RoleViewModel)
    viewModelOf(::ExploreViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::WalletViewModel)
    viewModelOf(::AddGuideViewModel)
}