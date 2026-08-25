package com.learn.catalog2

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.window.ComposeViewport
import com.learn.catalog2.di.initKoin
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // 💡 تشغيل Koin
    initKoin()

    // ⚡ ربط الـ Canvas بالمُستند مع تهيئة محرك الخطوط للـ Web
    ComposeViewport(viewportContainer = document.body!!) {
        val fontFamilyResolver = createFontFamilyResolver()

        CompositionLocalProvider(
            LocalFontFamilyResolver provides fontFamilyResolver
        ) {
            App()
        }
    }
}