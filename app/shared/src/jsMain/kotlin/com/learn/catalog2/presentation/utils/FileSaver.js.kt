package com.learn.catalog2.presentation.utils

// jsMain أو wasmJsMain
import kotlinx.browser.document
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.url.URL
import org.khronos.webgl.Uint8Array
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

actual fun saveDownloadedFile(fileName: String, bytes: ByteArray): String {
    val cleanName = fileName.split("/").last()

    // تحويل الـ ByteArray إلى JS Blob
    val array = Uint8Array(bytes.toTypedArray())
    val blob = Blob(arrayOf(array), BlobPropertyBag(type = "application/octet-stream"))
    val url = URL.createObjectURL(blob)

    // إنشاء عنصر <a> وهمي وضغطه لتنزل الحزمة للمستخدم
    val anchor = document.createElement("a") as HTMLAnchorElement
    anchor.href = url
    anchor.download = cleanName
    anchor.click()

    URL.revokeObjectURL(url)
    return "Browser Downloaded: $cleanName"
}

