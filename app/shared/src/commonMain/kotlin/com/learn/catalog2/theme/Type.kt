package com.learn.catalog2.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import catalog2.app.shared.generated.resources.Res
import catalog2.app.shared.generated.resources.Inter
import catalog2.app.shared.generated.resources.cairo
import org.jetbrains.compose.resources.Font

@Composable
fun getCatalogTypography(isArabic: Boolean): Typography {
    val selectedFont = if (isArabic) {
        FontFamily(Font(Res.font.cairo, FontWeight.Normal))
    } else {
        FontFamily(Font(Res.font.Inter, FontWeight.Normal))
    }

    val defaultTypography = Typography()

    return Typography(
        displayLarge = defaultTypography.displayLarge.copy(fontFamily = selectedFont),
        displayMedium = defaultTypography.displayMedium.copy(fontFamily = selectedFont),
        displaySmall = defaultTypography.displaySmall.copy(fontFamily = selectedFont),
        headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = selectedFont),
        headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = selectedFont),
        headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = selectedFont),
        titleLarge = defaultTypography.titleLarge.copy(fontFamily = selectedFont),
        titleMedium = defaultTypography.titleMedium.copy(fontFamily = selectedFont),
        titleSmall = defaultTypography.titleSmall.copy(fontFamily = selectedFont),
        bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = selectedFont),
        bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = selectedFont),
        bodySmall = defaultTypography.bodySmall.copy(fontFamily = selectedFont),
        labelLarge = defaultTypography.labelLarge.copy(fontFamily = selectedFont),
        labelMedium = defaultTypography.labelMedium.copy(fontFamily = selectedFont),
        labelSmall = defaultTypography.labelSmall.copy(fontFamily = selectedFont)
    )
}