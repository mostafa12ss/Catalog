package com.learn.catalog2

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport // أو CanvasBasedWindow حسب الإصدار
import com.learn.catalog2.di.initKoin
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // 💡 أهم خطوة: تشغيل Koin الأول هنا!
    initKoin()

    ComposeViewport(viewportContainer = document.body!!) {
        App()
    }
}