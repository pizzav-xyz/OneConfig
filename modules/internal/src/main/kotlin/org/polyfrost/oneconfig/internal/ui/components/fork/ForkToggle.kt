package org.polyfrost.oneconfig.internal.ui.components.fork

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import org.polyfrost.oneconfig.internal.ui.components.onClick
import org.polyfrost.oneconfig.internal.ui.components.rememberInteractionSource
import org.polyfrost.oneconfig.internal.ui.themes.Accent
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme

/** Explicit capsule shape driven by tokens. */
private val TrackShape = RoundedCornerShape(ForkTokens.Radii.pill)

/**
 * Fork-scoped toggle.
 *
 * Labelless capsule switch. Dimensions come from [ForkTokens]: 28×16 track,
 * 11dp knob, 2dp inset, spring animation. Hover brightens the track.
 * All colors from LocalTheme / Accent.
 */
@Composable
fun ForkToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    testKey: String? = null,
) {
    val interactionSource = rememberInteractionSource()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val trackColor by animateColorAsState(
        if (checked) Accent
        else if (isHovered) lerp(LocalTheme.current.controlTrackColor, Color.White, 0.15f)
        else LocalTheme.current.controlTrackColor
    )
    val thumbOffset by animateDpAsState(
        if (checked) ForkTokens.Size.toggleTrackWidth - ForkTokens.Size.toggleKnob - ForkTokens.Size.toggleInset
        else ForkTokens.Size.toggleInset,
        animationSpec = spring(),
    )

    Box(
        modifier = modifier
            .size(ForkTokens.Size.toggleTrackWidth, ForkTokens.Size.toggleTrackHeight)
            .testBounds(testKey)
            .clip(TrackShape)
            .background(trackColor)
            .onClick(interactionSource) { onCheckedChange(!checked) }
            .pointerHoverIcon(PointerIcon.Hand),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = thumbOffset)
                .size(ForkTokens.Size.toggleKnob)
                .background(LocalTheme.current.controlThumbColor, LocalTheme.current.circleShape),
        )
    }
}
