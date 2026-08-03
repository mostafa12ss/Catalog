package com.learn.catalog2.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import catalog2.app.shared.generated.resources.Res
import catalog2.app.shared.generated.resources.app_name
import catalog2.app.shared.generated.resources.app_sub
import catalog2.app.shared.generated.resources.get_started
import catalog2.app.shared.generated.resources.logo
import catalog2.app.shared.generated.resources.sub_Button
import com.learn.catalog2.theme.CatalogTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val baseModifier = Modifier.fillMaxSize()

        // لو الشاشة تابلت أو شاشة عريضة نحدد أقصى عرض للـ Column عشان الشكل ميبقاش ممطوط
        val contentModifier = if (maxWidth > 600.dp) {
            baseModifier.widthIn(max = 460.dp).align(Alignment.Center)
        } else {
            baseModifier
        }

        Column(
            modifier = contentModifier
                .verticalScroll(rememberScrollState()) // يضمن عدم بوظان التصميم على الشاشات الصغيرة جداً
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // صف زرار تغيير اللغة (هيتغير مكانه يمين/شمال تلقائياً مع لغة الجهاز)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(), // حماية من الحواف العلوية (Notch)
                horizontalArrangement = Arrangement.End
            ) {
                LanguageToggleButton()
            }

            Spacer(Modifier.weight(1.2f))

            // أيقونة اللوجو (حواف دائرية ناعمة زي الصورة تماماً)
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(Color.Transparent)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(Modifier.height(32.dp))

            // اسم التطبيق
            Text(
                text = stringResource(Res.string.app_name),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

//            Spacer(Modifier.height(10.dp))

            // النص الفرعي
            Text(
                text = stringResource(Res.string.app_sub),
                fontSize = 23.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Spacer(Modifier.height(32.dp))

            // مؤشر النقاط (مطابق للصورة: خط خفيف، نقطة في النص، خط خفيف)
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // الخط الأيسر
                Box(
                    modifier = Modifier
                        .height(1.5.dp)
                        .width(32.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                )
                // النقطة النشطة في المنتصف
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
                // الخط الأيمن
                Box(
                    modifier = Modifier
                        .height(1.5.dp)
                        .width(32.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                )
            }

            Spacer(Modifier.weight(1.5f))

            // زرار ابدأ الآن / Get Started
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = stringResource(Res.string.get_started),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            // النص السفلي خالص
            Text(
                text = stringResource(Res.string.sub_Button),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .navigationBarsPadding() // حماية من شريط التنقل السفلي للأجهزة
                    .padding(bottom = 8.dp)
            )
        }
    }
}

