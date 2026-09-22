package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.PlayerProfile
import com.example.data.PlayerRepository
import com.example.engine.LevelRepository
import com.example.engine.SoundSynthesizer
import com.example.model.LevelData
import com.example.ui.components.AchievementsDialog
import com.example.ui.components.CollectionDialog
import com.example.ui.components.DailySpinDialog
import com.example.ui.components.MissionsDialog
import com.example.ui.components.RewardedAdDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.components.SevenDayRewardsDialog
import com.example.ui.components.ShopDialog
import com.example.ui.theme.CandyGreen
import com.example.ui.theme.CandyRed
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.SurfaceCard

@Composable
fun HomeScreen(
    playerRepo: PlayerRepository,
    onNavigateToMap: () -> Unit,
    onStartLevel: (LevelData) -> Unit,
    modifier: Modifier = Modifier
) {
    val profile by playerRepo.playerProfile.collectAsState()
    val missions by playerRepo.missions.collectAsState()
    val achievements by playerRepo.achievements.collectAsState()

    // Dialog controllers
    var showDailyReward by remember { mutableStateOf(false) }
    var showDailySpin by remember { mutableStateOf(false) }
    var showMissions by remember { mutableStateOf(false) }
    var showShop by remember { mutableStateOf(false) }
    var showCollection by remember { mutableStateOf(false) }
    var showAchievements by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showRefillModal by remember { mutableStateOf(false) }
    var showRewardedAd by remember { mutableStateOf(false) }

    // Floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "home_anim")
    val cloudOffset by infiniteTransition.animateFloat(
        initialValue = -25f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloud"
    )
    val mascotBob by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot"
    )
    val playPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "play_pulse"
    )

    // Current unlocked level
    val currentLevelNum = profile.highestUnlockedLevel
    val currentLevelData = remember(currentLevelNum) {
        LevelRepository.getLevel(currentLevelNum) ?: LevelRepository.getLevel(1)!!
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF311B92),
                        Color(0xFF4A148C),
                        Color(0xFF880E4F),
                        Color(0xFF2C194D)
                    )
                )
            )
    ) {
        // Magical Candy Background Graphic Elements Canvas
        MagicalKingdomBackground(cloudOffset = cloudOffset)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP BAR: Lives, Coins, Rewards/Boosters, Sound Button
            HomeTopBar(
                profile = profile,
                onLivesClick = {
                    SoundSynthesizer.playClickSound()
                    showRefillModal = true
                },
                onShopClick = {
                    SoundSynthesizer.playClickSound()
                    showShop = true
                },
                onSpinClick = {
                    SoundSynthesizer.playClickSound()
                    showDailySpin = true
                },
                onSoundToggle = {
                    SoundSynthesizer.playClickSound()
                    playerRepo.toggleSoundFx()
                    playerRepo.toggleMusic()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // MASCOT & SPEECH BUBBLE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Mascot Speech Bubble
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(2.dp, Color(0xFFFFD54F), RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp))
                    ) {
                        Text(
                            text = "✨ Pip: Ready to blast sweet candies in Level $currentLevelNum?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF4A148C)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Mascot Character Display
                    Box(
                        modifier = Modifier
                            .offset(y = mascotBob.dp)
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFFFFD54F), PrimaryPink)
                                )
                            )
                            .border(3.dp, Color.White, CircleShape)
                            .shadow(12.dp, CircleShape)
                            .clickable {
                                SoundSynthesizer.playSweetComboFanfare()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_mascot_pip),
                            contentDescription = "Pip Mascot",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // CENTER LOGO: TJ SUGAR QUEST & TAGLINE
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "🍬 TJ SUGAR QUEST 🍬",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD54F),
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0xFF1E1035),
                            offset = Offset(3f, 4f),
                            blurRadius = 6f
                        )
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "MATCH • BLAST • EXPLORE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.5.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(1f, 2f),
                            blurRadius = 4f
                        )
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // MAIN CALL-TO-ACTION: PLAY BUTTON (Large & Pulsing)
            Button(
                onClick = {
                    SoundSynthesizer.playClickSound()
                    val hasLife = playerRepo.consumeLife()
                    if (hasLife) {
                        onStartLevel(currentLevelData)
                    } else {
                        showRefillModal = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .scale(playPulse)
                    .fillMaxWidth(0.85f)
                    .height(64.dp)
                    .border(3.dp, Color(0xFFFFD54F), RoundedCornerShape(22.dp))
                    .shadow(16.dp, RoundedCornerShape(22.dp))
                    .testTag("home_play_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "PLAY LEVEL $currentLevelNum",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "${currentLevelData.worldName} • ${currentLevelData.moves} Moves",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // MAP BUTTON (Adventure Saga Map)
            Button(
                onClick = {
                    SoundSynthesizer.playClickSound()
                    onNavigateToMap()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(50.dp)
                    .border(2.dp, Color(0xFFFFD54F).copy(alpha = 0.8f), RoundedCornerShape(18.dp))
                    .shadow(10.dp, RoundedCornerShape(18.dp))
                    .testTag("home_map_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🗺️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ADVENTURE MAP",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // FEATURE NAVIGATION GRID (DAILY REWARD, MISSIONS, SHOP, COLLECTION, ACHIEVEMENTS, SETTINGS)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Row 1: Daily Reward & Missions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HomeMenuCard(
                        title = "DAILY REWARD",
                        icon = "📅",
                        badge = "7-Day",
                        color = Color(0xFF4A148C),
                        onClick = {
                            SoundSynthesizer.playClickSound()
                            showDailyReward = true
                        },
                        modifier = Modifier.weight(1f)
                    )
                    HomeMenuCard(
                        title = "MISSIONS",
                        icon = "📜",
                        badge = "Daily",
                        color = Color(0xFF2E7D32),
                        onClick = {
                            SoundSynthesizer.playClickSound()
                            showMissions = true
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: Shop & Collection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HomeMenuCard(
                        title = "SHOP",
                        icon = "🍭",
                        badge = "Boosters",
                        color = Color(0xFFC2185B),
                        onClick = {
                            SoundSynthesizer.playClickSound()
                            showShop = true
                        },
                        modifier = Modifier.weight(1f)
                    )
                    HomeMenuCard(
                        title = "COLLECTION",
                        icon = "📖",
                        badge = "Almanac",
                        color = Color(0xFF1565C0),
                        onClick = {
                            SoundSynthesizer.playClickSound()
                            showCollection = true
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 3: Achievements & Settings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HomeMenuCard(
                        title = "ACHIEVEMENTS",
                        icon = "🏆",
                        badge = "Bounties",
                        color = Color(0xFFE65100),
                        onClick = {
                            SoundSynthesizer.playClickSound()
                            showAchievements = true
                        },
                        modifier = Modifier.weight(1f)
                    )
                    HomeMenuCard(
                        title = "SETTINGS",
                        icon = "⚙️",
                        badge = "Options",
                        color = Color(0xFF37474F),
                        onClick = {
                            SoundSynthesizer.playClickSound()
                            showSettings = true
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Rewarded Ad Free Coins Banner Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF00796B), Color(0xFF004D40))
                            )
                        )
                        .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(16.dp))
                        .clickable {
                            SoundSynthesizer.playClickSound()
                            showRewardedAd = true
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📺", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Watch Ad for Free Coins",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "Get +150 Free Coins 💰",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(CandyGreen)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text("WATCH", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // DIALOGS
    if (showDailyReward) {
        SevenDayRewardsDialog(
            profile = profile,
            onClaimReward = { day, coins, booster, count ->
                playerRepo.claimDailyRewardDay(day, coins, booster, count)
                SoundSynthesizer.playVictoryFanfare()
            },
            onDismiss = { showDailyReward = false }
        )
    }

    if (showDailySpin) {
        DailySpinDialog(
            onClaimReward = { coins, booster ->
                playerRepo.claimDailySpinReward(coins, booster)
            },
            onDismiss = { showDailySpin = false }
        )
    }

    if (showMissions) {
        MissionsDialog(
            missions = missions,
            onClaimMission = { id ->
                playerRepo.claimMission(id)
                SoundSynthesizer.playVictoryFanfare()
            },
            onDismiss = { showMissions = false }
        )
    }

    if (showShop) {
        ShopDialog(
            profile = profile,
            onBuyBooster = { type, count, cost ->
                playerRepo.buyBoosterPack(type, count, cost)
            },
            onBuyOutfit = { outfit ->
                playerRepo.buyOutfit(outfit)
            },
            onEquipOutfit = { outfit ->
                playerRepo.equipOutfit(outfit)
            },
            onFreeCoins = { count ->
                playerRepo.addCoins(count)
            },
            onDismiss = { showShop = false }
        )
    }

    if (showCollection) {
        CollectionDialog(
            unlockedLevel = profile.highestUnlockedLevel,
            onDismiss = { showCollection = false }
        )
    }

    if (showAchievements) {
        AchievementsDialog(
            achievements = achievements,
            onClaimAchievement = { id ->
                playerRepo.claimAchievement(id)
                SoundSynthesizer.playVictoryFanfare()
            },
            onDismiss = { showAchievements = false }
        )
    }

    if (showSettings) {
        SettingsDialog(
            musicEnabled = profile.musicEnabled,
            sfxEnabled = profile.soundFxEnabled,
            onToggleMusic = {
                playerRepo.toggleMusic()
            },
            onToggleSfx = {
                playerRepo.toggleSoundFx()
            },
            onResetProgress = {
                playerRepo.resetAllProgress()
            },
            onDismiss = { showSettings = false }
        )
    }

    if (showRewardedAd) {
        RewardedAdDialog(
            rewardDescription = "+150 Free Coins 💰",
            onRewardEarned = {
                playerRepo.addCoins(150)
            },
            onDismiss = { showRewardedAd = false }
        )
    }

    if (showRefillModal) {
        HomeLivesRefillDialog(
            coins = profile.coins,
            onRefill = {
                playerRepo.refillLives()
                SoundSynthesizer.playVictoryFanfare()
                showRefillModal = false
            },
            onWatchAd = {
                showRefillModal = false
                showRewardedAd = true
            },
            onDismiss = { showRefillModal = false }
        )
    }
}

@Composable
private fun HomeTopBar(
    profile: PlayerProfile,
    onLivesClick: () -> Unit,
    onShopClick: () -> Unit,
    onSpinClick: () -> Unit,
    onSoundToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Lives
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF38235E))
                .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(16.dp))
                .clickable { onLivesClick() }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Lives",
                    tint = CandyRed,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${profile.lives}/${profile.maxLives}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }

        // Coins
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF38235E))
                .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(16.dp))
                .clickable { onShopClick() }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🪙", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${profile.coins}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD54F)
                )
            }
        }

        // Daily Spin / Wheel Booster
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF38235E))
                .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(16.dp))
                .clickable { onSpinClick() }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "⚡", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "SPIN",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD54F)
                )
            }
        }

        // Sound Toggle
        IconButton(
            onClick = onSoundToggle,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFF38235E))
                .border(1.5.dp, Color(0xFFFFD54F), CircleShape)
        ) {
            Text(
                text = if (profile.soundFxEnabled) "🔊" else "🔇",
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun HomeMenuCard(
    title: String,
    icon: String,
    badge: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = modifier
            .height(72.dp)
            .border(1.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeLivesRefillDialog(
    coins: Int,
    onRefill: () -> Unit,
    onWatchAd: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(3.dp, CandyRed, RoundedCornerShape(24.dp))
                .shadow(16.dp, RoundedCornerShape(24.dp)),
            color = SurfaceCard
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2C1B4D))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "❤️", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Refill Lives",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "Restore 5 full lives to keep the adventure going!",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onRefill,
                    enabled = coins >= 100,
                    colors = ButtonDefaults.buttonColors(containerColor = CandyGreen),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("REFILL FOR 100 💰", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onWatchAd,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📺 Watch Ad for Free", fontSize = 12.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", color = Color.White.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@Composable
private fun MagicalKingdomBackground(cloudOffset: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Floating Clouds
        drawCircle(
            color = Color.White.copy(alpha = 0.08f),
            radius = 70f,
            center = Offset(width * 0.2f + cloudOffset, height * 0.15f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.08f),
            radius = 110f,
            center = Offset(width * 0.85f - cloudOffset, height * 0.35f)
        )

        // Candy Trees & Lollipop Hills silhouettes
        val hillPath = Path().apply {
            moveTo(0f, height * 0.82f)
            cubicTo(
                width * 0.3f, height * 0.74f,
                width * 0.7f, height * 0.88f,
                width, height * 0.80f
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = hillPath,
            brush = Brush.verticalGradient(
                listOf(Color(0xFF512DA8).copy(alpha = 0.4f), Color(0xFF311B92).copy(alpha = 0.8f))
            ),
            style = Fill
        )
    }
}
