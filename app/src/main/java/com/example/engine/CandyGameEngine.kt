package com.example.engine

import com.example.model.BoardTile
import com.example.model.BoosterType
import com.example.model.Candy
import com.example.model.CandyColor
import com.example.model.LevelData
import com.example.model.LevelGoal
import com.example.model.ObstacleType
import com.example.model.SpecialType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

data class GameScorePopup(
    val id: Long = System.currentTimeMillis() + Random.nextLong(1000),
    val text: String,
    val row: Int,
    val col: Int,
    val color: androidx.compose.ui.graphics.Color
)

enum class GameStatus {
    PLAYING,
    ANIMATING,
    SUGAR_CRUSH,
    WON,
    LOST
}

data class CandyGameUiState(
    val levelData: LevelData,
    val board: List<List<BoardTile>>,
    val movesRemaining: Int,
    val score: Int,
    val targetScore: Int,
    val starsEarned: Int = 0,
    val remainingJellies: Int = 0,
    val remainingCherries: Int = 0,
    val remainingHazelnuts: Int = 0,
    val colorOrdersCollected: Map<CandyColor, Int> = emptyMap(),
    val stripedOrdersCollected: Int = 0,
    val status: GameStatus = GameStatus.PLAYING,
    val selectedTile: Pair<Int, Int>? = null,
    val activeBooster: BoosterType? = null,
    val comboMessage: String? = null,
    val comboMultiplier: Int = 1,
    val popups: List<GameScorePopup> = emptyList(),
    val isReshuffling: Boolean = false,
    val laserBeams: List<LaserBeam> = emptyList() // For striped visual effects
)

data class LaserBeam(
    val id: Long = System.currentTimeMillis() + Random.nextLong(1000),
    val isHorizontal: Boolean,
    val index: Int
)

class CandyGameEngine(
    private val levelData: LevelData,
    private val onGameEnd: (isWin: Boolean, score: Int, stars: Int) -> Unit = { _, _, _ -> }
) {
    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<CandyGameUiState> = _uiState.asStateFlow()

    private var currentBoard: Array<Array<BoardTile>> = Array(levelData.rows) { r ->
        Array(levelData.cols) { c ->
            _uiState.value.board[r][c]
        }
    }

    private fun createInitialState(): CandyGameUiState {
        val rows = levelData.rows
        val cols = levelData.cols

        val grid = Array(rows) { r ->
            Array(cols) { c ->
                val pos = Pair(r, c)
                val isPlayable = pos !in levelData.unplayableTiles
                val jelly = when {
                    pos in levelData.doubleJellies -> 2
                    pos in levelData.initialJellies -> 1
                    else -> 0
                }
                val obstacle = levelData.initialObstacles[pos] ?: ObstacleType.NONE
                BoardTile(
                    row = r,
                    col = c,
                    isPlayable = isPlayable,
                    jellyLayers = jelly,
                    obstacle = obstacle,
                    isSpawner = (r == 0)
                )
            }
        }

        // Fill non-obstacle playable tiles with candies without starting matches
        fillBoardWithoutMatches(grid, levelData)

        // Count initial jellies and ingredients
        var initialJellies = 0
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                initialJellies += grid[r][c].jellyLayers
            }
        }

        var cherries = 0
        var hazelnuts = 0
        if (levelData.goal is LevelGoal.DropIngredients) {
            cherries = levelData.goal.cherries
            hazelnuts = levelData.goal.hazelnuts
        }

        val ordersMap = if (levelData.goal is LevelGoal.CollectOrders) {
            levelData.goal.colorOrders.mapValues { 0 }
        } else {
            emptyMap()
        }

        return CandyGameUiState(
            levelData = levelData,
            board = grid.map { it.toList() },
            movesRemaining = levelData.moves,
            score = 0,
            targetScore = levelData.star1Score,
            remainingJellies = initialJellies,
            remainingCherries = cherries,
            remainingHazelnuts = hazelnuts,
            colorOrdersCollected = ordersMap
        )
    }

    private fun fillBoardWithoutMatches(grid: Array<Array<BoardTile>>, levelData: LevelData) {
        val rows = grid.size
        val cols = grid[0].size
        val allowedColors = levelData.allowedColors

        // Place initial ingredients if any
        val ingredientPositions = levelData.initialIngredients.toMutableList()

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val tile = grid[r][c]
                if (!tile.isPlayable || tile.obstacle != ObstacleType.NONE) continue

                if (ingredientPositions.contains(Pair(r, c))) {
                    val special = if (Random.nextBoolean()) SpecialType.INGREDIENT_CHERRY else SpecialType.INGREDIENT_HAZELNUT
                    grid[r][c] = tile.copy(candy = Candy(color = allowedColors[0], special = special))
                    ingredientPositions.remove(Pair(r, c))
                    continue
                }

                // Choose a random color that doesn't create a match of 3
                val excludedColors = mutableSetOf<CandyColor>()
                if (c >= 2) {
                    val left1 = grid[r][c - 1].candy?.color
                    val left2 = grid[r][c - 2].candy?.color
                    if (left1 != null && left1 == left2) {
                        excludedColors.add(left1)
                    }
                }
                if (r >= 2) {
                    val up1 = grid[r - 1][c].candy?.color
                    val up2 = grid[r - 2][c].candy?.color
                    if (up1 != null && up1 == up2) {
                        excludedColors.add(up1)
                    }
                }

                val candidates = allowedColors.filter { it !in excludedColors }
                val color = if (candidates.isNotEmpty()) candidates.random() else allowedColors.random()
                grid[r][c] = tile.copy(candy = Candy(color = color))
            }
        }
    }

    suspend fun onTileClicked(row: Int, col: Int) {
        if (_uiState.value.status != GameStatus.PLAYING) return

        val activeBooster = _uiState.value.activeBooster
        if (activeBooster != null) {
            applyBoosterOnTile(row, col, activeBooster)
            return
        }

        val selected = _uiState.value.selectedTile
        if (selected == null) {
            val tile = currentBoard[row][col]
            if (tile.isPlayable && tile.candy != null && !tile.isBlocked) {
                _uiState.value = _uiState.value.copy(selectedTile = Pair(row, col))
                SoundSynthesizer.playSwapSound()
            }
        } else {
            val (prevR, prevC) = selected
            if (prevR == row && prevC == col) {
                // Deselect
                _uiState.value = _uiState.value.copy(selectedTile = null)
                return
            }

            val isAdjacent = (Math.abs(prevR - row) + Math.abs(prevC - col)) == 1
            if (isAdjacent) {
                _uiState.value = _uiState.value.copy(selectedTile = null)
                trySwapCandies(prevR, prevC, row, col)
            } else {
                val tile = currentBoard[row][col]
                if (tile.isPlayable && tile.candy != null && !tile.isBlocked) {
                    _uiState.value = _uiState.value.copy(selectedTile = Pair(row, col))
                    SoundSynthesizer.playSwapSound()
                } else {
                    _uiState.value = _uiState.value.copy(selectedTile = null)
                }
            }
        }
    }

    suspend fun onSwipe(startR: Int, startC: Int, deltaX: Float, deltaY: Float) {
        if (_uiState.value.status != GameStatus.PLAYING) return
        if (startR !in 0 until levelData.rows || startC !in 0 until levelData.cols) return

        var targetR = startR
        var targetC = startC

        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            if (deltaX > 20 && startC + 1 < levelData.cols) targetC++
            else if (deltaX < -20 && startC - 1 >= 0) targetC--
        } else {
            if (deltaY > 20 && startR + 1 < levelData.rows) targetR++
            else if (deltaY < -20 && startR - 1 >= 0) targetR--
        }

        if (targetR != startR || targetC != startC) {
            _uiState.value = _uiState.value.copy(selectedTile = null)
            trySwapCandies(startR, startC, targetR, targetC)
        }
    }

    fun selectBooster(booster: BoosterType?) {
        _uiState.value = _uiState.value.copy(
            activeBooster = if (_uiState.value.activeBooster == booster) null else booster
        )
    }

    private suspend fun applyBoosterOnTile(row: Int, col: Int, booster: BoosterType) {
        _uiState.value = _uiState.value.copy(activeBooster = null)

        when (booster) {
            BoosterType.LOLLIPOP_HAMMER -> {
                SoundSynthesizer.playHammerSmash()
                val tile = currentBoard[row][col]
                if (tile.isPlayable) {
                    val clearedCandy = tile.candy
                    var newObstacle = tile.obstacle
                    if (tile.obstacle == ObstacleType.FROSTING_2) {
                        newObstacle = ObstacleType.FROSTING_1
                    } else if (tile.obstacle != ObstacleType.NONE) {
                        newObstacle = ObstacleType.NONE
                    }
                    val newJelly = maxOf(0, tile.jellyLayers - 1)
                    currentBoard[row][col] = tile.copy(candy = null, obstacle = newObstacle, jellyLayers = newJelly)
                    addScore(250, row, col, "SMASH! +250")
                    syncBoardToState()
                    triggerCascade()
                }
            }
            BoosterType.SUGAR_LIGHTNING -> {
                SoundSynthesizer.playSugarLightningZap()
                showComboBanner("LIGHTNING ZAP!")
                val tile = currentBoard[row][col]
                if (tile.isPlayable) {
                    triggerLaserBeam(true, row)
                    triggerLaserBeam(false, col)
                    for (c in 0 until levelData.cols) clearTile(row, c)
                    for (r in 0 until levelData.rows) clearTile(r, col)
                    addScore(3500, row, col, "SUGAR ZAP! +3500")
                    syncBoardToState()
                    triggerCascade()
                }
            }
            BoosterType.COLOR_BOMB_START -> {
                SoundSynthesizer.playColorBombSparkle()
                val tile = currentBoard[row][col]
                if (tile.isPlayable && !tile.isBlocked) {
                    currentBoard[row][col] = tile.copy(candy = Candy(color = CandyColor.RED, special = SpecialType.COLOR_BOMB))
                    syncBoardToState()
                }
            }
            BoosterType.STRIPED_WRAPPED_START -> {
                SoundSynthesizer.playSweetComboFanfare()
                val tile = currentBoard[row][col]
                if (tile.isPlayable && !tile.isBlocked && tile.candy != null) {
                    currentBoard[row][col] = tile.copy(
                        candy = tile.candy.copy(special = if (Random.nextBoolean()) SpecialType.STRIPED_HORIZONTAL else SpecialType.WRAPPED)
                    )
                    syncBoardToState()
                }
            }
            BoosterType.EXTRA_MOVES -> {
                _uiState.value = _uiState.value.copy(movesRemaining = _uiState.value.movesRemaining + 5)
            }
            BoosterType.FREE_SWITCH -> {
                _uiState.value = _uiState.value.copy(selectedTile = Pair(row, col))
            }
        }
    }

    private suspend fun trySwapCandies(r1: Int, c1: Int, r2: Int, c2: Int) {
        val tile1 = currentBoard[r1][c1]
        val tile2 = currentBoard[r2][c2]

        if (!tile1.isPlayable || !tile2.isPlayable || tile1.isBlocked || tile2.isBlocked) return
        val candy1 = tile1.candy ?: return
        val candy2 = tile2.candy ?: return

        _uiState.value = _uiState.value.copy(status = GameStatus.ANIMATING)
        SoundSynthesizer.playSwapSound()

        // Check for Special combinations
        val isCombo = checkSpecialCombinations(r1, c1, candy1, r2, c2, candy2)
        if (isCombo) {
            decrementMove()
            return
        }

        // Swap temporarily
        currentBoard[r1][c1] = tile1.copy(candy = candy2)
        currentBoard[r2][c2] = tile2.copy(candy = candy1)
        syncBoardToState()
        delay(160)

        // Detect matches
        val matches = MatchDetector.findMatches(currentBoard, lastSwappedPos = Pair(r2, c2))
        if (matches.matchedPositions.isNotEmpty()) {
            decrementMove()
            processMatchesAndCascade(matches, comboChain = 1)
        } else {
            // No match! Swap back
            delay(100)
            currentBoard[r1][c1] = tile1.copy(candy = candy1)
            currentBoard[r2][c2] = tile2.copy(candy = candy2)
            syncBoardToState()
            _uiState.value = _uiState.value.copy(status = GameStatus.PLAYING)
        }
    }

    private suspend fun checkSpecialCombinations(
        r1: Int, c1: Int, candy1: Candy,
        r2: Int, c2: Int, candy2: Candy
    ): Boolean {
        // 1. Color Bomb + Color Bomb (Apocalyptic board wipe!)
        if (candy1.isColorBomb && candy2.isColorBomb) {
            SoundSynthesizer.playColorBombSparkle()
            showComboBanner("DIVINE!")
            currentBoard[r1][c1] = currentBoard[r1][c1].copy(candy = null)
            currentBoard[r2][c2] = currentBoard[r2][c2].copy(candy = null)
            syncBoardToState()
            delay(200)

            for (r in 0 until levelData.rows) {
                for (c in 0 until levelData.cols) {
                    val t = currentBoard[r][c]
                    if (t.isPlayable) {
                        currentBoard[r][c] = t.copy(candy = null, jellyLayers = maxOf(0, t.jellyLayers - 1))
                    }
                }
            }
            addScore(10000, r2, c2, "SUPER CRUSH! +10,000")
            syncBoardToState()
            triggerCascade()
            return true
        }

        // 2. Color Bomb + Striped Candy
        if ((candy1.isColorBomb && candy2.isStriped) || (candy2.isColorBomb && candy1.isStriped)) {
            val targetColor = if (candy1.isStriped) candy1.color else candy2.color
            SoundSynthesizer.playColorBombSparkle()
            showComboBanner("DELICIOUS!")
            currentBoard[r1][c1] = currentBoard[r1][c1].copy(candy = null)
            currentBoard[r2][c2] = currentBoard[r2][c2].copy(candy = null)

            // Convert all candies of targetColor to Striped
            for (r in 0 until levelData.rows) {
                for (c in 0 until levelData.cols) {
                    val t = currentBoard[r][c]
                    if (t.candy?.color == targetColor && !t.candy.isIngredient) {
                        val stripe = if (Random.nextBoolean()) SpecialType.STRIPED_HORIZONTAL else SpecialType.STRIPED_VERTICAL
                        currentBoard[r][c] = t.copy(candy = t.candy.copy(special = stripe))
                    }
                }
            }
            syncBoardToState()
            delay(250)

            // Detonate all of them!
            detonateAllSpecialsOfColor(targetColor)
            triggerCascade()
            return true
        }

        // 3. Color Bomb + Wrapped Candy
        if ((candy1.isColorBomb && candy2.isWrapped) || (candy2.isColorBomb && candy1.isWrapped)) {
            val targetColor = if (candy1.isWrapped) candy1.color else candy2.color
            SoundSynthesizer.playColorBombSparkle()
            showComboBanner("SWEET!")
            currentBoard[r1][c1] = currentBoard[r1][c1].copy(candy = null)
            currentBoard[r2][c2] = currentBoard[r2][c2].copy(candy = null)

            for (r in 0 until levelData.rows) {
                for (c in 0 until levelData.cols) {
                    val t = currentBoard[r][c]
                    if (t.candy?.color == targetColor && !t.candy.isIngredient) {
                        currentBoard[r][c] = t.copy(candy = t.candy.copy(special = SpecialType.WRAPPED))
                    }
                }
            }
            syncBoardToState()
            delay(250)

            detonateAllSpecialsOfColor(targetColor)
            triggerCascade()
            return true
        }

        // 4. Color Bomb + Regular Candy
        if (candy1.isColorBomb || candy2.isColorBomb) {
            val targetColor = if (candy1.isColorBomb) candy2.color else candy1.color
            SoundSynthesizer.playColorBombSparkle()
            showComboBanner("TASTY!")
            currentBoard[r1][c1] = currentBoard[r1][c1].copy(candy = null)
            currentBoard[r2][c2] = currentBoard[r2][c2].copy(candy = null)
            syncBoardToState()
            delay(200)

            var clearedCount = 0
            for (r in 0 until levelData.rows) {
                for (c in 0 until levelData.cols) {
                    val t = currentBoard[r][c]
                    if (t.candy?.color == targetColor && !t.candy.isIngredient) {
                        currentBoard[r][c] = t.copy(candy = null, jellyLayers = maxOf(0, t.jellyLayers - 1))
                        clearedCount++
                    }
                }
            }
            addScore(clearedCount * 200, r2, c2, "+${clearedCount * 200}")
            syncBoardToState()
            triggerCascade()
            return true
        }

        // 5. Striped + Wrapped (Giant 3x3 Cross blast!)
        if ((candy1.isStriped && candy2.isWrapped) || (candy2.isStriped && candy1.isWrapped)) {
            SoundSynthesizer.playWrappedExplosion()
            showComboBanner("SUGAR CRUSH!")
            currentBoard[r1][c1] = currentBoard[r1][c1].copy(candy = null)
            currentBoard[r2][c2] = currentBoard[r2][c2].copy(candy = null)

            val rowsToClear = listOf(r2 - 1, r2, r2 + 1).filter { it in 0 until levelData.rows }
            val colsToClear = listOf(c2 - 1, c2, c2 + 1).filter { it in 0 until levelData.cols }

            for (r in rowsToClear) {
                for (c in 0 until levelData.cols) {
                    clearTile(r, c)
                }
            }
            for (c in colsToClear) {
                for (r in 0 until levelData.rows) {
                    clearTile(r, c)
                }
            }
            addScore(4500, r2, c2, "MEGA BLAST! +4500")
            syncBoardToState()
            triggerCascade()
            return true
        }

        // 6. Striped + Striped (1 Row + 1 Col cross blast)
        if (candy1.isStriped && candy2.isStriped) {
            SoundSynthesizer.playStripedBlast()
            showComboBanner("SWEET!")
            currentBoard[r1][c1] = currentBoard[r1][c1].copy(candy = null)
            currentBoard[r2][c2] = currentBoard[r2][c2].copy(candy = null)

            for (c in 0 until levelData.cols) clearTile(r2, c)
            for (r in 0 until levelData.rows) clearTile(r, c2)

            addScore(2500, r2, c2, "CROSS BLAST! +2500")
            syncBoardToState()
            triggerCascade()
            return true
        }

        // 7. Wrapped + Wrapped (Mega 5x5 explosion)
        if (candy1.isWrapped && candy2.isWrapped) {
            SoundSynthesizer.playWrappedExplosion()
            showComboBanner("DELICIOUS!")
            currentBoard[r1][c1] = currentBoard[r1][c1].copy(candy = null)
            currentBoard[r2][c2] = currentBoard[r2][c2].copy(candy = null)

            for (dr in -2..2) {
                for (dc in -2..2) {
                    val nr = r2 + dr
                    val nc = c2 + dc
                    if (nr in 0 until levelData.rows && nc in 0 until levelData.cols) {
                        clearTile(nr, nc)
                    }
                }
            }
            addScore(3600, r2, c2, "MEGA BOMB! +3600")
            syncBoardToState()
            triggerCascade()
            return true
        }

        return false
    }

    private suspend fun detonateAllSpecialsOfColor(color: CandyColor) {
        val targets = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until levelData.rows) {
            for (c in 0 until levelData.cols) {
                val t = currentBoard[r][c]
                if (t.candy?.color == color) {
                    targets.add(Pair(r, c))
                }
            }
        }
        for ((r, c) in targets) {
            val candy = currentBoard[r][c].candy ?: continue
            detonateCandy(r, c, candy)
            delay(60)
        }
    }

    private fun detonateCandy(r: Int, c: Int, candy: Candy) {
        when (candy.special) {
            SpecialType.STRIPED_HORIZONTAL -> {
                SoundSynthesizer.playStripedBlast()
                triggerLaserBeam(true, r)
                for (col in 0 until levelData.cols) clearTile(r, col)
            }
            SpecialType.STRIPED_VERTICAL -> {
                SoundSynthesizer.playStripedBlast()
                triggerLaserBeam(false, c)
                for (row in 0 until levelData.rows) clearTile(row, c)
            }
            SpecialType.WRAPPED -> {
                SoundSynthesizer.playWrappedExplosion()
                for (dr in -1..1) {
                    for (dc in -1..1) {
                        val nr = r + dr
                        val nc = c + dc
                        if (nr in 0 until levelData.rows && nc in 0 until levelData.cols) {
                            clearTile(nr, nc)
                        }
                    }
                }
            }
            else -> {
                clearTile(r, c)
            }
        }
    }

    private fun clearTile(r: Int, c: Int) {
        val tile = currentBoard[r][c]
        if (!tile.isPlayable) return

        val candy = tile.candy
        if (candy != null) {
            // Track collected orders
            if (levelData.goal is LevelGoal.CollectOrders) {
                val currentOrders = _uiState.value.colorOrdersCollected.toMutableMap()
                val currentCount = currentOrders[candy.color] ?: 0
                currentOrders[candy.color] = currentCount + 1

                var stripedCount = _uiState.value.stripedOrdersCollected
                if (candy.isStriped) stripedCount++

                _uiState.value = _uiState.value.copy(
                    colorOrdersCollected = currentOrders,
                    stripedOrdersCollected = stripedCount
                )
            }

            // If it's an ingredient reaching bottom
            if (candy.isIngredient && r == levelData.rows - 1) {
                collectIngredient(candy.special)
            }
        }

        // Damage adjacent obstacles & jelly
        val newJelly = maxOf(0, tile.jellyLayers - 1)
        var newObstacle = tile.obstacle
        if (tile.obstacle == ObstacleType.CHOCOLATE || tile.obstacle == ObstacleType.FROSTING_1) {
            newObstacle = ObstacleType.NONE
        } else if (tile.obstacle == ObstacleType.FROSTING_2) {
            newObstacle = ObstacleType.FROSTING_1
        }

        currentBoard[r][c] = tile.copy(candy = null, jellyLayers = newJelly, obstacle = newObstacle)
    }

    private suspend fun processMatchesAndCascade(matchResult: MatchResult, comboChain: Int) {
        SoundSynthesizer.playMatchSound(comboChain)

        // Show Praise messages for high combo chains
        when (comboChain) {
            2 -> showComboBanner("SWEET!")
            3 -> showComboBanner("TASTY!")
            4 -> showComboBanner("DELICIOUS!")
            5 -> showComboBanner("DIVINE!")
            6 -> showComboBanner("SUGAR CRUSH!")
        }

        val baseScorePerCandy = 60 * comboChain
        var totalPoints = 0

        // Clear matched candies and detonate specials
        val specialsToDetonate = mutableListOf<Triple<Int, Int, Candy>>()

        for ((r, c) in matchResult.matchedPositions) {
            val tile = currentBoard[r][c]
            val candy = tile.candy
            if (candy != null && candy.special != SpecialType.NONE && !candy.isIngredient) {
                specialsToDetonate.add(Triple(r, c, candy))
            }
            clearTile(r, c)
            totalPoints += baseScorePerCandy

            // Hit adjacent obstacles
            hitAdjacentObstacles(r, c)
        }

        // Place newly formed special candies
        for ((pos, specialCandy) in matchResult.specialCreations) {
            currentBoard[pos.first][pos.second] = currentBoard[pos.first][pos.second].copy(candy = specialCandy)
        }

        val scoreCenter = matchResult.matchedPositions.firstOrNull() ?: Pair(levelData.rows / 2, levelData.cols / 2)
        addScore(totalPoints, scoreCenter.first, scoreCenter.second, "+$totalPoints")

        syncBoardToState()
        delay(180)

        // Detonate any triggered special candies
        for ((r, c, candy) in specialsToDetonate) {
            detonateCandy(r, c, candy)
            delay(80)
        }

        triggerCascade(comboChain + 1)
    }

    private suspend fun triggerCascade(nextComboChain: Int = 1) {
        var hadMovement = false

        // 1. Gravity (candies fall downwards)
        for (c in 0 until levelData.cols) {
            for (r in levelData.rows - 1 downTo 0) {
                val tile = currentBoard[r][c]
                if (tile.isPlayable && tile.candy == null && !tile.isBlocked) {
                    // Find highest candy above this tile in same column
                    for (aboveR in r - 1 downTo 0) {
                        val aboveTile = currentBoard[aboveR][c]
                        if (aboveTile.isPlayable && aboveTile.candy != null && !aboveTile.isBlocked) {
                            currentBoard[r][c] = tile.copy(candy = aboveTile.candy)
                            currentBoard[aboveR][c] = aboveTile.copy(candy = null)
                            hadMovement = true
                            break
                        }
                    }
                }
            }
        }

        // 2. Refill from top spawners
        for (c in 0 until levelData.cols) {
            for (r in 0 until levelData.rows) {
                val tile = currentBoard[r][c]
                if (tile.isPlayable && tile.candy == null && !tile.isBlocked) {
                    val color = levelData.allowedColors.random()
                    currentBoard[r][c] = tile.copy(candy = Candy(color = color))
                    hadMovement = true
                }
            }
        }

        // 3. Check for Ingredients at the bottom
        for (c in 0 until levelData.cols) {
            val bottomTile = currentBoard[levelData.rows - 1][c]
            val candy = bottomTile.candy
            if (candy != null && candy.isIngredient) {
                collectIngredient(candy.special)
                currentBoard[levelData.rows - 1][c] = bottomTile.copy(candy = null)
                hadMovement = true
            }
        }

        syncBoardToState()

        if (hadMovement) {
            delay(200)
        }

        // 4. Check for cascades
        val newMatches = MatchDetector.findMatches(currentBoard)
        if (newMatches.matchedPositions.isNotEmpty()) {
            processMatchesAndCascade(newMatches, nextComboChain)
        } else {
            // Cascade sequence finished!
            checkPostTurnState()
        }
    }

    private suspend fun checkPostTurnState() {
        updateGoalProgress()

        val isWin = checkWinCondition()
        if (isWin) {
            if (_uiState.value.movesRemaining > 0) {
                // Trigger Sugar Crush bonus sequence!
                startSugarCrush()
            } else {
                finishGame(true)
            }
            return
        }

        if (_uiState.value.movesRemaining <= 0) {
            finishGame(false)
            return
        }

        // Check if moves are possible; if not, reshuffle!
        if (!MatchDetector.hasPossibleMoves(currentBoard)) {
            reshuffleBoard()
        }

        _uiState.value = _uiState.value.copy(status = GameStatus.PLAYING)
    }

    private suspend fun startSugarCrush() {
        _uiState.value = _uiState.value.copy(status = GameStatus.SUGAR_CRUSH)
        showComboBanner("SUGAR CRUSH!")
        SoundSynthesizer.playSweetComboFanfare()
        delay(600)

        // Convert remaining moves into striped candies on random tiles!
        while (_uiState.value.movesRemaining > 0) {
            val moves = _uiState.value.movesRemaining - 1
            _uiState.value = _uiState.value.copy(movesRemaining = moves)

            val validPositions = mutableListOf<Pair<Int, Int>>()
            for (r in 0 until levelData.rows) {
                for (c in 0 until levelData.cols) {
                    val t = currentBoard[r][c]
                    if (t.isPlayable && t.candy != null && !t.candy.isIngredient && !t.candy.isStriped) {
                        validPositions.add(Pair(r, c))
                    }
                }
            }

            if (validPositions.isNotEmpty()) {
                val (r, c) = validPositions.random()
                val tile = currentBoard[r][c]
                val striped = if (Random.nextBoolean()) SpecialType.STRIPED_HORIZONTAL else SpecialType.STRIPED_VERTICAL
                currentBoard[r][c] = tile.copy(candy = tile.candy?.copy(special = striped))
                syncBoardToState()
                SoundSynthesizer.playSwapSound()
                delay(120)
            }
        }

        delay(300)

        // Detonate all striped candies sequentially
        for (r in 0 until levelData.rows) {
            for (c in 0 until levelData.cols) {
                val t = currentBoard[r][c]
                if (t.candy?.isStriped == true) {
                    detonateCandy(r, c, t.candy)
                    addScore(1500, r, c, "+1500")
                    syncBoardToState()
                    delay(150)
                }
            }
        }

        triggerCascade()
        finishGame(true)
    }

    private fun finishGame(isWin: Boolean) {
        val score = _uiState.value.score
        val stars = calculateStars(score)
        _uiState.value = _uiState.value.copy(
            status = if (isWin) GameStatus.WON else GameStatus.LOST,
            starsEarned = stars
        )
        if (isWin) {
            SoundSynthesizer.playVictoryFanfare()
        }
        onGameEnd(isWin, score, stars)
    }

    private fun checkWinCondition(): Boolean {
        return when (val goal = levelData.goal) {
            is LevelGoal.TargetScore -> _uiState.value.score >= goal.targetScore
            is LevelGoal.ClearJelly -> _uiState.value.remainingJellies <= 0
            is LevelGoal.DropIngredients -> _uiState.value.remainingCherries <= 0 && _uiState.value.remainingHazelnuts <= 0
            is LevelGoal.CollectOrders -> {
                val ordersMet = goal.colorOrders.all { (color, needed) ->
                    (_uiState.value.colorOrdersCollected[color] ?: 0) >= needed
                }
                val stripedMet = _uiState.value.stripedOrdersCollected >= goal.stripedNeeded
                ordersMet && stripedMet
            }
        }
    }

    private fun updateGoalProgress() {
        var remainingJellies = 0
        for (r in 0 until levelData.rows) {
            for (c in 0 until levelData.cols) {
                remainingJellies += currentBoard[r][c].jellyLayers
            }
        }
        _uiState.value = _uiState.value.copy(remainingJellies = remainingJellies)
    }

    private fun hitAdjacentObstacles(r: Int, c: Int) {
        val neighbors = listOf(Pair(r - 1, c), Pair(r + 1, c), Pair(r, c - 1), Pair(r, c + 1))
        for ((nr, nc) in neighbors) {
            if (nr in 0 until levelData.rows && nc in 0 until levelData.cols) {
                val tile = currentBoard[nr][nc]
                if (tile.obstacle == ObstacleType.CHOCOLATE || tile.obstacle == ObstacleType.FROSTING_1) {
                    currentBoard[nr][nc] = tile.copy(obstacle = ObstacleType.NONE)
                    addScore(200, nr, nc, "+200")
                } else if (tile.obstacle == ObstacleType.FROSTING_2) {
                    currentBoard[nr][nc] = tile.copy(obstacle = ObstacleType.FROSTING_1)
                    addScore(100, nr, nc, "+100")
                }
            }
        }
    }

    private fun collectIngredient(special: SpecialType) {
        SoundSynthesizer.playSweetComboFanfare()
        if (special == SpecialType.INGREDIENT_CHERRY) {
            val c = maxOf(0, _uiState.value.remainingCherries - 1)
            _uiState.value = _uiState.value.copy(remainingCherries = c)
        } else if (special == SpecialType.INGREDIENT_HAZELNUT) {
            val h = maxOf(0, _uiState.value.remainingHazelnuts - 1)
            _uiState.value = _uiState.value.copy(remainingHazelnuts = h)
        }
        addScore(3000, levelData.rows - 1, 0, "INGREDIENT COLLECTED! +3000")
    }

    private fun addScore(points: Int, r: Int, c: Int, text: String) {
        val newScore = _uiState.value.score + points
        val stars = calculateStars(newScore)
        val popup = GameScorePopup(
            text = text,
            row = r,
            col = c,
            color = if (points >= 1000) androidx.compose.ui.graphics.Color(0xFFFFD54F) else androidx.compose.ui.graphics.Color.White
        )
        val popups = (_uiState.value.popups + popup).takeLast(6)
        _uiState.value = _uiState.value.copy(score = newScore, starsEarned = stars, popups = popups)
    }

    private fun calculateStars(score: Int): Int {
        return when {
            score >= levelData.star3Score -> 3
            score >= levelData.star2Score -> 2
            score >= levelData.star1Score -> 1
            else -> 0
        }
    }

    private fun decrementMove() {
        val moves = maxOf(0, _uiState.value.movesRemaining - 1)
        _uiState.value = _uiState.value.copy(movesRemaining = moves)
    }

    private fun showComboBanner(message: String) {
        _uiState.value = _uiState.value.copy(comboMessage = message)
    }

    private fun triggerLaserBeam(isHorizontal: Boolean, index: Int) {
        val beam = LaserBeam(isHorizontal = isHorizontal, index = index)
        _uiState.value = _uiState.value.copy(laserBeams = _uiState.value.laserBeams + beam)
    }

    private suspend fun reshuffleBoard() {
        _uiState.value = _uiState.value.copy(isReshuffling = true)
        delay(400)
        fillBoardWithoutMatches(currentBoard, levelData)
        syncBoardToState()
        delay(300)
        _uiState.value = _uiState.value.copy(isReshuffling = false)
    }

    private fun syncBoardToState() {
        _uiState.value = _uiState.value.copy(board = currentBoard.map { it.toList() })
    }
}
