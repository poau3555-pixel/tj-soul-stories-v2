package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.model.LevelData
import com.example.ui.theme.BoardBackground
import com.example.ui.theme.CandyOrange
import com.example.ui.theme.CandyRed
import com.example.ui.theme.GoldStar
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.SurfaceCard
import kotlinx.coroutines.delay

@Composable
fun LevelWinDialog(
    levelData: LevelData,
    score: Int,
    stars: Int,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit,
    onBackToMap: () -> Unit
) {
    val star1Scale = remember { Animatable(0f) }
    val star2Scale = remember { Animatable(0f) }
    val star3Scale = remember { Animatable(0f) }

    LaunchedEffect(stars) {
        delay(200)
        if (stars >= 1) {
            star1Scale.animateTo(1.2f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            star1Scale.animateTo(1.0f, tween(100))
        }
        delay(200)
        if (stars >= 2) {
            star2Scale.animateTo(1.3f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            star2Scale.animateTo(1.0f, tween(100))
        }
        delay(200)
        if (stars >= 3) {
            star3Scale.animateTo(1.4f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            star3Scale.animateTo(1.0f, tween(100))
        }
    }

    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BoardBackground),
            modifier = Modifier
                .fillMaxWidth()
                .border(3.dp, Brush.verticalGradient(listOf(Color(0xFFFFD54F), PrimaryPink)), RoundedCornerShape(24.dp))
                .shadow(20.dp, RoundedCornerShape(24.dp))
                .testTag("win_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Title
                Text(
                    text = "LEVEL COMPLETED!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD54F)
                )
                Text(
                    text = "Level ${levelData.levelNumber} - ${levelData.worldName}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Victory Hero Graphic
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_sweet_victory),
                        contentDescription = "Sweet Victory",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3 Stars Row
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Star 1
                    Icon(
                        imageVector = if (stars >= 1) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Star 1",
                        tint = if (stars >= 1) GoldStar else Color.Gray,
                        modifier = Modifier
                            .size(44.dp)
                            .scale(if (stars >= 1) star1Scale.value else 1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Star 2 (center big)
                    Icon(
                        imageVector = if (stars >= 2) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Star 2",
                        tint = if (stars >= 2) GoldStar else Color.Gray,
                        modifier = Modifier
                            .size(56.dp)
                            .scale(if (stars >= 2) star2Scale.value else 1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Star 3
                    Icon(
                        imageVector = if (stars >= 3) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Star 3",
                        tint = if (stars >= 3) GoldStar else Color.Gray,
                        modifier = Modifier
                            .size(44.dp)
                            .scale(if (stars >= 3) star3Scale.value else 1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Score Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceCard)
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "FINAL SCORE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                        Text(
                            text = "$score",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "+${stars * 50} Coins Earned 💰",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E676)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Next Level Primary Action
                Button(
                    onClick = onNextLevel,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("next_level_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "NEXT LEVEL ➔",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onReplay,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Replay", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Replay", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = onBackToMap,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Map", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun LevelFailDialog(
    levelData: LevelData,
    score: Int,
    onRetry: () -> Unit,
    onAddMoves: () -> Unit,
    onBackToMap: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BoardBackground),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.5.dp, CandyRed, RoundedCornerShape(24.dp))
                .shadow(20.dp, RoundedCornerShape(24.dp))
                .testTag("fail_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "OUT OF MOVES!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = CandyRed
                )
                Text(
                    text = "Don't give up, sweet crusher!",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Score Reached
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceCard)
                        .padding(horizontal = 24.dp, vertical = 10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "SCORE REACHED",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                        Text(
                            text = "$score",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // +5 Extra Moves Booster Action
                Button(
                    onClick = onAddMoves,
                    colors = ButtonDefaults.buttonColors(containerColor = CandyOrange),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("extra_moves_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "+5 EXTRA MOVES (90💰)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("retry_level_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "TRY AGAIN 🔄",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onBackToMap,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Back to Saga Map", color = Color.White)
                }
            }
        }
    }
}
