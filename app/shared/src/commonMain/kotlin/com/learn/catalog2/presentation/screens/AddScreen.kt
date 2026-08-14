package com.learn.catalog2.presentation.screens

import com.learn.catalog2.presentation.components.AttachMediaStep
import com.learn.catalog2.presentation.components.BasicInfoStep
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.learn.catalog2.presentation.viewmodels.AddGuideViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.jetbrains.compose.resources.painterResource
import catalog2.app.shared.generated.resources.Res
import catalog2.app.shared.generated.resources.close

@Composable
fun AddNewCatalogScreen(
    onDismiss: () -> Unit,
    viewModel: AddGuideViewModel = koinViewModel()
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val guideTitle by viewModel.guideTitle.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val price by viewModel.price.collectAsState()
    val selectedFileTypes by viewModel.selectedFileTypes.collectAsState()
    val categories by viewModel.categories.collectAsState()

    // 💡 1. إظهار حالة الرفع لمنع الضغط المزدوج وإظهار المؤشر
    val isPublishing by viewModel.isPublishing.collectAsState()

    Scaffold(
        topBar = {
            AddCatalogTopBar(
                currentStep = currentStep,
                onClose = onDismiss
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            when (currentStep) {
                1 -> BasicInfoStep(
                    title = guideTitle,
                    onTitleChange = viewModel::updateTitle,
                    selectedCategory = selectedCategory,
                    onCategorySelect = viewModel::selectCategory,
                    price = price,
                    onPriceChange = viewModel::updatePrice,
                    onNext = viewModel::nextStep,
                    categories = categories
                )

                2 -> AttachMediaStep(
                    selectedFileTypes = selectedFileTypes,
                    onFileTypeToggle = viewModel::toggleFileType,
                    onBack = viewModel::previousStep,
                    // 💡 2. تمرير دالة إرفاق الملفات لـ AttachMediaStep لتغذية _selectedFiles
                    onFileSelected = viewModel::attachPlatformFile,
                    isPublishing = isPublishing,
                    onPublish = {
                        println("LOG: Publish Button Clicked!")
                        viewModel.publishGuide(onDismiss)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCatalogTopBar(
    currentStep: Int,
    onClose: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = if (currentStep == 1) "Basic Info" else "Attach Media",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(
                    painter = painterResource(Res.drawable.close),
                    contentDescription = "Close"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}