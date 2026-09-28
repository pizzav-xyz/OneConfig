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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
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
                color = theme.textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
            )
            value?.let {
                Text(
                    it,
                    color = theme.textColorSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
        control()
    }
}

/**
 * Stacked setting row for full-width controls (reference dropdown pattern).
 *
 * Label row on top (label left, optional value right), full-width control
 * beneath — mirrors [ForkSliderRow]. Used for dropdown / multi-select rows;
 * checkbox rows stay inline via [ForkSettingRow].
 */
@Composable
fun ForkStackedRow(
    label: String,
    valueText: String? = null,
    modifier: Modifier = Modifier,
    control: @Composable () -> Unit,
) {
    val theme = LocalTheme.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ForkTokens.Padding.controlRowVertical),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                label,
                color = theme.textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
            )
            valueText?.let {
                Text(
                    it,
                    color = if (LocalModuleActive.current) Accent else theme.textColorSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
        control()
    }
}

/**
 * A single module card: compact header with title, keybind, toggle, and collapse
 * affordance, plus an optional expandable body.
 *
 * Dimensions are sourced from [ForkTokens]: card padding, row gap, radius.card, and border width.
 * Three visual states: active (warm fill + accent border + glow), inactive (neutral fill + border),
 * disabled (uniform 0.45 opacity). Toggle-off bodies render muted; disabled dimming applies once, card-wide.
 * Disabled cards remain interactive for collapse, and the body stays rendered but visually muted.
 *
 * Collapse state is session-remembered unless an explicit [collapsed] override is passed.
 */

/**
 * Whether the enclosing [ModuleCard] is switched on. Value rows read this to
 * render neutral hues when off (CSS off-column reads gray, not dimmed coral).
 * Defaults to true so standalone rows keep accent values.
 */
public val LocalModuleActive: ProvidableCompositionLocal<Boolean> = compositionLocalOf { true }

@Composable
fun ModuleCard(
    title: String,
    keybind: String?,
    onKeybindCapture: (String) -> Unit,
    onKeybindCancel: () -> Unit = {},
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    collapsed: Boolean? = null,
    testKey: String? = null,
    disabled: Boolean = false,
    body: @Composable () -> Unit,
) {
    val theme = LocalTheme.current
    val interactionSource = rememberInteractionSource()
    val isHovered by interactionSource.collectIsHoveredAsState()

    var collapsedState by remember { mutableStateOf(false) }
    val collapsedFinal = collapsed ?: collapsedState

    val titleColor by animateColorAsState(
        if (enabled && !disabled) theme.textColor else theme.textColor.copy(alpha = ForkTokens.Alpha.disabledTitle)
    )
    val cardFill by animateColorAsState(
        if (enabled && !disabled) Accent.copy(alpha = ForkTokens.Alpha.cardActiveFill)
        else Color.White.copy(alpha = ForkTokens.Alpha.cardInactiveFill)
    )
    val cardBorder by animateColorAsState(
        if (enabled && !disabled) Accent.copy(alpha = ForkTokens.Alpha.cardActiveBorder)
        else Color.White.copy(alpha = ForkTokens.Alpha.cardInactiveBorder)
    )

    val chevronRotation by animateFloatAsState(if (collapsedFinal) 180f else 0f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .testBounds(testKey?.let { "$it-card" })
            .then(
                if (enabled && !disabled) Modifier.shadow(
                    ForkTokens.Size.cardBorder * 8,
                    ForkTokens.cardShape,
                    clip = false,
                    ambientColor = Accent.copy(alpha = ForkTokens.Alpha.cardGlow),
                    spotColor = Accent.copy(alpha = ForkTokens.Alpha.cardGlow),
                ) else Modifier
            )
            .clip(ForkTokens.cardShape)
            .background(cardFill, ForkTokens.cardShape)
            .border(ForkTokens.Size.cardBorder, cardBorder, ForkTokens.cardShape)
            .then(if (disabled) Modifier.alpha(ForkTokens.Alpha.disabledOpacity) else Modifier)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = ForkTokens.Padding.cardHorizontal,
                    vertical = ForkTokens.Padding.card,
                ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = ForkTokens.Size.toggleTrackHeight * 2),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        title,
                        color = titleColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    ForkKeybindBadge(
                        keyName = keybind,
                        onKeyCapture = onKeybindCapture,
                        onCancel = onKeybindCancel,
                        enabled = enabled && !disabled,
                        testKey = testKey?.let { "$it-keybind" },
                    )
                    Spacer(Modifier.width(ForkTokens.Padding.toggleLabelGap))
                    ForkToggle(
                        checked = enabled,
                        onCheckedChange = onEnabledChange,
                        enabled = !disabled,
                        testKey = testKey?.let { "$it-toggle" },
                    )
                    Spacer(Modifier.width(ForkTokens.Padding.chevronGap))
                    Box(
                        modifier = Modifier
                            .size(ForkTokens.Size.chevron)
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
                        Icon(
                            "up",
                            color = theme.textColorSecondary,
                            modifier = Modifier.size(ForkTokens.Size.chevron).rotate(chevronRotation),
                        )
                    }
                }
            }

            AnimatedVisibility(!collapsedFinal) {
                Column(
                    modifier = Modifier
                        .padding(top = ForkTokens.Padding.cardHeaderGap)
                        .then(if (!enabled && !disabled) Modifier.alpha(ForkTokens.Alpha.disabledOpacity) else Modifier),
                    verticalArrangement = Arrangement.spacedBy(ForkTokens.Padding.rowGap),
                ) {
                    CompositionLocalProvider(LocalModuleActive provides enabled) {
                        body()
                    }
                }
            }
        }
    }
}
