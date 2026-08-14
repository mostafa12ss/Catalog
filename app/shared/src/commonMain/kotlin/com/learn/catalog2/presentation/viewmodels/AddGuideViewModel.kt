package com.learn.catalog2.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.catalog2.data.repository.GuideRepository
import com.learn.catalog2.domain.models.DataModels.Category
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AddGuideViewModel(
    private val repository: GuideRepository
) : ViewModel() {

    // قائمة افتراضية نرجع لها لو قاعدة البيانات لسة فاضية
    private val defaultCategories = listOf(
        Category("1", "Hydraulics"),
        Category("2", "Electrical"),
        Category("3", "Pneumatics"),
        Category("4", "Mechanical")
    )

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

    // حفظ الملف المحدد مع اسمه
    private val _selectedFiles = MutableStateFlow<List<Pair<String, ByteArray>>>(emptyList())

    fun updateTitle(title: String) { _guideTitle.value = title }
    fun updatePrice(price: String) { _price.value = price }

    fun selectCategory(categoryId: String) {
        _selectedCategory.value = categoryId
    }

    fun toggleFileType(type: String) {
        _selectedFileTypes.update { set ->
            if (set.contains(type)) set - type else set + type
        }
    }

    // 💡 دالة جديدة لاستقبال PlatformFile تحويلها إلى ByteArray ورسمها للملفات
    fun attachPlatformFile(file: PlatformFile) {
        viewModelScope.launch {
            try {
                val bytes = file.readBytes()
                _selectedFiles.update { current ->
                    current + (file.name to bytes)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun nextStep() {
        if (_currentStep.value < 2) _currentStep.value += 1
    }

    fun previousStep() {
        if (_currentStep.value > 1) _currentStep.value -= 1
    }

    @OptIn(ExperimentalTime::class)
    fun publishGuide(onSuccess: () -> Unit) {
        if (_isPublishing.value) return

        viewModelScope.launch {
            _isPublishing.value = true
            try {
                println("LOG: Selected Files count = ${_selectedFiles.value.size}")

                val uploadedUrls = _selectedFiles.value.map { (name, data) ->
                    val uniqueName = "${Clock.System.now().toEpochMilliseconds()}_$name"
                    val uploadResult = repository.uploadFile("catalogs", "uploads/$uniqueName", data)
                    // Extract String from Result<String> to pass to createCatalog
                    val url = uploadResult.getOrThrow()
                    println("LOG: Uploaded URL = $url")
                    url
                }

                val result = repository.createCatalog(
                    title = _guideTitle.value,
                    subtitle = "Published by Senior",
                    categoryId = _selectedCategory.value ?: categories.value.firstOrNull()?.id ?: "1",
                    points = _price.value.toIntOrNull() ?: 0,
                    fileUrls = uploadedUrls
                )

                result.onSuccess {
                    println("LOG: Catalog Created Successfully!")
                    onSuccess()
                }.onFailure { exception ->
                    // 💥 هنا هيظهر لك السبب الحقيقي لعدم الرفع
                    println("LOG ERROR (Database): ${exception.message}")
                    exception.printStackTrace()
                }

            } catch (e: Exception) {
                // 💥 هنا هيظهر لك السبب لو مشكلة في الـ Storage/Upload
                println("LOG ERROR (Storage/Upload): ${e.message}")
                e.printStackTrace()
            } finally {
                _isPublishing.value = false
            }
        }
    }
}
