package org.polyfrost.oneconfig.internal.ui.components.fork

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import org.polyfrost.oneconfig.internal.ui.themes.Accent
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme
import kotlin.math.roundToInt

/**
 * Fork-scoped slider, adapted from SliderControl.
 *
 * Thin track, round thumb, pointerInput drag, min/max/step snapping.
 * All colors come from LocalTheme / Accent — no hard-coded literals.
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
    thumbSize: Dp = 19.dp,
    trackHeight: Dp = 5.dp,
) {
    val theme = LocalTheme.current
    var trackWidthPx by remember { mutableStateOf(0f) }
    val fraction by animateFloatAsState(
        ((value - min) / (max - min)).coerceIn(0f, 1f),
        animationSpec = androidx.compose.animation.core.spring(),
    )

    Box(
        modifier = modifier
            .height(thumbSize)
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
                .background(Accent)
        )
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .offset { IntOffset((fraction * (trackWidthPx - thumbSize.toPx())).toInt(), 0) }
                .size(thumbSize)
                .background(theme.controlThumbColor, theme.circleShape)
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
