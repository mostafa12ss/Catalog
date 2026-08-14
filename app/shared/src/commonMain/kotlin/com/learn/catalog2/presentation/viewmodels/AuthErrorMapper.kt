package com.learn.catalog2.presentation.viewmodels

fun Throwable.toUserFriendlyMessage(): String {
    val rawMessage = this.message ?: ""

    return when {
        // خطأ إنشاء الحساب المكرر في Supabase Auth
        rawMessage.contains("User already registered", ignoreCase = true) ->
            "هذا البريد الإلكتروني مسجل بالفعل. يرجى تسجيل الدخول."

        // خطأ التريجر/قاعدة البيانات (مثل المشكلة السابقة)
        rawMessage.contains("unexpected_failure", ignoreCase = true) ->
            "حدث خطأ أثناء حفظ بيانات الحساب في السيرفر. يرجى المحاولة لاحقاً."

        // خطأ بيانات الدخول الخاطئة
        rawMessage.contains("Invalid login credentials", ignoreCase = true) ->
            "البريد الإلكتروني أو كلمة المرور غير صحيحة."

        // خطأ كلمة المرور الضعيفة
        rawMessage.contains("Password should be at least", ignoreCase = true) ->
            "يجب أن تكون كلمة المرور مكونة من 6 أحرف على الأقل."

        // خطأ الاتصال بالإنترنت
        rawMessage.contains("Unable to resolve host", ignoreCase = true) ||
                rawMessage.contains("ConnectException", ignoreCase = true) ->
            "تحقق من اتصالك بالإنترنت وأعد المحاولة."

        else -> "حدث خطأ غير متوقع. يرجى المحاولة مرة أخرى."
    }
}