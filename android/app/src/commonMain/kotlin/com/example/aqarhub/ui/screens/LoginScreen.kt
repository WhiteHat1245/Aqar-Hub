package com.example.aqarhub.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aqarhub.theme.*
import com.example.aqarhub.ui.viewmodel.AuthState
import com.example.aqarhub.ui.viewmodel.AuthViewModel

enum class CustomFieldState {
    DEFAULT,
    FOCUSED,
    SUCCESS,
    ERROR
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    state: CustomFieldState = CustomFieldState.DEFAULT,
    errorMessage: String? = null,
    isPassword: Boolean = false,
    leadingIcon: ImageVector? = null,
    singleLine: Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val resolvedState = when {
        state == CustomFieldState.ERROR -> CustomFieldState.ERROR
        state == CustomFieldState.SUCCESS -> CustomFieldState.SUCCESS
        isFocused -> CustomFieldState.FOCUSED
        else -> CustomFieldState.DEFAULT
    }

    val borderColor = when (resolvedState) {
        CustomFieldState.DEFAULT -> EarthTextMuted.copy(alpha = 0.2f)
        CustomFieldState.FOCUSED -> EarthPrimaryLight
        CustomFieldState.SUCCESS -> EarthSuccess
        CustomFieldState.ERROR -> EarthError
    }

    val iconColor = when (resolvedState) {
        CustomFieldState.DEFAULT -> EarthTextMuted
        CustomFieldState.FOCUSED -> EarthPrimary
        CustomFieldState.SUCCESS -> EarthSuccess
        CustomFieldState.ERROR -> EarthError
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(Color.White, RoundedCornerShape(14.dp))
                .border(2.dp, borderColor, RoundedCornerShape(14.dp))
                .onFocusChanged { isFocused = it.isFocused }
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = Color(0xFFC8BDB0),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            color = EarthTextDark,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        singleLine = singleLine,
                        visualTransformation = if (isPassword && !passwordVisible) {
                            PasswordVisualTransformation()
                        } else {
                            VisualTransformation.None
                        },
                        cursorBrush = SolidColor(EarthPrimary)
                    )
                }

                // Trailing Icon (Eye toggle for password)
                when {
                    isPassword -> {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(EarthTextMuted.copy(alpha = 0.08f))
                                .clickable { passwordVisible = !passwordVisible },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "إخفاء كلمة المرور" else "إظهار كلمة المرور",
                                tint = EarthTextMedium,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    resolvedState == CustomFieldState.SUCCESS -> {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "صحيح",
                            tint = EarthSuccess,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    resolvedState == CustomFieldState.ERROR -> {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "خطأ",
                            tint = EarthError,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        if (resolvedState == CustomFieldState.ERROR && !errorMessage.isNullOrEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = EarthError,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = errorMessage,
                    color = EarthError,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun SharedLoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel
) {
    val state by viewModel.state.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isEmailValid = remember(email) { viewModel.isValidEmail(email) }
    val isFormValid = remember(email, password, isEmailValid) {
        isEmailValid && password.isNotBlank()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(EarthBg, EarthBgWarm)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Desktop / Tablet width constraint (Max 420.dp)
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 420.dp)
                .align(Alignment.Center)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 28.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(EarthPrimary, EarthPrimaryDark)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "عقار هب",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Column {
                    Text(
                        text = "عقار هب",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EarthPrimaryDark
                    )
                    Text(
                        text = "بوابتك الذكية لعالم الضيافة",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = EarthTextMedium
                    )
                }
            }

            // Input Fields
            val emailState = when {
                email.isEmpty() -> CustomFieldState.DEFAULT
                isEmailValid -> CustomFieldState.SUCCESS
                else -> CustomFieldState.ERROR
            }
            CustomTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "البريد الإلكتروني",
                leadingIcon = Icons.Default.Email,
                state = emailState,
                errorMessage = "البريد الإلكتروني غير صالح"
            )

            Spacer(modifier = Modifier.height(16.dp))

            val passwordState = when {
                password.isEmpty() -> CustomFieldState.DEFAULT
                password.length >= 8 -> CustomFieldState.SUCCESS
                else -> CustomFieldState.ERROR
            }
            CustomTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "كلمة المرور",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                state = passwordState,
                errorMessage = "كلمة المرور قصيرة جداً"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button (Enabled only when Regex & password validation pass)
            Button(
                onClick = {
                    if (isFormValid) {
                        viewModel.login(email, password)
                    }
                },
                enabled = isFormValid && state !is AuthState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EarthPrimary,
                    disabledContainerColor = EarthPrimary.copy(alpha = 0.4f)
                )
            ) {
                if (state is AuthState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "تسجيل الدخول",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
