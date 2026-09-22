package com.example.model

import java.util.UUID

data class Candy(
    val id: String = UUID.randomUUID().toString(),
    val color: CandyColor,
    val special: SpecialType = SpecialType.NONE,
    val isMatched: Boolean = false,
    val isExploding: Boolean = false,
    val isClearing: Boolean = false
) {
    val isIngredient: Boolean
        get() = special == SpecialType.INGREDIENT_CHERRY || special == SpecialType.INGREDIENT_HAZELNUT

    val isColorBomb: Boolean
        get() = special == SpecialType.COLOR_BOMB

    val isStriped: Boolean
        get() = special == SpecialType.STRIPED_HORIZONTAL || special == SpecialType.STRIPED_VERTICAL

    val isWrapped: Boolean
        get() = special == SpecialType.WRAPPED
}
