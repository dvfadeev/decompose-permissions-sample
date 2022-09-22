package com.decomposepermissions.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.decomposepermissions.R

private val roboto = FontFamily(
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_bold, FontWeight.Bold)
)

@Immutable
data class AppTypography constructor(
    val body: TextStyle,
    val bodyMedium: TextStyle,
    val caption: TextStyle,
    val header: TextStyle
) {
    constructor(
        defaultFontFamily: FontFamily = FontFamily.Default,
        bodyA: TextStyle = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            letterSpacing = 0.5.sp,
            lineHeight = 18.75.sp
        ),
        bodyAMedium: TextStyle = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            letterSpacing = 0.5.sp,
            lineHeight = 18.75.sp
        ),
        captionAMedium: TextStyle = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            letterSpacing = 0.4.sp,
            lineHeight = 14.06.sp
        ),
        headerA: TextStyle = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            letterSpacing = 0.0.sp,
            lineHeight = 28.13.sp
        )
    ) : this(
        body = bodyA.withDefaultFontFamily(defaultFontFamily),
        bodyMedium = bodyAMedium.withDefaultFontFamily(defaultFontFamily),
        caption = captionAMedium.withDefaultFontFamily(defaultFontFamily),
        header = headerA.withDefaultFontFamily(defaultFontFamily)
    )
}

fun AppTypography.toMaterialTypography(): Typography {
    return Typography(
        button = bodyMedium,
        h6 = header,
        body1 = body,
        caption = caption
    )
}

val LocalAppTypography = staticCompositionLocalOf {
    AppTypography(defaultFontFamily = roboto)
}

val MaterialTheme.appTypography: AppTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTypography.current

private fun TextStyle.withDefaultFontFamily(default: FontFamily): TextStyle {
    return if (fontFamily != null) this else copy(fontFamily = default)
}

