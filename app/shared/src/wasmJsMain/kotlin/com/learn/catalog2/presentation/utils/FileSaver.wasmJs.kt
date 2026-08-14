package com.learn.catalog2.presentation.utils

// jsMain أو wasmJsMain
import kotlinx.browser.document
import org.w3c.dom.HTMLAnchorElement

// 💡 استخدام JS Interop المخصص لـ Wasm لإنشاء Blob Download بدون أخطاء Types
private external fun triggerWasmDownload(bytes: ByteArray, fileName: String)

@OptIn(ExperimentalUnsignedTypes::class)
actual fun saveDownloadedFile(fileName: String, bytes: ByteArray): String {
    val cleanName = fileName.split("/").last()

    try {
        saveFileInWasm(bytes, cleanName)
    } catch (e: Exception) {
        println("❌ Wasm Download Error: ${e.message}")
    }

    return "Downloaded via Browser: $cleanName"
}

// دالة مساعدة لعمل Trigger للتحميل مباشرة عبر JS Interop المتوافق مع Wasm
private fun saveFileInWasm(bytes: ByteArray, fileName: String) {
    val jsArray = bytes.toJsArray()
    val blob = createBlob(jsArray, "application/octet-stream")
    val url = createObjectUrl(blob)

    val anchor = document.createElement("a") as HTMLAnchorElement
    anchor.href = url
    anchor.download = fileName
    anchor.click()

    revokeObjectUrl(url)
}

// 💡 JS External Functions متوافقة مع Wasm Compiler
private fun createBlob(bytes: JsAny, type: String): JsAny = js("new Blob([bytes], { type: type })")
private fun createObjectUrl(blob: JsAny): String = js("URL.createObjectURL(blob)")
private fun revokeObjectUrl(url: String): Unit = js("URL.revokeObjectURL(url)")

private fun ByteArray.toJsArray(): JsAny {
    val uByteArray = this.toUByteArray()
    return js("new Uint8Array(uByteArray)")
}