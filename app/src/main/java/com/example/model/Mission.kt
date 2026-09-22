package com.example.model

data class Mission(
    val id: String,
    val title: String,
    val description: String,
    val currentProgress: Int,
    val targetProgress: Int,
    val rewardCoins: Int,
    val isClaimed: Boolean = false,
    val iconEmoji: String
) {
    val isComplete: Boolean get() = currentProgress >= targetProgress
}

val DEFAULT_MISSIONS = listOf(
    Mission("m1", "Red Matcher", "Match 50 red candies", currentProgress = 0, targetProgress = 50, rewardCoins = 150, iconEmoji = "🔴"),
    Mission("m2", "Level Conqueror", "Complete 3 levels", currentProgress = 0, targetProgress = 3, rewardCoins = 200, iconEmoji = "🎯"),
    Mission("m3", "Special Artisan", "Create 5 special candies", currentProgress = 0, targetProgress = 5, rewardCoins = 250, iconEmoji = "⚡"),
    Mission("m4", "Combo Master", "Make 3 large combos", currentProgress = 0, targetProgress = 3, rewardCoins = 300, iconEmoji = "🔥"),
    Mission("m5", "Jelly Sweeper", "Clear 50 jelly tiles", currentProgress = 0, targetProgress = 50, rewardCoins = 400, iconEmoji = "🍧")
)
