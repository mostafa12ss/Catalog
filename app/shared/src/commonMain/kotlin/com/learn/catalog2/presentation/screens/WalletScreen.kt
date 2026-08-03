package com.learn.catalog2.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.learn.catalog2.domain.models.WalletTransaction
import com.learn.catalog2.presentation.viewmodels.WalletViewModel
import com.learn.catalog2.presentation.utils.toTwoDecimalString
import com.learn.catalog2.presentation.utils.withThousandsSeparator
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * شاشة المحفظة - تم إعادة بنائها بواسطة Senior Architect
 * تعتمد على WalletViewModel لجلب البيانات الحقيقية ودعم العمل Offline.
 */
@Composable
fun WalletScreen(
    viewModel: WalletViewModel = koinViewModel(),
    pointToCashRate: Double = 0.01,
    onTopUpClick: () -> Unit,
    onWithdrawClick: () -> Unit
) {
    // مراقبة البيانات من الـ Flow القادم من قاعدة البيانات المحلية
    val pointsBalance by viewModel.pointsBalance.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {

        Spacer(Modifier.height(12.dp))

        // كارت الرصيد
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        stringResource(Res.string.wallet_balance),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        pointsBalance.withThousandsSeparator(),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEDA13B) // لون النقاط المميز
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        stringResource(Res.string.approx_usd, (pointsBalance * pointToCashRate).toTwoDecimalString()),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF3D2E0F), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.outline_account_balance_wallet_24),
                        contentDescription = null,
                        tint = Color(0xFFEDA13B)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // أزرار التحكم
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onTopUpClick,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(painter = painterResource(Res.drawable.add), contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(stringResource(Res.string.top_up), fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onWithdrawClick,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(painter = painterResource(Res.drawable.callmade), contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(stringResource(Res.string.withdraw), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            stringResource(Res.string.recent_tx),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(8.dp))

        // قائمة المعاملات مع استخدام key لتحسين الأداء
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(transactions, key = { it.id }) { tx ->
                TransactionRow(tx)
            }
        }
    }
}

@Composable
private fun TransactionRow(tx: WalletTransaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val iconColor = if (tx.isIncome) Color(0xFF3FAE5A) else Color(0xFFEDA13B)
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(iconColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = if (tx.isIncome) painterResource(Res.drawable.arrowupward) else painterResource(Res.drawable.arrowdownward),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(tx.title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Text(tx.date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Text(
            (if (tx.amount > 0) "+" else "") + tx.amount,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = if (tx.isIncome) Color(0xFF3FAE5A) else Color(0xFFEDA13B)
        )
    }
}
