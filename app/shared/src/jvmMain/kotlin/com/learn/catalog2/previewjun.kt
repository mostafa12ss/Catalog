package com.learn.catalog2

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.learn.catalog2.domain.models.DataModels.Category
import com.learn.catalog2.domain.models.DataModels.Course
import com.learn.catalog2.domain.models.UserRole
import com.learn.catalog2.presentation.Navigation.AppBottomBar
import com.learn.catalog2.presentation.Navigation.AppTopBar
import com.learn.catalog2.presentation.screens.JuniorHomeScreenContent

// 1️⃣ تم حذفت "All " من القائمة منعاً للتكرار، لأن الـ UI بضيف خيار "All" تلقائياً
private val mockCategories = listOf(
    Category(id = "1", name = "Robotics & Embedded", count = 5),
    Category(id = "2", name = "Android KMP", count = 4),
    Category(id = "3", name = "PLC & Automation", count = 3)
)

private val mockGuides = listOf(
    Course(
        id = "c1",
        title = "Complete STM32 Microcontroller Guide",
        subtitle = "Master ARM Cortex-M architecture, GPIO, Timers, and FreeRTOS from scratch.",
        author = "Eng. Ahmed Hassan",
        level = "Beginner",
        downloads = 1420,
        rating = 4.8f,
        points = 250,
        rank = 1,
        isSaved = true,
        isDownloaded = true,
        fileUrls = listOf("https://example.com/files/stm32_guide.pdf")
    ),
    Course(
        id = "c2",
        title = "Kotlin Multiplatform Architecture Patterns",
        subtitle = "Build robust production cross-platform apps using KMP, Decompose, and Room.",
        author = "Eng. Mohamed Aly",
        level = "Intermediate",
        downloads = 850,
        rating = 4.9f,
        points = 400,
        rank = 2,
        isSaved = false,
        isDownloaded = false,
        fileUrls = listOf("https://example.com/files/kmp_arch.pdf")
    ),
    Course(
        id = "c3",
        title = "Advanced Reciprocating Mechanism Design",
        subtitle = "Detailed kinematics, stress analysis, and motor sizing for cutting machines.",
        author = "Eng. Mahmoud Tarek",
        level = "Advanced",
        downloads = 310,
        rating = 4.6f,
        points = 600,
        rank = 3,
        isSaved = false,
        isDownloaded = false,
        fileUrls = emptyList()
    )
)

@Preview
@Composable
fun JuniorHomeScreenDesktopPreview() {
    MaterialTheme {
        // 2️⃣ استبدال Surface بـ Scaffold لدعم topBar و bottomBar
        Scaffold(
            topBar = {
                AppTopBar(
                    screenTitle = "Catalog",
                    userRole = UserRole.JUNIOR,
                    pointsBalance = 1250,
                    onRoleClick = {}
                )
            },
            bottomBar = {
                // 💡 ملاحظة: لو AppBottomBar محتاج NavController حقيقي،
                // يمكنك تمرير null أو Callback حسب طريقة بنائه عندك
                AppBottomBar(navController = rememberNavController())
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            // 3️⃣ استخدام innerPadding لضمان عدم اختفاء المحتوى تحت الـ AppBars
            Box(modifier = Modifier.padding(innerPadding)) {
                JuniorHomeScreenContent(
                    guides = mockGuides,
                    categories = mockCategories,
                    onSearch = {},
                    onFavoriteToggle = { _, _ -> }
                )
            }
        }
    }
}