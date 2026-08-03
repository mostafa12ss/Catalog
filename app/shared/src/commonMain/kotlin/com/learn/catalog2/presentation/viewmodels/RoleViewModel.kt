package com.learn.catalog2.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.learn.catalog2.domain.models.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RoleViewModel  : ViewModel() {
    private val _currentRole = MutableStateFlow(UserRole.JUNIOR)
    val currentRole: StateFlow<UserRole> = _currentRole

    fun toggleRole() {
        _currentRole.value = if (_currentRole.value == UserRole.JUNIOR) UserRole.SENIOR else UserRole.JUNIOR
    }
}