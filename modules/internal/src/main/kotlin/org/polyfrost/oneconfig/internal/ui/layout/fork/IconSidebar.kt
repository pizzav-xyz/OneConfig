package org.polyfrost.oneconfig.internal.ui.layout.fork

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import org.polyfrost.oneconfig.internal.ui.themes.Accent
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme
import org.polyfrost.oneconfig.internal.ui.themes.withOpacityPercent

private val SidebarWidth = 56.dp
private val ItemSize = 36.dp
private val IconSize = 14.dp
private val RailBorderColor = Color(0xFF252A33)

/**
 * Icon-only vertical navigation rail at exactly 56dp width.
 *
 * Uses drawBehind for the right-edge border to avoid AA halo from Compose's
 * default border rendering. Icon bounding box 14dp, text tint #A3AAC0.
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
            .padding(vertical = 8.dp)
            .background(theme.sidebarBackground.withOpacityPercent(80f))
            .drawBehind {
                // 1px right-edge border drawn via inset to avoid AA halo
                drawLine(
                    color = RailBorderColor,
                    start = Offset(size.width - 0.5f, 0f),
                    end = Offset(size.width - 0.5f, size.height),
                    strokeWidth = 1f,
                )
            },
        verticalArrangement = Arrangement.spacedBy(28.dp),
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
                    else -> Color(0xFFA3AAC0)
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
