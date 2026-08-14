package com.learn.catalog2


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.learn.catalog2.domain.UseCases.PurchaseGuideUseCase.PurchaseResult
import com.learn.catalog2.presentation.utils.PurchaseManager
import org.koin.compose.koinInject

@Composable
fun GlobalPurchaseHandler(
    onNavigateToWallet: () -> Unit,
    purchaseManager: PurchaseManager = koinInject(),
    content: @Composable () -> Unit
) {
    var showNoBalanceDialog by remember { mutableStateOf(false) }
    var requiredPoints by remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        purchaseManager.purchaseResult.collect { result ->
            when (result) {
                is PurchaseResult.InsufficientPoints -> {
                    requiredPoints = result.required
                    showNoBalanceDialog = true
                }
                is PurchaseResult.Success -> {
                    snackbarHostState.showSnackbar("تم الشراء والتنزيل بنجاح!")
                }
                is PurchaseResult.Error -> {
                    snackbarHostState.showSnackbar(result.message)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        content()

        // الـ Dialog المركزي لشحن الرصيد
        if (showNoBalanceDialog) {
            AlertDialog(
                onDismissRequest = { showNoBalanceDialog = false },
                title = { Text("رصيد النقاط غير كافٍ") },
                text = { Text("تحتاج إلى $requiredPoints نقطة للحصول على هذا الكتالوج. هل تريد الانتقال لشحن المحفظة الآن؟") },
                confirmButton = {
                    Button(
                        onClick = {
                            showNoBalanceDialog = false
                            onNavigateToWallet()
                        }
                    ) {
                        Text("شحن المحفظة")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNoBalanceDialog = false }) {
                        Text("إلغاء")
                    }
                }
            )
        }
    }
}