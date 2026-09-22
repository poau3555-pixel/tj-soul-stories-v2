package com.example.engine

import com.example.model.CandyColor
import com.example.model.LevelData
import com.example.model.LevelGoal
import com.example.model.ObstacleType

object LevelRepository {

    val worlds = listOf(
        WorldInfo(1, "Candy Town", 1..5, "Learn the sweet basics & master your first challenges!"),
        WorldInfo(2, "Lemon Lake", 6..10, "Clear sticky obstacles across the sparkling lake!"),
        WorldInfo(3, "Chocolate Mountain", 11..15, "Conquer chocolate-covered challenges!"),
        WorldInfo(4, "Cookie Forest", 16..20, "Explore a crunchy world full of surprises!"),
        WorldInfo(5, "Rainbow Clouds", 21..25, "Reach the sweetest heights!")
    )

    private val levelDefinitions: List<LevelData> by lazy {
        listOf(
            // LEVEL 1: Welcome to Candy Town! Simple target score
            LevelData(
                levelNumber = 1,
                worldName = "Candy Town",
                worldEpisode = 1,
                moves = 18,
                rows = 7,
                cols = 7,
                allowedColors = listOf(CandyColor.RED, CandyColor.YELLOW, CandyColor.BLUE, CandyColor.GREEN),
                goal = LevelGoal.TargetScore(targetScore = 1500),
                star1Score = 1500,
                star2Score = 3000,
                star3Score = 5000
            ),
            // LEVEL 2: Introduction to Special Candies
            LevelData(
                levelNumber = 2,
                worldName = "Candy Town",
                worldEpisode = 1,
                moves = 20,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.YELLOW, CandyColor.BLUE, CandyColor.GREEN, CandyColor.ORANGE),
                goal = LevelGoal.TargetScore(targetScore = 3500),
                star1Score = 3500,
                star2Score = 6000,
                star3Score = 9000
            ),
            // LEVEL 3: Order Match: Collect Candies
            LevelData(
                levelNumber = 3,
                worldName = "Candy Town",
                worldEpisode = 1,
                moves = 22,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.YELLOW, CandyColor.BLUE, CandyColor.GREEN, CandyColor.PURPLE),
                goal = LevelGoal.CollectOrders(
                    colorOrders = mapOf(CandyColor.RED to 15, CandyColor.BLUE to 15),
                    stripedNeeded = 2
                ),
                star1Score = 4000,
                star2Score = 8000,
                star3Score = 12000
            ),
            // LEVEL 4: High Score Challenge
            LevelData(
                levelNumber = 4,
                worldName = "Candy Town",
                worldEpisode = 1,
                moves = 24,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.ORANGE, CandyColor.YELLOW, CandyColor.GREEN, CandyColor.BLUE, CandyColor.PURPLE),
                goal = LevelGoal.TargetScore(targetScore = 7000),
                star1Score = 7000,
                star2Score = 12000,
                star3Score = 18000
            ),
            // LEVEL 5: Candy Town Grand Finale
            LevelData(
                levelNumber = 5,
                worldName = "Candy Town",
                worldEpisode = 1,
                moves = 25,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.ORANGE, CandyColor.YELLOW, CandyColor.GREEN, CandyColor.BLUE),
                goal = LevelGoal.CollectOrders(
                    colorOrders = mapOf(CandyColor.PURPLE to 0, CandyColor.YELLOW to 20, CandyColor.GREEN to 20),
                    stripedNeeded = 3
                ),
                star1Score = 8000,
                star2Score = 14000,
                star3Score = 20000
            ),

            // WORLD 2: LEMON LAKE (Jelly levels!)
            // LEVEL 6: First Jelly Level!
            LevelData(
                levelNumber = 6,
                worldName = "Lemon Lake",
                worldEpisode = 2,
                moves = 22,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.ORANGE, CandyColor.YELLOW, CandyColor.GREEN, CandyColor.BLUE),
                goal = LevelGoal.ClearJelly(totalJellies = 16),
                star1Score = 5000,
                star2Score = 9000,
                star3Score = 15000,
                initialJellies = (2..5).flatMap { r -> (2..5).map { c -> Pair(r, c) } }.toSet()
            ),
            // LEVEL 7: Double Jelly Island
            LevelData(
                levelNumber = 7,
                worldName = "Lemon Lake",
                worldEpisode = 2,
                moves = 24,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.ORANGE, CandyColor.YELLOW, CandyColor.BLUE, CandyColor.PURPLE),
                goal = LevelGoal.ClearJelly(totalJellies = 24),
                star1Score = 6000,
                star2Score = 12000,
                star3Score = 18000,
                initialJellies = (1..6).flatMap { r -> listOf(Pair(r, 1), Pair(r, 6)) }.toSet(),
                doubleJellies = (2..5).flatMap { r -> (3..4).map { c -> Pair(r, c) } }.toSet()
            ),
            // LEVEL 8: Heart-shaped Jelly Board
            LevelData(
                levelNumber = 8,
                worldName = "Lemon Lake",
                worldEpisode = 2,
                moves = 25,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.YELLOW, CandyColor.GREEN, CandyColor.BLUE, CandyColor.PURPLE),
                goal = LevelGoal.ClearJelly(totalJellies = 28),
                star1Score = 7500,
                star2Score = 14000,
                star3Score = 22000,
                initialJellies = (1..6).flatMap { r -> (1..6).map { c -> Pair(r, c) } }.toSet()
            ),
            // LEVEL 9: Full Board Jelly Sweep
            LevelData(
                levelNumber = 9,
                worldName = "Lemon Lake",
                worldEpisode = 2,
                moves = 28,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.ORANGE, CandyColor.YELLOW, CandyColor.GREEN, CandyColor.BLUE),
                goal = LevelGoal.ClearJelly(totalJellies = 36),
                star1Score = 9000,
                star2Score = 17000,
                star3Score = 26000,
                initialJellies = (1..6).flatMap { r -> (1..6).map { c -> Pair(r, c) } }.toSet(),
                doubleJellies = (2..5).flatMap { r -> (2..5).map { c -> Pair(r, c) } }.toSet()
            ),
            // LEVEL 10: Lemon Lake Boss Challenge
            LevelData(
                levelNumber = 10,
                worldName = "Lemon Lake",
                worldEpisode = 2,
                moves = 30,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.ORANGE, CandyColor.YELLOW, CandyColor.GREEN, CandyColor.BLUE, CandyColor.PURPLE),
                goal = LevelGoal.ClearJelly(totalJellies = 40),
                star1Score = 12000,
                star2Score = 22000,
                star3Score = 32000,
                initialJellies = (0..7).flatMap { r -> (0..7).filter { (r + it) % 2 == 0 }.map { c -> Pair(r, c) } }.toSet()
            ),

            // WORLD 3: CHOCOLATE MOUNTAIN (Obstacles & Frosting)
            // LEVEL 11: Frosting Barrier
            LevelData(
                levelNumber = 11,
                worldName = "Chocolate Mountain",
                worldEpisode = 3,
                moves = 24,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.ORANGE, CandyColor.YELLOW, CandyColor.GREEN, CandyColor.BLUE),
                goal = LevelGoal.TargetScore(targetScore = 10000),
                star1Score = 10000,
                star2Score = 18000,
                star3Score = 28000,
                initialObstacles = mapOf(
                    Pair(3, 2) to ObstacleType.FROSTING_1,
                    Pair(3, 3) to ObstacleType.FROSTING_1,
                    Pair(3, 4) to ObstacleType.FROSTING_1,
                    Pair(3, 5) to ObstacleType.FROSTING_1,
                    Pair(4, 2) to ObstacleType.FROSTING_2,
                    Pair(4, 3) to ObstacleType.FROSTING_2,
                    Pair(4, 4) to ObstacleType.FROSTING_2,
                    Pair(4, 5) to ObstacleType.FROSTING_2
                )
            ),
            // LEVEL 12: Beware of Chocolate!
            LevelData(
                levelNumber = 12,
                worldName = "Chocolate Mountain",
                worldEpisode = 3,
                moves = 25,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.ORANGE, CandyColor.YELLOW, CandyColor.GREEN, CandyColor.BLUE),
                goal = LevelGoal.ClearJelly(totalJellies = 20),
                star1Score = 11000,
                star2Score = 20000,
                star3Score = 30000,
                initialJellies = (1..6).flatMap { r -> (1..6).filter { (r in 1..2 || r in 5..6) }.map { c -> Pair(r, c) } }.toSet(),
                initialObstacles = mapOf(
                    Pair(7, 0) to ObstacleType.CHOCOLATE,
                    Pair(7, 7) to ObstacleType.CHOCOLATE,
                    Pair(6, 0) to ObstacleType.CHOCOLATE,
                    Pair(6, 7) to ObstacleType.CHOCOLATE
                )
            ),
            // LEVEL 13: Chocolate Fortress
            LevelData(
                levelNumber = 13,
                worldName = "Chocolate Mountain",
                worldEpisode = 3,
                moves = 26,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.ORANGE, CandyColor.YELLOW, CandyColor.BLUE, CandyColor.PURPLE),
                goal = LevelGoal.CollectOrders(
                    colorOrders = mapOf(CandyColor.RED to 25, CandyColor.PURPLE to 25),
                    stripedNeeded = 4
                ),
                star1Score = 12000,
                star2Score = 22000,
                star3Score = 35000,
                initialObstacles = mapOf(
                    Pair(4, 3) to ObstacleType.CHOCOLATE,
                    Pair(4, 4) to ObstacleType.CHOCOLATE,
                    Pair(3, 3) to ObstacleType.FROSTING_1,
                    Pair(3, 4) to ObstacleType.FROSTING_1
                )
            ),
            // LEVEL 14: Frosting & Jelly Maze
            LevelData(
                levelNumber = 14,
                worldName = "Chocolate Mountain",
                worldEpisode = 3,
                moves = 28,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.YELLOW, CandyColor.GREEN, CandyColor.BLUE, CandyColor.PURPLE),
                goal = LevelGoal.ClearJelly(totalJellies = 30),
                star1Score = 14000,
                star2Score = 25000,
                star3Score = 38000,
                initialJellies = (2..5).flatMap { r -> (0..7).map { c -> Pair(r, c) } }.toSet(),
                initialObstacles = mapOf(
                    Pair(2, 2) to ObstacleType.FROSTING_2,
                    Pair(2, 5) to ObstacleType.FROSTING_2,
                    Pair(5, 2) to ObstacleType.FROSTING_2,
                    Pair(5, 5) to ObstacleType.FROSTING_2
                )
            ),
            // LEVEL 15: The Great Chocolate Volcano
            LevelData(
                levelNumber = 15,
                worldName = "Chocolate Mountain",
                worldEpisode = 3,
                moves = 30,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.ORANGE, CandyColor.YELLOW, CandyColor.GREEN, CandyColor.BLUE, CandyColor.PURPLE),
                goal = LevelGoal.ClearJelly(totalJellies = 32),
                star1Score = 16000,
                star2Score = 28000,
                star3Score = 42000,
                initialJellies = (1..6).flatMap { r -> (1..6).map { c -> Pair(r, c) } }.toSet(),
                initialObstacles = mapOf(
                    Pair(7, 3) to ObstacleType.CHOCOLATE,
                    Pair(7, 4) to ObstacleType.CHOCOLATE,
                    Pair(6, 3) to ObstacleType.CHOCOLATE,
                    Pair(6, 4) to ObstacleType.CHOCOLATE
                )
            ),

            // WORLD 4: COOKIE FOREST (Ingredients Drop!)
            // LEVEL 16: Dropping Cherries
            LevelData(
                levelNumber = 16,
                worldName = "Cookie Forest",
                worldEpisode = 4,
                moves = 24,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.ORANGE, CandyColor.YELLOW, CandyColor.GREEN, CandyColor.BLUE),
                goal = LevelGoal.DropIngredients(cherries = 2, hazelnuts = 0),
                star1Score = 10000,
                star2Score = 20000,
                star3Score = 30000,
                initialIngredients = listOf(Pair(0, 2), Pair(0, 5))
            ),
            // LEVEL 17: Cherries & Hazelnuts Downhill
            LevelData(
                levelNumber = 17,
                worldName = "Cookie Forest",
                worldEpisode = 4,
                moves = 26,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.YELLOW, CandyColor.GREEN, CandyColor.BLUE, CandyColor.PURPLE),
                goal = LevelGoal.DropIngredients(cherries = 2, hazelnuts = 2),
                star1Score = 12000,
                star2Score = 24000,
                star3Score = 36000,
                initialIngredients = listOf(Pair(0, 1), Pair(0, 3), Pair(0, 4), Pair(0, 6)),
                initialObstacles = mapOf(
                    Pair(4, 1) to ObstacleType.FROSTING_1,
                    Pair(4, 6) to ObstacleType.FROSTING_1
                )
            ),
            // LEVEL 18: Mint Forest Ingredient Trail
            LevelData(
                levelNumber = 18,
                worldName = "Cookie Forest",
                worldEpisode = 4,
                moves = 28,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.ORANGE, CandyColor.YELLOW, CandyColor.BLUE, CandyColor.PURPLE),
                goal = LevelGoal.DropIngredients(cherries = 3, hazelnuts = 1),
                star1Score = 14000,
                star2Score = 26000,
                star3Score = 40000,
                initialIngredients = listOf(Pair(0, 2), Pair(0, 3), Pair(0, 4), Pair(0, 5))
            ),
            // LEVEL 19: Double Drop Under Threat
            LevelData(
                levelNumber = 19,
                worldName = "Cookie Forest",
                worldEpisode = 4,
                moves = 30,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.ORANGE, CandyColor.YELLOW, CandyColor.GREEN, CandyColor.BLUE, CandyColor.PURPLE),
                goal = LevelGoal.DropIngredients(cherries = 2, hazelnuts = 2),
                star1Score = 16000,
                star2Score = 30000,
                star3Score = 45000,
                initialIngredients = listOf(Pair(0, 2), Pair(0, 5), Pair(1, 3), Pair(1, 4)),
                initialObstacles = mapOf(
                    Pair(6, 2) to ObstacleType.FROSTING_2,
                    Pair(6, 5) to ObstacleType.FROSTING_2
                )
            ),
            // LEVEL 20: Minty Meadow Master
            LevelData(
                levelNumber = 20,
                worldName = "Cookie Forest",
                worldEpisode = 4,
                moves = 32,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.ORANGE, CandyColor.YELLOW, CandyColor.GREEN, CandyColor.BLUE),
                goal = LevelGoal.DropIngredients(cherries = 3, hazelnuts = 2),
                star1Score = 18000,
                star2Score = 35000,
                star3Score = 50000,
                initialIngredients = listOf(Pair(0, 1), Pair(0, 3), Pair(0, 4), Pair(0, 6), Pair(1, 2))
            ),

            // WORLD 5: RAINBOW CLOUDS (Grand Champion Levels)
            // LEVEL 21: Soda Burst Combo Rush
            LevelData(
                levelNumber = 21,
                worldName = "Rainbow Clouds",
                worldEpisode = 5,
                moves = 25,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.YELLOW, CandyColor.BLUE, CandyColor.PURPLE),
                goal = LevelGoal.CollectOrders(
                    colorOrders = mapOf(CandyColor.RED to 30, CandyColor.BLUE to 30),
                    stripedNeeded = 5
                ),
                star1Score = 20000,
                star2Score = 40000,
                star3Score = 60000
            ),
            // LEVEL 22: Soda Spring Jelly Geyser
            LevelData(
                levelNumber = 22,
                worldName = "Rainbow Clouds",
                worldEpisode = 5,
                moves = 28,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.ORANGE, CandyColor.YELLOW, CandyColor.GREEN, CandyColor.BLUE, CandyColor.PURPLE),
                goal = LevelGoal.ClearJelly(totalJellies = 48),
                star1Score = 22000,
                star2Score = 44000,
                star3Score = 65000,
                initialJellies = (1..6).flatMap { r -> (0..7).map { c -> Pair(r, c) } }.toSet(),
                doubleJellies = (2..5).flatMap { r -> (1..6).map { c -> Pair(r, c) } }.toSet()
            ),
            // LEVEL 23: Chocolate & Ingredient Soda River
            LevelData(
                levelNumber = 23,
                worldName = "Rainbow Clouds",
                worldEpisode = 5,
                moves = 30,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.ORANGE, CandyColor.YELLOW, CandyColor.GREEN, CandyColor.BLUE),
                goal = LevelGoal.DropIngredients(cherries = 3, hazelnuts = 2),
                star1Score = 25000,
                star2Score = 50000,
                star3Score = 75000,
                initialIngredients = listOf(Pair(0, 2), Pair(0, 3), Pair(0, 4), Pair(0, 5), Pair(1, 3)),
                initialObstacles = mapOf(
                    Pair(7, 0) to ObstacleType.CHOCOLATE,
                    Pair(7, 7) to ObstacleType.CHOCOLATE
                )
            ),
            // LEVEL 24: Ultimate Color Order Extravaganza
            LevelData(
                levelNumber = 24,
                worldName = "Rainbow Clouds",
                worldEpisode = 5,
                moves = 32,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.ORANGE, CandyColor.YELLOW, CandyColor.GREEN, CandyColor.BLUE, CandyColor.PURPLE),
                goal = LevelGoal.CollectOrders(
                    colorOrders = mapOf(
                        CandyColor.RED to 25,
                        CandyColor.YELLOW to 25,
                        CandyColor.GREEN to 25
                    ),
                    stripedNeeded = 6
                ),
                star1Score = 28000,
                star2Score = 55000,
                star3Score = 80000
            ),
            // LEVEL 25: The Royal Sugar Palace (Final Grand Challenge)
            LevelData(
                levelNumber = 25,
                worldName = "Rainbow Clouds",
                worldEpisode = 5,
                moves = 35,
                rows = 8,
                cols = 8,
                allowedColors = listOf(CandyColor.RED, CandyColor.ORANGE, CandyColor.YELLOW, CandyColor.GREEN, CandyColor.BLUE, CandyColor.PURPLE),
                goal = LevelGoal.ClearJelly(totalJellies = 52),
                star1Score = 35000,
                star2Score = 70000,
                star3Score = 100000,
                initialJellies = (0..7).flatMap { r -> (0..7).map { c -> Pair(r, c) } }.toSet(),
                doubleJellies = (1..6).flatMap { r -> (1..6).map { c -> Pair(r, c) } }.toSet(),
                initialObstacles = mapOf(
                    Pair(3, 3) to ObstacleType.FROSTING_2,
                    Pair(3, 4) to ObstacleType.FROSTING_2,
                    Pair(4, 3) to ObstacleType.FROSTING_2,
                    Pair(4, 4) to ObstacleType.FROSTING_2,
                    Pair(7, 0) to ObstacleType.CHOCOLATE,
                    Pair(7, 7) to ObstacleType.CHOCOLATE
                )
            )
        )
    }

    fun getLevel(levelNumber: Int): LevelData {
        return levelDefinitions.find { it.levelNumber == levelNumber }
            ?: generateProceduralLevel(levelNumber)
    }

    fun getAllLevels(): List<LevelData> = levelDefinitions

    private fun generateProceduralLevel(levelNumber: Int): LevelData {
        val moves = maxOf(20, 35 - (levelNumber / 5))
        return LevelData(
            levelNumber = levelNumber,
            worldName = "Sugar Infinity",
            worldEpisode = 6,
            moves = moves,
            rows = 8,
            cols = 8,
            goal = LevelGoal.TargetScore(targetScore = 15000 + levelNumber * 1000),
            star1Score = 15000 + levelNumber * 1000,
            star2Score = 30000 + levelNumber * 2000,
            star3Score = 50000 + levelNumber * 3000
        )
    }
}

data class WorldInfo(
    val id: Int,
    val name: String,
    val levelRange: IntRange,
    val description: String
)
