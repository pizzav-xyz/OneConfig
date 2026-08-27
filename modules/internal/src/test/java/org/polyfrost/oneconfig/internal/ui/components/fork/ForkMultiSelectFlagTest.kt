package org.polyfrost.oneconfig.internal.ui.components.fork

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ForkMultiSelectFlagTest {

    @Test
    fun `toggle single flips flag`() {
        val flags = booleanArrayOf(false, false, false)
        val toggled = toggleFlag(flags, 1)
        assertTrue(toggled[1])
        assertFalse(toggled[0])
        assertFalse(flags[1])
    }

    @Test
    fun `toggle all flips each flag`() {
        val flags = booleanArrayOf(false, false, false)
        var cur = flags
        cur = toggleFlag(cur, 0)
        cur = toggleFlag(cur, 1)
        cur = toggleFlag(cur, 2)
        assertTrue(cur.all { it })
    }

    @Test
    fun `formatCountLabel shows none selected`() {
        assertEquals("None selected", formatCountLabel(booleanArrayOf(false, false, false), 3))
    }

    @Test
    fun `formatCountLabel shows all selected`() {
        assertEquals("All selected", formatCountLabel(booleanArrayOf(true, true, true), 3))
    }

    @Test
    fun `formatCountLabel shows count summary`() {
        assertEquals("2 / 3", formatCountLabel(booleanArrayOf(true, true, false), 3))
    }

    @Test
    fun `formatCountLabel handles empty flags`() {
        assertEquals("None selected", formatCountLabel(booleanArrayOf(), 0))
    }

    @Test
    fun `toggle out of bounds leaves unchanged`() {
        val flags = booleanArrayOf(true, false)
        val out = toggleFlag(flags, 5)
        assertEquals(flags.toList(), out.toList())
    }
}
