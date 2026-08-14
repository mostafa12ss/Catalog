package com.learn.catalog2.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.catalog2.AppSettings
import com.learn.catalog2.data.repository.GuideRepository
import com.learn.catalog2.data.repository.UserProfileStats
import com.learn.catalog2.data.repository.UserRepository
import com.learn.catalog2.domain.models.AppUser
import com.learn.catalog2.domain.models.UserRole
import com.learn.catalog2.domain.repository.WalletRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(
    private val userRepository: UserRepository,
    private val guideRepository: GuideRepository,
    private val walletRepository: WalletRepository, // 👈 ربط المحفظة للرصيد الحقيقي
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _user = MutableStateFlow<AppUser?>(null)
    val user: StateFlow<AppUser?> = _user.asStateFlow()

    // 💡 الإحصائيات تتحدث أوتوماتيكياً مع تغيّر المستخدم الحالي
    val userStats: StateFlow<UserProfileStats> = _user
        .flatMapLatest { currentUser ->
            guideRepository.getUserStatsFlow()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfileStats()
        )

    private val _currentRole = MutableStateFlow(UserRole.JUNIOR)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _isSeniorMode = MutableStateFlow(false)
    val isSeniorMode: StateFlow<Boolean> = _isSeniorMode.asStateFlow()

    // ⚡ نقاط المحفظة المباشرة والمزامنة مع SQLDelight/Supabase
    val pointsBalance: StateFlow<Int> = walletRepository.getBalance()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    private val _isOfflineSync = MutableStateFlow(true)
    val isOfflineSync: StateFlow<Boolean> = _isOfflineSync.asStateFlow()

    private val _isDarkMode = MutableStateFlow(AppSettings.isDarkMode)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _currentLanguage = MutableStateFlow(
        if (AppSettings.language.startsWith("ar")) "العربية" else "English"
    )
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        observeAuthState()
    }

    /**
     * الاستماع للحالة الحقيقية لـ Supabase Auth لتحديث بيانات المستخدم فوراً
     */
    private fun observeAuthState() {
        viewModelScope.launch {
            supabase.auth.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> loadUserProfile()
                    is SessionStatus.NotAuthenticated -> {
                        _user.value = null
                        _isLoading.value = false
                    }
                    else -> {}
                }
            }
        }
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val authUser = supabase.auth.currentUserOrNull()

                if (authUser != null) {
                    val metadata = authUser.userMetadata
                    val fullName = metadata?.get("full_name")?.toString()?.removeSurrounding("\"")
                        ?: authUser.email?.substringBefore("@")
                        ?: "User"

                    val roleString = metadata?.get("user_type")?.toString()?.removeSurrounding("\"")
                    val role = if (roleString?.contains("Senior", ignoreCase = true) == true || roleString == "expert") {
                        UserRole.SENIOR
                    } else {
                        UserRole.JUNIOR
                    }

                    val appUser = AppUser(
                        id = authUser.id,
                        email = authUser.email,
                        fullName = fullName,
                        role = role,
                        userType = roleString ?: "Junior / Student"
                    )

                    _user.value = appUser
                    _currentRole.value = role
                    _isSeniorMode.value = role == UserRole.SENIOR
                } else {
                    _user.value = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _user.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleRole() {
        val newRole = if (_currentRole.value == UserRole.JUNIOR) UserRole.SENIOR else UserRole.JUNIOR
        changeRole(newRole)
    }

    fun changeRole(newRole: UserRole) {
        viewModelScope.launch {
            try {
                _currentRole.value = newRole
                _isSeniorMode.value = newRole == UserRole.SENIOR
                _user.value = _user.value?.copy(role = newRole)
                userRepository.updateUserRole(newRole)
            } catch (e: Exception) {
                e.printStackTrace()
                loadUserProfile()
            }
        }
    }

    fun changeMode(isSenior: Boolean) {
        val newRole = if (isSenior) UserRole.SENIOR else UserRole.JUNIOR
        changeRole(newRole)
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                supabase.auth.signOut()
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        AppSettings.isDarkMode = enabled
    }

    fun toggleLanguage() {
        val isCurrentlyArabic = AppSettings.language.startsWith("ar")
        val newLangCode = if (isCurrentlyArabic) "en" else "ar"
        val newLangName = if (newLangCode == "ar") "العربية" else "English"

        _currentLanguage.value = newLangName
        AppSettings.language = newLangCode
    }

    fun toggleOfflineSync(enabled: Boolean) {
        _isOfflineSync.value = enabled
    }

    fun refreshProfile() {
        loadUserProfile()
    }
}