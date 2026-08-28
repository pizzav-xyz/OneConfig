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

/** Explicit capsule shape: 36×18 r=9. */
private val TrackShape = RoundedCornerShape(9.dp)

/**
 * Fork-scoped toggle, adapted from SwitchControl.
 *
 * 36×18dp track, 14dp knob, 2dp inset, spring animation, on/off label.
 * All colors come from LocalTheme / Accent — no hard-coded literals.
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
    // 36×18 track, 14 knob, 2dp inset each side → off=2dp, on=36-14-2=20dp
    val thumbOffset by animateDpAsState(if (checked) 20.dp else 2.dp, animationSpec = spring())
    val borderColor by animateColorAsState(
        if (isHovered) LocalTheme.current.textColor.copy(alpha = 0.15f) else Color.Transparent
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                .size(36.dp, 18.dp)
                .clip(TrackShape)
                .background(trackColor)
                .border(1.dp, borderColor, TrackShape)
                .onClick(interactionSource) { onCheckedChange(!checked) }
                .pointerHoverIcon(PointerIcon.Hand),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = thumbOffset)
                    .size(14.dp)
                    .background(LocalTheme.current.controlThumbColor, LocalTheme.current.circleShape),
            )
        }
    }
}
