package com.learn.catalog2.presentation.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog2.app.shared.generated.resources.Res
import catalog2.app.shared.generated.resources.lang
import com.learn.catalog2.theme.LocalAppLocale
import com.learn.catalog2.theme.customAppLocale

import org.jetbrains.compose.resources.painterResource

@Composable
fun LanguageToggleButton() {
    val currentLocale = LocalAppLocale.current  // "ar" أو "en" مثلاً
    val isArabic = currentLocale.startsWith("en")

    OutlinedButton(
        onClick = {
            // ده اللي فعلاً بيغيّر اللغة الحقيقية
            customAppLocale = if (isArabic) "en" else "ar"
        },
        shape = RoundedCornerShape(50)
    ) {
        Text(if (isArabic) "EN" else "عربي")
        Spacer(Modifier.width(6.dp))
        Icon(painter = painterResource(Res.drawable.lang), contentDescription = null, modifier = Modifier.width(16.dp))
    }
}