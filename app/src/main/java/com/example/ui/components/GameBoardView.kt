package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.CandyGameUiState
import com.example.engine.GameScorePopup
import com.example.ui.theme.BoardBackground
import com.example.ui.theme.BoardBorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameBoardView(
    uiState: CandyGameUiState,
    onTileClick: (Int, Int) -> Unit,
    onSwipe: (Int, Int, Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = uiState.levelData.rows
    val cols = uiState.levelData.cols

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .shadow(12.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(BoardBackground)
            .border(3.dp, Brush.linearGradient(listOf(BoardBorder, Color(0xFFFF8A80), BoardBorder)), RoundedCornerShape(20.dp))
            .padding(6.dp)
            .testTag("game_board")
    ) {
        val totalWidth = maxWidth
        val tileWidth = totalWidth / cols

        var dragStartPos = remember { mutableStateOf<Pair<Int, Int>?>(null) }
        var totalDragX = remember { mutableStateOf(0f) }
        var totalDragY = remember { mutableStateOf(0f) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(cols.toFloat() / rows.toFloat())
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val c = (offset.x / (size.width / cols)).toInt().coerceIn(0, cols - 1)
                            val r = (offset.y / (size.height / rows)).toInt().coerceIn(0, rows - 1)
                            dragStartPos.value = Pair(r, c)
                            totalDragX.value = 0f
                            totalDragY.value = 0f
                        },
                        onDrag = { _, dragAmount ->
                            totalDragX.value += dragAmount.x
                            totalDragY.value += dragAmount.y
                        },
                        onDragEnd = {
                            val start = dragStartPos.value
                            if (start != null && (Math.abs(totalDragX.value) > 30 || Math.abs(totalDragY.value) > 30)) {
                                onSwipe(start.first, start.second, totalDragX.value, totalDragY.value)
                            }
                            dragStartPos.value = null
                        },
                        onDragCancel = {
                            dragStartPos.value = null
                        }
                    )
                }
        ) {
            // Render 2D Grid
            Column(modifier = Modifier.fillMaxSize()) {
                for (r in 0 until rows) {
                    Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        for (c in 0 until cols) {
                            val tile = uiState.board.getOrNull(r)?.getOrNull(c)
                            val isSelected = uiState.selectedTile?.first == r && uiState.selectedTile?.second == c

                            if (tile != null) {
                                CandyTileView(
                                    tile = tile,
                                    isSelected = isSelected,
                                    onClick = { onTileClick(r, c) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Box(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Laser Beams Overlay for Striped Candies
            for (beam in uiState.laserBeams) {
                LaserBeamEffect(beam = beam, rows = rows, cols = cols)
            }

            // Floating Popups Overlay
            for (popup in uiState.popups) {
                ScorePopupView(popup = popup, rows = rows, cols = cols)
            }

            // Reshuffling banner
            if (uiState.isReshuffling) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SWEET RESHUFFLE!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFD54F),
                        style = TextStyle(shadow = Shadow(Color.Black, Offset(3f, 3f), 4f))
                    )
                }
            }
        }
    }
}

// Helper mutable state for pointer
private fun <T> mutableStateOf(value: T) = androidx.compose.runtime.mutableStateOf(value)

@Composable
fun ScorePopupView(popup: GameScorePopup, rows: Int, cols: Int) {
    val animOffset = remember { Animatable(0f) }
    val animAlpha = remember { Animatable(1f) }

    LaunchedEffect(popup.id) {
        launch {
            animOffset.animateTo(-50f, tween(650, easing = FastOutSlowInEasing))
        }
        launch {
            delay(350)
            animAlpha.animateTo(0f, tween(300, easing = LinearEasing))
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val cellWidthPx = with(density) { maxWidth.toPx() / cols }
        val cellHeightPx = with(density) { maxHeight.toPx() / rows }
        val posX = (popup.col + 0.5f) * cellWidthPx
        val posY = (popup.row + 0.5f) * cellHeightPx

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = (posX - 40).toInt(),
                        y = (posY + animOffset.value).toInt()
                    )
                }
        ) {
            Text(
                text = popup.text,
                color = popup.color.copy(alpha = animAlpha.value),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                style = TextStyle(
                    shadow = Shadow(Color.Black.copy(alpha = animAlpha.value), Offset(2f, 2f), 3f)
                )
            )
        }
    }
}

@Composable
fun LaserBeamEffect(beam: com.example.engine.LaserBeam, rows: Int, cols: Int) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(beam.id) {
        progress.animateTo(1f, tween(240, easing = LinearEasing))
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val alpha = (1f - progress.value).coerceIn(0f, 1f)

        if (beam.isHorizontal) {
            val cellHeight = h / rows
            val y = (beam.index + 0.5f) * cellHeight
            drawLine(
                brush = Brush.horizontalGradient(listOf(Color.Transparent, Color.White, Color(0xFFFFD54F), Color.White, Color.Transparent)),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 14f * (1f - progress.value * 0.5f),
                alpha = alpha
            )
        } else {
            val cellWidth = w / cols
            val x = (beam.index + 0.5f) * cellWidth
            drawLine(
                brush = Brush.verticalGradient(listOf(Color.Transparent, Color.White, Color(0xFFFFD54F), Color.White, Color.Transparent)),
                start = Offset(x, 0f),
                end = Offset(x, h),
                strokeWidth = 14f * (1f - progress.value * 0.5f),
                alpha = alpha
            )
        }
    }
}
