package org.polyfrost.oneconfig.internal.ui.components.fork

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.polyfrost.oneconfig.internal.ui.components.Text
import org.polyfrost.oneconfig.internal.ui.components.onClick
import org.polyfrost.oneconfig.internal.ui.components.rememberInteractionSource
import org.polyfrost.oneconfig.internal.ui.themes.Accent
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme

/** Explicit capsule shape driven by tokens. */
private val TrackShape = RoundedCornerShape(ForkTokens.Radii.pill)

/**
 * Fork-scoped toggle.
 *
 * Dimensions and spacing come from [ForkTokens]: 36×18 track, 14dp knob, 2dp inset,
 * spring animation, on/off label. All colors from LocalTheme / Accent.
 */
@Composable
fun ForkToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = rememberInteractionSource()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val trackColor by animateColorAsState(if (checked) Accent else LocalTheme.current.controlTrackColor)
    val thumbOffset by animateDpAsState(
        if (checked) ForkTokens.Size.toggleTrackWidth - ForkTokens.Size.toggleKnob - ForkTokens.Size.toggleInset
        else ForkTokens.Size.toggleInset,
        animationSpec = spring(),
    )
    val borderColor by animateColorAsState(
        if (isHovered) LocalTheme.current.textColor.copy(alpha = 0.15f) else Color.Transparent
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ForkTokens.Padding.toggleLabelGap),
        modifier = modifier,
    ) {
        Text(
            if (checked) "On" else "Off",
            color = LocalTheme.current.textColorSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Box(
            modifier = Modifier
                .size(ForkTokens.Size.toggleTrackWidth, ForkTokens.Size.toggleTrackHeight)
                .clip(TrackShape)
                .background(trackColor)
                .border(ForkTokens.Size.controlBorder, borderColor, TrackShape)
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
}
