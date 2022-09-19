package com.decomposepermissions.theme

import android.annotation.SuppressLint
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@SuppressLint("UnrememberedMutableState")
@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val materialColors = getMaterialLightColors()
    val additionalColors = AdditionalColors()
    val appTypography = AppTypography()
    val appShapes = AppShapes()

    CompositionLocalProvider(
        LocalAdditionalColors provides additionalColors,
        LocalAppTypography provides appTypography,
        LocalAppShapes provides appShapes
    ) {
        MaterialTheme(
            colors = materialColors,
            typography = appTypography.toMaterialTypography(),
            shapes = appShapes.toMaterialShapes(),
            content = content
        )
    }
}
