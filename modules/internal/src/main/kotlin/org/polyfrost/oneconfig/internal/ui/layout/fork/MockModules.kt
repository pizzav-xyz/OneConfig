package org.polyfrost.oneconfig.internal.ui.layout.fork

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkCheckbox
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkDropdown
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkMultiSelectDropdown
import org.polyfrost.oneconfig.internal.ui.layout.fork.ForkSettingRow
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkSlider
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkTokens

/**
 * Static preview data for the Fork's module grid.
 *
 * Real runtime modules will come from ConfigRegistry; this object only exists
 * so the grid can render without wiring every data layer.
 */
data class MockModule(
    val id: String,
    val title: String,
    val keybind: String?,
    val enabled: Boolean,
    val selectedDropdown: Int = 0,
    val sliderValue: Float = 50f,
    val checkboxChecked: Boolean = false,
    val multiSelectFlags: BooleanArray = booleanArrayOf(true, false, false, false),
    val bodyComposable: @Composable () -> Unit,
)

object MockModules {
    val modules: List<MockModule> = listOf(
        MockModule(
            id = "auto-clicker",
            title = "Auto Clicker",
            keybind = "L",
            enabled = true,
            selectedDropdown = 1,
            bodyComposable = {
                ForkSettingRow(label = "Clicks per second", value = "12", control = {
                    ForkSlider(
                        value = 12f,
                        onValueChange = {},
                        min = 1f,
                        max = 50f,
                        step = 1f,
                    )
                })
                Spacer(Modifier.height(ForkTokens.Spacing.controlRowGap))
                ForkSettingRow(label = "Target entity", control = {
                    ForkDropdown(
                        options = listOf("Players", "Mobs", "All"),
                        selectedIndex = 1,
                        onSelected = {},
                    )
                })
            },
        ),
        MockModule(
            id = "auto-eat",
            title = "Auto Eat",
            keybind = null,
            enabled = false,
            bodyComposable = {
                ForkSettingRow(label = "Hunger threshold", value = "80%", control = {
                    ForkSlider(
                        value = 80f,
                        onValueChange = {},
                        min = 0f,
                        max = 100f,
                        step = 5f,
                    )
                })
            },
        ),
        MockModule(
            id = "auto-tool",
            title = "Auto Tool",
            keybind = "G",
            enabled = true,
            multiSelectFlags = booleanArrayOf(true, true, false, false),
            bodyComposable = {
                ForkSettingRow(label = "Preferred materials", control = {
                    ForkMultiSelectDropdown(
                        options = listOf("Netherite", "Diamond", "Iron", "Stone"),
                        selectedFlags = booleanArrayOf(true, true, false, false),
                        onToggle = {},
                    )
                })
            },
        ),
        MockModule(
            id = "chams",
            title = "Chams",
            keybind = "H",
            enabled = true,
            checkboxChecked = true,
            bodyComposable = {
                ForkSettingRow(label = "Show entities through walls", control = {
                    ForkCheckbox(
                        checked = true,
                        onCheckedChange = {},
                    )
                })
                Spacer(Modifier.height(ForkTokens.Spacing.controlRowGap))
                ForkSettingRow(label = "Mode", control = {
                    ForkDropdown(
                        options = listOf("Wireframe", "Solid"),
                        selectedIndex = 0,
                        onSelected = {},
                    )
                })
            },
        ),
        MockModule(
            id = "velocity",
            title = "Velocity",
            keybind = null,
            enabled = false,
            bodyComposable = {
                ForkSettingRow(label = "Horizontal", value = "95%", control = {
                    ForkSlider(
                        value = 95f,
                        onValueChange = {},
                        min = 0f,
                        max = 100f,
                        step = 1f,
                    )
                })
                Spacer(Modifier.height(ForkTokens.Spacing.controlRowGap))
                ForkSettingRow(label = "Vertical", value = "100%", control = {
                    ForkSlider(
                        value = 100f,
                        onValueChange = {},
                        min = 0f,
                        max = 100f,
                        step = 1f,
                    )
                })
            },
        ),
        MockModule(
            id = "reach",
            title = "Reach",
            keybind = "R",
            enabled = true,
            selectedDropdown = 2,
            bodyComposable = {
                ForkSettingRow(label = "Mode", control = {
                    ForkDropdown(
                        options = listOf("Silent", "Packet", "Entity"),
                        selectedIndex = 2,
                        onSelected = {},
                    )
                })
            },
        ),
        MockModule(
            id = "timer",
            title = "Timer",
            keybind = null,
            enabled = false,
            sliderValue = 20f,
            bodyComposable = {
                ForkSettingRow(label = "Tick duration", value = "20 t/s", control = {
                    ForkSlider(
                        value = 20f,
                        onValueChange = {},
                        min = 1f,
                        max = 100f,
                        step = 1f,
                    )
                })
            },
        ),
        MockModule(
            id = "waypoint",
            title = "Waypoint",
            keybind = null,
            enabled = true,
            multiSelectFlags = booleanArrayOf(true, false, true, false),
            bodyComposable = {
                ForkSettingRow(label = "Dimensions", control = {
                    ForkMultiSelectDropdown(
                        options = listOf("Overworld", "Nether", "End", "Custom"),
                        selectedFlags = booleanArrayOf(true, false, true, false),
                        onToggle = {},
                    )
                })
            },
        ),
    )
}
