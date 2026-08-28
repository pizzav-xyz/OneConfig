package org.polyfrost.oneconfig.internal.ui.components.fork

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
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
import org.polyfrost.oneconfig.internal.ui.components.Icon
import org.polyfrost.oneconfig.internal.ui.components.Text
import org.polyfrost.oneconfig.internal.ui.components.onClick
import org.polyfrost.oneconfig.internal.ui.components.rememberInteractionSource
import org.polyfrost.oneconfig.internal.ui.themes.Accent
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme
import androidx.compose.foundation.shape.RoundedCornerShape

/**
 * Fork-scoped checkbox.
 *
 * 16dp box, centered filled accent dot when checked (instead of tick icon).
 * Border inset 0 to prevent overdraw; all colors from LocalTheme / Accent.
 */
@Composable
fun ForkCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalTheme.current
    val interactionSource = rememberInteractionSource()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val bgColor by animateColorAsState(if (checked) Accent else theme.componentBackground)
    val borderColor by animateColorAsState(
        when {
            checked -> Accent
            isHovered -> theme.textColorSecondary
            else -> theme.borderColor
        }
    )

    Box(
        modifier = modifier
            .size(ForkTokens.Size.checkbox)
            .clip(ForkTokens.controlShape)
            .background(bgColor, ForkTokens.controlShape)
            .border(ForkTokens.Size.controlBorder, borderColor, ForkTokens.controlShape)
            .onClick(interactionSource) { onCheckedChange(!checked) }
            .pointerHoverIcon(PointerIcon.Hand),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(checked, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .size(ForkTokens.Padding.checkboxDot)
                    .clip(LocalTheme.current.circleShape)
                    .background(Accent),
            )
        }
    }
}

/**
 * Wrapper: checkbox with an On/Off label, mirroring CheckboxControl.
 */
@Composable
fun ForkCheckboxControl(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ForkTokens.Padding.toggleLabelGap),
        modifier = modifier,
    ) {
        Text(
            label ?: if (checked) "On" else "Off",
            color = LocalTheme.current.textColorSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
        ForkCheckbox(checked, onCheckedChange)
    }
}
