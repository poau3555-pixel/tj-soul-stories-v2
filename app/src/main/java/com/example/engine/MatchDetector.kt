package com.example.engine

import com.example.model.BoardTile
import com.example.model.Candy
import com.example.model.CandyColor
import com.example.model.SpecialType

data class MatchResult(
    val matchedPositions: Set<Pair<Int, Int>>,
    val specialCreations: Map<Pair<Int, Int>, Candy> // position to created special candy
)

object MatchDetector {

    fun findMatches(
        board: Array<Array<BoardTile>>,
        lastSwappedPos: Pair<Int, Int>? = null
    ): MatchResult {
        val rows = board.size
        val cols = board[0].size

        val horizontalMatches = mutableListOf<List<Pair<Int, Int>>>()
        val verticalMatches = mutableListOf<List<Pair<Int, Int>>>()

        // 1. Scan Horizontal matches
        for (r in 0 until rows) {
            var currentRun = mutableListOf<Pair<Int, Int>>()
            var currentColor: CandyColor? = null

            for (c in 0 until cols) {
                val tile = board[r][c]
                val candy = tile.candy

                if (tile.isPlayable && candy != null && !candy.isIngredient && !candy.isColorBomb) {
                    if (candy.color == currentColor) {
                        currentRun.add(Pair(r, c))
                    } else {
                        if (currentRun.size >= 3) {
                            horizontalMatches.add(currentRun.toList())
                        }
                        currentRun = mutableListOf(Pair(r, c))
                        currentColor = candy.color
                    }
                } else {
                    if (currentRun.size >= 3) {
                        horizontalMatches.add(currentRun.toList())
                    }
                    currentRun.clear()
                    currentColor = null
                }
            }
            if (currentRun.size >= 3) {
                horizontalMatches.add(currentRun.toList())
            }
        }

        // 2. Scan Vertical matches
        for (c in 0 until cols) {
            var currentRun = mutableListOf<Pair<Int, Int>>()
            var currentColor: CandyColor? = null

            for (r in 0 until rows) {
                val tile = board[r][c]
                val candy = tile.candy

                if (tile.isPlayable && candy != null && !candy.isIngredient && !candy.isColorBomb) {
                    if (candy.color == currentColor) {
                        currentRun.add(Pair(r, c))
                    } else {
                        if (currentRun.size >= 3) {
                            verticalMatches.add(currentRun.toList())
                        }
                        currentRun = mutableListOf(Pair(r, c))
                        currentColor = candy.color
                    }
                } else {
                    if (currentRun.size >= 3) {
                        verticalMatches.add(currentRun.toList())
                    }
                    currentRun.clear()
                    currentColor = null
                }
            }
            if (currentRun.size >= 3) {
                verticalMatches.add(currentRun.toList())
            }
        }

        val allMatchedPositions = mutableSetOf<Pair<Int, Int>>()
        val specialCreations = mutableMapOf<Pair<Int, Int>, Candy>()

        // Check for T and L shapes (Wrapped Candies)
        val hMatchesByTile = mutableMapOf<Pair<Int, Int>, List<Pair<Int, Int>>>()
        for (h in horizontalMatches) {
            for (p in h) {
                hMatchesByTile[p] = h
            }
        }

        val vMatchesByTile = mutableMapOf<Pair<Int, Int>, List<Pair<Int, Int>>>()
        for (v in verticalMatches) {
            for (p in v) {
                vMatchesByTile[p] = v
            }
        }

        val processedRuns = mutableSetOf<List<Pair<Int, Int>>>()

        // Check intersections (T/L shape -> Wrapped Candy)
        for ((pos, hRun) in hMatchesByTile) {
            val vRun = vMatchesByTile[pos]
            if (vRun != null) {
                // Intersection found!
                val color = board[pos.first][pos.second].candy?.color ?: CandyColor.RED
                val creationPos = if (lastSwappedPos != null && (lastSwappedPos in hRun || lastSwappedPos in vRun)) {
                    lastSwappedPos
                } else {
                    pos
                }
                specialCreations[creationPos] = Candy(color = color, special = SpecialType.WRAPPED)
                allMatchedPositions.addAll(hRun)
                allMatchedPositions.addAll(vRun)
                processedRuns.add(hRun)
                processedRuns.add(vRun)
            }
        }

        // Process Horizontal runs
        for (h in horizontalMatches) {
            if (h in processedRuns) continue
            allMatchedPositions.addAll(h)
            val color = board[h[0].first][h[0].second].candy?.color ?: CandyColor.RED

            if (h.size >= 5) {
                // Color Bomb
                val creationPos = if (lastSwappedPos != null && lastSwappedPos in h) lastSwappedPos else h[h.size / 2]
                specialCreations[creationPos] = Candy(color = color, special = SpecialType.COLOR_BOMB)
            } else if (h.size == 4) {
                // Striped Vertical (sweeps columns or swapped horizontally)
                val creationPos = if (lastSwappedPos != null && lastSwappedPos in h) lastSwappedPos else h[h.size / 2]
                specialCreations[creationPos] = Candy(color = color, special = SpecialType.STRIPED_VERTICAL)
            }
        }

        // Process Vertical runs
        for (v in verticalMatches) {
            if (v in processedRuns) continue
            allMatchedPositions.addAll(v)
            val color = board[v[0].first][v[0].second].candy?.color ?: CandyColor.RED

            if (v.size >= 5) {
                // Color Bomb
                val creationPos = if (lastSwappedPos != null && lastSwappedPos in v) lastSwappedPos else v[v.size / 2]
                specialCreations[creationPos] = Candy(color = color, special = SpecialType.COLOR_BOMB)
            } else if (v.size == 4) {
                // Striped Horizontal
                val creationPos = if (lastSwappedPos != null && lastSwappedPos in v) lastSwappedPos else v[v.size / 2]
                specialCreations[creationPos] = Candy(color = color, special = SpecialType.STRIPED_HORIZONTAL)
            }
        }

        return MatchResult(allMatchedPositions, specialCreations)
    }

    fun hasPossibleMoves(board: Array<Array<BoardTile>>): Boolean {
        val rows = board.size
        val cols = board[0].size

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val tile = board[r][c]
                val candy = tile.candy ?: continue
                if (!tile.isPlayable || tile.isBlocked) continue

                // Color bomb always has a move if adjacent to any candy
                if (candy.isColorBomb) {
                    val neighbors = listOf(Pair(r - 1, c), Pair(r + 1, c), Pair(r, c - 1), Pair(r, c + 1))
                    if (neighbors.any { (nr, nc) -> nr in 0 until rows && nc in 0 until cols && board[nr][nc].candy != null }) {
                        return true
                    }
                }

                // Check right swap
                if (c + 1 < cols && board[r][c + 1].isPlayable && !board[r][c + 1].isBlocked && board[r][c + 1].candy != null) {
                    if (testSwapCreatesMatch(board, r, c, r, c + 1)) return true
                }
                // Check down swap
                if (r + 1 < rows && board[r + 1][c].isPlayable && !board[r + 1][c].isBlocked && board[r + 1][c].candy != null) {
                    if (testSwapCreatesMatch(board, r, c, r + 1, c)) return true
                }
            }
        }
        return false
    }

    private fun testSwapCreatesMatch(
        board: Array<Array<BoardTile>>,
        r1: Int, c1: Int,
        r2: Int, c2: Int
    ): Boolean {
        val candy1 = board[r1][c1].candy ?: return false
        val candy2 = board[r2][c2].candy ?: return false

        // Special combo checks
        if (candy1.special != SpecialType.NONE && candy2.special != SpecialType.NONE) return true
        if (candy1.isColorBomb || candy2.isColorBomb) return true

        // Swap temporarily
        board[r1][c1] = board[r1][c1].copy(candy = candy2)
        board[r2][c2] = board[r2][c2].copy(candy = candy1)

        val matches = findMatches(board)

        // Restore
        board[r1][c1] = board[r1][c1].copy(candy = candy1)
        board[r2][c2] = board[r2][c2].copy(candy = candy2)

        return matches.matchedPositions.isNotEmpty()
    }
}
