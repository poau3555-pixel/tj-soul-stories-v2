package com.example.model

data class LevelData(
    val levelNumber: Int,
    val worldName: String,
    val worldEpisode: Int,
    val moves: Int,
    val rows: Int = 8,
    val cols: Int = 8,
    val allowedColors: List<CandyColor> = listOf(
        CandyColor.RED,
        CandyColor.ORANGE,
        CandyColor.YELLOW,
        CandyColor.GREEN,
        CandyColor.BLUE,
        CandyColor.PURPLE
    ),
    val goal: LevelGoal,
    val star1Score: Int,
    val star2Score: Int,
    val star3Score: Int,
    val initialJellies: Set<Pair<Int, Int>> = emptySet(),
    val doubleJellies: Set<Pair<Int, Int>> = emptySet(),
    val initialObstacles: Map<Pair<Int, Int>, ObstacleType> = emptyMap(),
    val initialIngredients: List<Pair<Int, Int>> = emptyList(),
    val unplayableTiles: Set<Pair<Int, Int>> = emptySet()
)
