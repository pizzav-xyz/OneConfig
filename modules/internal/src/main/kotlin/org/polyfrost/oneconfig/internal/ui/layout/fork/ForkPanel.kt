package org.polyfrost.oneconfig.internal.ui.layout.fork

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkTokens
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme

/** Fraction of the viewport height the panel may grow to (reference §1: 82%). */
private const val PanelMaxHeightFraction = 0.82f

/**
 * Centered floating config panel (reference §1 Shell).
 *
 * Max width [ForkTokens.Size.panelMaxWidth], max height 82% of the viewport,
 * panel radius, 16dp shadow, card fill at 100%, no border.
 * Height is intrinsic up to the max: short content packs tight, tall content
 * is capped so the grid scrolls *inside* the panel while the rail stays fixed.
 */
@Composable
fun ForkPanel(
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        val theme = LocalTheme.current
        val panelWidth =
            if (maxWidth >= ForkTokens.Size.panelMaxWidth) ForkTokens.Size.panelMaxWidth
            else maxWidth - ForkTokens.Padding.gridEmptyTop * 2
        Box(
            modifier = Modifier
                .width(panelWidth)
                .heightIn(min = ForkTokens.Size.panelMinHeight, max = maxHeight * PanelMaxHeightFraction)
                .shadow(
                    ForkTokens.Size.panelShadow,
                    ForkTokens.panelShape,
                    clip = false,
                    ambientColor = Color.Black.copy(alpha = 0.6f),
                    spotColor = Color.Black.copy(alpha = 0.6f),
                )
                .background(theme.modCardBackground, ForkTokens.panelShape)
                .clip(ForkTokens.panelShape),
        ) {
            CappedIntrinsicHeight(content)
        }
    }
}

/**
 * Measures [content] at its intrinsic height, capped to the incoming max height,
 * then re-measures exactly at that height so `fillMaxHeight` children (the rail)
 * stretch to the panel while scrollable children (the grid) scroll within it.
 */
@Composable
private fun CappedIntrinsicHeight(
    content: @Composable () -> Unit,
) {
    Layout(content) { measurables, constraints ->
        val measurable = measurables.single()
        val contentHeight = measurable.minIntrinsicHeight(constraints.maxWidth)
        val height = contentHeight.coerceIn(constraints.minHeight, constraints.maxHeight)
        val placeable = measurable.measure(
            constraints.copy(minHeight = height, maxHeight = height),
        )
        layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
        }
    }
}
