package org.polyfrost.oneconfig.internal.ui.components.fork

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.polyfrost.oneconfig.internal.ui.components.Text
import org.polyfrost.oneconfig.internal.ui.components.onClick
import org.polyfrost.oneconfig.internal.ui.components.rememberInteractionSource
import org.polyfrost.oneconfig.internal.ui.themes.Accent
import org.polyfrost.oneconfig.internal.ui.layout.fork.LocalModuleActive
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme

/**
 * Inline single-select enum options for card bodies (reference: AntiBot-style
 * dot lists). Up to 4 options render as rows with a trailing coral dot on the
 * active one; larger enums keep the closed dropdown and must not use this.
 */
@Composable
fun ForkEnumOptions(
    options: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    testKeyPrefix: String? = null,
) {
    require(options.size in 1..4) { "ForkEnumOptions supports 1-4 options, got ${options.size}" }
    val theme = LocalTheme.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ForkTokens.Padding.controlRowVertical),
    ) {
        if (label != null) {
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
                Text(
                    "${selectedIndex + 1}/${options.size}",
                    color = if (LocalModuleActive.current) Accent else theme.textColorSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ForkTokens.controlShape)
                .background(
                    Accent.copy(alpha = ForkTokens.Alpha.popupFill),
                    ForkTokens.controlShape,
                )
                .border(
                    ForkTokens.Size.controlBorder,
                    Accent.copy(alpha = ForkTokens.Alpha.controlFill),
                    ForkTokens.controlShape,
                ),
        ) {
            options.forEachIndexed { index, option ->
                val active = index == selectedIndex
                val interactionSource = rememberInteractionSource()
                val isHovered by interactionSource.collectIsHoveredAsState()
                val contentColor by animateColorAsState(
                    when {
                        active -> Accent
                        isHovered -> theme.textColor
                        else -> theme.textColorSecondary
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ForkTokens.Size.dropdownRowHeight)
                        .testBounds(testKeyPrefix?.let { "$it-item-$index" })
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, Accent.copy(alpha = ForkTokens.Alpha.popupFill)),
                            ),
                        )
                        .onClick(interactionSource) { onSelected(index) }
                        .hoverable(interactionSource)
                        .pointerHoverIcon(PointerIcon.Hand)
                        .padding(
                            horizontal = ForkTokens.Padding.dropdownItemHorizontal,
                            vertical = ForkTokens.Padding.dropdownItemVertical,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        option,
                        color = contentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    if (active) {
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
        }
    }
}
