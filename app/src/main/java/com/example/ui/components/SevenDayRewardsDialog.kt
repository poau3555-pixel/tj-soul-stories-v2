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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.data.PlayerProfile
import com.example.engine.SoundSynthesizer
import com.example.model.BoosterType
import com.example.model.DAILY_7_DAY_REWARDS
import com.example.model.DailyReward
import com.example.ui.theme.CandyBlue
import com.example.ui.theme.CandyGreen
import com.example.ui.theme.CandyOrange
import com.example.ui.theme.CandyYellow
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SurfaceCard

@Composable
fun SevenDayRewardsDialog(
    profile: PlayerProfile,
    onClaimReward: (day: Int, coins: Int, booster: BoosterType?, count: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
    val alreadyClaimedToday = profile.lastRewardClaimDate == todayStr

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(3.dp, Color(0xFFFFD54F), RoundedCornerShape(24.dp))
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .testTag("seven_day_rewards_dialog"),
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
                    Column {
                        Text(
                            text = "📅 7-Day Sugar Streak",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFD54F)
                        )
                        Text(
                            text = "Current Streak: Day ${profile.dailyStreak}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
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

                Spacer(modifier = Modifier.height(14.dp))

                // Days 1..6 (2 columns or 3 columns grid) + Day 7 Big Card
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(310.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(DAILY_7_DAY_REWARDS.take(6)) { reward ->
                        val isClaimed = profile.claimedRewardDays.contains(reward.day)
                        val isCurrentDay = (profile.dailyStreak == reward.day)
                        val canClaim = isCurrentDay && !alreadyClaimedToday && !isClaimed

                        DailyRewardCard(
                            reward = reward,
                            isClaimed = isClaimed,
                            isCurrentDay = isCurrentDay,
                            canClaim = canClaim,
                            onClaim = {
                                onClaimReward(reward.day, reward.rewardCoins, reward.rewardBooster, reward.boosterCount)
                            }
                        )
                    }

                    // Big Day 7 Card
                    val day7 = DAILY_7_DAY_REWARDS.last()
                    val isClaimed7 = profile.claimedRewardDays.contains(day7.day)
                    val isCurrentDay7 = (profile.dailyStreak == day7.day)
                    val canClaim7 = isCurrentDay7 && !alreadyClaimedToday && !isClaimed7

                    item(span = { GridItemSpan(3) }) {
                        BigDay7RewardCard(
                            reward = day7,
                            isClaimed = isClaimed7,
                            isCurrentDay = isCurrentDay7,
                            canClaim = canClaim7,
                            onClaim = {
                                onClaimReward(day7.day, day7.rewardCoins, day7.rewardBooster, day7.boosterCount)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyRewardCard(
    reward: DailyReward,
    isClaimed: Boolean,
    isCurrentDay: Boolean,
    canClaim: Boolean,
    onClaim: () -> Unit
) {
    val bgBrush = if (canClaim) {
        Brush.verticalGradient(listOf(Color(0xFF6A1B9A), Color(0xFF4A148C)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFF38235E), Color(0xFF38235E)))
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgBrush)
            .border(
                1.5.dp,
                if (canClaim) Color(0xFFFFD54F) else if (isClaimed) CandyGreen else Color.White.copy(alpha = 0.15f),
                RoundedCornerShape(14.dp)
            )
            .padding(8.dp)
            .clickable(enabled = canClaim, onClick = {
                SoundSynthesizer.playVictoryFanfare()
                onClaim()
            }),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Day ${reward.day}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (canClaim) Color(0xFFFFD54F) else Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = reward.iconEmoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "+${reward.rewardCoins}💰",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD54F)
        )

        if (isClaimed) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(CandyGreen)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("DONE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        } else if (canClaim) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(CandyGreen)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("CLAIM", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
        }
    }
}

@Composable
private fun BigDay7RewardCard(
    reward: DailyReward,
    isClaimed: Boolean,
    isCurrentDay: Boolean,
    canClaim: Boolean,
    onClaim: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    if (canClaim) listOf(Color(0xFFFF8F00), Color(0xFFFF6F00))
                    else listOf(Color(0xFF4A148C), Color(0xFF38235E))
                )
            )
            .border(
                2.dp,
                if (canClaim) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.2f),
                RoundedCornerShape(16.dp)
            )
            .padding(12.dp)
            .clickable(enabled = canClaim, onClick = {
                SoundSynthesizer.playVictoryFanfare()
                onClaim()
            }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "👑", fontSize = 34.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Day 7: Royal Treasure Vault!",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = "+500 Coins + 2x Sugar Lightning!",
                fontSize = 11.sp,
                color = Color(0xFFFFD54F),
                fontWeight = FontWeight.Bold
            )
        }

        if (isClaimed) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(CandyGreen)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text("CLAIMED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        } else if (canClaim) {
            Button(
                onClick = {
                    SoundSynthesizer.playVictoryFanfare()
                    onClaim()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CandyGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("claim_day_7_button")
            ) {
                Text("CLAIM!", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
        }
    }
}
