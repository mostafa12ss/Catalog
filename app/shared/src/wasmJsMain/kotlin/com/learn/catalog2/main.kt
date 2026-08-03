package com.learn.catalog2

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.learn.catalog2.di.initKoin
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // 💡 تشغيل Koin
    initKoin()

    // الربط بـ HTML Body
    ComposeViewport(viewportContainer = document.body!!) {
        App()
    }
}