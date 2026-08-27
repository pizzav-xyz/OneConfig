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
) {
    var controlTrackColor: Color = DefaultControlTrackColor
        private set

    /**
     * Canonical accent color for this theme — used by toggle-on fill, slider fill,
     * chip-selected background, dropdown-active border, keybind-dot, etc.
     * Each theme must define its own accent via [withAccentColor] so the fork
     * surface never inherits a mismatched blue from ThemeConfig defaults.
     */
    var accentColor: Color = DefaultAccentColor
        private set

    fun withControlTrackColor(color: Color): UITheme =
        copy().also { it.controlTrackColor = color }

    fun withAccentColor(color: Color): UITheme =
        copy().also { it.accentColor = color }

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
