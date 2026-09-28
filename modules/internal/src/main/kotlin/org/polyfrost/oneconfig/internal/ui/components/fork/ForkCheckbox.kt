package org.polyfrost.oneconfig.internal.ui.components.fork

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.polyfrost.oneconfig.internal.ui.components.Text
import org.polyfrost.oneconfig.internal.ui.components.onClick
import org.polyfrost.oneconfig.internal.ui.components.rememberInteractionSource
import org.polyfrost.oneconfig.internal.ui.themes.Accent
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme

/**
 * Fork-scoped checkbox.
 *
 * 14dp box with translucent coral fill, centered glowing accent dot when
 * checked (instead of tick icon). All colors from LocalTheme / Accent.
 */
@Composable
fun ForkCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    testKey: String? = null,
) {
    val interactionSource = rememberInteractionSource()

    val bgColor = if (checked) Accent.copy(alpha = ForkTokens.Alpha.controlFill) else Color.Transparent

    Box(
        modifier = modifier
            .size(ForkTokens.Size.checkbox)
            .testBounds(testKey)
            .clip(ForkTokens.controlShape)
            .background(bgColor, ForkTokens.controlShape)
            .border(ForkTokens.Size.controlBorder, Color.White.copy(alpha = ForkTokens.Alpha.checkboxBorder), ForkTokens.controlShape)
            .onClick(interactionSource) { onCheckedChange(!checked) }
            .pointerHoverIcon(PointerIcon.Hand),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(checked, enter = fadeIn(), exit = fadeOut()) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(ForkTokens.Padding.checkboxDot)
                        .clip(LocalTheme.current.circleShape)
                        .background(Accent.copy(alpha = ForkTokens.Alpha.glowHalo))
                        .blur(ForkTokens.Size.checkboxGlow),
                )
                Box(
                    modifier = Modifier
                        .size(ForkTokens.Padding.checkboxDot - ForkTokens.Size.controlBorder * 2)
                        .clip(LocalTheme.current.circleShape)
                        .background(Accent),
                )
            }
        }
    }
}

/**
 * Wrapper: checkbox with an optional label. No On/Off fallback — when the
 * label is null the row renders the checkbox alone.
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
        if (label != null) {
            Text(
                label,
                color = LocalTheme.current.textColorSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        ForkCheckbox(checked, onCheckedChange)
    }
}
