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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
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
) {
    val theme = LocalTheme.current
    var expanded by remember { mutableStateOf(false) }
    var triggerHeightPx by remember { mutableStateOf(0) }

    val triggerInteraction = rememberInteractionSource()
    val isHovered by triggerInteraction.collectIsHoveredAsState()

    val borderColor by animateColorAsState(if (expanded) Accent else theme.borderColor)
    val textColor by animateColorAsState(
        if (isHovered || expanded) theme.textColor else theme.textColorSecondary
    )
    val backgroundColor by animateColorAsState(
        if (expanded) Accent.copy(0.2f).compositeOver(theme.componentBackground)
        else theme.componentBackground
    )
    val chevronRotation by animateFloatAsState(if (expanded) 0f else 180f)

    val count = countSelectedFlags(selectedFlags)
    val triggerLabel = formatMultiSelectLabel(count, options.size)
    val triggerPadding = ForkTokens.Padding.dropdownPadding
    val dotGap = ForkTokens.Padding.dropdownDotGap

    Box(modifier = modifier.testBounds(testKey)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ForkTokens.Size.dropdownTriggerHeight)
                .onSizeChanged { triggerHeightPx = it.height }
                .background(backgroundColor, theme.sideBarNavigationEntryShape)
                .border(ForkTokens.Size.controlBorder, borderColor, theme.sideBarNavigationEntryShape)
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
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Icon("up", modifier = Modifier.rotate(chevronRotation), color = textColor)
        }

        if (expanded) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, triggerHeightPx + 10),
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true),
            ) {
                AnimatedVisibility(visible = expanded, enter = fadeIn(), exit = fadeOut()) {
                    val scrollState = rememberScrollState()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(theme.sideBarNavigationEntryShape)
                            .background(theme.componentBackground, theme.sideBarNavigationEntryShape)
                            .border(ForkTokens.Size.controlBorder, theme.borderColor, theme.sideBarNavigationEntryShape)
                    ) {
                        Column(
                            modifier = Modifier
                                .heightIn(max = ForkTokens.Size.dropdownPopupMaxHeight)
                                .verticalScroll(scrollState)
                                .padding(vertical = ForkTokens.Padding.dropdownItemVertical, horizontal = ForkTokens.Padding.dropdownItemHorizontal)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(ForkTokens.Padding.dropdownItemGap),
                        ) {
                            options.forEachIndexed { index, option ->
                                ForkMultiselectItem(
                                    label = option,
                                    checked = selectedFlags.getOrElse(index) { false },
                                    onToggle = { onToggle(index) },
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
) {
    val theme = LocalTheme.current
    val interactionSource = rememberInteractionSource()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val contentColor by animateColorAsState(
        if (checked || isHovered) theme.textColor else theme.textColorSecondary
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
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
            fontSize = 13.sp,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (checked) {
            Spacer(Modifier.width(ForkTokens.Padding.dropdownDotGap))
            Box(
                modifier = Modifier
                    .size(ForkTokens.Size.dropdownDot)
                    .clip(LocalTheme.current.circleShape)
                    .background(Accent),
            )
        }
    }
}
