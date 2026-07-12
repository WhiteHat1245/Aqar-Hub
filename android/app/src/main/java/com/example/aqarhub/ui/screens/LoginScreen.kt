package com.example.aqarhub.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aqarhub.theme.*
import com.example.aqarhub.ui.components.*
import com.example.aqarhub.ui.viewmodel.AuthState
import com.example.aqarhub.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    var isRegister by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // Toast States
    var toastVisible by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var toastStatus by remember { mutableStateOf(ToastStatus.INFO) }

    // Navigation and status listening
    LaunchedEffect(state) {
        if (state is AuthState.Success) {
            toastMessage = "تمت العملية بنجاح!"
            toastStatus = ToastStatus.SUCCESS
            toastVisible = true
            delay(1200)
            onLoginSuccess()
            viewModel.resetState()
        } else if (state is AuthState.Error) {
            toastMessage = (state as AuthState.Error).message
            toastStatus = ToastStatus.ERROR
            toastVisible = true
            viewModel.resetState()
        }
    }

    // Force RTL for Arabic layout
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(EarthBg, EarthBgWarm)
                    )
                )
        ) {
            // Main Centered Content (Max Width 420dp for tablets)
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(max = 420.dp)
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(28.dp))

                // Header Component
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
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
                            fontWeight = FontWeight.W500,
                            color = EarthTextMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cloud Tab Slider
                CloudTabSlider(
                    selectedIndex = if (isRegister) 1 else 0,
                    onTabSelected = { index ->
                        isRegister = index == 1
                    },
                    tabs = listOf("تسجيل الدخول", "إنشاء حساب"),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Animating Forms (Slide + Fade switching animation)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    AnimatedContent(
                        targetState = isRegister,
                        transitionSpec = {
                            if (targetState) {
                                // Slide in from right (40dp), slide out to left (-40dp)
                                (slideInHorizontally(
                                    animationSpec = tween(400, easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)),
                                    initialOffsetX = { 120 }
                                ) + fadeIn(animationSpec = tween(400))).togetherWith(
                                    slideOutHorizontally(
                                        animationSpec = tween(400, easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)),
                                        targetOffsetX = { -120 }
                                    ) + fadeOut(animationSpec = tween(400))
                                )
                            } else {
                                // Slide in from left (-40dp), slide out to right (40dp)
                                (slideInHorizontally(
                                    animationSpec = tween(400, easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)),
                                    initialOffsetX = { -120 }
                                ) + fadeIn(animationSpec = tween(400))).togetherWith(
                                    slideOutHorizontally(
                                        animationSpec = tween(400, easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)),
                                        targetOffsetX = { 120 }
                                    ) + fadeOut(animationSpec = tween(400))
                                )
                            }
                        },
                        label = "formSwitchAnimation"
                    ) { targetIsRegister ->
                        if (targetIsRegister) {
                            // REGISTER FORM
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Name Field Validation
                                val nameState = when {
                                    name.isEmpty() -> FieldState.DEFAULT
                                    viewModel.isValidName(name) -> FieldState.SUCCESS
                                    else -> FieldState.ERROR
                                }
                                AqarTextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    placeholder = "الاسم بالكامل",
                                    leadingIcon = Icons.Default.Person,
                                    state = nameState,
                                    errorMessage = "الاسم يجب أن يتكون من 3 أحرف على الأقل"
                                )

                                // Email Field Validation
                                val emailState = when {
                                    email.isEmpty() -> FieldState.DEFAULT
                                    viewModel.isValidEmail(email) -> FieldState.SUCCESS
                                    else -> FieldState.ERROR
                                }
                                AqarTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    placeholder = "البريد الإلكتروني",
                                    leadingIcon = Icons.Default.Email,
                                    state = emailState,
                                    errorMessage = "البريد الإلكتروني غير صالح"
                                )

                                // Phone Field Validation
                                val phoneState = when {
                                    phone.isEmpty() -> FieldState.DEFAULT
                                    viewModel.isValidPhone(phone) -> FieldState.SUCCESS
                                    else -> FieldState.ERROR
                                }
                                AqarTextField(
                                    value = phone,
                                    onValueChange = { phone = it },
                                    placeholder = "رقم الهاتف (اختياري)",
                                    leadingIcon = Icons.Default.Phone,
                                    state = phoneState,
                                    errorMessage = "رقم الهاتف غير صالح. يجب أن يحتوي على أرقام فقط (بين 7 و 15 رقماً)"
                                )

                                // Password Field Validation & Strength Meter
                                val passwordState = when {
                                    password.isEmpty() -> FieldState.DEFAULT
                                    password.length >= 8 -> FieldState.SUCCESS
                                    else -> FieldState.ERROR
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    AqarTextField(
                                        value = password,
                                        onValueChange = { password = it },
                                        placeholder = "كلمة المرور",
                                        leadingIcon = Icons.Default.Lock,
                                        isPassword = true,
                                        state = passwordState,
                                        errorMessage = "كلمة المرور يجب أن تكون 8 أحرف على الأقل"
                                    )

                                    if (password.isNotEmpty()) {
                                        // Calculate password strength
                                        var score = 0
                                        for (i in 1..4) {
                                            when (i) {
                                                1 -> if (password.length >= 8) score++
                                                2 -> if (password.any { it.isUpperCase() }) score++
                                                3 -> if (password.any { it.isDigit() }) score++
                                                4 -> if (password.any { !it.isLetterOrDigit() }) score++
                                            }
                                        }

                                        PasswordStrengthMeter(score = score)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Main Register Button
                                Button(
                                    onClick = {
                                        viewModel.register(name, email, phone, password)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EarthPrimary),
                                    enabled = state !is AuthState.Loading
                                ) {
                                    if (state is AuthState.Loading) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "إنشاء حساب جديد",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        } else {
                            // LOGIN FORM
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Email Field Validation
                                val emailState = when {
                                    email.isEmpty() -> FieldState.DEFAULT
                                    viewModel.isValidEmail(email) -> FieldState.SUCCESS
                                    else -> FieldState.ERROR
                                }
                                AqarTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    placeholder = "البريد الإلكتروني",
                                    leadingIcon = Icons.Default.Email,
                                    state = emailState,
                                    errorMessage = "البريد الإلكتروني غير صالح"
                                )

                                // Password Field Validation
                                val passwordState = when {
                                    password.isEmpty() -> FieldState.DEFAULT
                                    password.length >= 8 -> FieldState.SUCCESS
                                    else -> FieldState.ERROR
                                }
                                AqarTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    placeholder = "كلمة المرور",
                                    leadingIcon = Icons.Default.Lock,
                                    isPassword = true,
                                    state = passwordState,
                                    errorMessage = "كلمة المرور قصيرة جداً"
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Main Login Button
                                Button(
                                    onClick = {
                                        viewModel.login(email, password)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EarthPrimary),
                                    enabled = state !is AuthState.Loading
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
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Social Login Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "أو سجل دخولك عبر",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = EarthTextMuted,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SocialLoginButton(
                            text = "Google",
                            onClick = {
                                toastMessage = "تسجيل الدخول بـ Google قيد التطوير..."
                                toastStatus = ToastStatus.INFO
                                toastVisible = true
                            },
                            logo = { GoogleLogo() },
                            modifier = Modifier.weight(1f)
                        )

                        SocialLoginButton(
                            text = "Facebook",
                            onClick = {
                                toastMessage = "تسجيل الدخول بـ Facebook قيد التطوير..."
                                toastStatus = ToastStatus.INFO
                                toastVisible = true
                            },
                            logo = { FacebookLogo() },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Bottom Page / Nav Dot Indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    repeat(3) { index ->
                        val isActive = (isRegister && index == 1) || (!isRegister && index == 0)
                        val width by animateDpAsState(
                            targetValue = if (isActive) 24.dp else 8.dp,
                            animationSpec = tween(durationMillis = 300),
                            label = "dotWidth"
                        )
                        val opacity by animateFloatAsState(
                            targetValue = if (isActive) 1f else 0.3f,
                            animationSpec = tween(durationMillis = 300),
                            label = "dotOpacity"
                        )

                        Box(
                            modifier = Modifier
                                .size(width = width, height = 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(EarthPrimary.copy(alpha = opacity))
                        )
                    }
                }
            }

            // Custom Top Toast component overlay
            AqarTopToast(
                message = toastMessage,
                status = toastStatus,
                visible = toastVisible,
                onDismiss = { toastVisible = false },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
