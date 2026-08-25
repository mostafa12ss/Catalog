package com.learn.catalog2.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import catalog2.app.shared.generated.resources.Res
import catalog2.app.shared.generated.resources.*
import com.learn.catalog2.domain.models.AppUser
import com.learn.catalog2.domain.models.DataModels.Course
import com.learn.catalog2.presentation.Navigation.LocalBottomPadding
import com.learn.catalog2.presentation.components.StatsSection
import com.learn.catalog2.presentation.viewmodels.ExploreViewModel
import com.learn.catalog2.presentation.viewmodels.ProfileViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SeniorHomeScreen(
    onAddCatalogClick: () -> Unit,
    profileViewModel: ProfileViewModel = koinViewModel(),
    exploreViewModel: ExploreViewModel = koinViewModel()
) {
    // Fetch real user data, statistics, and catalogs / جلب بيانات المستخدم والإحصائيات والكتالوجات الحقيقية
    val userStats by profileViewModel.userStats.collectAsState()
    val userNullable by profileViewModel.user.collectAsState()
    val user = userNullable ?: AppUser.getDemoUser()

    val allCourses by exploreViewModel.allCourses.collectAsState()

    // 🟢 جلب قيمة الـ Padding السفلي المخصصة للبار الطافي
    val bottomPadding = LocalBottomPadding.current

    // Filter catalogs published by the current user only / فلترة الكتالوجات المنشورة بواسطة المستخدم الحالي فقط
    val myGuides = remember(allCourses, user) {
        allCourses.filter { course ->
            course.author.equals(user.fullName, ignoreCase = true) ||
                    course.author.equals(user.id, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(4.dp))

        // Display Senior mode statistics / عرض إحصائيات وضع الـ Senior
        StatsSection(
            user = user,
            isSeniorMode = true,
            stats = userStats
        )

        // Publish new catalog card / كارت نشر كتالوج جديد
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(Res.string.publish_new), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    stringResource(Res.string.publish_sub),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(12.dp))
            Button(
                onClick = onAddCatalogClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("+ " + stringResource(Res.string.add_catalog), fontSize = 13.sp)
            }
        }

        Text(
            stringResource(Res.string.published),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Empty state or published guides list / حالة عدم وجود عناصر أو عرض الكتالوجات المرفوعة
        if (myGuides.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 30.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(Res.string.no_published_catalogs),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                myGuides.forEach { guide ->
                    PublishedGuideCard(guide)
                }
            }
        }

        // 🟢 مسافة سفلية ديناميكية تمنع انغماس آخر الكروت تحت الشريط السفلي الطافي
        Spacer(Modifier.height(bottomPadding + 24.dp))
    }
}

@Composable
private fun PublishedGuideCard(guide: Course) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(guide.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .background(Color(0xFF3FAE5A).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(stringResource(Res.string.live_label), fontSize = 11.sp, color = Color(0xFF3FAE5A))
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val revenue = (guide.points * guide.downloads).toString()
            StatBox(revenue, stringResource(Res.string.revenue), Color(0xFF3FAE5A), Modifier.weight(1f))
            StatBox(guide.downloads.toString(), stringResource(Res.string.downloads), MaterialTheme.colorScheme.onSurfaceVariant, Modifier.weight(1f))
            StatBox("${guide.rating}", stringResource(Res.string.rating), Color(0xFFEDA13B), Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatBox(value: String, label: String, valueColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = valueColor)
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}