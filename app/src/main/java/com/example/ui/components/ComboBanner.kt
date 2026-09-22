package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ComboBanner(
    message: String?,
    modifier: Modifier = Modifier
) {
    var displayedMessage by remember { mutableStateOf<String?>(null) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(message) {
        if (!message.isNullOrEmpty()) {
            displayedMessage = message
            isVisible = true
            delay(1100)
            isVisible = false
        }
    }

    AnimatedVisibility(
        visible = isVisible && displayedMessage != null,
        enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) + fadeIn(),
        exit = scaleOut(tween(250)) + fadeOut(),
        modifier = modifier
    ) {
        val bannerColors = when (displayedMessage) {
            "SUGAR CRUSH!" -> listOf(Color(0xFFFF1744), Color(0xFFFFD54F), Color(0xFFFF4081))
            "DIVINE!" -> listOf(Color(0xFFD500F9), Color(0xFF00E5FF), Color(0xFFFFD54F))
            "DELICIOUS!" -> listOf(Color(0xFFFF9100), Color(0xFFFFD54F), Color(0xFFFF4081))
            "TASTY!" -> listOf(Color(0xFF00E676), Color(0xFF00B0FF), Color(0xFFFFD54F))
            else -> listOf(Color(0xFFFF4081), Color(0xFFFF80AB), Color(0xFFFFD54F))
        }

        Box(
            modifier = Modifier
                .shadow(16.dp, RoundedCornerShape(28.dp))
                .clip(RoundedCornerShape(28.dp))
                .background(Brush.horizontalGradient(bannerColors))
                .border(3.dp, Color.White, RoundedCornerShape(28.dp))
                .padding(horizontal = 28.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayedMessage ?: "",
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.5.sp,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color(0xFF260E3A),
                        offset = Offset(4f, 4f),
                        blurRadius = 6f
                    )
                )
            )
        }
    }
}
