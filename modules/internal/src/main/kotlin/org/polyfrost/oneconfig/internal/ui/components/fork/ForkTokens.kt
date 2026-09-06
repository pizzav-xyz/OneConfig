package org.polyfrost.oneconfig.internal.ui.components.fork

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Single source of truth for all Fork-scoped design tokens.
 *
 * Every dimension, radius, spacing, stroke, and fork-specific color in the
 * dark-orange config surface must come from here. No literal `dp`, `sp`, or
 * `Color(...)` values outside this file — transient local state is the only
 * exception.
 */
public object ForkTokens {
    // ── Radii ────────────────────────────────────────────────────────────────
    public object Radii {
        public val card = 12.dp
        public val control = 6.dp
        public val pill = 8.dp
    }

    // ── Spacing ─────────────────────────────────────────────────────────────
    public object Spacing {
        public val cardGap = 12.dp
        public val railPad = 8.dp
        public val railGap = 28.dp
        public val controlRowGap = 4.dp
    }

    // ── Padding ─────────────────────────────────────────────────────────────
    public object Padding {
        public val card = 10.dp
        public val rowGap = 6.dp
        public val controlRowVertical = 2.dp
        public val dropdownItemHorizontal = 12.dp
        public val dropdownItemVertical = 8.dp
        public val keybindHorizontal = 10.dp
        public val keybindVertical = 6.dp
        public val toggleLabelGap = 8.dp
        public val checkboxDot = 8.dp
        public val dropdownPadding = 4.dp
        public val dropdownItemGap = 4.dp
        public val dropdownDotGap = 8.dp
    }

    // ── Sizing ──────────────────────────────────────────────────────────────
    public object Size {
        // Toggle
        public val toggleTrackWidth = 36.dp
        public val toggleTrackHeight = 18.dp
        public val toggleKnob = 14.dp
        public val toggleInset = 2.dp

        // Checkbox
        public val checkbox = 16.dp

        // Slider
        public val sliderThumb = 19.dp
        public val sliderTrack = 4.dp
        public val sliderThumbRing = 2.dp

        // Dropdown / MultiSelect
        public val dropdownTriggerHeight = 32.dp
        public val dropdownDot = 6.dp
        public val dropdownPopupMaxHeight = 280.dp

        // KeybindBadge
        public val keybindMinWidth = 60.dp
        public val keybindIcon = 14.dp

        // Sidebar
        public val sidebarWidth = 56.dp
        public val sidebarItem = 36.dp
        public val sidebarIcon = 14.dp

        // Misc
        public val cardBorder = 1.dp
        public val controlBorder = 1.dp
    }

    public val cardShape = RoundedCornerShape(Radii.card)
    public val controlShape = RoundedCornerShape(Radii.control)
    public val pillShape = RoundedCornerShape(Radii.pill)

    public val cardInsetHighlightColor = Color(0x0FFFFFFF)
    public val railBorder = Color(0xFF252A33)
    public val railIconTint = Color(0xFFA3AAC0)
}
