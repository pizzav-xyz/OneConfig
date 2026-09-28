package org.polyfrost.oneconfig.internal.ui.components.fork

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

/**
 * Shared trigger-size tracking + popup open/close recording for the fork
 * dropdown triggers ([ForkDropdown], [ForkMultiSelectDropdown]).
 *
 * Extracted so the two triggers cannot drift apart: popup width always
 * matches the measured trigger width, and E2E `popup-open:`/`popup-closed:`
 * events fire identically for both.
 */
internal class DropdownTriggerMetrics {
    var heightPx by mutableStateOf(0)
    var widthPx by mutableStateOf(0)
}

@Composable
internal fun rememberDropdownTriggerMetrics(): DropdownTriggerMetrics =
    remember { DropdownTriggerMetrics() }

internal fun Modifier.trackDropdownTrigger(metrics: DropdownTriggerMetrics): Modifier =
    onSizeChanged {
        metrics.heightPx = it.height
        metrics.widthPx = it.width
    }

@Composable
internal fun dropdownTriggerWidthDp(metrics: DropdownTriggerMetrics): Dp =
    with(LocalDensity.current) { metrics.widthPx.toDp() }

@Composable
internal fun RecordDropdownPopup(expanded: Boolean, key: String?) {
    LaunchedEffect(expanded) {
        ForkTestHooks.record((if (expanded) "popup-open:" else "popup-closed:") + key)
    }
}
