package com.learn.catalog2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.learn.catalog2.theme.customAppLocale

/**
 * Global Settings Engine - ensures the entire app reacts to changes.
 */
object AppSettings {
    // Theme state using standard delegation
    var isDarkMode by mutableStateOf(false)

    // Internal state for language to allow custom setter logic for theme engine sync
    private var _language by mutableStateOf("en")

    var language: String
        get() = _language
        set(value) {
            _language = value
            customAppLocale = value // Trigger the Compose key refresh in AppEnvironment
        }
}
