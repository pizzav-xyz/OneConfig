package org.polyfrost.oneconfig.internal.ui.layout.fork

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.polyfrost.oneconfig.internal.ui.components.Icon
import org.polyfrost.oneconfig.internal.ui.components.Text
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkTokens
import org.polyfrost.oneconfig.internal.ui.components.fork.testBounds
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkKeybindBadge
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkToggle
import org.polyfrost.oneconfig.internal.ui.components.onClick
import org.polyfrost.oneconfig.internal.ui.components.rememberInteractionSource
import org.polyfrost.oneconfig.internal.ui.themes.Accent
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme
import org.polyfrost.oneconfig.internal.ui.themes.withOpacityPercent

/**
 * Visual row inside a module card body.
 *
 * A label (left), an optional value line, and a trailing control composable.
 *
 * Typography (§6): label 11sp Medium, value 10sp Normal.
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
                color = theme.textColorSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
            )
            value?.let {
                Text(
                    it,
                    color = theme.textColorSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
        control()
    }
}

/**
 * A single module card: compact header with title, category icon, keybind, toggle, and collapse
 * affordance, plus an optional expandable body.
 *
 * Dimensions are sourced from [ForkTokens]: card padding, row gap, radius.card, and border width.
 * A 1px accent top-sheen is drawn when enabled; an ambient accent ring is drawn in the background.
 * Disabled cards remain interactive for collapse, and the body stays rendered but visually muted.
 *
 * Collapse state is session-remembered unless an explicit [collapsed] override is passed.
 */
@Composable
fun ModuleCard(
    title: String,
    category: String? = null,
    keybind: String?,
    onKeybindCapture: (String) -> Unit,
    onKeybindCancel: () -> Unit = {},
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    collapsed: Boolean? = null,
    testKey: String? = null,
    body: @Composable () -> Unit,
) {
    val theme = LocalTheme.current
    val interactionSource = rememberInteractionSource()
    val isHovered by interactionSource.collectIsHoveredAsState()

    var collapsedState by remember { mutableStateOf(false) }
    val collapsedFinal = collapsed ?: collapsedState

    val borderColor by animateColorAsState(
        if (enabled) {
            if (isHovered) theme.borderColor else theme.borderColor.copy(alpha = 0.5f)
        } else {
            theme.borderColor.copy(alpha = 0.35f)
        }
    )
    val titleColor by animateColorAsState(
        if (enabled) theme.textColor else theme.textColor.copy(alpha = 0.82f)
    )

    val chevronRotation by animateFloatAsState(if (collapsedFinal) 0f else 180f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .testBounds(testKey?.let { "$it-card" })
            .clip(ForkTokens.cardShape)
            .background(theme.modCardBackground.withOpacityPercent(92f), ForkTokens.cardShape)
            .border(ForkTokens.Size.cardBorder, borderColor, ForkTokens.cardShape)
            .drawBehind {
                // Top sheen: accent gradient fades across the top border.
                val sheenAlpha = if (enabled) 0.28f else 0f
                if (sheenAlpha > 0f) {
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(theme.accentColor.copy(alpha = sheenAlpha), Color.Transparent),
                            startX = 0f,
                            endX = size.width,
                        ),
                        start = Offset(0f, ForkTokens.Size.cardBorder.toPx()),
                        end = Offset(size.width, ForkTokens.Size.cardBorder.toPx()),
                        strokeWidth = ForkTokens.Size.cardBorder.toPx(),
                    )
                }
            }
            .drawBehind {
                // Ambient accent ring behind the card.
                val glowAlpha = if (enabled) 0.08f else 0f
                if (glowAlpha > 0f) {
                    drawCircle(
                        color = Accent.copy(alpha = glowAlpha),
                        radius = size.minDimension / 2f + 12f,
                    )
                }
            }
            .alpha(if (enabled) 1f else 0.82f)
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
                    Icon(
                        catIcon,
                        color = theme.accentColor,
                        modifier = Modifier
                            .size(ForkTokens.Size.sidebarIcon * 2f)
                            .offset(y = 1.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        title,
                        color = titleColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    ForkKeybindBadge(
                        keyName = keybind,
                        onKeyCapture = onKeybindCapture,
                        onCancel = onKeybindCancel,
                        modifier = Modifier.width(ForkTokens.Size.keybindMinWidth),
                        testKey = testKey?.let { "$it-keybind" },
                    )
                    Spacer(Modifier.width(ForkTokens.Padding.toggleLabelGap))
                    ForkToggle(
                        checked = enabled,
                        onCheckedChange = onEnabledChange,
                        testKey = testKey?.let { "$it-toggle" },
                    )
                    Spacer(Modifier.width(ForkTokens.Padding.toggleLabelGap))
                    Box(
                        modifier = Modifier
                            .size(ForkTokens.Size.sidebarIcon)
                            .testBounds(testKey?.let { "$it-collapse" })
                            .clip(ForkTokens.cardShape)
                            .background(
                                if (isHovered) theme.textColor.copy(alpha = 0.08f)
                                else Color.Transparent,
                                ForkTokens.cardShape,
                            )
                            .onClick(interactionSource) { collapsedState = !collapsedState }
                            .pointerHoverIcon(PointerIcon.Hand),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            if (collapsedFinal) "v" else "^",
                            color = if (enabled) theme.textColorSecondary else theme.textColor.copy(alpha = 0.5f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.rotate(chevronRotation),
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = ForkTokens.Padding.rowGap)
                    .height(ForkTokens.Size.cardBorder)
                    .background(theme.borderColor.copy(alpha = 0.5f)),
            )

            AnimatedVisibility(!collapsedFinal) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(ForkTokens.Padding.rowGap),
                ) {
                    body()
                }
            }
        }
    }
}
