package com.example.ui.components

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.engine.SoundSynthesizer
import com.example.model.ALL_COLLECTION_ITEMS
import com.example.model.CollectionCategory
import com.example.model.CollectionItem
import com.example.ui.theme.CandyBlue
import com.example.ui.theme.CandyOrange
import com.example.ui.theme.CandyYellow
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SurfaceCard

@Composable
fun CollectionDialog(
    unlockedLevel: Int,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(3.dp, Color(0xFFFFD54F), RoundedCornerShape(24.dp))
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .testTag("collection_dialog"),
            color = SurfaceCard
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF2C1B4D), Color(0xFF1E1035))
                        )
                    )
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📖 Sugar Almanac",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFD54F)
                    )
                    IconButton(
                        onClick = {
                            SoundSynthesizer.playClickSound()
                            onDismiss()
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Text(
                    text = "Discover all magical treats across the 5 Sweet Worlds!",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 4.dp, bottom = 14.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ALL_COLLECTION_ITEMS) { item ->
                        val isUnlocked = (unlockedLevel >= item.unlockWorld)
                        CollectionItemCard(item = item, isUnlocked = isUnlocked)
                    }
                }
            }
        }
    }
}

@Composable
private fun CollectionItemCard(
    item: CollectionItem,
    isUnlocked: Boolean
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF38235E))
            .border(
                1.5.dp,
                if (isUnlocked) Color(0xFFFFD54F).copy(alpha = 0.6f) else Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(14.dp)
            )
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                    if (isUnlocked) Brush.radialGradient(listOf(Color(0xFFFFD54F), CandyOrange))
                    else Brush.radialGradient(listOf(Color(0xFF3A3A50), Color(0xFF1E1E28)))
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isUnlocked) item.iconEmoji else "❓",
                fontSize = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (isUnlocked) item.name else "Locked Candy",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isUnlocked) Color.White else Color.Gray,
            textAlign = TextAlign.Center
        )

        Text(
            text = if (isUnlocked) item.description else "Reach Level ${item.unlockWorld} to unlock",
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            lineHeight = 12.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
