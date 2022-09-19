package com.decomposepermissions.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

@Immutable
data class AppShapes(
    val field: CornerBasedShape = RoundedCornerShape(4.dp),
    val button: CornerBasedShape = RoundedCornerShape(8.dp),
    val card: CornerBasedShape = RoundedCornerShape(12.dp),
    val sheet: CornerBasedShape = RoundedCornerShape(16.dp),
    val textField: CornerBasedShape = RoundedCornerShape(8.dp)
)

fun AppShapes.toMaterialShapes(): Shapes {
    return Shapes(
        small = button,
        medium = card,
        large = sheet
    )
}

val LocalAppShapes = staticCompositionLocalOf {
    AppShapes()
}

val MaterialTheme.appShapes: AppShapes
    @Composable
    @ReadOnlyComposable
    get() = LocalAppShapes.current

