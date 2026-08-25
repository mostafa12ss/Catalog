package com.learn.catalog2.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.catalog2.data.repository.GuideRepository
import com.learn.catalog2.domain.models.DataModels.Category
import com.learn.catalog2.domain.models.DataModels.Course
import com.learn.catalog2.domain.models.UserRole
import com.learn.catalog2.domain.repository.WalletRepository
import com.learn.catalog2.presentation.utils.PurchaseManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val repository: GuideRepository,
    private val walletRepository: WalletRepository,
    private val purchaseManager: PurchaseManager
) : ViewModel() {

    private var isSynced = false

    init {
        refresh()
    }

    val allCourses: StateFlow<List<Course>> = repository.getCatalogsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // ⚡ الفلترة المرنة والآمنة بعد إصلاح توقيع دالة combine
    val trendingCourses: StateFlow<List<Course>> = combine(
        allCourses,
        _selectedCategoryId,
        _searchQuery
    ) { catalogs, selectedCategory, query ->
        if (catalogs.isEmpty()) return@combine emptyList()

        catalogs.filter { course ->
            val matchesCategory = selectedCategory.isNullOrBlank() ||
                    course.categoryId.equals(selectedCategory, ignoreCase = true) ||
                    course.categoryName.equals(selectedCategory, ignoreCase = true)

            val matchesSearch = query.isBlank() ||
                    course.title.contains(query, ignoreCase = true) ||
                    course.subtitle.contains(query, ignoreCase = true) ||
                    course.author.contains(query, ignoreCase = true)

            matchesCategory && matchesSearch
        }.sortedByDescending { it.downloads }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    fun selectCategory(categoryIdentifier: String) {
        _selectedCategoryId.update { current ->
            if (current.equals(categoryIdentifier, ignoreCase = true)) null else categoryIdentifier
        }
    }

    fun searchGuides(query: String) {
        _searchQuery.value = query
    }

    val categories: StateFlow<List<Category>> = combine(
        repository.getCategories(),
        repository.getCategoryCountsFlow()
    ) { categoryList, countMap ->
        categoryList.map { category ->
            val realCount = countMap[category.id]?.toInt()
                ?: countMap[category.name]?.toInt()
                ?: category.count
            category.copy(count = realCount)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    private val _userRole = MutableStateFlow(UserRole.JUNIOR)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    val pointsBalance: StateFlow<Int> = walletRepository.getBalance()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0
        )

    private val _downloadingIds = MutableStateFlow<Set<String>>(emptySet())
    val downloadingIds: StateFlow<Set<String>> = _downloadingIds.asStateFlow()

    fun refresh() {
        if (isSynced) return
        viewModelScope.launch {
            repository.syncData()
            isSynced = true
        }
    }

    fun forceSync() {
        viewModelScope.launch {
            repository.syncData()
            isSynced = true
        }
    }

    fun toggleFavorite(courseId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(courseId, isFavorite)
        }
    }

    fun downloadGuide(guide: Course) {
        if (_downloadingIds.value.contains(guide.id)) return

        viewModelScope.launch {
            _downloadingIds.update { it + guide.id }
            try {
                purchaseManager.processPurchase(guide)

                // 💡 خصم النقاط محلياً في الـ UI State فور نجاح العملية


            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _downloadingIds.update { it - guide.id }
            }
        }
    }

    fun rateGuide(guideId: String, rating: Float) {
        viewModelScope.launch {
            repository.rateGuide(guideId, rating)
                .onSuccess { println("✅ Guide rated successfully") }
                .onFailure { error -> println("❌ Error rating guide: ${error.message}") }
        }
    }
}