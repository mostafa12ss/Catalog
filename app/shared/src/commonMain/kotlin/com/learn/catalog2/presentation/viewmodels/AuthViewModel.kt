package com.learn.catalog2.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.catalog2.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data object Success : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val currentUser = authRepository.currentUser

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.signIn(email, password)
                .onSuccess { _uiState.value = AuthUiState.Success }
                .onFailure { throwable ->
                    println("❌ SignIn Error: ${throwable.message}")
                    throwable.printStackTrace()
                    _uiState.value = AuthUiState.Error(throwable.toUserFriendlyMessage("تأكد من بيانات الدخول"))
                }
        }
    }

    fun signUp(email: String, password: String, fullName: String, userType: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            // 👈 تنظيف الإيميل والبيانات من المسافات الزائدة
            val cleanEmail = email.trim()
            val cleanPassword = password.trim()
            val cleanFullName = fullName.trim()

            authRepository.signUp(cleanEmail, cleanPassword, cleanFullName, userType)
                .onSuccess { _uiState.value = AuthUiState.Success }
                .onFailure { throwable ->
                    println("❌ SignUp Error: ${throwable.message}")
                    _uiState.value = AuthUiState.Error(throwable.toUserFriendlyMessage("حدث خطأ أثناء إنشاء الحساب"))
                }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.signInWithGoogle()
                .onSuccess { _uiState.value = AuthUiState.Success }
                .onFailure { throwable ->
                    println("❌ Google SignIn Error: ${throwable.message}")
                    _uiState.value = AuthUiState.Error(throwable.toUserFriendlyMessage("تأكد من خدمات جوجل"))
                }
        }
    }

    fun signInWithGithub() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.signInWithGithub()
                .onSuccess { _uiState.value = AuthUiState.Success }
                .onFailure { throwable ->
                    println("❌ Github SignIn Error: ${throwable.message}")
                    _uiState.value = AuthUiState.Error(throwable.toUserFriendlyMessage("حدث خطأ أثناء الاتصال بجيت هاب"))
                }
        }
    }

    // 💡 دالة تحويل واستخراج الأخطاء
    private fun Throwable.toUserFriendlyMessage(defaultMessage: String): String {
        val msg = this.message ?: return defaultMessage

        return when {
            msg.contains("User already registered", ignoreCase = true) || msg.contains("already exists", ignoreCase = true) ->
                "هذا البريد الإلكتروني مسجل بالفعل"
            msg.contains("Invalid login credentials", ignoreCase = true) ->
                "البريد الإلكتروني أو كلمة المرور غير صحيحة"
            msg.contains("unexpected_failure", ignoreCase = true) ->
                "حدث خطأ في الخادم أثناء حفظ البيانات"
            msg.contains("Password should be at least", ignoreCase = true) ->
                "كلمة المرور يجب أن تكون 6 أحرف على الأقل"
            msg.contains("Email rate limit exceeded", ignoreCase = true) ->
                "تم تجاوز حد طلبات البريد، حاول لاحقاً"
            msg.contains("Unable to resolve host", ignoreCase = true) || msg.contains("ConnectException", ignoreCase = true) ->
                "تعذر الاتصال بالشبكة، تحقق من الإنترنت"
            msg.contains("invalid format", ignoreCase = true) || msg.contains("Unable to validate email", ignoreCase = true) ->
                "صيغة البريد الإلكتروني غير صحيحة"
            msg.contains("Email not confirmed", ignoreCase = true) ->
                "لم يتم تفعيل الحساب بعد، يرجى مراجعة بريدك الإلكتروني والأنساب بالضغط على رابط التأكيد."
            // إذا لم يتطابق مع القواعد، تعرض الرسالة الأصلية أو الرسالة الافتراضية لتسهيل معرفة الخطأ
            else -> if (msg.isNotBlank()) msg else defaultMessage
        }
    }
}