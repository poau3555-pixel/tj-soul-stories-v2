package com.example.model

enum class ObstacleType {
    NONE,
    CHOCOLATE,      // Blocks tile, multiplies if turn makes no chocolate hits
    FROSTING_1,     // Breaks in 1 adjacent match
    FROSTING_2,     // Breaks in 2 hits (transforms to FROSTING_1)
    LOCK_CHAIN      // Traps candy in place until matched
}
