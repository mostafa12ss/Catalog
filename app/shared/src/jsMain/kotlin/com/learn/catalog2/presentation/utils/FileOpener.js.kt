package com.learn.catalog2.presentation.utils

// wasmJsMain / jsMain
import kotlinx.browser.document
import org.w3c.dom.HTMLAnchorElement

actual class FileOpener actual constructor() {
    actual fun openFile(filePath: String) {
        // في الويب يتم استخدام رابط الملف المحلي أو الـ Blob URL
        val anchor = document.createElement("a") as HTMLAnchorElement
        anchor.href = filePath
        anchor.target = "_blank" // فتحه في تبويب جديد
        anchor.click()
    }
}

