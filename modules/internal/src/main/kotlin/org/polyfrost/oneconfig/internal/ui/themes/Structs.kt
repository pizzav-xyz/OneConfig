package org.polyfrost.oneconfig.internal.ui.themes

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontFamily

/** Sets a panel background color's alpha from an opacity percentage between 0 and 100 */
fun Color.withOpacityPercent(percent: Float): Color =
    copy(alpha = (percent / 100f).coerceIn(0f, 1f))

data class UITheme(
    val previewImage: String,
    val name: String,

    val pageBackground: Color,
    val sidebarBackground: Color,
    val chipBackground: Color,
    val modCardBackground: Color,
    val componentBackground: Color,
    val popupBackground: Color,

    val borderColor: Color,
    val textColor: Color,
    val textColorSecondary: Color,
    val accentTextColor: Color,

    val shadowColor: Color,
    val controlThumbColor: Color,

    val shadowEnabled: Boolean,

    val backgroundShape: Shape,
    val sideBarNavigationEntryShape: Shape,
    val modCardShape: Shape,
    val checkBoxShape: Shape,
    val buttonShape: Shape,
    val popupShape: Shape,
    val circleShape: Shape,

    val branding: UIBranding,
    val typography: UITypography,
    val iconOverrides: Map<String, String> = emptyMap(),

    /** Canonical accent — toggle-on fill, slider fill, chip-selected bg, dropdown active, keybind dot. */
    val accentColor: Color = DefaultAccentColor,

    /** Unfilled track color — warm brown for orange accent. */
    val controlTrackColor: Color = DefaultControlTrackColor,
) {
    fun withControlTrackColor(color: Color): UITheme = copy(controlTrackColor = color)

    fun withAccentColor(color: Color): UITheme = copy(accentColor = color)

    companion object {
        val DefaultControlTrackColor = Color(0xFF74777F)
        /** Base OneConfig blue — only used by non-fork themes. */
        val DefaultAccentColor = Color(0xFF2B4BFF)
    }
}

data class UIBranding(
    val logoPath: String
)

data class UITypography(
    val family: FontFamily,
)
