package com.learn.catalog2.data.util

import kotlin.time.Clock
import kotlin.time.ExperimentalTime


object StorageUtils {

    /**
     * Sanitizes a filename to be safe for Supabase Storage object keys.
     */
    @OptIn(ExperimentalTime::class)
    fun sanitizeFileName(originalName: String): String {
        val dotIndex = originalName.lastIndexOf('.')
        val extension = if (dotIndex != -1) originalName.substring(dotIndex).lowercase() else ""
        val baseName = if (dotIndex != -1) originalName.substring(0, dotIndex) else originalName

        // Clean non-ASCII/special characters
        var sanitizedBase = baseName.replace(Regex("[^a-zA-Z0-9\\-_]"), "_")
        sanitizedBase = sanitizedBase.replace(Regex("_{2,}"), "_").trim('_')

        // Fetch current epoch millis safely
        val timestamp = Clock.System.now().toEpochMilliseconds()

        val finalBase = if (sanitizedBase.isEmpty()) "file" else sanitizedBase

        return "${timestamp}_$finalBase$extension"
    }
}