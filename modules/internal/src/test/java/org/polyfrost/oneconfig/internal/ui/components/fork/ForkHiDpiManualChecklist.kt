package org.polyfrost.oneconfig.internal.ui.components.fork

import androidx.compose.ui.unit.Density

/**
 * HiDPI verification scaffolding for the Fork's config surface (tasks.md §6.2.1 / §6.3.1).
 *
 * These are pure-logic helpers — no Compose, no render pipeline, no UI mocking. They let the
 * §6 manual-verification checklist record crispness/scale expectations and the click-through
 * matrix without depending on a running client, so the harness can assert the contract the
 * human reviewer must confirm at GUI scales 1x / 2x / 3x.
 */

/** GUI scales the Fork must be manually verified at, per §6.2.1. */
val HIDPI_SCALES: List<Int> = listOf(1, 2, 3)

/**
 * Pixel-grid snap factor the theme applies for a given GUI scale.
 *
 * The Fork renders through the shared pixel-grid density path (Provider.pixelGridScale); this
 * helper mirrors its intent so the scaffolding can assert a non-degenerate scale is chosen. A
 * scale <= 0 means the surface would not be drawn — the reviewer must treat that as a FAIL.
 */
fun expectedHiDpiScale(guiScale: Int): Float = if (guiScale <= 0) 0f else guiScale.toFloat()

/**
 * Whether the given [guiScale] is one the §6 checklist must be exercised at.
 */
fun isVerifiedHiDpiScale(guiScale: Int): Boolean = guiScale in HIDPI_SCALES

/**
 * Recommended capture density hint for a screenshot at [guiScale] so crop tools can resolve
 * the 1px seams the visual audit flagged (AA stair, 1px border halo). Returns a [Density]-style
 * multiplier, not a real Density, to keep this harness UI-free.
 */
fun hidpiCaptureDensity(guiScale: Int): Float = expectedHiDpiScale(guiScale).coerceAtLeast(1f)

/**
 * Click-through checklist matrix for §6.3.1.
 *
 * Every interactive control the Fork introduces must be manually exercised in a real
 * `./gradlew runClient` instance. The matrix is the canonical list the reviewer walks through;
 * [allControlsCovered] lets the harness confirm no new control was missed.
 */
enum class ForkClickTarget {
    Toggle,
    Checkbox,
    Slider,
    Dropdown,
    MultiSelectDropdown,
    KeybindBadge,
    IconSidebarEntry,
}

/** Human-readable click-through steps for a single [target], in order. */
fun clickThroughSteps(target: ForkClickTarget): List<String> = when (target) {
    ForkClickTarget.Toggle ->
        listOf("Click On/Off", "Confirm state flips", "Hover for border fade-in", "Confirm no 1px knob offset")
    ForkClickTarget.Checkbox ->
        listOf("Click box", "Confirm trailing accent dot appears", "Confirm no blue bleed beyond border")
    ForkClickTarget.Slider ->
        listOf("Drag thumb", "Confirm value snaps to step", "Confirm no white halo bleed", "Confirm thumb height matches track")
    ForkClickTarget.Dropdown ->
        listOf("Open popup", "Select an entry", "Confirm trigger label updates", "Confirm popup closes (Escape/outside)")
    ForkClickTarget.MultiSelectDropdown ->
        listOf("Open popup", "Toggle several flags", "Confirm trigger shows N/total", "Confirm popup closes")
    ForkClickTarget.KeybindBadge ->
        listOf("Click badge", "Press a key", "Confirm capture", "Press Escape to dismiss")
    ForkClickTarget.IconSidebarEntry ->
        listOf("Click rail icon", "Confirm selected pill (accent bg)", "Confirm no icon clip at 52dp")
}

/** True when every required Fork control is present in [covered]. */
fun allControlsCovered(covered: Set<ForkClickTarget>): Boolean =
    ForkClickTarget.entries.all { it in covered }
