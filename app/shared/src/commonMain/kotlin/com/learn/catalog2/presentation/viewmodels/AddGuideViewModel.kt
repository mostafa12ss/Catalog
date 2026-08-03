package com.learn.catalog2.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.catalog2.data.repository.GuideRepository
import com.learn.catalog2.domain.models.DataModels.Category
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddGuideViewModel(
    private val repository: GuideRepository
) : ViewModel() {

    // قائمة افتراضية نرجع لها لو قاعدة البيانات لسة فاضية
    private val defaultCategories = listOf(
        Category(
            "1", "Hydraulics",

        ),
        Category(
            "2", "Electrical",
        ),
        Category(
            "3", "Pneumatics",

        ),
        Category(
            "4", "Mechanical",

        )
    )

    // دمج بيانات الـ Repository مع الـ Fallback لضمان عدم ظهور القائمة فاضية أبداً
    val categories: StateFlow<List<Category>> = repository.getCategories()
        .map { list -> if (list.isEmpty()) defaultCategories else list }
        .catch { emit(defaultCategories) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = defaultCategories
        )

    private val _currentStep = MutableStateFlow(1)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    private val _guideTitle = MutableStateFlow("")
    val guideTitle: StateFlow<String> = _guideTitle.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _price = MutableStateFlow("")
    val price: StateFlow<String> = _price.asStateFlow()

    private val _isPublishing = MutableStateFlow(false)
    val isPublishing: StateFlow<Boolean> = _isPublishing.asStateFlow()

    private val _selectedFileTypes = MutableStateFlow<Set<String>>(emptySet())
    val selectedFileTypes: StateFlow<Set<String>> = _selectedFileTypes.asStateFlow()

    private val _selectedFiles = MutableStateFlow<List<Pair<String, ByteArray>>>(emptyList())

    fun updateTitle(title: String) { _guideTitle.value = title }
    fun updatePrice(price: String) { _price.value = price }

    // استقبال الـ categoryId مباشرة
    fun selectCategory(categoryId: String) {
        _selectedCategory.value = categoryId
    }

    fun toggleFileType(type: String) {
        _selectedFileTypes.update { set ->
            if (set.contains(type)) set - type else set + type
        }
    }

    fun addFile(name: String, data: ByteArray) {
        _selectedFiles.update { it + (name to data) }
    }

    fun nextStep() {
        if (_currentStep.value < 2) _currentStep.value += 1
    }

    fun previousStep() {
        if (_currentStep.value > 1) _currentStep.value -= 1
    }

    fun publishGuide(onSuccess: () -> Unit) {
        if (_isPublishing.value) return

        viewModelScope.launch {
            _isPublishing.value = true
            try {
                val uploadedUrls = _selectedFiles.value.map { (name, data) ->
                    repository.uploadFile("catalogs", "uploads/$name", data)
                }

                val result = repository.createCatalog(
                    title = _guideTitle.value,
                    subtitle = "Published by Senior",
                    categoryId = _selectedCategory.value ?: categories.value.firstOrNull()?.id ?: "1",
                    points = _price.value.toIntOrNull() ?: 0,
                    fileUrls = uploadedUrls
                )

                if (result.isSuccess) {
                    onSuccess()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isPublishing.value = false
            }
        }
    }
}