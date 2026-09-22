package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.BoardTile
import com.example.model.Candy
import com.example.model.CandyColor
import com.example.model.CandyShape
import com.example.model.ObstacleType
import com.example.model.SpecialType
import com.example.ui.theme.BoardBackground
import com.example.ui.theme.CandyChocolate
import com.example.ui.theme.CandyChocolateDark
import com.example.ui.theme.CandyChocolateLight
import com.example.ui.theme.CandyFrosting
import com.example.ui.theme.CandyJelly
import com.example.ui.theme.CandyJellyBorder
import com.example.ui.theme.TileDark
import com.example.ui.theme.TileLight
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CandyTileView(
    tile: BoardTile,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!tile.isPlayable) {
        Box(modifier = modifier.aspectRatio(1f))
        return
    }

    val isEven = (tile.row + tile.col) % 2 == 0
    val tileBg = if (isEven) TileLight else TileDark

    val infiniteTransition = rememberInfiniteTransition(label = "selected_anim")
    val selectedScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val candyScale = if (isSelected) selectedScale else 1f

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(1.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(tileBg)
            .then(
                if (isSelected) Modifier.border(2.5.dp, Color(0xFFFFD54F), RoundedCornerShape(8.dp))
                else Modifier
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .testTag("tile_${tile.row}_${tile.col}"),
        contentAlignment = Alignment.Center
    ) {
        // Jelly Layer underneath candy
        if (tile.hasJelly) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val jellyColor = if (tile.jellyLayers >= 2) CandyJelly.copy(alpha = 0.75f) else CandyJelly.copy(alpha = 0.45f)
                drawRoundRect(
                    color = jellyColor,
                    cornerRadius = CornerRadius(16f, 16f)
                )
                drawRoundRect(
                    color = CandyJellyBorder,
                    style = Stroke(width = if (tile.jellyLayers >= 2) 4f else 2f),
                    cornerRadius = CornerRadius(16f, 16f)
                )
            }
        }

        // Draw Obstacles (Chocolate / Frosting)
        if (tile.hasObstacle) {
            ObstacleView(tile.obstacle)
        } else if (tile.candy != null) {
            // Draw Candy
            Box(
                modifier = Modifier
                    .fillMaxSize(0.88f)
                    .scale(candyScale)
            ) {
                CandyGraphic(tile.candy)
            }
        }
    }
}

@Composable
fun CandyGraphic(candy: Candy, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "color_bomb_spin")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing)
        ),
        label = "spin"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        when {
            candy.isColorBomb -> {
                drawColorBomb(spinAngle)
            }
            candy.isIngredient -> {
                drawIngredient(candy.special)
            }
            else -> {
                drawStandardCandy(candy)
            }
        }
    }
}

private fun DrawScope.drawStandardCandy(candy: Candy) {
    val width = size.width
    val height = size.height
    val center = Offset(width / 2f, height / 2f)
    val color = candy.color

    val gradientBrush = Brush.radialGradient(
        colors = listOf(color.lightColor, color.primaryColor, color.darkColor),
        center = Offset(width * 0.35f, height * 0.35f),
        radius = width * 0.75f
    )

    when (color.shapeType) {
        CandyShape.CIRCLE -> {
            drawCircle(brush = gradientBrush, radius = width * 0.42f, center = center)
            // Glossy Specular Highlight
            drawCircle(
                color = Color.White.copy(alpha = 0.5f),
                radius = width * 0.12f,
                center = Offset(width * 0.35f, height * 0.35f)
            )
        }
        CandyShape.BEAN -> {
            // Red Jelly Bean oval shape
            val path = Path().apply {
                val r = width * 0.44f
                addOval(androidx.compose.ui.geometry.Rect(width * 0.1f, height * 0.2f, width * 0.9f, height * 0.8f))
            }
            drawPath(path, brush = gradientBrush)
            drawOval(
                color = Color.White.copy(alpha = 0.55f),
                topLeft = Offset(width * 0.25f, height * 0.3f),
                size = Size(width * 0.4f, height * 0.18f)
            )
        }
        CandyShape.OVAL -> {
            // Orange Lozenge
            drawRoundRect(
                brush = gradientBrush,
                topLeft = Offset(width * 0.08f, height * 0.22f),
                size = Size(width * 0.84f, height * 0.56f),
                cornerRadius = CornerRadius(width * 0.28f, height * 0.28f)
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.5f),
                topLeft = Offset(width * 0.2f, height * 0.28f),
                size = Size(width * 0.5f, height * 0.15f),
                cornerRadius = CornerRadius(12f, 12f)
            )
        }
        CandyShape.DROP -> {
            // Yellow Lemon Drop
            val path = Path().apply {
                moveTo(center.x, height * 0.1f)
                cubicTo(
                    width * 0.9f, height * 0.45f,
                    width * 0.85f, height * 0.9f,
                    center.x, height * 0.9f
                )
                cubicTo(
                    width * 0.15f, height * 0.9f,
                    width * 0.1f, height * 0.45f,
                    center.x, height * 0.1f
                )
                close()
            }
            drawPath(path, brush = gradientBrush)
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = width * 0.1f,
                center = Offset(width * 0.38f, height * 0.42f)
            )
        }
        CandyShape.SQUARE -> {
            // Green Chiclet rounded square
            drawRoundRect(
                brush = gradientBrush,
                topLeft = Offset(width * 0.12f, height * 0.12f),
                size = Size(width * 0.76f, height * 0.76f),
                cornerRadius = CornerRadius(width * 0.2f, width * 0.2f)
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.5f),
                topLeft = Offset(width * 0.22f, height * 0.2f),
                size = Size(width * 0.35f, height * 0.16f),
                cornerRadius = CornerRadius(8f, 8f)
            )
        }
        CandyShape.TEARDROP -> {
            // Purple Cluster / Teardrop
            drawCircle(brush = gradientBrush, radius = width * 0.38f, center = Offset(center.x, height * 0.55f))
            drawCircle(brush = gradientBrush, radius = width * 0.24f, center = Offset(center.x, height * 0.32f))
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = width * 0.1f,
                center = Offset(width * 0.36f, height * 0.38f)
            )
        }
    }

    // Draw Special Effects (Stripes or Wrapper)
    when (candy.special) {
        SpecialType.STRIPED_HORIZONTAL -> {
            drawStripes(isHorizontal = true)
        }
        SpecialType.STRIPED_VERTICAL -> {
            drawStripes(isHorizontal = false)
        }
        SpecialType.WRAPPED -> {
            drawWrapperKnots()
        }
        else -> {}
    }
}

private fun DrawScope.drawStripes(isHorizontal: Boolean) {
    val width = size.width
    val height = size.height
    val stripeColor = Color.White.copy(alpha = 0.85f)
    val strokeWidth = 5.5f

    if (isHorizontal) {
        drawLine(stripeColor, Offset(width * 0.15f, height * 0.35f), Offset(width * 0.85f, height * 0.35f), strokeWidth)
        drawLine(stripeColor, Offset(width * 0.1f, height * 0.5f), Offset(width * 0.9f, height * 0.5f), strokeWidth)
        drawLine(stripeColor, Offset(width * 0.15f, height * 0.65f), Offset(width * 0.85f, height * 0.65f), strokeWidth)
    } else {
        drawLine(stripeColor, Offset(width * 0.35f, height * 0.15f), Offset(width * 0.35f, height * 0.85f), strokeWidth)
        drawLine(stripeColor, Offset(width * 0.5f, height * 0.1f), Offset(width * 0.5f, height * 0.9f), strokeWidth)
        drawLine(stripeColor, Offset(width * 0.65f, height * 0.15f), Offset(width * 0.65f, height * 0.85f), strokeWidth)
    }
}

private fun DrawScope.drawWrapperKnots() {
    val width = size.width
    val height = size.height
    val wrapperColor = Color.White.copy(alpha = 0.75f)

    // Candy wrapper side knots
    val leftKnot = Path().apply {
        moveTo(width * 0.18f, height * 0.5f)
        lineTo(width * 0.02f, height * 0.28f)
        lineTo(width * 0.02f, height * 0.72f)
        close()
    }
    val rightKnot = Path().apply {
        moveTo(width * 0.82f, height * 0.5f)
        lineTo(width * 0.98f, height * 0.28f)
        lineTo(width * 0.98f, height * 0.72f)
        close()
    }

    drawPath(leftKnot, color = wrapperColor)
    drawPath(rightKnot, color = wrapperColor)

    // Shimmering wrapper border
    drawCircle(
        color = Color(0xFFFFD54F).copy(alpha = 0.6f),
        radius = width * 0.42f,
        center = Offset(width / 2f, height / 2f),
        style = Stroke(width = 3f)
    )
}

private fun DrawScope.drawColorBomb(spinAngle: Float) {
    val width = size.width
    val height = size.height
    val center = Offset(width / 2f, height / 2f)

    // Dark Chocolate Ball Base
    val chocoBrush = Brush.radialGradient(
        colors = listOf(Color(0xFF5D4037), Color(0xFF3E2723), Color(0xFF1B0000)),
        center = Offset(width * 0.38f, height * 0.38f),
        radius = width * 0.45f
    )
    drawCircle(brush = chocoBrush, radius = width * 0.42f, center = center)

    // Glowing Golden rim
    drawCircle(
        brush = Brush.sweepGradient(listOf(Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red)),
        radius = width * 0.44f,
        center = center,
        style = Stroke(width = 3.5f)
    )

    // Rainbow Sprinkles
    val sprinkleColors = listOf(
        Color(0xFFFF1744), Color(0xFFFFEA00), Color(0xFF00E676),
        Color(0xFF00E5FF), Color(0xFFD500F9), Color(0xFFFF9100), Color.White
    )

    val rad = (spinAngle * PI / 180f).toFloat()
    for (i in sprinkleColors.indices) {
        val angle = rad + (i * 2 * PI / sprinkleColors.size).toFloat()
        val dist = width * 0.25f
        val sx = center.x + cos(angle) * dist
        val sy = center.y + sin(angle) * dist
        drawCircle(color = sprinkleColors[i], radius = width * 0.07f, center = Offset(sx, sy))
        drawCircle(color = Color.White.copy(alpha = 0.7f), radius = width * 0.025f, center = Offset(sx - 2, sy - 2))
    }

    // Center sprinkle
    drawCircle(color = Color.Yellow, radius = width * 0.08f, center = center)
}

private fun DrawScope.drawIngredient(special: SpecialType) {
    val width = size.width
    val height = size.height
    val center = Offset(width / 2f, height / 2f)

    if (special == SpecialType.INGREDIENT_CHERRY) {
        // Red Cherry with green stem
        val cherryBrush = Brush.radialGradient(
            colors = listOf(Color(0xFFFF5252), Color(0xFFD50000), Color(0xFF5f0000)),
            center = Offset(width * 0.38f, height * 0.48f),
            radius = width * 0.4f
        )
        // Two cherries
        drawCircle(brush = cherryBrush, radius = width * 0.26f, center = Offset(width * 0.38f, height * 0.62f))
        drawCircle(brush = cherryBrush, radius = width * 0.26f, center = Offset(width * 0.65f, height * 0.58f))

        // Specular glare
        drawCircle(Color.White.copy(alpha = 0.6f), radius = width * 0.07f, center = Offset(width * 0.32f, height * 0.52f))
        drawCircle(Color.White.copy(alpha = 0.6f), radius = width * 0.07f, center = Offset(width * 0.59f, height * 0.48f))

        // Stem & Leaf
        val stemPath = Path().apply {
            moveTo(width * 0.38f, height * 0.42f)
            cubicTo(width * 0.45f, height * 0.25f, width * 0.52f, height * 0.15f, width * 0.55f, height * 0.12f)
            moveTo(width * 0.65f, height * 0.38f)
            cubicTo(width * 0.6f, height * 0.25f, width * 0.55f, height * 0.15f, width * 0.55f, height * 0.12f)
        }
        drawPath(stemPath, color = Color(0xFF2E7D32), style = Stroke(width = 4.5f))

        // Green Leaf
        val leafPath = Path().apply {
            moveTo(width * 0.55f, height * 0.12f)
            quadraticTo(width * 0.75f, height * 0.08f, width * 0.82f, height * 0.18f)
            quadraticTo(width * 0.65f, height * 0.22f, width * 0.55f, height * 0.12f)
        }
        drawPath(leafPath, color = Color(0xFF4CAF50))
    } else {
        // Hazelnut (Chestnut / Acorn)
        val nutBrush = Brush.radialGradient(
            colors = listOf(Color(0xFFD7CCC8), Color(0xFF8D6E63), Color(0xFF4E342E)),
            center = Offset(width * 0.4f, height * 0.5f),
            radius = width * 0.45f
        )
        // Acorn Body
        val nutPath = Path().apply {
            moveTo(center.x, height * 0.88f)
            cubicTo(width * 0.85f, height * 0.75f, width * 0.82f, height * 0.45f, width * 0.75f, height * 0.38f)
            lineTo(width * 0.25f, height * 0.38f)
            cubicTo(width * 0.18f, height * 0.45f, width * 0.15f, height * 0.75f, center.x, height * 0.88f)
            close()
        }
        drawPath(nutPath, brush = nutBrush)

        // Acorn Top Cap
        val capPath = Path().apply {
            moveTo(width * 0.2f, height * 0.38f)
            cubicTo(width * 0.2f, height * 0.18f, width * 0.8f, height * 0.18f, width * 0.8f, height * 0.38f)
            close()
        }
        drawPath(capPath, color = Color(0xFF3E2723))

        // Small stem
        drawLine(Color(0xFF3E2723), Offset(center.x, height * 0.2f), Offset(center.x, height * 0.08f), 5f)
    }
}

@Composable
fun ObstacleView(obstacle: ObstacleType) {
    Canvas(modifier = Modifier.fillMaxSize().padding(2.dp)) {
        val width = size.width
        val height = size.height

        when (obstacle) {
            ObstacleType.CHOCOLATE -> {
                // Rich chocolate block
                val chocoBrush = Brush.linearGradient(
                    colors = listOf(CandyChocolateLight, CandyChocolate, CandyChocolateDark),
                    start = Offset.Zero,
                    end = Offset(width, height)
                )
                drawRoundRect(
                    brush = chocoBrush,
                    cornerRadius = CornerRadius(12f, 12f)
                )
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.2f),
                    topLeft = Offset(4f, 4f),
                    size = Size(width - 8f, height - 8f),
                    style = Stroke(width = 3f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
            }
            ObstacleType.FROSTING_1, ObstacleType.FROSTING_2 -> {
                // Frosting / Icing block
                val isHeavy = obstacle == ObstacleType.FROSTING_2
                drawRoundRect(
                    color = if (isHeavy) CandyFrosting else Color(0xAAFFFFFF),
                    cornerRadius = CornerRadius(12f, 12f)
                )
                drawRoundRect(
                    color = Color(0xFFB0BEC5),
                    style = Stroke(width = if (isHeavy) 4f else 2f),
                    cornerRadius = CornerRadius(12f, 12f)
                )
                // Crack lines for damaged frosting
                if (!isHeavy) {
                    drawLine(Color(0xFF78909C), Offset(width * 0.2f, height * 0.3f), Offset(width * 0.5f, height * 0.6f), 3f)
                    drawLine(Color(0xFF78909C), Offset(width * 0.5f, height * 0.6f), Offset(width * 0.8f, height * 0.4f), 3f)
                }
            }
            ObstacleType.LOCK_CHAIN -> {
                drawCircle(
                    color = Color(0xFFFFD54F),
                    radius = width * 0.35f,
                    style = Stroke(width = 5f)
                )
            }
            ObstacleType.NONE -> {}
        }
    }
}
