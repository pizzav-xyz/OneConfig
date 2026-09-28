package org.polyfrost.oneconfig.internal.ui.layout.fork

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import org.polyfrost.oneconfig.internal.ui.components.Icon
import org.polyfrost.oneconfig.internal.ui.components.PlayerHead
import org.polyfrost.oneconfig.internal.ui.components.onClick
import org.polyfrost.oneconfig.internal.ui.components.rememberInteractionSource
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkTokens
import org.polyfrost.oneconfig.internal.ui.components.fork.testBounds
import org.polyfrost.oneconfig.internal.ui.themes.Accent
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme
import org.polyfrost.oneconfig.internal.ui.themes.withOpacityPercent

/**
 * Icon-only vertical navigation rail.
 *
 * Dimensions are fully tokenised: width [ForkTokens.Size.sidebarWidth], padding [ForkTokens.Spacing.railPad],
 * gap [ForkTokens.Spacing.railGap]. The right-edge border is drawn via `drawBehind` at an inset
 * coordinate so the 1px line is absorbed inside the background fill — this removes the AA halo
 * that Compose's default `border` modifier produces on integer boundaries.
 *
 * Selected entry renders a warm pill behind the coral [Accent] icon tint;
 * hover is a separate, muted tint so it does not read as selected.
 */
@Composable
fun IconSidebar(
    entries: List<SidebarEntry>,
    selectedId: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    onChildSelected: (Pair<String, String>) -> Unit = {},
    testKeyPrefix: String = "rail",
) {
    val theme = LocalTheme.current
    val width = ForkTokens.Size.sidebarWidth
    val railPad = ForkTokens.Spacing.railPad
    val railGap = ForkTokens.Spacing.railGap
    val itemSize = ForkTokens.Size.sidebarItem
    val iconSize = ForkTokens.Size.sidebarIcon
    val subRailWidth = ForkTokens.Size.subRailWidth
    val subRailItem = ForkTokens.Size.subRailItem

    Column(
        modifier = modifier
            .width(width)
            .fillMaxHeight()
            .padding(vertical = railPad)
            .background(theme.sidebarBackground.withOpacityPercent(80f))
            .drawBehind {
                // 1px right-edge border drawn at the inner inset so AA does not create a halo outside the rail.
                drawLine(
                    color = theme.borderColor,
                    start = Offset(size.width - 0.5f, 0f),
                    end = Offset(size.width - 0.5f, size.height),
                    strokeWidth = 1f,
                )
            },
        verticalArrangement = Arrangement.spacedBy(railGap),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Rail separator (CSS): 16px white-0.12 rule between logo and categories.
        Box(
            modifier = Modifier
                .size(ForkTokens.Size.sidebarIcon, ForkTokens.Size.cardBorder)
                .background(Color.White.copy(alpha = ForkTokens.Alpha.separatorFill)),
        )
        entries.forEach { entry ->
            val selectedEntry = entries.find { it.id == selectedId || it.children.any { child -> child.id == selectedId } }
            val isThisSelected = entry == selectedEntry
            val childSelection = selectedEntry?.children?.find { it.id == selectedId }
            val isSelected = isThisSelected && childSelection == null

            val interactionSource = rememberInteractionSource()
            val isHovered by interactionSource.collectIsHoveredAsState()

            val itemBg by animateColorAsState(
                when {
                    isSelected -> Accent.copy(alpha = 0.14f)
                    isThisSelected && childSelection != null -> theme.textColor.copy(alpha = 0.12f)
                    isHovered -> theme.textColor.copy(alpha = 0.10f)
                    else -> Color.Transparent
                }
            )
            val itemTint by animateColorAsState(
                when {
                    isSelected -> Accent
                    isThisSelected && childSelection != null -> theme.textColor
                    isHovered -> theme.textColor
                    else -> theme.textColorSecondary
                }
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(itemSize),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(itemSize)
                            .testBounds(testKeyPrefix + "-" + entry.id)
                            .clip(ForkTokens.railPillShape)
                            .background(itemBg, ForkTokens.railPillShape)
                            .onClick(interactionSource) { onSelected(entry.id) }
                            .hoverable(interactionSource)
                            .pointerHoverIcon(PointerIcon.Hand),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            entry.icon,
                            color = itemTint,
                            modifier = Modifier.size(iconSize),
                        )
                    }
                }

                if (isThisSelected && entry.children.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(railGap))
                    Column(
                        modifier = Modifier
                            .width(subRailWidth)
                            .height(subRailItem * entry.children.size + railGap * (entry.children.size - 1)),
                        verticalArrangement = Arrangement.spacedBy(railGap),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        entry.children.forEach { child ->
                            val childSelected = child.id == selectedId
                            val childInteractionSource = rememberInteractionSource()
                            val isChildHovered by childInteractionSource.collectIsHoveredAsState()

                            val childBg by animateColorAsState(
                                when {
                                    childSelected -> Accent.copy(alpha = 0.14f)
                                    isChildHovered -> theme.textColor.copy(alpha = 0.10f)
                                    else -> Color.Transparent
                                }
                            )
                            val childTint by animateColorAsState(
                                when {
                                    childSelected -> Accent
                                    isChildHovered -> theme.textColor
                                    else -> theme.textColorSecondary
                                }
                            )

                            Box(
                                modifier = Modifier
                                    .size(subRailItem)
                                    .clip(ForkTokens.railPillShape)
                                    .background(childBg, ForkTokens.railPillShape)
                                    .onClick(childInteractionSource) { onChildSelected(entry.id to child.id) }
                                    .hoverable(childInteractionSource)
                                    .pointerHoverIcon(PointerIcon.Hand),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    child.icon,
                                    color = childTint,
                                    modifier = Modifier.size(iconSize),
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .padding(bottom = ForkTokens.Padding.avatarInset)
                .size(ForkTokens.Size.avatarRing)
                .border(
                    ForkTokens.Size.avatarRingBorder,
                    Accent,
                    theme.circleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            PlayerHead(
                modifier = Modifier
                    .size(ForkTokens.Size.avatar)
                    .clip(theme.circleShape),
            )
        }
    }
}

/** One entry in the icon-only sidebar rail. */
data class SidebarEntry(
    val id: String,
    val icon: String,
    val children: List<SidebarEntry> = emptyList(),
)
