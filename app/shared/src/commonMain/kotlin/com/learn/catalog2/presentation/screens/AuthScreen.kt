package com.learn.catalog2.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import catalog2.app.shared.generated.resources.Res
import catalog2.app.shared.generated.resources.*
import com.learn.catalog2.presentation.viewmodels.AuthUiState
import com.learn.catalog2.presentation.viewmodels.AuthViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit,
    viewModel: AuthViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) onAuthenticated()
    }

    AuthScreenContent(
        uiState = uiState,
        onSignIn = viewModel::signIn,
        onSignUp = viewModel::signUp,
        onSignInWithGoogle = viewModel::signInWithGoogle,
        onSignInWithGithub = viewModel::signInWithGithub
    )
}

@Composable
fun AuthScreenContent(
    uiState: AuthUiState,
    onSignIn: (String, String) -> Unit,
    onSignUp: (String, String, String, String) -> Unit,
    onSignInWithGoogle: () -> Unit,
    onSignInWithGithub: () -> Unit
) {
    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Junior / Student") }
    val isLoading = uiState == AuthUiState.Loading

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val horizontalPadding = if (maxWidth > 600.dp) 48.dp else 24.dp

        Column(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding, vertical = 24.dp)
        ) {

            // الشعار + اسم التطبيق
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.logo),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(Res.string.app_name),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                stringResource(if (isLoginMode) Res.string.welcome_back else Res.string.create_account),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(20.dp))

            // Tab selector: Log In / Sign Up
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50))
                    .padding(4.dp)
            ) {
                AuthTab(
                    text = stringResource(Res.string.login),
                    selected = isLoginMode,
                    modifier = Modifier.weight(1f),
                    onClick = { isLoginMode = true }
                )
                AuthTab(
                    text = stringResource(Res.string.signup),
                    selected = !isLoginMode,
                    modifier = Modifier.weight(1f),
                    onClick = { isLoginMode = false }
                )
            }

            Spacer(Modifier.height(24.dp))

            // حقل الاسم بالكامل - يظهر بس في وضع Sign Up
            if (!isLoginMode) {
                Text(
                    stringResource(Res.string.full_name),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = { Text(stringResource(Res.string.full_name_ph)) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
            }

            Text(
                stringResource(Res.string.email),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text(stringResource(Res.string.email_ph)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(Res.string.password),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isLoginMode) {
                    Text(
                        stringResource(Res.string.forgot_pw),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { /* لاحقًا: شاشة استرجاع كلمة المرور */ }
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text(stringResource(Res.string.password_ph)) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = if (passwordVisible) painterResource(Res.drawable.visibility) else painterResource(Res.drawable.baseline_visibility_off_24),
                            contentDescription = null
                        )
                    }
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // اختيار الدور - يظهر بس في وضع Sign Up
            if (!isLoginMode) {
                Spacer(Modifier.height(16.dp))
                Text(
                    stringResource(Res.string.your_role),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    RoleCard(
                        label = stringResource(Res.string.junior),
                        selected = selectedRole == "Junior / Student",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedRole = "Junior / Student" }
                    )
                    RoleCard(
                        label = stringResource(Res.string.senior),
                        selected = selectedRole == "Senior Expert",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedRole = "Senior Expert" }
                    )
                }
            }

            // 💡 عرض الخطأ بشكل مبسط وأنيق
            if (uiState is AuthUiState.Error) {
                Spacer(Modifier.height(14.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (isLoginMode) onSignIn(email, password)
                    else onSignUp(email, password, fullName, selectedRole)
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(
                        stringResource(if (isLoginMode) Res.string.login else Res.string.create_account),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    stringResource(if (isLoginMode) Res.string.no_account else Res.string.have_account),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
                Text(
                    stringResource(if (isLoginMode) Res.string.signup else Res.string.login),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { isLoginMode = !isLoginMode }
                )
            }


// زراير Google وGitHub - تظهر بس في وضع تسجيل الدخول
            if (isLoginMode) {
                Spacer(Modifier.height(28.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(
                        stringResource(Res.string.or_continue_with),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { onSignInWithGoogle() },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.free_google_logo_icon_svg_download_png_2476479),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = androidx.compose.ui.graphics.Color.Unspecified
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(Res.string.continue_google))
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { onSignInWithGithub() },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.github),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = androidx.compose.ui.graphics.Color.Unspecified
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(Res.string.continue_github))
                }
            }

        }
    }
}

@Composable
private fun AuthTab(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                if (selected) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent,
                RoundedCornerShape(50)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

// كارت اختيار الدور - نص بس من غير أيقونة مخصصة دلوقتي
@Composable
private fun RoleCard(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

