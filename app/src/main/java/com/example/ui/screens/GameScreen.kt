package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.PlayerRepository
import com.example.engine.CandyGameEngine
import com.example.engine.GameStatus
import com.example.engine.LevelRepository
import com.example.model.BoosterType
import com.example.model.LevelData
import com.example.ui.components.BoosterBar
import com.example.ui.components.ComboBanner
import com.example.ui.components.GameBoardView
import com.example.ui.components.LevelFailDialog
import com.example.ui.components.LevelWinDialog
import com.example.ui.components.TopHeaderBar
import com.example.ui.theme.BoardBackground
import kotlinx.coroutines.launch

@Composable
fun GameScreen(
    levelData: LevelData,
    playerRepo: PlayerRepository,
    onBackToMap: () -> Unit,
    onNextLevel: (LevelData) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val profile by playerRepo.playerProfile.collectAsState()

    val engine = remember(levelData.levelNumber) {
        CandyGameEngine(
            levelData = levelData,
            onGameEnd = { isWin, score, stars ->
                if (isWin) {
                    playerRepo.recordLevelWin(levelData.levelNumber, score, stars)
                }
            }
        )
    }

    val uiState by engine.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BoardBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF2C194D),
                            Color(0xFF3B1E63),
                            Color(0xFF1E0D36)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header (Moves, Level, Score bar, Goal Target)
                TopHeaderBar(
                    uiState = uiState,
                    onBackClick = onBackToMap
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Game Board with gesture handlers
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    GameBoardView(
                        uiState = uiState,
                        onTileClick = { r, c ->
                            scope.launch { engine.onTileClicked(r, c) }
                        },
                        onSwipe = { r, c, dx, dy ->
                            scope.launch { engine.onSwipe(r, c, dx, dy) }
                        }
                    )

                    // Combo / Praise Banner in Center of Board
                    ComboBanner(
                        message = uiState.comboMessage,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Bottom Boosters Bar
                BoosterBar(
                    activeBooster = uiState.activeBooster,
                    profile = profile,
                    onSelectBooster = { booster ->
                        val canUse = playerRepo.useBooster(booster)
                        if (canUse) {
                            engine.selectBooster(booster)
                        }
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Win Dialog
            if (uiState.status == GameStatus.WON) {
                LevelWinDialog(
                    levelData = levelData,
                    score = uiState.score,
                    stars = uiState.starsEarned,
                    onNextLevel = {
                        val nextLvl = LevelRepository.getLevel(levelData.levelNumber + 1)
                        val hasLife = playerRepo.consumeLife()
                        if (hasLife) {
                            onNextLevel(nextLvl)
                        } else {
                            onBackToMap()
                        }
                    },
                    onReplay = {
                        val hasLife = playerRepo.consumeLife()
                        if (hasLife) {
                            onNextLevel(levelData)
                        } else {
                            onBackToMap()
                        }
                    },
                    onBackToMap = onBackToMap
                )
            }

            // Fail Dialog
            if (uiState.status == GameStatus.LOST) {
                LevelFailDialog(
                    levelData = levelData,
                    score = uiState.score,
                    onRetry = {
                        val hasLife = playerRepo.consumeLife()
                        if (hasLife) {
                            onNextLevel(levelData)
                        } else {
                            onBackToMap()
                        }
                    },
                    onAddMoves = {
                        val canBuy = playerRepo.useBooster(BoosterType.EXTRA_MOVES)
                        if (canBuy) {
                            scope.launch {
                                engine.selectBooster(BoosterType.EXTRA_MOVES)
                            }
                        }
                    },
                    onBackToMap = onBackToMap
                )
            }
        }
    }
}
