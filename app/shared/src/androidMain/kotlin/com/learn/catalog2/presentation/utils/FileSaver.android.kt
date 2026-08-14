package com.learn.catalog2.presentation.utils

import java.io.File
import android.os.Environment

actual fun saveDownloadedFile(fileName: String, bytes: ByteArray): String {
    val cleanName = fileName.split("/").last()
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    if (!downloadsDir.exists()) downloadsDir.mkdirs()

    val file = File(downloadsDir, cleanName)
    file.writeBytes(bytes)
    return file.absolutePath
}