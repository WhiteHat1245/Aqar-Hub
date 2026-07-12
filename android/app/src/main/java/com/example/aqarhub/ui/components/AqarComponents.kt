package com.example.aqarhub.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aqarhub.theme.*
import kotlinx.coroutines.delay

enum class FieldState {
    DEFAULT,
    FOCUSED,
    SUCCESS,
    ERROR
}

@Composable
fun AqarTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    state: FieldState = FieldState.DEFAULT,
    errorMessage: String? = null,
    successMessage: String? = null,
    isPassword: Boolean = false,
    leadingIcon: ImageVector? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val resolvedState = when {
        state == FieldState.ERROR -> FieldState.ERROR
        state == FieldState.SUCCESS -> FieldState.SUCCESS
        isFocused -> FieldState.FOCUSED
        else -> FieldState.DEFAULT
    }

    val borderColor = when (resolvedState) {
        FieldState.DEFAULT -> EarthTextMuted.copy(alpha = 0.2f)
        FieldState.FOCUSED -> EarthPrimaryLight
        FieldState.SUCCESS -> EarthSuccess
        FieldState.ERROR -> EarthError
    }

    val iconColor = when (resolvedState) {
        FieldState.DEFAULT -> EarthTextMuted
        FieldState.FOCUSED -> EarthPrimary
        FieldState.SUCCESS -> EarthSuccess
        FieldState.ERROR -> EarthError
    }

    val glowAlpha by animateFloatAsState(
        targetValue = if (resolvedState == FieldState.FOCUSED) 0.15f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "glowAlpha"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(
                    elevation = if (resolvedState == FieldState.FOCUSED) 6.dp else 0.dp,
                    shape = RoundedCornerShape(14.dp),
                    clip = false,
                    ambientColor = EarthPrimaryLight.copy(alpha = glowAlpha),
                    spotColor = EarthPrimaryLight.copy(alpha = glowAlpha)
                )
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
                // Leading Icon (Renders on the right in RTL layout direction)
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
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        cursorBrush = SolidColor(EarthPrimary)
                    )
                }

                // Trailing Icon / Password Toggle (Renders on the left in RTL)
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
                    resolvedState == FieldState.SUCCESS -> {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "صحيح",
                            tint = EarthSuccess,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    resolvedState == FieldState.ERROR -> {
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

        // Error or Success Message
        AnimatedVisibility(
            visible = (resolvedState == FieldState.ERROR && !errorMessage.isNullOrEmpty()) ||
                    (resolvedState == FieldState.SUCCESS && !successMessage.isNullOrEmpty()),
            enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = if (resolvedState == FieldState.ERROR) Icons.Default.Error else Icons.Default.Check,
                    contentDescription = null,
                    tint = if (resolvedState == FieldState.ERROR) EarthError else EarthSuccess,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = (if (resolvedState == FieldState.ERROR) errorMessage else successMessage) ?: "",
                    color = if (resolvedState == FieldState.ERROR) EarthError else EarthSuccess,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun PasswordStrengthMeter(
    score: Int,
    modifier: Modifier = Modifier
) {
    val progress = score / 4f
    val (color, text) = when (score) {
        1 -> EarthError to "ضعيفة جداً"
        2 -> Color(0xFFE67E22) to "ضعيفة"
        3 -> Color(0xFFF1C40F) to "متوسطة"
        4 -> EarthSuccess to "قوية جداً"
        else -> EarthTextMuted to ""
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "strengthProgress"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "قوة كلمة المرور",
                color = EarthTextMedium,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = text,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(EarthTextMuted.copy(alpha = 0.15f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
fun SocialLoginButton(
    text: String,
    onClick: () -> Unit,
    logo: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val translationY by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "bounceY"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 3.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "bounceShadow"
    )

    Card(
        modifier = modifier
            .graphicsLayer {
                this.translationY = translationY.toPx()
            }
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(12.dp),
                clip = true
            )
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(1.dp, EarthTextMuted.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            logo()
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = EarthTextDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun GoogleLogo() {
    Canvas(modifier = Modifier.size(18.dp)) {
        drawArc(
            brush = SolidColor(Color(0xFFEA4335)),
            startAngle = 180f,
            sweepAngle = 90f,
            useCenter = true
        )
        drawArc(
            brush = SolidColor(Color(0xFFFBBC05)),
            startAngle = 90f,
            sweepAngle = 90f,
            useCenter = true
        )
        drawArc(
            brush = SolidColor(Color(0xFF34A853)),
            startAngle = 0f,
            sweepAngle = 90f,
            useCenter = true
        )
        drawArc(
            brush = SolidColor(Color(0xFF4285F4)),
            startAngle = 270f,
            sweepAngle = 90f,
            useCenter = true
        )
    }
}

@Composable
fun FacebookLogo() {
    Canvas(modifier = Modifier.size(18.dp)) {
        drawCircle(color = Color(0xFF1877F2))
    }
}

@Composable
fun CloudTabSlider(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    tabs: List<String>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(EarthPrimary.copy(alpha = 0.06f))
            .padding(4.dp)
    ) {
        var containerWidth by remember { mutableStateOf(0.dp) }
        val tabWidth = containerWidth / tabs.size.coerceAtLeast(1)

        val animatedOffset by animateDpAsState(
            targetValue = tabWidth * selectedIndex,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            ),
            label = "tabOffset"
        )

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            containerWidth = maxWidth

            // Floating Active Background
            if (tabs.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .offset(x = animatedOffset)
                        .width(tabWidth)
                        .fillMaxHeight()
                        .shadow(2.dp, RoundedCornerShape(12.dp))
                        .background(Color.White, RoundedCornerShape(12.dp))
                )
            }

            // Tabs Content
            Row(modifier = Modifier.fillMaxSize()) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = index == selectedIndex
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) EarthTextDark else EarthTextMuted,
                        animationSpec = tween(durationMillis = 250),
                        label = "tabTextColor"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onTabSelected(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = textColor,
                            fontSize = 15.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

enum class ToastStatus {
    SUCCESS,
    ERROR,
    INFO
}

@Composable
fun AqarTopToast(
    message: String,
    status: ToastStatus,
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (status) {
        ToastStatus.SUCCESS -> EarthSuccess
        ToastStatus.ERROR -> EarthError
        ToastStatus.INFO -> EarthPrimary
    }

    val icon = when (status) {
        ToastStatus.SUCCESS -> Icons.Default.CheckCircle
        ToastStatus.ERROR -> Icons.Default.Error
        ToastStatus.INFO -> Icons.Default.Info
    }

    LaunchedEffect(visible) {
        if (visible) {
            delay(3500)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis = 400, easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f))
        ) + fadeIn(animationSpec = tween(400)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(durationMillis = 300, easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f))
        ) + fadeOut(animationSpec = tween(300)),
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = message,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
