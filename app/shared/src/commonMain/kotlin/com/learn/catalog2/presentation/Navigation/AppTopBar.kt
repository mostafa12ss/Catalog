package com.learn.catalog2.presentation.Navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import catalog2.app.shared.generated.resources.Res
import catalog2.app.shared.generated.resources.arrowupward // استخدم أيقونة الرجوع المتوفرة لديك أو arrow_back
import catalog2.app.shared.generated.resources.baseline_expand_more_24
import catalog2.app.shared.generated.resources.junior
import catalog2.app.shared.generated.resources.outline_account_balance_wallet_24
import catalog2.app.shared.generated.resources.outline_lightning_stand_24
import catalog2.app.shared.generated.resources.senior
import catalog2.app.shared.generated.resources.your_role
import com.learn.catalog2.domain.models.UserRole
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppTopBar(
    screenTitle: String,
    userRole: UserRole,          // "JUNIOR" أو "SENIOR"
    pointsBalance: Int,
    onRoleClick: () -> Unit,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    onWalletClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 💡 تجميع سهم الرجوع مع العناوين في Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // إظهار سهم الرجوع فقط إذا كنا في شاشة فرعية مثل "wallet" أو "add_catalog"
                if (showBackButton) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            // 📌 استبدل باسم أيكونة الرجوع الموجودة لديك في Res.drawable
                            painter = painterResource(Res.drawable.arrowupward),
                            contentDescription = "Back",
                            tint = colorScheme.onBackground
                        )
                    }
                }

                Column {
                    Text(
                        text = stringResource(Res.string.your_role),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = screenTitle,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onBackground
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // شارة الدور (Role Badge)
                Row(
                    modifier = Modifier
                        .background(colorScheme.surfaceVariant, RoundedCornerShape(50))
                        .clickable(onClick = onRoleClick)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(40),
                            ambientColor = Color.Black.copy(alpha = 0.1f),
                            spotColor = Color.Black.copy(alpha = 0.1f)
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.outline_lightning_stand_24),
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = stringResource(if (userRole == UserRole.JUNIOR) Res.string.junior else Res.string.senior),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurfaceVariant
                    )
                    Icon(
                        painter = painterResource(Res.drawable.baseline_expand_more_24),
                        contentDescription = null,
                        tint = colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // شارة رصيد النقاط (فتح المحفظة)
                Row(
                    modifier = Modifier
                        .background(colorScheme.primaryContainer.copy(alpha = 0.15f), RoundedCornerShape(50))
                        .clickable { onWalletClick() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.outline_account_balance_wallet_24),
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = pointsBalance.toString(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            }
        }
    }
}