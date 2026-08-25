package com.learn.catalog2.presentation.utils

import kotlinx.browser.document
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.set
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import kotlin.js.JsAny
import kotlin.js.JsArray

actual fun saveDownloadedFile(fileName: String, bytes: ByteArray): String {
    val cleanName = fileName.split("/").last()

    try {
        val uint8Array = Uint8Array(bytes.size)
        for (i in bytes.indices) {
            uint8Array[i] = bytes[i]
        }

        // تحويل Uint8Array إلى Blob عبر JsArray نظيف وبدون Casting
        val blob = Blob(
            blobParts = jsArrayOf(uint8Array),
            options = BlobPropertyBag(type = "application/octet-stream")
        )

        val blobUrl = URL.createObjectURL(blob)

        val anchor = document.createElement("a") as HTMLAnchorElement
        anchor.href = blobUrl
        anchor.download = cleanName
        document.body?.appendChild(anchor)
        anchor.click()

        document.body?.removeChild(anchor)
        URL.revokeObjectURL(blobUrl)

    } catch (e: Exception) {
        println("❌ Wasm Download Error: ${e.message}")
    }

    return "Downloaded via Browser: $cleanName"
}

// دالة مساعدة متوافقة مع Kotlin/Wasm لتغليف العناصر داخل JsArray
private fun jsArrayOf(element: JsAny): JsArray<JsAny?> {
    val array = JsArray<JsAny?>()
    array[0] = element
    return array
}