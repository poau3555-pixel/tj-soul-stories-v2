package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.CandyGameUiState
import com.example.model.LevelGoal
import com.example.ui.theme.CandyJelly
import com.example.ui.theme.CandyOrange
import com.example.ui.theme.CandyRed
import com.example.ui.theme.CandyYellow
import com.example.ui.theme.GoldStar
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.SurfaceCard

@Composable
fun TopHeaderBar(
    uiState: CandyGameUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val levelData = uiState.levelData
    val scoreProgress = (uiState.score.toFloat() / levelData.star3Score.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = scoreProgress,
        animationSpec = tween(400),
        label = "score_bar"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // Navigation & Level Title Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SurfaceCard)
                    .border(1.5.dp, Color(0xFFFFD54F), CircleShape)
                    .testTag("back_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back to map",
                    tint = Color.White
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "LEVEL ${levelData.levelNumber}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD54F),
                    style = TextStyle(shadow = Shadow(Color.Black, Offset(2f, 2f), 3f))
                )
                Text(
                    text = levelData.worldName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            // Score Display
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCard)
                    .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${uiState.score}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Center Stats Row: MOVES vs GOAL OBJECTIVE
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Big Moves Counter Circle
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                if (uiState.movesRemaining <= 5) CandyRed else PrimaryPink,
                                Color(0xFF4A148C)
                            )
                        )
                    )
                    .border(2.5.dp, Color(0xFFFFD54F), CircleShape)
                    .testTag("moves_counter"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${uiState.movesRemaining}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        style = TextStyle(shadow = Shadow(Color.Black, Offset(2f, 2f), 4f))
                    )
                    Text(
                        text = "MOVES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                }
            }

            // Level Objective Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
                    .height(68.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceCard)
                    .border(1.5.dp, Color(0xFF8E24AA), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                GoalTargetView(uiState = uiState)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3-Star Score Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF1E0D36))
                .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(10.dp))
        ) {
            // Filled bar
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFFF9800), Color(0xFFFFEB3B), Color(0xFF00E676))
                        )
                    )
            )

            // Star icons along the bar (at 1-star, 2-star, 3-star thresholds)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val s1 = levelData.star1Score.toFloat() / levelData.star3Score.toFloat()
                val s2 = levelData.star2Score.toFloat() / levelData.star3Score.toFloat()

                // Star 1
                Icon(
                    imageVector = if (uiState.starsEarned >= 1) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Star 1",
                    tint = if (uiState.starsEarned >= 1) GoldStar else Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )

                // Star 2
                Icon(
                    imageVector = if (uiState.starsEarned >= 2) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Star 2",
                    tint = if (uiState.starsEarned >= 2) GoldStar else Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )

                // Star 3
                Icon(
                    imageVector = if (uiState.starsEarned >= 3) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Star 3",
                    tint = if (uiState.starsEarned >= 3) GoldStar else Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun GoalTargetView(uiState: CandyGameUiState) {
    when (val goal = uiState.levelData.goal) {
        is LevelGoal.TargetScore -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "TARGET SCORE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD54F)
                )
                Text(
                    text = "${uiState.score} / ${goal.targetScore}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = if (uiState.score >= goal.targetScore) Color(0xFF00E676) else Color.White
                )
            }
        }
        is LevelGoal.ClearJelly -> {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(CandyJelly)
                        .border(1.5.dp, Color(0xFFFF4081), RoundedCornerShape(6.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "CLEAR JELLY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                    Text(
                        text = "${uiState.remainingJellies} LEFT",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = if (uiState.remainingJellies == 0) Color(0xFF00E676) else Color.White
                    )
                }
            }
        }
        is LevelGoal.DropIngredients -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "DROP INGREDIENTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD54F)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🍒 ${uiState.remainingCherries}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    if (goal.hazelnuts > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🌰 ${uiState.remainingHazelnuts}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }
        is LevelGoal.CollectOrders -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "COLLECT ORDERS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD54F)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    for ((color, needed) in goal.colorOrders) {
                        val collected = uiState.colorOrdersCollected[color] ?: 0
                        val isDone = collected >= needed
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(color.primaryColor)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "$collected/$needed",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDone) Color(0xFF00E676) else Color.White
                            )
                        }
                    }
                    if (goal.stripedNeeded > 0) {
                        val isDone = uiState.stripedOrdersCollected >= goal.stripedNeeded
                        Text(
                            text = "⚡${uiState.stripedOrdersCollected}/${goal.stripedNeeded}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDone) Color(0xFF00E676) else Color.White
                        )
                    }
                }
            }
        }
    }
}
