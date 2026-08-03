package com.learn.catalog2.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue

import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale

actual object LocalAppLocale {
    private var default: Locale? = null
    private val LocalAppLocaleState = staticCompositionLocalOf { Locale.getDefault().toString() }

    actual val current: String
        @Composable get() = LocalAppLocaleState.current

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        if (default == null) default = LocalLocale.current.platformLocale
        val new = if (value == null) default!! else Locale(value)
        Locale.setDefault(new)
        return LocalAppLocaleState.provides(new.toString())
    }
}