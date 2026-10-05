package com.example.ui.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Water Liquid Glossy Container Composable.
 * Features:
 * - Pure white / crystal backdrop
 * - Liquid glass subtle gradient
 * - Shimmering watery border highlight
 * - Interactive Liquid Wave / Droplet Ripple on press
 */
@Composable
fun LiquidGlossyCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    borderWidth: Dp = 1.dp,
    elevation: Dp = 2.dp,
    isDark: Boolean = isSystemInDarkTheme(),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    var touchPosition by remember { mutableStateOf<Offset?>(null) }
    val rippleRadius = remember { Animatable(0f) }
    val rippleAlpha = remember { Animatable(0f) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    touchPosition = interaction.pressPosition
                    rippleRadius.snapTo(10f)
                    rippleAlpha.snapTo(0.45f)
                    rippleRadius.animateTo(
                        targetValue = 600f,
                        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)
                    )
                }
                is PressInteraction.Release, is PressInteraction.Cancel -> {
                    rippleAlpha.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 350)
                    )
                    touchPosition = null
                }
            }
        }
    }

    val backgroundBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                OceanSurfaceDark.copy(alpha = 0.95f),
                OceanCardDark.copy(alpha = 0.90f)
            )
        )
    } else {
        // Pure white with subtle crystal water gloss
        Brush.verticalGradient(
            colors = listOf(
                PureWhite,
                CrystalWaterLight,
                WaterAqua50.copy(alpha = 0.40f)
            )
        )
    }

    val borderBrush = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                WaterAqua400.copy(alpha = 0.4f),
                OceanCardDark,
                WaterAqua600.copy(alpha = 0.3f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                PureWhite,
                WaterAqua200.copy(alpha = 0.7f),
                WaterAqua400.copy(alpha = 0.3f)
            )
        )
    }

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundBrush, shape)
            .border(borderWidth, borderBrush, shape)
            .then(clickableModifier)
            .drawWithContent {
                drawContent()
                // Draw dynamic water liquid glossy ripple when pressed
                touchPosition?.let { pos ->
                    if (rippleAlpha.value > 0f) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    WaterAqua400.copy(alpha = rippleAlpha.value),
                                    WaterAqua200.copy(alpha = rippleAlpha.value * 0.7f),
                                    WaterAqua100.copy(alpha = rippleAlpha.value * 0.3f),
                                    Color.Transparent
                                ),
                                center = pos,
                                radius = rippleRadius.value
                            ),
                            center = pos,
                            radius = rippleRadius.value
                        )
                    }
                }
            }
    ) {
        content()
    }
}
