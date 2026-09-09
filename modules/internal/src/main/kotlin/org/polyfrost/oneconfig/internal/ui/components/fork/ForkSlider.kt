package org.polyfrost.oneconfig.internal.ui.components.fork

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.polyfrost.oneconfig.internal.ui.components.Text
import org.polyfrost.oneconfig.internal.ui.components.rememberInteractionSource
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme
import kotlin.math.roundToInt

/**
 * Fork-scoped slider.
 *
 * Thin track from [ForkTokens.Size.sliderTrack], round thumb from [ForkTokens.Size.sliderThumb],
 * pointerInput drag, min/max/step snapping. All colors from LocalTheme / Accent.
 *
 * Pure-logic [snap] is extracted as a top-level function so it can be unit-tested.
 */
@Composable
fun ForkSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    min: Float,
    max: Float,
    step: Float,
    modifier: Modifier = Modifier,
    thumbSize: Dp = ForkTokens.Size.sliderThumb,
    trackHeight: Dp = ForkTokens.Size.sliderTrack,
    testKey: String? = null,
) {
    val theme = LocalTheme.current
    var trackWidthPx by remember { mutableStateOf(0f) }
    val fraction by animateFloatAsState(
        ((value - min) / (max - min)).coerceIn(0f, 1f),
        animationSpec = androidx.compose.animation.core.spring(),
    )
    val interactionSource = rememberInteractionSource()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val fillColor by animateColorAsState(
        if (isHovered) lerp(theme.accentColor, Color.White, 0.15f) else theme.accentColor
    )

    Box(
        modifier = modifier
            .height(thumbSize)
            .testBounds(testKey)
            .hoverable(interactionSource)
            .onSizeChanged { trackWidthPx = it.width.toFloat() }
            .pointerInput(min, max, step) {
                val thumbPx = thumbSize.toPx()
                awaitEachGesture {
                    val down = awaitFirstDown()
                    onValueChange(snap(min, max, step, down.position.x, trackWidthPx, thumbPx))
                    do {
                        val event = awaitPointerEvent()
                        event.changes.forEach { ch ->
                            if (ch.pressed) {
                                onValueChange(snap(min, max, step, ch.position.x, trackWidthPx, thumbPx))
                                ch.consume()
                            }
                        }
                    } while (event.changes.any { it.pressed })
                }
            }
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(trackHeight)
                .align(Alignment.CenterStart)
                .clip(theme.checkBoxShape)
                .background(theme.controlTrackColor)
        )
        Box(
            Modifier
                .fillMaxWidth(fraction)
                .height(trackHeight)
                .align(Alignment.CenterStart)
                .clip(theme.checkBoxShape)
                .background(fillColor)
        )
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .offset { IntOffset((fraction * (trackWidthPx - thumbSize.toPx())).toInt(), 0) }
                .size(thumbSize)
                .drawWithContent {
                    // Solid ring in track color prevents alpha-blend halo.
                    drawCircle(
                        color = theme.controlTrackColor,
                        radius = this.size.minDimension / 2f,
                    )
                    drawCircle(
                        color = theme.controlThumbColor,
                        radius = this.size.minDimension / 2f - ForkTokens.Size.sliderThumbRing.toPx(),
                    )
                }
        )
    }
}

/** Snaps a raw x-position to the nearest step within [min, max]. */
fun snap(min: Float, max: Float, step: Float, x: Float, trackWidthPx: Float, thumbPx: Float): Float {
    val usable = (trackWidthPx - thumbPx).coerceAtLeast(1f)
    val ratio = ((x - thumbPx / 2f) / usable).coerceIn(0f, 1f)
    val raw = min + ratio * (max - min)
    val clamped = raw.coerceIn(min, max)
    if (step <= 0f) return clamped
    return (min + ((clamped - min) / step).roundToInt() * step).coerceIn(min, max)
}

/**
 * Compact slider row (reference §5): label left with the value right-aligned on
 * the same row, full-width track beneath.
 */
@Composable
fun ForkSliderRow(
    label: String,
    valueText: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    min: Float,
    max: Float,
    step: Float,
    modifier: Modifier = Modifier,
    testKey: String? = null,
) {
    val theme = LocalTheme.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ForkTokens.Padding.controlRowVertical),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                label,
                color = theme.textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                valueText,
                color = theme.textColorSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
            )
        }
        ForkSlider(
            value = value,
            onValueChange = onValueChange,
            min = min,
            max = max,
            step = step,
            modifier = Modifier.fillMaxWidth(),
            testKey = testKey,
        )
    }
}
