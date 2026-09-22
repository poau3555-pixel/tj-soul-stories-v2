package com.example.model

data class DailyReward(
    val day: Int,
    val title: String,
    val rewardCoins: Int,
    val rewardBooster: BoosterType? = null,
    val boosterCount: Int = 1,
    val iconEmoji: String,
    val isMysteryChest: Boolean = false
) {
    val dayNumber: Int get() = day
}

typealias DailyRewardDay = DailyReward

val DAILY_7_DAY_REWARDS = listOf(
    DailyReward(1, "Day 1: Sweet Start", rewardCoins = 100, iconEmoji = "💰"),
    DailyReward(2, "Day 2: Sugar Hammer", rewardCoins = 50, rewardBooster = BoosterType.LOLLIPOP_HAMMER, iconEmoji = "🔨"),
    DailyReward(3, "Day 3: Gold Stash", rewardCoins = 250, iconEmoji = "💰"),
    DailyReward(4, "Day 4: Free Switch", rewardCoins = 75, rewardBooster = BoosterType.FREE_SWITCH, iconEmoji = "🔄"),
    DailyReward(5, "Day 5: Rainbow Donut", rewardCoins = 100, rewardBooster = BoosterType.COLOR_BOMB_START, iconEmoji = "🍩"),
    DailyReward(6, "Day 6: Sugar Lightning", rewardCoins = 150, rewardBooster = BoosterType.SUGAR_LIGHTNING, iconEmoji = "⚡"),
    DailyReward(7, "Day 7: Royal Chest!", rewardCoins = 500, rewardBooster = BoosterType.SUGAR_LIGHTNING, boosterCount = 2, iconEmoji = "👑", isMysteryChest = true)
)

val SEVEN_DAY_REWARDS = DAILY_7_DAY_REWARDS
