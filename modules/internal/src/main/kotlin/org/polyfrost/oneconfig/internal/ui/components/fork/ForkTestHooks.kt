package org.polyfrost.oneconfig.internal.ui.components.fork

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

/**
 * Test-only hooks for the E2E click-through test.
 *
 * Components report their window bounds (compose pixels) under stable keys, and
 * the mock surface records callback events here. Unused in production; the maps
 * stay empty unless a test reads them.
 */
public object ForkTestHooks {
    public data class Bounds(val x: Float, val y: Float, val width: Float, val height: Float) {
        public val centerX: Float get() = x + width / 2f
        public val centerY: Float get() = y + height / 2f
    }

    private val bounds = ConcurrentHashMap<String, Bounds>()
    private val events = Collections.synchronizedList(mutableListOf<String>())

    public fun register(key: String, x: Float, y: Float, width: Float, height: Float) {
        bounds[key] = Bounds(x, y, width, height)
    }

    public fun record(event: String) {
        events.add(event)
    }

    /** Snapshot copies for Java callers. */
    public fun boundsSnapshot(): Map<String, Bounds> = HashMap(bounds)

    public fun eventsSnapshot(): List<String> = ArrayList(events)

    public fun clearEvents() {
        events.clear()
    }
}

/**
 * Reports this node's window bounds under [key] whenever layout changes.
 * No-op when [key] is null (production path).
 */
public fun Modifier.testBounds(key: String?): Modifier {
    if (key == null) return this
    return this.onGloballyPositioned { coordinates ->
        val rect = coordinates.boundsInWindow()
        ForkTestHooks.register(key, rect.left, rect.top, rect.width, rect.height)
    }
}
