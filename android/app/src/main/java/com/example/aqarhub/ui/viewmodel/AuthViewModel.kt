package com.example.aqarhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aqarhub.data.local.EncryptedSharedPreferencesManager
import com.example.aqarhub.data.remote.AuthApiService
import com.example.aqarhub.data.model.LoginRequest
import com.example.aqarhub.data.model.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthState {
    object Idle : AuthState
    object Loading : AuthState
    object Success : AuthState
    data class Error(val message: String) : AuthState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authApi: AuthApiService,
    private val encryptedPrefs: EncryptedSharedPreferencesManager
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }

    fun isValidPhone(phone: String): Boolean {
        if (phone.isEmpty()) return true // Phone is optional in registration
        val phoneRegex = "^\\+?[0-9]{7,15}$".toRegex()
        return phone.matches(phoneRegex)
    }

    fun isValidName(name: String): Boolean {
        return name.trim().length >= 3
    }

    fun login(email: String, password: String) {
        if (!isValidEmail(email)) {
            _state.value = AuthState.Error("البريد الإلكتروني المدخل غير صالح")
            return
        }
        if (password.isBlank()) {
            _state.value = AuthState.Error("كلمة المرور لا يمكن أن تكون فارغة")
            return
        }

        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val response = authApi.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    encryptedPrefs.saveAccessToken(body.access_token)
                    _state.value = AuthState.Success
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "فشل تسجيل الدخول"
                    _state.value = AuthState.Error(if (errorMsg.contains("Unauthorized") || errorMsg.contains("credentials")) "خطأ في البريد الإلكتروني أو كلمة المرور" else errorMsg)
                }
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.localizedMessage ?: "حدث خطأ ما أثناء الاتصال بالخادم")
            }
        }
    }

    fun register(name: String, email: String, phone: String, password: String) {
        if (!isValidName(name)) {
            _state.value = AuthState.Error("الاسم يجب أن يتكون من 3 أحرف على الأقل")
            return
        }
        if (!isValidEmail(email)) {
            _state.value = AuthState.Error("البريد الإلكتروني المدخل غير صالح")
            return
        }
        if (!isValidPhone(phone)) {
            _state.value = AuthState.Error("رقم الهاتف غير صالح. يجب أن يحتوي على أرقام فقط (بين 7 و 15 رقماً)")
            return
        }
        if (password.length < 8) {
            _state.value = AuthState.Error("كلمة المرور يجب أن تكون 8 أحرف على الأقل")
            return
        }

        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val response = authApi.register(RegisterRequest(name, email, password, phone.ifBlank { null }))
                if (response.isSuccessful) {
                    login(email, password)
                } else {
                    _state.value = AuthState.Error(response.errorBody()?.string() ?: "فشل إنشاء الحساب")
                }
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.localizedMessage ?: "حدث خطأ أثناء إنشاء الحساب")
            }
        }
    }

    fun resetState() {
        _state.value = AuthState.Idle
    }
}

