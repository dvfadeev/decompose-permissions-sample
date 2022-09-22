@file:Suppress("MatchingDeclarationName")

package com.decomposepermissions.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

@Immutable
data class AppShapes(
    val small: CornerBasedShape = RoundedCornerShape(8.dp),
    val medium: CornerBasedShape = RoundedCornerShape(12.dp),
    val large: CornerBasedShape = RoundedCornerShape(16.dp),
)

fun AppShapes.toMaterialShapes(): Shapes {
    return Shapes(
        small = small,
        medium = medium,
        large = large
    )
}

val LocalAppShapes = staticCompositionLocalOf {
    AppShapes()
}
