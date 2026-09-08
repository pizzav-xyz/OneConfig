package org.polyfrost.oneconfig.internal.ui.layout.fork

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.unit.dp
import org.polyfrost.oneconfig.internal.ui.components.Icon
import org.polyfrost.oneconfig.internal.ui.components.onClick
import org.polyfrost.oneconfig.internal.ui.components.rememberInteractionSource
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkTokens
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme
import org.polyfrost.oneconfig.internal.ui.themes.withOpacityPercent

/**
 * Icon-only vertical navigation rail.
 *
 * Dimensions are fully tokenised: width [ForkTokens.Size.sidebarWidth], padding [ForkTokens.Spacing.railPad],
 * gap [ForkTokens.Spacing.railGap]. The right-edge border is drawn via `drawBehind` at an inset
 * coordinate so the 1px line is absorbed inside the background fill — this removes the AA halo
 * that Compose's default `border` modifier produces on integer boundaries.
 */
@Composable
fun IconSidebar(
    entries: List<SidebarEntry>,
    selectedId: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalTheme.current
    val width = ForkTokens.Size.sidebarWidth
    val railPad = ForkTokens.Spacing.railPad
    val railGap = ForkTokens.Spacing.railGap
    val itemSize = ForkTokens.Size.sidebarItem
    val iconSize = ForkTokens.Size.sidebarIcon

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
        entries.forEach { entry ->
            val interactionSource = rememberInteractionSource()
            val isHovered by interactionSource.collectIsHoveredAsState()
            val isSelected = entry.id == selectedId

            val bgColor by animateColorAsState(
                when {
                    isSelected -> theme.accentColor
                    isHovered -> theme.textColor.copy(alpha = 0.1f)
                    else -> Color.Transparent
                }
            )
            val iconTint by animateColorAsState(
                when {
                    isSelected -> theme.accentTextColor
                    isHovered -> theme.textColor
                    else -> theme.textColorSecondary
                }
            )

            Box(
                modifier = Modifier
                    .size(itemSize)
                    .clip(theme.sideBarNavigationEntryShape)
                    .background(bgColor, theme.sideBarNavigationEntryShape)
                    .onClick(interactionSource) { onSelected(entry.id) }
                    .hoverable(interactionSource)
                    .pointerHoverIcon(PointerIcon.Hand),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    entry.icon,
                    color = iconTint,
                    modifier = Modifier.size(iconSize),
                )
            }
        }
    }
}

/** One entry in the icon-only sidebar rail. */
data class SidebarEntry(
    val id: String,
    val icon: String,
)
