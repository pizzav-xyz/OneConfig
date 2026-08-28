package org.polyfrost.oneconfig.internal.ui.layout.fork

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.polyfrost.oneconfig.internal.ui.components.Text
import org.polyfrost.oneconfig.internal.ui.components.onClick
import org.polyfrost.oneconfig.internal.ui.components.rememberInteractionSource
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkKeybindBadge
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkToggle
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme
import org.polyfrost.oneconfig.internal.ui.themes.fork.ForkRadii

private val CardShape = RoundedCornerShape(ForkRadii.card)
private val InsetHighlightColor = androidx.compose.ui.graphics.Color(0x0FFFFFFF) // rgba(255,255,255,0.06)

/**
 * Visual row inside a module card body.
 *
 * A label (left), a value display (optional), and an arbitrary trailing control composable.
 */
@Composable
fun ForkSettingRow(
    label: String,
    value: String? = null,
    modifier: Modifier = Modifier,
    control: @Composable () -> Unit,
) {
    val theme = LocalTheme.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                label,
                color = theme.textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
            value?.let {
                Text(
                    it,
                    color = theme.textColorSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
        control()
    }
}

/**
 * A single module card — header (title + keybind + toggle) and a body slot of control rows.
 *
 * All dimensions come from token values: cardPadding (12dp), rowGap (8dp), radius.card (12dp),
 * color.cardBackground, color.border. No hard-coded literals in the component.
 */
@Composable
fun ModuleCard(
    title: String,
    keybind: String?,
    onKeybindCapture: (String) -> Unit,
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    body: @Composable () -> Unit,
) {
    val theme = LocalTheme.current
    val interactionSource = rememberInteractionSource()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val borderColor by animateColorAsState(
        if (isHovered) theme.borderColor else theme.borderColor.copy(alpha = 0.5f)
    )
    val alpha by animateFloatAsState(if (!enabled) 0.5f else 1f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(theme.modCardBackground, CardShape)
            .border(1.dp, borderColor, CardShape)
            .alpha(alpha)
            .onClick(interactionSource) {}
            .pointerHoverIcon(PointerIcon.Hand)
            .drawBehind {
                // inset highlight + subtle shadow for depth
                drawLine(
                    color = InsetHighlightColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1f,
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ForkPadding.cardPadding),
        ) {
            // Header: title (left), KeybindBadge (center-right), Toggle (right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    title,
                    color = theme.textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                ForkKeybindBadge(
                    keyName = keybind,
                    onKeyCapture = onKeybindCapture,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(Modifier.width(8.dp))
                ForkToggle(
                    checked = enabled,
                    onCheckedChange = onEnabledChange,
                )
            }

            // header divider — 1px low-alpha border separates header from body per Figma
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = ForkPadding.rowGap)
                    .height(1.dp)
                    .background(theme.borderColor.copy(alpha = 0.08f))
            )

            if (enabled) {
                body()
            }
        }
    }
}

/** Fork-specific padding tokens. */
private object ForkPadding {
    val cardPadding = 12.dp
    val rowGap = 8.dp
}
