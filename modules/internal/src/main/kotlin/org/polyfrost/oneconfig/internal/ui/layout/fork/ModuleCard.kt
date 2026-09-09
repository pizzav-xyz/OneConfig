package org.polyfrost.oneconfig.internal.ui.layout.fork

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.polyfrost.oneconfig.internal.ui.components.Text
import org.polyfrost.oneconfig.internal.ui.components.onClick
import org.polyfrost.oneconfig.internal.ui.components.rememberInteractionSource
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkTokens.cardShape
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkKeybindBadge
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkTokens
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkToggle
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme
import org.polyfrost.oneconfig.internal.ui.themes.withOpacityPercent



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
            .padding(vertical = ForkTokens.Padding.controlRowVertical),
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
 * All dimensions come from [ForkTokens]: card padding, row gap, radius.card, border width.
 * The card draws its own 1px inset top-highlight via `drawBehind` to avoid AA stair
 * where the outer border meets the inner surface.
 */
@Composable
fun ModuleCard(
    title: String,
    category: String? = null,
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
    val titleColor by animateColorAsState(
        if (enabled) theme.textColor else theme.textColor.copy(alpha = 0.8f)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, ForkTokens.cardShape, clip = false)
            .clip(ForkTokens.cardShape)
            .background(theme.modCardBackground.withOpacityPercent(92f), ForkTokens.cardShape)
            .border(ForkTokens.Size.cardBorder, borderColor, ForkTokens.cardShape)
            .onClick(interactionSource) {}
            .pointerHoverIcon(PointerIcon.Hand)
            .drawBehind {
                drawLine(
                    color = theme.accentTextColor.copy(alpha = 0.06f),
                    start = Offset(ForkTokens.Size.cardBorder.toPx(), ForkTokens.Size.cardBorder.toPx()),
                    end = Offset(size.width - ForkTokens.Size.cardBorder.toPx(), ForkTokens.Size.cardBorder.toPx()),
                    strokeWidth = 1f,
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ForkTokens.Padding.card),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val catIcon = when (category) {
                        "combat" -> "combat"
                        "player" -> "profiles"
                        "movement" -> "move"
                        "render" -> "paintbrush"
                        "world" -> "layers"
                        "misc" -> "qol"
                        else -> "settings"
                    }
                    org.polyfrost.oneconfig.internal.ui.components.Icon(
                        catIcon,
                        color = theme.accentColor,
                        modifier = Modifier.width(14.dp).height(14.dp).offset(y = 1.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        title,
                        color = titleColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                ForkKeybindBadge(
                    keyName = keybind,
                    onKeyCapture = onKeybindCapture,
                    modifier = Modifier.width(ForkTokens.Size.keybindMinWidth),
                )
                Spacer(Modifier.width(ForkTokens.Padding.toggleLabelGap))
                ForkToggle(
                    checked = enabled,
                    onCheckedChange = onEnabledChange,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = ForkTokens.Padding.rowGap)
                    .height(ForkTokens.Size.cardBorder)
                    .background(theme.borderColor.copy(alpha = 0.12f))
            )

            if (enabled) {
                body()
            }
        }
    }
}
