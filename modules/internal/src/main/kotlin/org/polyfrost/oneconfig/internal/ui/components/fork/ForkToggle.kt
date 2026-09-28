package org.polyfrost.oneconfig.internal.ui.components.fork

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import org.polyfrost.oneconfig.internal.ui.components.onClick
import org.polyfrost.oneconfig.internal.ui.components.rememberInteractionSource
import org.polyfrost.oneconfig.internal.ui.themes.Accent
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme

/**
 * Fork-scoped toggle.
 *
 * Labelless capsule switch. Dimensions come from [ForkTokens]: 24×14 track,
 * 10dp knob, 2dp inset, spring animation. Track is opaque [Accent] when
 * checked, translucent white otherwise (dimmer translucent white pair when
 * disabled); the white knob carries a soft accent halo when checked.
 * All colors from LocalTheme / Accent.
 */
@Composable
fun ForkToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testKey: String? = null,
) {
    val interactionSource = rememberInteractionSource()

    val trackColor = when {
        !enabled -> Color.White.copy(alpha = ForkTokens.Alpha.disabledFill)
        checked -> Accent
        else -> Color.White.copy(alpha = ForkTokens.Alpha.controlFill)
    }
    val knobColor = when {
        !enabled -> Color.White.copy(alpha = ForkTokens.Alpha.disabledContent)
        checked -> Color.White
        else -> Color.White.copy(alpha = 0.5f)
    }
    val thumbOffset by animateDpAsState(
        if (checked) ForkTokens.Size.toggleTrackWidth - ForkTokens.Size.toggleKnob - ForkTokens.Size.toggleInset
        else ForkTokens.Size.toggleInset,
        animationSpec = spring(),
    )

    Box(
        modifier = modifier
            .size(ForkTokens.Size.toggleTrackWidth, ForkTokens.Size.toggleTrackHeight)
            .testBounds(testKey)
            .clip(LocalTheme.current.circleShape)
            .background(trackColor)
            .onClick(interactionSource) { onCheckedChange(!checked) }
            .pointerHoverIcon(PointerIcon.Hand),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = thumbOffset)
                .size(ForkTokens.Size.toggleKnob),
            contentAlignment = Alignment.Center,
        ) {
            if (checked && enabled) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(LocalTheme.current.circleShape)
                        .background(Accent.copy(alpha = ForkTokens.Alpha.glowHalo))
                        .blur(ForkTokens.Size.checkboxGlow),
                )
            }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(LocalTheme.current.circleShape)
                    .background(knobColor),
            )
        }
    }
}
