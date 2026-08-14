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
    private val purchaseManager: PurchaseManager // 👈 تغيير هنا: حَقن PurchaseManager
) : ViewModel() {

    init {
        refresh()
    }

    val allCourses: StateFlow<List<Course>> = repository.getCatalogsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    val trendingCourses: StateFlow<List<Course>> = combine(
        repository.getCatalogsFlow(),
        _selectedCategoryId
    ) { catalogs, categoryId ->
        val filtered = if (categoryId == null) {
            catalogs
        } else {
            catalogs.filter { it.categoryId == categoryId }
        }
        filtered.sortedByDescending { it.downloads }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectCategory(categoryId: String) {
        _selectedCategoryId.update { current ->
            if (current == categoryId) null else categoryId
        }
    }

    val categories: StateFlow<List<Category>> = combine(
        repository.getCategories(),
        repository.getCategoryCountsFlow()
    ) { categoryList, countMap ->
        categoryList.map { category ->
            val realCount = countMap[category.id]?.toInt() ?: 0
            category.copy(count = realCount)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _userRole = MutableStateFlow(UserRole.JUNIOR)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    // ⚡ نقاط المحفظة المباشرة
    val pointsBalance: StateFlow<Int> = walletRepository.getBalance()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    private val _downloadingIds = MutableStateFlow<Set<String>>(emptySet())
    val downloadingIds: StateFlow<Set<String>> = _downloadingIds.asStateFlow()

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
            repository.searchGuides(query)
        }
    }

    // ⚡ دالة الشراء والتنزيل أصبحت تمرر العملية للمدير المركزي
    fun downloadGuide(guide: Course) {
        if (_downloadingIds.value.contains(guide.id)) return

        viewModelScope.launch {
            _downloadingIds.update { it + guide.id }
            try {
                purchaseManager.processPurchase(guide)
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
                .onSuccess {
                    println("✅ Guide rated successfully")
                }
                .onFailure { error ->
                    println("❌ Error rating guide: ${error.message}")
                }
        }
    }
}