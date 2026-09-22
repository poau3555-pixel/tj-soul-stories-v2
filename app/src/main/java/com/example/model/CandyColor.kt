package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CandyBlue
import com.example.ui.theme.CandyBlueDark
import com.example.ui.theme.CandyBlueLight
import com.example.ui.theme.CandyGreen
import com.example.ui.theme.CandyGreenDark
import com.example.ui.theme.CandyGreenLight
import com.example.ui.theme.CandyOrange
import com.example.ui.theme.CandyOrangeDark
import com.example.ui.theme.CandyOrangeLight
import com.example.ui.theme.CandyPurple
import com.example.ui.theme.CandyPurpleDark
import com.example.ui.theme.CandyPurpleLight
import com.example.ui.theme.CandyRed
import com.example.ui.theme.CandyRedDark
import com.example.ui.theme.CandyRedLight
import com.example.ui.theme.CandyYellow
import com.example.ui.theme.CandyYellowDark
import com.example.ui.theme.CandyYellowLight

enum class CandyColor(
    val displayName: String,
    val primaryColor: Color,
    val darkColor: Color,
    val lightColor: Color,
    val shapeType: CandyShape
) {
    RED("Red Bean", CandyRed, CandyRedDark, CandyRedLight, CandyShape.BEAN),
    ORANGE("Orange Lozenge", CandyOrange, CandyOrangeDark, CandyOrangeLight, CandyShape.OVAL),
    YELLOW("Lemon Drop", CandyYellow, CandyYellowDark, CandyYellowLight, CandyShape.DROP),
    GREEN("Green Chiclet", CandyGreen, CandyGreenDark, CandyGreenLight, CandyShape.SQUARE),
    BLUE("Blue Lollipop", CandyBlue, CandyBlueDark, CandyBlueLight, CandyShape.CIRCLE),
    PURPLE("Purple Cluster", CandyPurple, CandyPurpleDark, CandyPurpleLight, CandyShape.TEARDROP);

    companion object {
        fun random(allowedColors: List<CandyColor> = entries): CandyColor {
            return allowedColors.random()
        }
    }
}

enum class CandyShape {
    BEAN,
    OVAL,
    DROP,
    SQUARE,
    CIRCLE,
    TEARDROP
}
