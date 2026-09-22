package com.example.model

data class BoardTile(
    val row: Int,
    val col: Int,
    val isPlayable: Boolean = true,
    val candy: Candy? = null,
    val jellyLayers: Int = 0, // 0 = none, 1 = single jelly, 2 = double jelly
    val obstacle: ObstacleType = ObstacleType.NONE,
    val isSpawner: Boolean = (row == 0) // Top spawners
) {
    val hasJelly: Boolean
        get() = jellyLayers > 0

    val hasObstacle: Boolean
        get() = obstacle != ObstacleType.NONE

    val isBlocked: Boolean
        get() = !isPlayable || obstacle == ObstacleType.CHOCOLATE || obstacle == ObstacleType.FROSTING_1 || obstacle == ObstacleType.FROSTING_2
}
