package org.polyfrost.oneconfig.internal.ui.components.fork

import org.junit.jupiter.api.Test
import kotlin.math.abs

private fun assertEqual(expected: Float, actual: Float, delta: Float = 0.01f) {
    if (abs(expected - actual) > delta) {
        throw AssertionError("Expected <$expected> but was <$actual> (delta=$delta)")
    }
}

class ForkSliderSnapTest {

    @Test
    fun `snap returns min when x is at left edge`() {
        val result = snap(min = 0f, max = 100f, step = 10f, x = 0f, trackWidthPx = 200f, thumbPx = 20f)
        assertEqual(0f, result)
    }

    @Test
    fun `snap returns max when x is at right edge`() {
        val result = snap(min = 0f, max = 100f, step = 10f, x = 200f, trackWidthPx = 200f, thumbPx = 20f)
        assertEqual(100f, result)
    }

    @Test
    fun `snap rounds to nearest step`() {
        // x=55, trackWidthPx=200, thumbPx=20 -> ratio = (55-10)/190 = 0.2368 -> raw = 23.68
        // step 25: round(23.68/25) = round(0.947) = 1 -> 0 + 1*25 = 25
        val result = snap(min = 0f, max = 100f, step = 25f, x = 55f, trackWidthPx = 200f, thumbPx = 20f)
        assertEqual(25f, result)
    }

    @Test
    fun `snap clamps value inside min max`() {
        val result = snap(min = 10f, max = 50f, step = 5f, x = 999f, trackWidthPx = 200f, thumbPx = 20f)
        assertEqual(50f, result)
    }

    @Test
    fun `snap ignores step when step is zero`() {
        // usable = trackWidthPx - thumbPx = 180; ratio = (73.4 - 10) / 180 = 0.3522; raw = 35.22
        val result = snap(min = 0f, max = 100f, step = 0f, x = 73.4f, trackWidthPx = 200f, thumbPx = 20f)
        assertEqual(35.22f, result)
    }

    @Test
    fun `snap ignores negative step`() {
        // usable = trackWidthPx - thumbPx = 180; ratio = (73.4 - 10) / 180 = 0.3522; raw = 35.22
        val result = snap(min = 0f, max = 100f, step = -10f, x = 73.4f, trackWidthPx = 200f, thumbPx = 20f)
        assertEqual(35.22f, result)
    }

    @Test
    fun `snap handles one pixel usable width`() {
        val result = snap(min = 0f, max = 1f, step = 1f, x = 5f, trackWidthPx = 6f, thumbPx = 5f)
        assertEqual(1f, result)
    }
}
