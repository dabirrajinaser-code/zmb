package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val LocalFontSizeMultiplier = compositionLocalOf { 1.0f }

private val WaterLightColorScheme = lightColorScheme(
    primary = WaterAqua800,
    onPrimary = PureWhite,
    primaryContainer = WaterAqua100,
    onPrimaryContainer = WaterAqua900,
    secondary = OceanBlue500,
    onSecondary = PureWhite,
    secondaryContainer = WaterAqua50,
    onSecondaryContainer = OceanBlue700,
    tertiary = WaterAqua600,
    background = PureWhite,
    onBackground = Color(0xFF1A262F),
    surface = PureWhite,
    onSurface = Color(0xFF1A262F),
    surfaceVariant = CrystalWaterLight,
    onSurfaceVariant = Color(0xFF37474F),
    outline = WaterAqua200,
    outlineVariant = WaterAqua100
)

private val WaterDarkColorScheme = darkColorScheme(
    primary = WaterAqua400,
    onPrimary = OceanDeepDark,
    primaryContainer = OceanCardDark,
    onPrimaryContainer = WaterAqua100,
    secondary = WaterAqua200,
    onSecondary = OceanDeepDark,
    secondaryContainer = OceanSurfaceDark,
    onSecondaryContainer = WaterAqua50,
    tertiary = WaterAqua400,
    background = OceanDeepDark,
    onBackground = PureWhite,
    surface = OceanSurfaceDark,
    onSurface = PureWhite,
    surfaceVariant = OceanCardDark,
    onSurfaceVariant = WaterAqua100,
    outline = WaterAqua700,
    outlineVariant = OceanCardDark
)

fun getScaledTypography(multiplier: Float): Typography {
    return Typography(
        displayLarge = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = (32 * multiplier).sp),
        titleLarge = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = (22 * multiplier).sp),
        titleMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = (18 * multiplier).sp),
        titleSmall = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = (15 * multiplier).sp),
        bodyLarge = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = (16 * multiplier).sp, lineHeight = (24 * multiplier).sp),
        bodyMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = (14 * multiplier).sp, lineHeight = (20 * multiplier).sp),
        bodySmall = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = (12 * multiplier).sp),
        labelLarge = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = (14 * multiplier).sp),
        labelMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = (12 * multiplier).sp),
        labelSmall = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = (10 * multiplier).sp)
    )
}

@Composable
fun ZihanFishTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fontMultiplier: Float = 1.0f,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) WaterDarkColorScheme else WaterLightColorScheme
    val typography = getScaledTypography(fontMultiplier)

    CompositionLocalProvider(LocalFontSizeMultiplier provides fontMultiplier) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content
        )
    }
}
