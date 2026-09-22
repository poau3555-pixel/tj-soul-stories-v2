package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.PlayerProfile
import com.example.engine.SoundSynthesizer
import com.example.model.BoosterType
import com.example.model.MascotOutfit
import com.example.ui.theme.CandyBlue
import com.example.ui.theme.CandyGreen
import com.example.ui.theme.CandyOrange
import com.example.ui.theme.CandyYellow
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SurfaceCard

data class BoosterShopItem(
    val boosterType: BoosterType,
    val count: Int,
    val costCoins: Int,
    val iconEmoji: String
)

val SHOP_BOOSTER_ITEMS = listOf(
    BoosterShopItem(BoosterType.LOLLIPOP_HAMMER, 3, 220, "🔨"),
    BoosterShopItem(BoosterType.FREE_SWITCH, 3, 200, "🔄"),
    BoosterShopItem(BoosterType.COLOR_BOMB_START, 2, 280, "🍩"),
    BoosterShopItem(BoosterType.SUGAR_LIGHTNING, 2, 320, "⚡"),
    BoosterShopItem(BoosterType.EXTRA_MOVES, 3, 250, "➕")
)

@Composable
fun ShopDialog(
    profile: PlayerProfile,
    onBuyBooster: (BoosterType, count: Int, cost: Int) -> Boolean,
    onBuyOutfit: (MascotOutfit) -> Boolean,
    onEquipOutfit: (MascotOutfit) -> Unit,
    onFreeCoins: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(3.dp, Color(0xFFFFD54F), RoundedCornerShape(24.dp))
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .testTag("shop_dialog"),
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
                // Header with Coins
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🍭 Sugar Emporium",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFD54F)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF4A148C))
                            .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "💰 ${profile.coins}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F))
                    }

                    IconButton(
                        onClick = {
                            SoundSynthesizer.playClickSound()
                            onDismiss()
                        },
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tabs: 0: Boosters, 1: TJ Mascot Outfits, 2: Free Gift
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF38235E),
                    contentColor = Color(0xFFFFD54F),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFFFFD54F)
                        )
                    },
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            SoundSynthesizer.playClickSound()
                            selectedTab = 0
                        },
                        text = { Text("⚡ Boosters", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            SoundSynthesizer.playClickSound()
                            selectedTab = 1
                        },
                        text = { Text("🐻 TJ Outfits", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = {
                            SoundSynthesizer.playClickSound()
                            selectedTab = 2
                        },
                        text = { Text("🎁 Free", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                when (selectedTab) {
                    0 -> BoosterShopTab(profile = profile, onBuyBooster = onBuyBooster)
                    1 -> MascotOutfitsTab(
                        profile = profile,
                        onBuyOutfit = onBuyOutfit,
                        onEquipOutfit = onEquipOutfit
                    )
                    2 -> FreeRewardsTab(onFreeCoins = onFreeCoins)
                }
            }
        }
    }
}

@Composable
private fun BoosterShopTab(
    profile: PlayerProfile,
    onBuyBooster: (BoosterType, count: Int, cost: Int) -> Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(290.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(SHOP_BOOSTER_ITEMS) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF38235E))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(CandyYellow, CandyOrange))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.iconEmoji, fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${item.count}x ${item.boosterType.title}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = item.boosterType.description,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                val canAfford = profile.coins >= item.costCoins
                Button(
                    onClick = {
                        val success = onBuyBooster(item.boosterType, item.count, item.costCoins)
                        if (success) SoundSynthesizer.playSweetComboFanfare()
                    },
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canAfford) CandyGreen else Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("buy_booster_${item.boosterType.name.lowercase()}")
                ) {
                    Text(
                        text = "${item.costCoins} 💰",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun MascotOutfitsTab(
    profile: PlayerProfile,
    onBuyOutfit: (MascotOutfit) -> Boolean,
    onEquipOutfit: (MascotOutfit) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(290.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(MascotOutfit.values()) { outfit ->
            val isUnlocked = profile.unlockedOutfits.contains(outfit.name)
            val isEquipped = profile.currentOutfit == outfit
            val canAfford = profile.coins >= outfit.costCoins

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF38235E))
                    .border(
                        1.5.dp,
                        if (isEquipped) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.15f),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (isEquipped) Brush.radialGradient(listOf(CandyYellow, CandyOrange))
                            else Brush.radialGradient(listOf(PrimaryPink, PrimaryPurple))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = outfit.iconEmoji, fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = outfit.displayName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = outfit.description,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (isEquipped) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CandyGreen.copy(alpha = 0.3f))
                            .border(1.dp, CandyGreen, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("EQUIPPED", fontSize = 10.sp, fontWeight = FontWeight.Black, color = CandyGreen)
                    }
                } else if (isUnlocked) {
                    Button(
                        onClick = {
                            SoundSynthesizer.playClickSound()
                            onEquipOutfit(outfit)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("EQUIP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                } else {
                    Button(
                        onClick = {
                            val success = onBuyOutfit(outfit)
                            if (success) SoundSynthesizer.playVictoryFanfare()
                        },
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(containerColor = if (canAfford) CandyGreen else Color.Gray),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("${outfit.costCoins} 💰", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun FreeRewardsTab(
    onFreeCoins: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(290.dp)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🎁", fontSize = 56.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "TJ's Sweet Daily Gift!",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD54F)
        )
        Text(
            text = "Claim +200 free coins anytime you need a boost!",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                SoundSynthesizer.playSweetComboFanfare()
                onFreeCoins(200)
            },
            colors = ButtonDefaults.buttonColors(containerColor = CandyGreen),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(48.dp)
                .testTag("claim_free_coins_button")
        ) {
            Text("CLAIM 200 COINS! 💰", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
        }
    }
}
