package com.learn.catalog2.di

import com.learn.catalog2.app.shared.data.remote.SupabaseClientProvider
import com.learn.catalog2.data.local.DatabaseDriverFactory
import com.learn.catalog2.data.local.createDatabase
import com.learn.catalog2.data.repository.AuthRepository
import com.learn.catalog2.data.repository.AuthRepositoryImpl
import com.learn.catalog2.data.repository.GuideRepository
import com.learn.catalog2.data.repository.GuideRepositoryImpl
import com.learn.catalog2.data.repository.UserRepository
import com.learn.catalog2.data.repository.UserRepositoryImpl
import com.learn.catalog2.data.repository.WalletRepositoryImpl
import com.learn.catalog2.database.CatalogDatabase
import com.learn.catalog2.domain.UseCases.PurchaseGuideUseCase.PurchaseGuideUseCase
import com.learn.catalog2.domain.repository.WalletRepository
import com.learn.catalog2.presentation.utils.PurchaseManager
import com.learn.catalog2.presentation.viewmodels.AddGuideViewModel
import com.learn.catalog2.presentation.viewmodels.AuthViewModel
import com.learn.catalog2.presentation.viewmodels.ExploreViewModel
import com.learn.catalog2.presentation.viewmodels.ProfileViewModel
import com.learn.catalog2.presentation.viewmodels.RoleViewModel
import com.learn.catalog2.presentation.viewmodels.WalletViewModel
import io.github.jan.supabase.SupabaseClient
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {

    // 1. Supabase Client
    single { SupabaseClientProvider.client }

    // 2. Database Creation
    single<CatalogDatabase> {
        createDatabase(get<DatabaseDriverFactory>())
    }

    // 3. Repositories (Singletons)
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
    singleOf(::GuideRepositoryImpl) { bind<GuideRepository>() }
    singleOf(::WalletRepositoryImpl) { bind<WalletRepository>() }

    // 4. Use Cases & Managers
    factory {
        PurchaseGuideUseCase(
            guideRepository = get<GuideRepository>(),
            walletRepository = get<WalletRepository>()
        )
    }
    singleOf(::PurchaseManager)

    // 5. ViewModels
    factory { AuthViewModel(get<AuthRepository>()) }

    // RoleViewModel بدون أي Parameters بداخل الـ Constructor
    factory { RoleViewModel() }

    factory { AddGuideViewModel(get<GuideRepository>()) }
    factory { WalletViewModel(get<WalletRepository>()) }

    factory {
        ExploreViewModel(
            repository = get<GuideRepository>(),
            walletRepository = get<WalletRepository>(),
            purchaseManager = get<PurchaseManager>()
        )
    }

    factory {
        ProfileViewModel(
            userRepository = get<UserRepository>(),
            guideRepository = get<GuideRepository>(),
            walletRepository = get<WalletRepository>(),
            supabase = get<SupabaseClient>()
        )
    }
}