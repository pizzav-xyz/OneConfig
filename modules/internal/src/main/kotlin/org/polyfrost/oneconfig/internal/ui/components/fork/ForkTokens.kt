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
    // ── Radii ──
    public object Radii {
        public val card = 10.dp
        public val control = 4.dp
        public val pill = 4.dp
        public val panel = 16.dp
        public val railPill = 10.dp
    }

    // ── Spacing ──
    public object Spacing {
        public val cardGap = 10.dp
        public val railPad = 14.dp
        public val railGap = 10.dp
    }

    // ── Padding ─────────────────────────────────────────────────────────────
    public object Padding {
        public val card = 10.dp
        public val cardHorizontal = 12.dp
        public val cardHeaderGap = 6.dp
        public val rowGap = 5.dp
        public val controlRowVertical = 2.dp
        public val dropdownItemHorizontal = 10.dp
        public val dropdownItemVertical = 8.dp
        public val keybindHorizontal = 4.dp
        public val keybindVertical = 2.dp
        public val toggleLabelGap = 8.dp
        public val chevronGap = 6.dp
        public val checkboxDot = 6.dp
        public val dropdownPadding = 10.dp
        public val dropdownDotGap = 12.dp
        public val avatarInset = 8.dp
        public val gridEmptyTop = 32.dp
        public val gridOuter = 12.dp
    }

    // ── Sizing ──────────────────────────────────────────────────────────────
    public object Size {
        // Toggle
        public val toggleTrackWidth = 24.dp
        public val toggleTrackHeight = 14.dp
        public val toggleKnob = 10.dp
        public val toggleInset = 2.dp

        // Checkbox
        public val checkbox = 14.dp
        public val checkboxGlow = 7.dp

        // Slider
        public val sliderThumb = 4.dp
        public val sliderTrack = 4.dp

        public val dropdownTriggerHeight = 28.dp
        public val dropdownRowHeight = 30.dp
        public val dropdownDot = 5.dp
        public val dropdownPopupMaxHeight = 320.dp
        public val popupShadow = 8.dp

        public val keybindMinWidth = 16.dp
        public val keybindHeight = 16.dp
        public val keybindDashWidth = 6.dp
        public val keybindDashHeight = 1.dp
        public val chevron = 12.dp

        public val panelMaxWidth = 736.dp
        public val panelMinHeight = 472.dp
        public val panelShadow = 16.dp

        public val sidebarWidth = 56.dp
        public val sidebarItem = 36.dp
        public val sidebarIcon = 16.dp
        public val subRailWidth = 44.dp
        public val subRailItem = 28.dp
        public val avatar = 30.dp
        public val avatarRing = 34.dp
        public val avatarRingBorder = 1.dp

        public val cardBorder = 1.dp
        public val controlBorder = 1.dp
        public val focusRing = 2.dp
    }

    // ── Fill alphas (CSS-derived translucent coral surfaces) ─────────────────
    public object Alpha {
        public val controlFill = 0.16f
        public val checkboxBorder = 0.18f
        public val triggerBorder = 0.08f
        public val separatorFill = 0.12f
        public val popupFill = 0.04f
        public val glowHalo = 0.35f
        public val disabledFill = 0.04f
        public val disabledContent = 0.12f
        public val disabledTitle = 0.24f
        public val cardActiveFill = 0.06f
        public val cardActiveBorder = 0.35f
        public val cardGlow = 0.20f
        public val cardInactiveFill = 0.03f
        public val cardInactiveBorder = 0.06f
        public val disabledOpacity = 0.45f
    }

    public val cardShape = RoundedCornerShape(Radii.card)
    public val controlShape = RoundedCornerShape(Radii.control)
    public val pillShape = RoundedCornerShape(Radii.pill)
    public val panelShape = RoundedCornerShape(Radii.panel)
    public val railPillShape = RoundedCornerShape(Radii.railPill)
}
