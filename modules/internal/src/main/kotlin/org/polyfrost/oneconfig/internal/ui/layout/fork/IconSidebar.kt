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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.width
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
import org.polyfrost.oneconfig.internal.ui.components.onClick
import org.polyfrost.oneconfig.internal.ui.components.rememberInteractionSource
import org.polyfrost.oneconfig.internal.ui.themes.Accent
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme
import org.polyfrost.oneconfig.internal.ui.themes.withOpacityPercent

private val SidebarWidth = 52.dp
private val ItemSize = 36.dp
private val IconSize = 20.dp

/**
 * Icon-only vertical navigation rail at exactly 52dp width.
 *
 * Reuses upstream icon rendering (Icon() composable) for navigation entries.
 * Drops label text and the Account() expansion per the Fork's scope.
 * States: default, hover (distinct), selected (accent-background pill).
 */
@Composable
fun IconSidebar(
    entries: List<SidebarEntry>,
    selectedId: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalTheme.current

    Column(
        modifier = modifier
            .width(SidebarWidth)
            .fillMaxHeight()
            .background(theme.sidebarBackground.withOpacityPercent(80f))
            .border(1.dp, theme.borderColor.copy(alpha = 0.3f), theme.sideBarNavigationEntryShape),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        entries.forEach { entry ->
            val interactionSource = rememberInteractionSource()
            val isHovered by interactionSource.collectIsHoveredAsState()
            val isSelected = entry.id == selectedId

            val bgColor by animateColorAsState(
                when {
                    isSelected -> Accent
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
                    .size(ItemSize)
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
                    modifier = Modifier.size(IconSize),
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
