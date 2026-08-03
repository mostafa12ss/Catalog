package com.learn.catalog2.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.preferredLanguages

actual object LocalAppLocale {
    private val LocalAppLocaleState = staticCompositionLocalOf {
        (NSLocale.preferredLanguages.firstOrNull() as? String) ?: "en"
    }

    actual val current: String
        @Composable get() = LocalAppLocaleState.current

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val new = value ?: ((NSLocale.preferredLanguages.firstOrNull() as? String) ?: "en")
        if (value != null) {
            NSUserDefaults.standardUserDefaults.setObject(listOf(value), forKey = "AppleLanguages")
        }
        return LocalAppLocaleState.provides(new)
    }
}