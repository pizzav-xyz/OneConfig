package org.polyfrost.oneconfig.internal.ui.themes.fork

/**
 * Scope exclusions for the Dark Orange Fork.
 *
 * The Fork renders a single dark-orange configuration surface (icon sidebar +
 * 2-column module-card grid + 6 control types). Subsystems listed here are left
 * in-tree but are NOT part of the Fork's rendered surface.
 *
 * Out of scope (not rendered by the Fork):
 * - HUD editing & overlay system
 * - Notification system
 * - Non-mock control types: color picker, file picker, item list, number-field-with-spinner
 * - poly-compose item-icon runtime (PolyItemVisuals thumbnail mini-runtime)
 * - Vulkan render-path variants (VulkanService / VariantService)
 * - Theme switcher UI (only DarkOrangeTheme is exposed)
 * - Navigation-routed per-mod config screens (the grid is the sole host)
 */
object ForkScopeExclusions
