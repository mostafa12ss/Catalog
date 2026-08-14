package com.learn.catalog2.presentation.utils

import java.awt.Desktop
import java.io.File
import java.net.URI

actual class FileOpener actual constructor() {
    actual fun openFile(filePath: String) {
        val trimmedPath = filePath.trim()
        println("🚀 Attempting to open path: '$trimmedPath'")

        if (trimmedPath.isEmpty()) {
            println("❌ Path is empty!")
            return
        }

        try {
            if (Desktop.isDesktopSupported()) {
                val desktop = Desktop.getDesktop()

                // 1. إذا كان المسار عبارة عن رابط أونلاين (http/https)
                if (trimmedPath.startsWith("http://", ignoreCase = true) ||
                    trimmedPath.startsWith("https://", ignoreCase = true)) {
                    println("🌐 Opening URL in default web browser...")
                    desktop.browse(URI(trimmedPath))
                    return
                }

                // 2. التعامل مع الملفات المحلية (Offline Files)
                val file = File(trimmedPath)

                if (file.exists()) {
                    if (desktop.isSupported(Desktop.Action.OPEN)) {
                        println("📂 Opening local file with default app...")
                        desktop.open(file)
                    } else if (desktop.isSupported(Desktop.Action.BROWSE_FILE_DIR)) {
                        println("📁 Opening parent directory...")
                        desktop.browseFileDirectory(file)
                    }
                } else {
                    println("⚠️ File does not exist on disk: ${file.absolutePath}")
                    // فتح المجلد الأب إذا كان موجوداً على الأقل
                    file.parentFile?.let { parentDir ->
                        if (parentDir.exists()) {
                            println("📁 File missing, opening parent folder instead...")
                            desktop.open(parentDir)
                        }
                    }
                }
            } else {
                println("❌ Desktop API is not supported on this platform.")
            }
        } catch (e: Exception) {
            println("❌ Error opening file: ${e.message}")
            e.printStackTrace()
        }
    }
}