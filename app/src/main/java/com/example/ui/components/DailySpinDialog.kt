package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.engine.SoundSynthesizer
import com.example.model.BoosterType
import com.example.ui.theme.BoardBackground
import com.example.ui.theme.CandyOrange
import com.example.ui.theme.CandyRed
import com.example.ui.theme.CandyYellow
import com.example.ui.theme.GoldStar
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.PrimaryPurple
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class SpinSlice(
    val label: String,
    val color: Color,
    val rewardCoins: Int = 0,
    val rewardBooster: BoosterType? = null
)

val WHEEL_SLICES = listOf(
    SpinSlice("100 💰", Color(0xFFFF4081), rewardCoins = 100),
    SpinSlice("🔨 Hammer", Color(0xFF7B1FA2), rewardBooster = BoosterType.LOLLIPOP_HAMMER),
    SpinSlice("250 💰", Color(0xFFFF9800), rewardCoins = 250),
    SpinSlice("🔄 Switch", Color(0xFF00B0FF), rewardBooster = BoosterType.FREE_SWITCH),
    SpinSlice("500 💰", Color(0xFF00E676), rewardCoins = 500),
    SpinSlice("🍩 Bomb", Color(0xFFE91E63), rewardBooster = BoosterType.COLOR_BOMB_START),
    SpinSlice("➕ Moves", Color(0xFFFF5722), rewardBooster = BoosterType.EXTRA_MOVES),
    SpinSlice("1000 👑", Color(0xFFFFD54F), rewardCoins = 1000)
)

@Composable
fun DailySpinDialog(
    onClaimReward: (Int, BoosterType?) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isSpinning by remember { mutableStateOf(false) }
    var wonReward by remember { mutableStateOf<SpinSlice?>(null) }
    val rotationAngle = remember { Animatable(0f) }

    Dialog(onDismissRequest = { if (!isSpinning) onDismiss() }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BoardBackground),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.5.dp, Color(0xFFFFD54F), RoundedCornerShape(24.dp))
                .shadow(20.dp, RoundedCornerShape(24.dp))
                .testTag("daily_spin_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SWEET DAILY SPIN! 🎡",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD54F)
                )
                Text(
                    text = "Spin every day for free boosters & gold!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Wheel Container with Needle
                Box(
                    modifier = Modifier.size(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Rotating Wheel Canvas
                    Canvas(
                        modifier = Modifier
                            .size(230.dp)
                            .rotate(rotationAngle.value)
                    ) {
                        drawWheel(WHEEL_SLICES)
                    }

                    // Center Hub Button
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF9800))))
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🍬", fontSize = 24.sp)
                    }

                    // Top Pointer Needle
                    Canvas(
                        modifier = Modifier
                            .size(30.dp)
                            .align(Alignment.TopCenter)
                    ) {
                        val path = Path().apply {
                            moveTo(size.width / 2f, size.height)
                            lineTo(0f, 0f)
                            lineTo(size.width, 0f)
                            close()
                        }
                        drawPath(path, color = Color(0xFFFFD54F))
                        drawPath(path, color = Color.White, style = Stroke(width = 2f))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (wonReward != null) {
                    Text(
                        text = "YOU WON: ${wonReward!!.label}!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00E676)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            onClaimReward(wonReward!!.rewardCoins, wonReward!!.rewardBooster)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("CLAIM REWARD 🎁", fontWeight = FontWeight.Black)
                    }
                } else {
                    Button(
                        onClick = {
                            if (!isSpinning) {
                                isSpinning = true
                                SoundSynthesizer.playColorBombSparkle()
                                scope.launch {
                                    val winningIndex = Random.nextInt(WHEEL_SLICES.size)
                                    val sliceAngle = 360f / WHEEL_SLICES.size
                                    // Target angle brings slice to top pointer (270 degrees)
                                    val targetSliceAngle = 360f - (winningIndex * sliceAngle + sliceAngle / 2f)
                                    val totalSpins = 360f * 5 + targetSliceAngle

                                    rotationAngle.animateTo(
                                        targetValue = totalSpins,
                                        animationSpec = tween(3200, easing = FastOutSlowInEasing)
                                    )
                                    wonReward = WHEEL_SLICES[winningIndex]
                                    SoundSynthesizer.playSweetComboFanfare()
                                    isSpinning = false
                                }
                            }
                        },
                        enabled = !isSpinning,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("spin_wheel_button"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = if (isSpinning) "SPINNING..." else "SPIN NOW! 🎯",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Close", color = Color.White)
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawWheel(slices: List<SpinSlice>) {
    val radius = size.width / 2f
    val sliceAngle = 360f / slices.size

    for (i in slices.indices) {
        val startAngle = i * sliceAngle
        drawArc(
            color = slices[i].color,
            startAngle = startAngle,
            sweepAngle = sliceAngle,
            useCenter = true,
            size = Size(size.width, size.height)
        )
        // Slice border
        drawArc(
            color = Color.White.copy(alpha = 0.4f),
            startAngle = startAngle,
            sweepAngle = sliceAngle,
            useCenter = true,
            style = Stroke(width = 2.5f),
            size = Size(size.width, size.height)
        )

        // Outer dot pegs
        val angleRad = (startAngle + sliceAngle / 2f) * PI / 180f
        val dotX = (radius + (radius - 12f) * cos(angleRad)).toFloat()
        val dotY = (radius + (radius - 12f) * sin(angleRad)).toFloat()
        drawCircle(color = Color.White, radius = 4f, center = Offset(dotX, dotY))
    }

    // Outer golden wheel border
    drawCircle(
        color = Color(0xFFFFD54F),
        radius = radius,
        style = Stroke(width = 6f)
    )
}
