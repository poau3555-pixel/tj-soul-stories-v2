package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.LevelProgress
import com.example.data.PlayerProfile
import com.example.data.PlayerRepository
import com.example.engine.LevelRepository
import com.example.engine.SoundSynthesizer
import com.example.model.LevelData
import com.example.model.LevelGoal
import com.example.model.MascotMood
import com.example.ui.components.AchievementsDialog
import com.example.ui.components.CollectionDialog
import com.example.ui.components.DailySpinDialog
import com.example.ui.components.MissionsDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.components.SevenDayRewardsDialog
import com.example.ui.components.ShopDialog
import com.example.ui.theme.BoardBackground
import com.example.ui.theme.CandyOrange
import com.example.ui.theme.CandyRed
import com.example.ui.theme.CandyYellow
import com.example.ui.theme.GoldStar
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SurfaceCard

@Composable
fun SagaMapScreen(
    playerRepo: PlayerRepository,
    onStartLevel: (LevelData) -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile by playerRepo.playerProfile.collectAsState()
    val progressList by playerRepo.levelProgressFlow.collectAsState(initial = emptyList())
    val missions by playerRepo.missions.collectAsState()
    val achievements by playerRepo.achievements.collectAsState()

    val levels = remember { LevelRepository.getAllLevels() }
    var selectedLevelForModal by remember { mutableStateOf<LevelData?>(null) }
    var showDailySpin by remember { mutableStateOf(false) }
    var showSevenDayStreak by remember { mutableStateOf(false) }
    var showMissions by remember { mutableStateOf(false) }
    var showAchievements by remember { mutableStateOf(false) }
    var showShop by remember { mutableStateOf(false) }
    var showAlmanac by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showRefillModal by remember { mutableStateOf(false) }

    var mascotMood by remember { mutableStateOf(MascotMood.EXCITED) }
    var mascotCheerText by remember { mutableStateOf("Ready for sweet fun?") }

    val scrollState = rememberScrollState()

    LaunchedEffect(profile.musicEnabled) {
        SoundSynthesizer.setMusicEnabled(profile.musicEnabled)
        SoundSynthesizer.setSoundFxEnabled(profile.soundFxEnabled)
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
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            SagaTopBar(
                profile = profile,
                onHomeClick = {
                    SoundSynthesizer.playClickSound()
                    onBackToHome()
                },
                onSpinClick = {
                    SoundSynthesizer.playClickSound()
                    showDailySpin = true
                },
                onLivesClick = {
                    SoundSynthesizer.playClickSound()
                    showRefillModal = true
                },
                onShopClick = {
                    SoundSynthesizer.playClickSound()
                    showShop = true
                },
                onSettingsClick = {
                    SoundSynthesizer.playClickSound()
                    showSettings = true
                }
            )

            // Scrollable Map Canvas
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 90.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // World Hero Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .border(2.5.dp, Color(0xFFFFD54F), RoundedCornerShape(20.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_map_banner),
                            contentDescription = "TJ Sugar Quest Map",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                                    )
                                )
                                .padding(12.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text(
                                        text = "TJ SUGAR QUEST 🍭✨",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFFFD54F)
                                    )
                                    Text(
                                        text = "A Sweet Adventure Beyond Imagination!",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "⭐️ ${profile.totalStars} Stars",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F)
                                )
                            }
                        }
                    }

                    // Mascot Interactive Banner
                    MascotGreetingCard(
                        profile = profile,
                        cheerText = mascotCheerText,
                        onTapMascot = {
                            SoundSynthesizer.playSweetComboFanfare()
                            mascotCheerText = listOf(
                                "You've got this! ✨",
                                "Sweet Match! 🍭",
                                "Look at all those Stars! ⭐️",
                                "Let's explore Episode ${profile.highestUnlockedLevel / 5 + 1}!",
                                "Delicious combos await! 💖"
                            ).random()
                        }
                    )

                    // Level Nodes list rendered in winding S-curve
                    WindingLevelRoad(
                        levels = levels,
                        progressList = progressList,
                        onLevelClick = { level ->
                            SoundSynthesizer.playSwapSound()
                            selectedLevelForModal = level
                        }
                    )
                }
            }
        }

        // Bottom Meta Navigation Floating Dock
        BottomMetaDock(
            onOpenMissions = {
                SoundSynthesizer.playClickSound()
                showMissions = true
            },
            onOpenAchievements = {
                SoundSynthesizer.playClickSound()
                showAchievements = true
            },
            onOpenStreak = {
                SoundSynthesizer.playClickSound()
                showSevenDayStreak = true
            },
            onOpenShop = {
                SoundSynthesizer.playClickSound()
                showShop = true
            },
            onOpenAlmanac = {
                SoundSynthesizer.playClickSound()
                showAlmanac = true
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Level Details Modal
        selectedLevelForModal?.let { level ->
            LevelPreviewDialog(
                levelData = level,
                progress = progressList.find { it.levelNumber == level.levelNumber },
                profile = profile,
                onPlay = {
                    val hasLife = playerRepo.consumeLife()
                    if (hasLife) {
                        selectedLevelForModal = null
                        onStartLevel(level)
                    } else {
                        showRefillModal = true
                    }
                },
                onDismiss = { selectedLevelForModal = null }
            )
        }

        // Daily Spin Wheel
        if (showDailySpin) {
            DailySpinDialog(
                onClaimReward = { coins, booster ->
                    playerRepo.claimDailySpinReward(coins, booster)
                },
                onDismiss = { showDailySpin = false }
            )
        }

        // 7-Day Rewards Streak
        if (showSevenDayStreak) {
            SevenDayRewardsDialog(
                profile = profile,
                onClaimReward = { day, coins, booster, count ->
                    playerRepo.claimDailyRewardDay(day, coins, booster, count)
                },
                onDismiss = { showSevenDayStreak = false }
            )
        }

        // Missions Dialog
        if (showMissions) {
            MissionsDialog(
                missions = missions,
                onClaimMission = { id -> playerRepo.claimMission(id) },
                onDismiss = { showMissions = false }
            )
        }

        // Achievements Dialog
        if (showAchievements) {
            AchievementsDialog(
                achievements = achievements,
                onClaimAchievement = { id -> playerRepo.claimAchievement(id) },
                onDismiss = { showAchievements = false }
            )
        }

        // Shop Dialog
        if (showShop) {
            ShopDialog(
                profile = profile,
                onBuyBooster = { type, count, cost -> playerRepo.buyBoosterPack(type, count, cost) },
                onBuyOutfit = { outfit -> playerRepo.buyOutfit(outfit) },
                onEquipOutfit = { outfit -> playerRepo.equipOutfit(outfit) },
                onFreeCoins = { coins -> playerRepo.addCoins(coins) },
                onDismiss = { showShop = false }
            )
        }

        // Almanac Collection Dialog
        if (showAlmanac) {
            CollectionDialog(
                unlockedLevel = profile.highestUnlockedLevel,
                onDismiss = { showAlmanac = false }
            )
        }

        // Settings Dialog
        if (showSettings) {
            SettingsDialog(
                musicEnabled = profile.musicEnabled,
                sfxEnabled = profile.soundFxEnabled,
                onToggleMusic = { playerRepo.toggleMusic() },
                onToggleSfx = { playerRepo.toggleSoundFx() },
                onResetProgress = { playerRepo.resetAllProgress() },
                onDismiss = { showSettings = false }
            )
        }

        // Refill Lives Modal
        if (showRefillModal) {
            RefillLivesDialog(
                profile = profile,
                onRefill = {
                    playerRepo.refillLives()
                    showRefillModal = false
                },
                onDismiss = { showRefillModal = false }
            )
        }
    }
}

@Composable
fun SagaTopBar(
    profile: PlayerProfile,
    onHomeClick: () -> Unit,
    onSpinClick: () -> Unit,
    onLivesClick: () -> Unit,
    onShopClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceCard.copy(alpha = 0.95f))
            .border(1.dp, Color(0xFF6A1B9A))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home Navigation Button
        IconButton(
            onClick = onHomeClick,
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFF4A148C))
                .border(1.5.dp, Color(0xFFFFD54F), CircleShape)
                .testTag("map_back_home_button")
        ) {
            Text("🏠", fontSize = 16.sp)
        }

        // Lives counter pill
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF2C194D))
                .border(1.5.dp, CandyRed, RoundedCornerShape(16.dp))
                .clickable(onClick = onLivesClick)
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .testTag("lives_indicator"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Favorite, contentDescription = "Lives", tint = CandyRed, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${profile.lives}/${profile.maxLives}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(text = "+", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFF00E676))
        }

        // Coins balance pill
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF2C194D))
                .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(16.dp))
                .clickable(onClick = onShopClick)
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .testTag("coins_indicator"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "💰", fontSize = 13.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${profile.coins}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFD54F)
            )
        }

        // Daily Spin Button
        IconButton(
            onClick = onSpinClick,
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF9800))))
                .border(1.5.dp, Color.White, CircleShape)
                .testTag("daily_spin_button")
        ) {
            Text("🎡", fontSize = 16.sp)
        }

        // Shop Button
        IconButton(
            onClick = onShopClick,
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFFE91E63))
                .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                .testTag("top_shop_button")
        ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = "Shop", tint = Color.White, modifier = Modifier.size(17.dp))
        }

        // Settings Button
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFF2C194D))
                .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                .testTag("settings_button")
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier.size(17.dp)
            )
        }
    }
}

@Composable
private fun MascotGreetingCard(
    profile: PlayerProfile,
    cheerText: String,
    onTapMascot: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF4A148C), Color(0xFF6A1B9A))
                )
            )
            .border(1.5.dp, Color(0xFFFFD54F).copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .clickable(onClick = onTapMascot)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFFFFD54F), CandyOrange)))
                .border(1.5.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = profile.currentOutfit.iconEmoji, fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "TJ Bear (${profile.currentOutfit.displayName})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD54F)
            )
            Text(
                text = cheerText,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Text(text = "💬", fontSize = 18.sp)
    }
}

@Composable
fun BottomMetaDock(
    onOpenMissions: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenStreak: () -> Unit,
    onOpenShop: () -> Unit,
    onOpenAlmanac: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF1E1035).copy(alpha = 0.95f))
            .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(22.dp))
            .shadow(12.dp, RoundedCornerShape(22.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MetaDockItem(label = "Missions", iconEmoji = "📜", onClick = onOpenMissions, tag = "dock_missions")
        MetaDockItem(label = "Badges", iconEmoji = "🏆", onClick = onOpenAchievements, tag = "dock_achievements")
        MetaDockItem(label = "7-Day", iconEmoji = "📅", onClick = onOpenStreak, tag = "dock_streak")
        MetaDockItem(label = "Shop", iconEmoji = "🍭", onClick = onOpenShop, tag = "dock_shop")
        MetaDockItem(label = "Almanac", iconEmoji = "📖", onClick = onOpenAlmanac, tag = "dock_almanac")
    }
}

@Composable
private fun MetaDockItem(
    label: String,
    iconEmoji: String,
    onClick: () -> Unit,
    tag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .testTag(tag)
    ) {
        Text(text = iconEmoji, fontSize = 20.sp)
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun WindingLevelRoad(
    levels: List<LevelData>,
    progressList: List<LevelProgress>,
    onLevelClick: (LevelData) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "player_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val worlds = LevelRepository.worlds

        for (world in worlds) {
            // World Episode Header Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(PrimaryPurple, PrimaryPink, PrimaryPurple)
                        )
                    )
                    .border(2.dp, Color(0xFFFFD54F), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "EPISODE ${world.id}: ${world.name.uppercase()}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFD54F)
                        )
                        Text(
                            text = world.description,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                    Text(
                        text = when (world.id) {
                            1 -> "🍬"
                            2 -> "🍋"
                            3 -> "🍫"
                            4 -> "🍪"
                            else -> "🌈"
                        },
                        fontSize = 24.sp
                    )
                }
            }

            // Render Levels for this world
            val worldLevels = levels.filter { it.levelNumber in world.levelRange }
            for ((index, level) in worldLevels.withIndex()) {
                val prog = progressList.find { it.levelNumber == level.levelNumber }
                val isUnlocked = prog?.isUnlocked == true || level.levelNumber == 1
                val isCompleted = prog?.isCompleted == true
                val stars = prog?.stars ?: 0

                val horizontalOffset = when (index % 4) {
                    0 -> (-75).dp
                    1 -> (-20).dp
                    2 -> 75.dp
                    3 -> 20.dp
                    else -> 0.dp
                }

                Box(
                    modifier = Modifier
                        .offset(x = horizontalOffset)
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LevelNodeButton(
                        level = level,
                        isUnlocked = isUnlocked,
                        isCompleted = isCompleted,
                        stars = stars,
                        pulseScale = pulseScale,
                        onClick = {
                            if (isUnlocked) onLevelClick(level)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LevelNodeButton(
    level: LevelData,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    stars: Int,
    pulseScale: Float,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .scale(if (isUnlocked && !isCompleted) pulseScale else 1f)
                .shadow(if (isUnlocked) 10.dp else 2.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> Brush.radialGradient(listOf(Color(0xFF00E676), Color(0xFF1B5E20)))
                        isUnlocked -> Brush.radialGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF9800)))
                        else -> Brush.radialGradient(listOf(Color(0xFF757575), Color(0xFF424242)))
                    }
                )
                .border(
                    width = if (isUnlocked) 2.5.dp else 1.5.dp,
                    color = if (isUnlocked) Color.White else Color.Gray,
                    shape = CircleShape
                )
                .clickable(enabled = isUnlocked, onClick = onClick)
                .testTag("level_node_${level.levelNumber}"),
            contentAlignment = Alignment.Center
        ) {
            if (isUnlocked) {
                Text(
                    text = "${level.levelNumber}",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isCompleted) Color.White else Color(0xFF3E2723)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color.LightGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Star Rating below node
        if (isCompleted) {
            Row(modifier = Modifier.padding(top = 2.dp)) {
                for (s in 1..3) {
                    Icon(
                        imageVector = if (s <= stars) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Star $s",
                        tint = if (s <= stars) GoldStar else Color.Gray,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LevelPreviewDialog(
    levelData: LevelData,
    progress: LevelProgress?,
    profile: PlayerProfile,
    onPlay: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BoardBackground),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.5.dp, Color(0xFFFFD54F), RoundedCornerShape(24.dp))
                .shadow(20.dp, RoundedCornerShape(24.dp))
                .testTag("level_preview_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "LEVEL ${levelData.levelNumber}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD54F)
                )
                Text(
                    text = "${levelData.worldName} (Episode ${levelData.worldEpisode})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Goal Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceCard)
                        .padding(14.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "LEVEL OBJECTIVE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (val g = levelData.goal) {
                                is LevelGoal.TargetScore -> "Score ${g.targetScore} points in ${levelData.moves} moves"
                                is LevelGoal.ClearJelly -> "Clear all ${g.totalJellies} jellies in ${levelData.moves} moves"
                                is LevelGoal.DropIngredients -> "Drop all ${g.cherries + g.hazelnuts} ingredients to the bottom"
                                is LevelGoal.CollectOrders -> "Collect required candy orders in ${levelData.moves} moves"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Moves Allowed: ${levelData.moves} moves",
                            fontSize = 12.sp,
                            color = Color(0xFF00E676)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // High score info
                if (progress?.highScore != null && progress.highScore > 0) {
                    Text(
                        text = "High Score: ${progress.highScore} (⭐️ ${progress.stars}/3)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Play Button
                Button(
                    onClick = onPlay,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("play_level_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PLAY (1 ❤️)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Cancel", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun RefillLivesDialog(
    profile: PlayerProfile,
    onRefill: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BoardBackground),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.5.dp, CandyRed, RoundedCornerShape(24.dp))
                .shadow(20.dp, RoundedCornerShape(24.dp))
                .testTag("refill_lives_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "NEED MORE LIVES? ❤️",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = CandyRed
                )
                Text(
                    text = "Current Lives: ${profile.lives}/5",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onRefill,
                    enabled = profile.coins >= 100,
                    colors = ButtonDefaults.buttonColors(containerColor = CandyOrange),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("refill_coins_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "REFILL TO FULL (100 💰)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
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
