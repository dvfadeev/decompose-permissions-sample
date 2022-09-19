package com.decomposepermissions.theme

import androidx.compose.material.Colors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

object RawColors {
    val grayA: Color = Color(0xFF9E9E9E)
    val grayB: Color = Color(0xFF616161)
    val grayC: Color = Color(0xFFF5F5F5)
    val whiteA: Color = Color(0xFFFFFFFF)
    val blackA: Color = Color(0xFF000000)
    val redA: Color = Color(0xFFE26060)
}

fun getMaterialLightColors(): Colors {
    return lightColors(
        primary = RawColors.grayA,
        primaryVariant = RawColors.grayB,
        secondary = RawColors.grayA,
        secondaryVariant = RawColors.grayB,
        background = RawColors.grayC,
        surface = RawColors.grayC,
        error = RawColors.redA,
        onPrimary = RawColors.whiteA,
        onSecondary = RawColors.whiteA,
        onBackground = RawColors.blackA,
        onSurface = RawColors.blackA
    )
}
