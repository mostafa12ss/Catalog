package com.learn.catalog2.presentation.screens
import androidx.compose.foundation.BorderStroke
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
import catalog2.app.shared.generated.resources.add
import catalog2.app.shared.generated.resources.approx_usd
import catalog2.app.shared.generated.resources.arrowdownward
import catalog2.app.shared.generated.resources.arrowupward
import catalog2.app.shared.generated.resources.callmade
import catalog2.app.shared.generated.resources.claim_free_points
import catalog2.app.shared.generated.resources.error_claim_limit
import catalog2.app.shared.generated.resources.free_offer_desc
import catalog2.app.shared.generated.resources.free_offer_title
import catalog2.app.shared.generated.resources.outline_account_balance_wallet_24
import catalog2.app.shared.generated.resources.recent_tx
import catalog2.app.shared.generated.resources.success_claim_points
import catalog2.app.shared.generated.resources.top_up_coming_soon
import catalog2.app.shared.generated.resources.top_up_points
import catalog2.app.shared.generated.resources.wallet_balance
import catalog2.app.shared.generated.resources.withdraw
import catalog2.app.shared.generated.resources.withdraw_not_available
import com.learn.catalog2.domain.models.WalletTransaction
import com.learn.catalog2.presentation.utils.toTwoDecimalString
import com.learn.catalog2.presentation.utils.withThousandsSeparator
import com.learn.catalog2.presentation.viewmodels.WalletViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
@Composable
fun WalletScreen(
    viewModel: WalletViewModel = koinViewModel(),
    pointToCashRate: Double = 0.01
) {
    val pointsBalance by viewModel.pointsBalance.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }

    // localized string values for snackbars
    val successMsg = stringResource(Res.string.success_claim_points)
    val errorDefaultMsg = stringResource(Res.string.error_claim_limit)
    val topUpComingSoonMsg = stringResource(Res.string.top_up_coming_soon)
    val withdrawNotAvailableMsg = stringResource(Res.string.withdraw_not_available)

    // Calculate remaining free claims based on transaction history title matching
    val freeClaimCount = remember(transactions) {
        transactions.count { it.title.contains("مكافأة مجانية") || it.title.contains("Free Reward") }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            // 1. Balance Card / كارت الرصيد
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
                            text = stringResource(Res.string.wallet_balance),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = pointsBalance.withThousandsSeparator(),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEDA13B)
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = stringResource(Res.string.approx_usd, (pointsBalance * pointToCashRate).toTwoDecimalString()),
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

            // 2. Free Offer Card / كارت العرض المجاني
            if (freeClaimCount < 2) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, Color(0xFFEDA13B).copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(Res.string.free_offer_title),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEDA13B)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(Res.string.free_offer_desc, (2 - freeClaimCount)),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (!isLoading) {
                                    isLoading = true
                                    viewModel.claimFreeRewardPoints { result ->
                                        isLoading = false
                                        scope.launch {
                                            result.onSuccess {
                                                snackbarHostState.showSnackbar(successMsg)
                                            }.onFailure { err ->
                                                snackbarHostState.showSnackbar(err.message ?: errorDefaultMsg)
                                            }
                                        }
                                    }
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEDA13B))
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    painter = painterResource(Res.drawable.add),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.Black
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = stringResource(Res.string.claim_free_points),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            } else {
                // Standard Top Up Button / زر الشحن القياسي
                Button(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar(topUpComingSoonMsg)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEDA13B))
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.add),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.Black
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(Res.string.top_up_points),
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // 3. Withdraw Button / زر سحب الأرباح
            OutlinedButton(
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar(withdrawNotAvailableMsg)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.callmade),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(Res.string.withdraw),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = stringResource(Res.string.recent_tx),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            // 4. Transactions List / قائمة المعاملات
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