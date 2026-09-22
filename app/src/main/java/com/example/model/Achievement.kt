package com.example.model

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val current: Int,
    val max: Int,
    val rewardCoins: Int,
    val isClaimed: Boolean = false,
    val iconEmoji: String
) {
    val target: Int get() = max
    val isUnlocked: Boolean get() = current >= max
}

val DEFAULT_ACHIEVEMENTS = listOf(
    Achievement("a1", "Sweet Beginner", "Complete your first level in Candy Town", 0, 1, 200, iconEmoji = "🌱"),
    Achievement("a2", "Sugar Explorer", "Conquer all 5 levels in Candy Town", 0, 5, 500, iconEmoji = "🗺️"),
    Achievement("a3", "Combo Master", "Trigger 10 spectacular Sugar Combos", 0, 10, 350, iconEmoji = "🔥"),
    Achievement("a4", "Candy Collector", "Create 5 Rainbow Sprinkle Core Candies", 0, 5, 400, iconEmoji = "🍬"),
    Achievement("a5", "Star Chaser", "Earn 20 Stars on the Adventure Map", 0, 20, 750, iconEmoji = "⭐"),
    Achievement("a6", "World Explorer", "Reach Episode 5: Rainbow Clouds", 0, 21, 1000, iconEmoji = "👑")
)
