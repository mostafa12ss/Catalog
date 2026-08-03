package com.learn.catalog2.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import catalog2.app.shared.generated.resources.Res
import catalog2.app.shared.generated.resources.Cairo
import catalog2.app.shared.generated.resources.Inter
import org.jetbrains.compose.resources.Font

// دالة ذكية لتوليد الـ Typography الخاص بالتطبيق بحسب اللغة الحالية
@Composable
fun getCatalogTypography(isArabic: Boolean): Typography {
    val selectedFont = if (isArabic) {
        FontFamily(Font(Res.font.Cairo, FontWeight.Normal))
    } else {
        FontFamily(Font(Res.font.Inter, FontWeight.Normal))
    }

    return Typography(
        headlineLarge = TextStyle(
            fontFamily = selectedFont,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp
        ),
        titleLarge = TextStyle(
            fontFamily = selectedFont,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = selectedFont,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = selectedFont,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        ),
        labelLarge = TextStyle(
            fontFamily = selectedFont,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    )
}