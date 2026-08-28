package org.polyfrost.oneconfig.internal.ui.components.fork

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ForkMultiSelectFlagTest {

    // ── countSelectedFlags ──

    @Test
    fun `count returns 0 for all-false array`() {
        val flags = booleanArrayOf(false, false, false)
        assert(countSelectedFlags(flags) == 0)
    }

    @Test
    fun `count returns full length for all-true array`() {
        val flags = booleanArrayOf(true, true, true, true)
        assert(countSelectedFlags(flags) == 4)
    }

    @Test
    fun `count returns correct mixed value`() {
        val flags = booleanArrayOf(true, false, true, false, true)
        assert(countSelectedFlags(flags) == 3)
    }

    @Test
    fun `count returns 0 for empty array`() {
        val flags = booleanArrayOf()
        assert(countSelectedFlags(flags) == 0)
    }

    @Test
    fun `count returns 1 for single true`() {
        val flags = booleanArrayOf(true)
        assert(countSelectedFlags(flags) == 1)
    }

    // ── formatMultiSelectLabel ──

    @Test
    fun `label is None selected when count is 0`() {
        assert(formatMultiSelectLabel(0, 5) == "None selected")
    }

    @Test
    fun `label is All selected when count equals total`() {
        assert(formatMultiSelectLabel(3, 3) == "All selected")
    }

    @Test
    fun `label is count slash total otherwise`() {
        assert(formatMultiSelectLabel(2, 5) == "2 / 5")
    }

    @Test
    fun `label is 1 slash 1 when single option selected`() {
        assert(formatMultiSelectLabel(1, 1) == "All selected")
    }

    @Test
    fun `label is None selected when total is 0 and count is 0`() {
        assert(formatMultiSelectLabel(0, 0) == "None selected")
    }

    // ── toggleFlag ──

    @Test
    fun `toggle flips false to true`() {
        val flags = booleanArrayOf(false, false, false)
        val result = toggleFlag(flags, 1)
        assert(result.contentEquals(booleanArrayOf(false, true, false)))
    }

    @Test
    fun `toggle flips true to false`() {
        val flags = booleanArrayOf(true, true, true)
        val result = toggleFlag(flags, 0)
        assert(result.contentEquals(booleanArrayOf(false, true, true)))
    }

    @Test
    fun `toggle does not mutate original array`() {
        val flags = booleanArrayOf(false, true, false)
        val original = flags.copyOf()
        toggleFlag(flags, 0)
        assert(flags.contentEquals(original))
    }

    @Test
    fun `toggle works on last index`() {
        val flags = booleanArrayOf(true, true, false)
        val result = toggleFlag(flags, 2)
        assert(result.contentEquals(booleanArrayOf(true, true, true)))
    }

    @Test
    fun `toggle throws on out-of-bounds index`() {
        val flags = booleanArrayOf(false, false)
        assertThrows<IllegalArgumentException> { toggleFlag(flags, 5) }
    }

    @Test
    fun `toggle throws on negative index`() {
        val flags = booleanArrayOf(false, false)
        assertThrows<IllegalArgumentException> { toggleFlag(flags, -1) }
    }

    @Test
    fun `multi-toggle sequence produces expected states`() {
        var flags = booleanArrayOf(false, false, false)
        flags = toggleFlag(flags, 0)
        assert(flags.contentEquals(booleanArrayOf(true, false, false)))
        flags = toggleFlag(flags, 2)
        assert(flags.contentEquals(booleanArrayOf(true, false, true)))
        flags = toggleFlag(flags, 0)
        assert(flags.contentEquals(booleanArrayOf(false, false, true)))
        assert(countSelectedFlags(flags) == 1)
        assert(formatMultiSelectLabel(countSelectedFlags(flags), flags.size) == "1 / 3")
    }
}
