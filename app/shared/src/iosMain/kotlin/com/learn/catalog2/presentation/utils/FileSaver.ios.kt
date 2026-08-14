package com.learn.catalog2.presentation.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.pin
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.writeToURL

@OptIn(ExperimentalForeignApi::class)
actual fun saveDownloadedFile(fileName: String, bytes: ByteArray): String {
    val cleanName = fileName.split("/").last()
    val fileManager = NSFileManager.defaultManager
    val urls = fileManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
    val documentsUrl = urls.first() as NSURL
    val fileUrl = documentsUrl.URLByAppendingPathComponent(cleanName)!!

    val nsData = if (bytes.isNotEmpty()) {
        bytes.toNSData()
    } else {
        NSData()
    }

    nsData.writeToURL(fileUrl, atomically = true)

    return fileUrl.path ?: cleanName
}

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData = kotlinx.cinterop.memScoped {
    val pinned = this@toNSData.pin()
    val nsData = NSData.create(bytes = pinned.addressOf(0), length = this@toNSData.size.toULong())
    pinned.unpin()
    nsData
}