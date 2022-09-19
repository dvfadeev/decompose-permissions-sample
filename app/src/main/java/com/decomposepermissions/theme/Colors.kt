package com.decomposepermissions.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object RawColors {
    val greenA: Color = Color(0xFF95C554)
    val greenB: Color = Color(0xFFF1F8E8)
    val blueA: Color = Color(0xFF31C3D5)
    val blueB: Color = Color(0xFFECF9FB)
    val blueC: Color = Color(0xFF359EFF)
    val orangeA: Color = Color(0xFFF7931D)
    val orangeB: Color = Color(0xFFFFF5EA)
    val orangeC: Color = Color(0xFFFCD2A2)
    val whiteA: Color = Color(0xFFFFFFFF)
    val whiteAAlpha20: Color = Color(0x33FFFFFF)
    val whiteAAlpha70: Color = Color(0xB3FFFFFF)
    val grayA: Color = Color(0xFFA1ACB4)
    val grayB: Color = Color(0xFFF3F4F7)
    val grayC: Color = Color(0xFFD9DCE7)
    val grayDAlfa70: Color = Color(0xB37F8B8F)
    val blackA: Color = Color(0xFF000000)
    val redA: Color = Color(0xFFE26060)
    val gradientA: Brush = Brush.linearGradient(
        1.0f to whiteA,
        0.7f to whiteA
    )
    val gradientB: Brush = Brush.linearGradient(
        1.0f to Color(0xFFF3F4F7),
        0.7f to Color(0xFFF3F4F7)
    )
    val violetA: Color = Color(0xFFAD00FF)
    val greenC: Color = Color(0xFF77D970)
    val yellowA: Color = Color(0xFFFFE459)
    val pinkA: Color = Color(0xFFFF35AE)
}

// TODO: добавить цвета, которых не хватает в Material палитре
@Immutable
data class AdditionalColors(
    val secondaryOnSurface: Color = RawColors.grayA,
    val secondaryOnBackground: Color = RawColors.grayA,
    val tertiaryOnSurface: Color = RawColors.grayC,
    val tertiaryOnBackground: Color = RawColors.grayC,
    val quaternaryOnBackground: Color = RawColors.grayB,
    val success: Color = RawColors.greenA,
    val warning: Color = RawColors.orangeA,
    val imageOverlay: Color = RawColors.grayDAlfa70
)

val LocalAdditionalColors = staticCompositionLocalOf {
    AdditionalColors()
}

fun getMaterialLightColors(): Colors {
    return lightColors(
        primary = RawColors.orangeA,
        primaryVariant = RawColors.orangeB,
        secondary = RawColors.orangeA,
        secondaryVariant = RawColors.orangeB,
        background = RawColors.whiteA,
        surface = RawColors.grayB,
        error = RawColors.redA,
        onPrimary = RawColors.whiteA,
        onSecondary = RawColors.whiteA,
        onBackground = RawColors.blackA,
        onSurface = RawColors.blackA
    )
}

val MaterialTheme.additionalColors: AdditionalColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAdditionalColors.current

