package org.polyfrost.oneconfig.internal.ui.components.fork

import org.junit.jupiter.api.Test

class ForkDropdownIndexTest {

    // ── resolveDropdownLabel ──

    @Test
    fun `label returns option at valid index`() {
        val options = listOf("Alpha", "Beta", "Gamma")
        assert(resolveDropdownLabel(options, 1) == "Beta")
    }

    @Test
    fun `label returns first option at index 0`() {
        val options = listOf("Alpha", "Beta", "Gamma")
        assert(resolveDropdownLabel(options, 0) == "Alpha")
    }

    @Test
    fun `label returns last option at last index`() {
        val options = listOf("Alpha", "Beta", "Gamma")
        assert(resolveDropdownLabel(options, 2) == "Gamma")
    }

    @Test
    fun `label returns em-dash for out-of-bounds positive index`() {
        val options = listOf("Alpha", "Beta")
        assert(resolveDropdownLabel(options, 5) == "\u2014")
    }

    @Test
    fun `label returns em-dash for negative index`() {
        val options = listOf("Alpha", "Beta")
        assert(resolveDropdownLabel(options, -1) == "\u2014")
    }

    @Test
    fun `label returns em-dash for empty options list`() {
        val options = emptyList<String>()
        assert(resolveDropdownLabel(options, 0) == "\u2014")
    }

    @Test
    fun `label returns option for single-item list at index 0`() {
        val options = listOf("Only")
        assert(resolveDropdownLabel(options, 0) == "Only")
    }

    // ── isValidSelectionIndex ──

    @Test
    fun `index 0 is valid for non-empty list`() {
        val options = listOf("Alpha", "Beta")
        assert(isValidSelectionIndex(options, 0))
    }

    @Test
    fun `last index is valid`() {
        val options = listOf("Alpha", "Beta", "Gamma")
        assert(isValidSelectionIndex(options, 2))
    }

    @Test
    fun `out-of-bounds index is invalid`() {
        val options = listOf("Alpha", "Beta")
        assert(!isValidSelectionIndex(options, 3))
    }

    @Test
    fun `negative index is invalid`() {
        val options = listOf("Alpha", "Beta")
        assert(!isValidSelectionIndex(options, -1))
    }

    @Test
    fun `no index is valid for empty list`() {
        val options = emptyList<String>()
        assert(!isValidSelectionIndex(options, 0))
    }
}
