package com.learn.catalog2.presentation.utils

import java.io.File

actual fun saveDownloadedFile(fileName: String, bytes: ByteArray): String {
    val cleanName = fileName.split("/").last()
    val userHome = System.getProperty("user.home") ?: "."
    val downloadsDir = File(userHome, "Downloads")
    if (!downloadsDir.exists()) downloadsDir.mkdirs()

    val file = File(downloadsDir, cleanName)
    file.writeBytes(bytes)
    return file.absolutePath
}