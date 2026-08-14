package com.learn.catalog2.presentation.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

actual class FileOpener actual constructor() : KoinComponent {

    private val context: Context by inject()

    actual fun openFile(filePath: String) {
        try {
            val file = File(filePath)
            if (!file.exists()) return

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, context.contentResolver.getType(uri) ?: "*/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}