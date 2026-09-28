package org.polyfrost.oneconfig.internal.ui.components.fork

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import org.polyfrost.oneconfig.internal.ui.components.Icon
import org.polyfrost.oneconfig.internal.ui.components.Text
import org.polyfrost.oneconfig.internal.ui.components.onClick
import org.polyfrost.oneconfig.internal.ui.components.rememberInteractionSource
import org.polyfrost.oneconfig.internal.ui.themes.Accent
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme
import org.polyfrost.oneconfig.internal.ui.themes.concentric

fun countSelectedFlags(flags: BooleanArray): Int = flags.count { it }

fun formatMultiSelectLabel(count: Int, total: Int): String = when {
    count == 0 -> "None selected"
    count == total -> "All selected"
    else -> "$count / $total"
}

fun toggleFlag(flags: BooleanArray, index: Int): BooleanArray {
    require(index in flags.indices) { "Index $index out of bounds [0, ${flags.size})" }
    return flags.copyOf().also { it[index] = !it[index] }
}

/**
 * Fork-scoped multi-select dropdown.
 *
 * Trigger height, popup max height, item padding, and trailing dot all come from [ForkTokens].
 */
@Composable
fun ForkMultiSelectDropdown(
    options: List<String>,
    selectedFlags: BooleanArray,
    onToggle: (Int) -> Unit,
    modifier: Modifier = Modifier,
    testKey: String? = null,
    testKeyPrefix: String? = testKey,
) {
    val theme = LocalTheme.current
    var expanded by remember { mutableStateOf(false) }
    val trigger = rememberDropdownTriggerMetrics()

    val triggerInteraction = rememberInteractionSource()
    val isHovered by triggerInteraction.collectIsHoveredAsState()

    val borderColor by animateColorAsState(if (expanded) Accent else Color.White.copy(alpha = ForkTokens.Alpha.triggerBorder))
    val textColor by animateColorAsState(
        if (isHovered || expanded) theme.textColor else theme.textColorSecondary
    )
    val chevronColor by animateColorAsState(if (expanded) Accent else textColor)
    val backgroundColor by animateColorAsState(
        if (expanded) Accent.copy(0.2f).compositeOver(theme.componentBackground)
        else Color.Black.copy(alpha = 0.35f)
    )
    val chevronRotation by animateFloatAsState(if (expanded) 0f else 180f)

    val count = countSelectedFlags(selectedFlags)
    val triggerLabel = formatMultiSelectLabel(count, options.size)
    val triggerPadding = ForkTokens.Padding.dropdownPadding
    val dotGap = ForkTokens.Padding.dropdownDotGap
    RecordDropdownPopup(expanded, testKeyPrefix ?: testKey)

    Box(modifier = modifier.testBounds(testKey)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ForkTokens.Size.dropdownTriggerHeight)
                .trackDropdownTrigger(trigger)
                .background(backgroundColor, ForkTokens.controlShape)
                .border(ForkTokens.Size.controlBorder, borderColor, ForkTokens.controlShape)
                .onClick(triggerInteraction) { expanded = !expanded }
                .hoverable(triggerInteraction)
                .pointerHoverIcon(PointerIcon.Hand)
                .padding(horizontal = triggerPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                triggerLabel,
                modifier = Modifier.weight(1f, fill = false).padding(end = dotGap),
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Icon("up", modifier = Modifier.rotate(chevronRotation), color = chevronColor)
        }

        if (expanded) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, trigger.heightPx + 4),
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true),
            ) {
                AnimatedVisibility(visible = expanded, enter = fadeIn(), exit = fadeOut()) {
                    val scrollState = rememberScrollState()
                    Box(
                        modifier = Modifier
                            .width(dropdownTriggerWidthDp(trigger))
                            .shadow(
                                ForkTokens.Size.popupShadow,
                                ForkTokens.controlShape,
                                clip = false,
                                ambientColor = Color.Black.copy(alpha = 0.5f),
                                spotColor = Color.Black.copy(alpha = 0.5f),
                            )
                            .clip(ForkTokens.controlShape)
                            .background(theme.componentBackground.copy(alpha = 1f), ForkTokens.controlShape)
                            .border(ForkTokens.Size.controlBorder, Accent.copy(alpha = ForkTokens.Alpha.controlFill), ForkTokens.controlShape)
                    ) {
                        Column(
                            modifier = Modifier
                                .heightIn(max = ForkTokens.Size.dropdownPopupMaxHeight)
                                .verticalScroll(scrollState)
                                .fillMaxWidth(),
                        ) {
                            options.forEachIndexed { index, option ->
                                ForkMultiselectItem(
                                    label = option,
                                    checked = selectedFlags.getOrElse(index) { false },
                                    onToggle = { onToggle(index) },
                                    testKey = testKeyPrefix?.let { "$it-item-$index" },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ForkMultiselectItem(
    label: String,
    checked: Boolean,
    onToggle: () -> Unit,
    testKey: String? = null,
) {
    val theme = LocalTheme.current
    val interactionSource = rememberInteractionSource()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val contentColor by animateColorAsState(
        if (checked) Accent else if (isHovered) theme.textColor else theme.textColorSecondary
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ForkTokens.Size.dropdownRowHeight)
            .testBounds(testKey)
            .background(
                Brush.horizontalGradient(
                    listOf(Color.Transparent, Accent.copy(alpha = ForkTokens.Alpha.popupFill)),
                ),
            )
            .clip(theme.sideBarNavigationEntryShape.concentric(ForkTokens.Padding.dropdownPadding))
            .onClick(interactionSource, onToggle)
            .hoverable(interactionSource)
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(horizontal = ForkTokens.Padding.dropdownItemHorizontal, vertical = ForkTokens.Padding.dropdownItemVertical),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            label,
            color = contentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (checked) {
            Spacer(Modifier.width(ForkTokens.Padding.dropdownDotGap))
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
                        .size(ForkTokens.Size.dropdownDot)
                        .clip(LocalTheme.current.circleShape)
                        .background(Accent),
                )
            }
        }
    }
}
