package org.polyfrost.oneconfig.internal.ui.components.fork

import androidx.compose.foundation.shape.RoundedCornerShape
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
        public val card = 8.dp
        public val control = 4.dp
        public val pill = 6.dp
        public val panel = 12.dp
    }

    // ── Spacing ─────────────────────────────────────────────────────────────
    public object Spacing {
        public val cardGap = 8.dp
        public val railPad = 8.dp
        public val railGap = 16.dp
        public val controlRowGap = 4.dp
    }

    // ── Padding ─────────────────────────────────────────────────────────────
    public object Padding {
        public val card = 8.dp
        public val rowGap = 4.dp
        public val controlRowVertical = 2.dp
        public val dropdownItemHorizontal = 12.dp
        public val dropdownItemVertical = 8.dp
        public val keybindHorizontal = 10.dp
        public val keybindVertical = 6.dp
        public val toggleLabelGap = 8.dp
        public val checkboxDot = 6.dp
        public val dropdownPadding = 4.dp
        public val dropdownItemGap = 4.dp
        public val dropdownDotGap = 8.dp
        public val avatarInset = 8.dp
    }

    // ── Sizing ──────────────────────────────────────────────────────────────
    public object Size {
        // Toggle
        public val toggleTrackWidth = 28.dp
        public val toggleTrackHeight = 16.dp
        public val toggleKnob = 11.dp
        public val toggleInset = 2.dp

        // Checkbox
        public val checkbox = 16.dp

        // Slider
        public val sliderThumb = 10.dp
        public val sliderTrack = 4.dp
        public val sliderThumbRing = 1.5.dp

        // Dropdown / MultiSelect
        public val dropdownTriggerHeight = 28.dp
        public val dropdownDot = 6.dp
        public val dropdownPopupMaxHeight = 320.dp

        // KeybindBadge
        public val keybindMinWidth = 44.dp
        public val keybindIcon = 14.dp

        // Panel
        public val panelRadius = 12.dp
        public val panelMaxWidth = 640.dp
        public val panelShadow = 16.dp

        // Sidebar
        public val sidebarWidth = 52.dp
        public val sidebarItem = 36.dp
        public val sidebarIcon = 14.dp
        public val subRailWidth = 44.dp
        public val subRailItem = 28.dp
        public val avatar = 28.dp

        // Misc
        public val cardBorder = 1.dp
        public val controlBorder = 1.dp
    }

    public val cardShape = RoundedCornerShape(Radii.card)
    public val controlShape = RoundedCornerShape(Radii.control)
    public val pillShape = RoundedCornerShape(Radii.pill)
    public val panelShape = RoundedCornerShape(Radii.panel)
}
