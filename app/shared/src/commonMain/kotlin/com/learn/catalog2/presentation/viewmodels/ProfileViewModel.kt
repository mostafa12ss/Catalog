package com.learn.catalog2.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.catalog2.data.repository.UserRepository
import com.learn.catalog2.domain.models.AppUser
import com.learn.catalog2.domain.models.UserRole
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    // Current User Data
    private val _user = MutableStateFlow(AppUser.getDemoUser())
    val user: StateFlow<AppUser> = _user.asStateFlow()

    // Role Management
    private val _currentRole = MutableStateFlow(UserRole.JUNIOR)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _isSeniorMode = MutableStateFlow(false)
    val isSeniorMode: StateFlow<Boolean> = _isSeniorMode.asStateFlow()

    // Points
    private val _pointsBalance = MutableStateFlow(1340)
    val pointsBalance: StateFlow<Int> = _pointsBalance.asStateFlow()

    // Stats
    private val _guidesOwned = MutableStateFlow(3)
    private val _pointsSpent = MutableStateFlow(265)
    private val _offlineCount = MutableStateFlow(3)

    private val _publishedGuides = MutableStateFlow(2)
    private val _totalDownloads = MutableStateFlow(2755)
    private val _totalEarned = MutableStateFlow(26900)

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val profile = userRepository.getCurrentUser()
                _user.value = profile
                _currentRole.value = profile.role
                _isSeniorMode.value = profile.role == UserRole.SENIOR
                _pointsBalance.value = profile.pointsBalance
            } catch (e: Exception) {
                e.printStackTrace()
                // Keep demo data if API fails
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
                userRepository.updateUserRole(newRole)
                _currentRole.value = newRole
                _isSeniorMode.value = newRole == UserRole.SENIOR

                // Refresh user data
                loadUserProfile()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun changeMode(isSenior: Boolean) {
        val newRole = if (isSenior) UserRole.SENIOR else UserRole.JUNIOR
        changeRole(newRole)
    }

    // Stats getters (used in UI)
    fun getGuidesOwned() = _guidesOwned.value
    fun getPointsSpent() = _pointsSpent.value
    fun getOfflineCount() = _offlineCount.value

    fun getPublishedGuides() = _publishedGuides.value
    fun getTotalDownloads() = _totalDownloads.value
    fun getTotalEarned() = _totalEarned.value

    // Future methods
    fun refreshProfile() {
        loadUserProfile()
    }
}