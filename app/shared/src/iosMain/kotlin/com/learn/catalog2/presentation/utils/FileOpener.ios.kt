// iosMain/kotlin/com/learn/catalog2/presentation/utils/FileOpener.ios.kt
package com.learn.catalog2.presentation.utils

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentInteractionController
import platform.darwin.NSObject

actual class FileOpener actual constructor() {

    @OptIn(ExperimentalForeignApi::class)
    actual fun openFile(filePath: String) {
        val fileUrl = NSURL.fileURLWithPath(filePath)
        val controller = UIDocumentInteractionController.interactionControllerWithURL(fileUrl)

        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
            ?: return

        controller.delegate = object : NSObject(), platform.UIKit.UIDocumentInteractionControllerDelegateProtocol {}

        controller.presentOpenInMenuFromRect(
            rect = rootViewController.view.bounds,
            inView = rootViewController.view,
            animated = true
        )
    }
}