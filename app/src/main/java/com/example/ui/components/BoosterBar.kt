package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PlayerProfile
import com.example.model.BoosterType
import com.example.ui.theme.CandyOrange
import com.example.ui.theme.CandyRed
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SurfaceCard

@Composable
fun BoosterBar(
    activeBooster: BoosterType?,
    profile: PlayerProfile,
    onSelectBooster: (BoosterType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Lollipop Hammer
        BoosterItem(
            booster = BoosterType.LOLLIPOP_HAMMER,
            iconEmoji = "🔨",
            count = profile.lollipopHammers,
            isActive = activeBooster == BoosterType.LOLLIPOP_HAMMER,
            onClick = { onSelectBooster(BoosterType.LOLLIPOP_HAMMER) }
        )

        // Free Switch
        BoosterItem(
            booster = BoosterType.FREE_SWITCH,
            iconEmoji = "🔄",
            count = profile.freeSwitches,
            isActive = activeBooster == BoosterType.FREE_SWITCH,
            onClick = { onSelectBooster(BoosterType.FREE_SWITCH) }
        )

        // Color Bomb
        BoosterItem(
            booster = BoosterType.COLOR_BOMB_START,
            iconEmoji = "🍩",
            count = profile.colorBombs,
            isActive = activeBooster == BoosterType.COLOR_BOMB_START,
            onClick = { onSelectBooster(BoosterType.COLOR_BOMB_START) }
        )

        // Sugar Lightning
        BoosterItem(
            booster = BoosterType.SUGAR_LIGHTNING,
            iconEmoji = "⚡",
            count = profile.sugarLightning,
            isActive = activeBooster == BoosterType.SUGAR_LIGHTNING,
            onClick = { onSelectBooster(BoosterType.SUGAR_LIGHTNING) }
        )

        // +5 Extra Moves
        BoosterItem(
            booster = BoosterType.EXTRA_MOVES,
            iconEmoji = "➕",
            count = profile.extraMoves,
            isActive = activeBooster == BoosterType.EXTRA_MOVES,
            onClick = { onSelectBooster(BoosterType.EXTRA_MOVES) }
        )
    }
}

@Composable
fun BoosterItem(
    booster: BoosterType,
    iconEmoji: String,
    count: Int,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_active")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .scale(if (isActive) pulseScale else 1f)
                .shadow(if (isActive) 12.dp else 4.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    if (isActive) Brush.radialGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF9800)))
                    else Brush.verticalGradient(listOf(SurfaceCard, PrimaryPurple))
                )
                .border(
                    width = if (isActive) 2.5.dp else 1.5.dp,
                    color = if (isActive) Color.White else Color(0xFFFFD54F),
                    shape = CircleShape
                )
                .testTag("booster_${booster.name.lowercase()}"),
            contentAlignment = Alignment.Center
        ) {
            Text(text = iconEmoji, fontSize = 22.sp)

            // Badge Count / Price
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 4.dp, y = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (count > 0) PrimaryPink else CandyOrange)
                    .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = if (count > 0) "$count" else "${booster.costCoins}💰",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Text(
            text = booster.title.take(8),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
