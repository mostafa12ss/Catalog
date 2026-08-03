package com.learn.catalog2
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.learn.catalog2.di.initKoin

fun main() {
    initKoin()
    application {
        Window(onCloseRequest = ::exitApplication, title = "Catalog2") {
            App()
        }
    }
}