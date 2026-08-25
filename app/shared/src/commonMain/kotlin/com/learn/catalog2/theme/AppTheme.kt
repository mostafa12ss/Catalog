package com.learn.catalog2.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection


private val DarkColorScheme = darkColorScheme(
    primary = TealTextDark, // 🟢 جعل اللون الأساسي أفتح قليلاً للأيقونات المحددة
    onPrimary = BgDark,
    primaryContainer = TealDimDark,
    onPrimaryContainer = TealTextDark,

    secondary = AmberTextDark,
    onSecondary = BgDark,
    secondaryContainer = AmberDimDark,
    onSecondaryContainer = AmberTextDark,

    tertiary = EmeraldTextDark,
    onTertiary = BgDark,
    tertiaryContainer = EmeraldDimDark,
    onTertiaryContainer = EmeraldTextDark,

    background = BgDark, // 0xFF0F172A (الأسود الداكن المائل للكحلي Slate)
    onBackground = Text1Dark,

    surface = SurfaceDark, // 0xFF1E293B (الكروت)
    onSurface = Text1Dark,
    surfaceVariant = SurfaceRaiseDark, // 0xFF253347 (الـ Floating Bottom Bar)
    onSurfaceVariant = Text2Dark,

    error = RedDark,
    onError = WhiteDark,
    errorContainer = RedDimDark,
    onErrorContainer = RedTextDark,

    outline = BorderStrongDark,
    outlineVariant = BorderDark,
    scrim = OverlayBgDark
)

private val LightColorScheme = lightColorScheme(
    primary = TealLight,
    onPrimary = WhiteLight,
    primaryContainer = TealDimLight,
    onPrimaryContainer = TealTextLight,

    secondary = AmberLight,
    onSecondary = WhiteLight,
    secondaryContainer = AmberDimLight,
    onSecondaryContainer = AmberTextLight,

    tertiary = EmeraldLight,
    onTertiary = WhiteLight,
    tertiaryContainer = EmeraldDimLight,
    onTertiaryContainer = EmeraldTextLight,

    background = BgLight,
    onBackground = Text1Light,

    surface = SurfaceLight,
    onSurface = Text1Light,
    surfaceVariant = SurfaceRaiseLight,
    onSurfaceVariant = Text2Light,

    error = RedLight,
    onError = WhiteLight,
    errorContainer = RedDimLight,
    onErrorContainer = RedTextLight,

    outline = BorderStrongLight,
    outlineVariant = BorderLight,
    scrim = OverlayBgLight
)

@Composable
fun CatalogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isArabic: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    // ⚡ ضبط اتجاه الواجهة والـ Typography بناءً على اللغة
    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = getCatalogTypography(isArabic),
            content = content
        )
    }
}