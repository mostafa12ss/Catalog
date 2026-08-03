package com.learn.catalog2.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.catalog2.data.repository.GuideRepository
import com.learn.catalog2.domain.models.DataModels.Category
import com.learn.catalog2.domain.models.DataModels.Course
import com.learn.catalog2.domain.models.UserRole
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val repository: GuideRepository
) : ViewModel() {

    // تحويل Flow المستمر من الـ Repository إلى StateFlow للـ UI
    val trendingCourses: StateFlow<List<Course>> = repository.getTrendingCourses()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val categories: StateFlow<List<Category>> = repository.getCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _userRole = MutableStateFlow(UserRole.JUNIOR)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    private val _pointsBalance = MutableStateFlow(1340)
    val pointsBalance: StateFlow<Int> = _pointsBalance.asStateFlow()

    // طلب مزامنة يدوية إذا لزم الأمر
    fun refresh() {
        viewModelScope.launch {
            repository.syncData()
        }
    }

    fun toggleFavorite(courseId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(courseId, isFavorite)
        }
    }

    fun searchGuides(query: String) {
        viewModelScope.launch {
            val results = repository.searchGuides(query)
            // هنا يمكن إضافة Flow خاص بالبحث إذا لزم الأمر
        }
    }
}
