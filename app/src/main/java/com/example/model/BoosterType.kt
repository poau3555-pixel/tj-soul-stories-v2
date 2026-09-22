package com.example.model

sealed class LevelGoal {
    data class TargetScore(val targetScore: Int) : LevelGoal()
    data class ClearJelly(val totalJellies: Int) : LevelGoal()
    data class DropIngredients(val cherries: Int, val hazelnuts: Int) : LevelGoal()
    data class CollectOrders(val colorOrders: Map<CandyColor, Int>, val stripedNeeded: Int = 0) : LevelGoal()
}

enum class BoosterType(
    val title: String,
    val description: String,
    val costCoins: Int
) {
    LOLLIPOP_HAMMER("Lollipop Hammer", "Smash any candy or obstacle instantly!", 100),
    FREE_SWITCH("Free Switch", "Swap any two adjacent candies without losing a move!", 80),
    COLOR_BOMB_START("Rainbow Donut", "Start level with a powerful Rainbow Sprinkle Bomb!", 150),
    STRIPED_WRAPPED_START("Sweet Combo", "Start level with Striped and Wrapped candies!", 120),
    SUGAR_LIGHTNING("Sugar Lightning", "Zap and clear an entire row and column at once!", 140),
    EXTRA_MOVES("+5 Moves", "Add 5 extra moves when you run out of moves!", 90)
}
