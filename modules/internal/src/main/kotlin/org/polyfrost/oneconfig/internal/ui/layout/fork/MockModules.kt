package org.polyfrost.oneconfig.internal.ui.layout.fork

import androidx.compose.runtime.Composable
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkCheckbox
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkDropdown
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkMultiSelectDropdown
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkSliderRow
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkTestHooks

data class MockModule(
    val id: String,
    val title: String,
    val category: String,
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
        MockModule(id = "auto-clicker", title = "Auto Clicker", category = "combat", keybind = "L", enabled = true, selectedDropdown = 1, bodyComposable = {
            ForkSliderRow(label = "Clicks per second", valueText = "12", value = 12f, onValueChange = { ForkTestHooks.record("slider:auto-clicker=$it") }, min = 1f, max = 50f, step = 1f, testKey = "slider-auto-clicker")
            ForkSettingRow(label = "Target entity", control = { ForkDropdown(options = listOf("Players", "Mobs", "All"), selectedIndex = 1, onSelected = { ForkTestHooks.record("dropdown:auto-clicker=$it") }, testKey = "dropdown-auto-clicker") })
        }),
        MockModule(id = "auto-eat", title = "Auto Eat", category = "player", keybind = null, enabled = false, bodyComposable = {
            ForkSliderRow(label = "Hunger threshold", valueText = "80%", value = 80f, onValueChange = { ForkTestHooks.record("slider:auto-eat=$it") }, min = 0f, max = 100f, step = 5f, testKey = "slider-auto-eat")
        }),
        MockModule(id = "auto-tool", title = "Auto Tool", category = "player", keybind = "G", enabled = true, multiSelectFlags = booleanArrayOf(true, true, false, false), bodyComposable = {
            ForkSettingRow(label = "Preferred materials", control = { ForkMultiSelectDropdown(options = listOf("Netherite", "Diamond", "Iron", "Stone"), selectedFlags = booleanArrayOf(true, true, false, false), onToggle = { ForkTestHooks.record("multiselect:auto-tool=$it") }, testKey = "multiselect-auto-tool") })
        }),
        MockModule(id = "chams", title = "Chams", category = "render", keybind = "H", enabled = true, checkboxChecked = true, bodyComposable = {
            ForkSettingRow(label = "Show entities through walls", control = { ForkCheckbox(checked = true, onCheckedChange = { ForkTestHooks.record("checkbox:chams=$it") }, testKey = "checkbox-chams") })
            ForkSettingRow(label = "Mode", control = { ForkDropdown(options = listOf("Wireframe", "Solid"), selectedIndex = 0, onSelected = { ForkTestHooks.record("dropdown:chams=$it") }, testKey = "dropdown-chams") })
        }),
        MockModule(id = "velocity", title = "Velocity", category = "movement", keybind = null, enabled = false, bodyComposable = {
            ForkSliderRow(label = "Horizontal", valueText = "95%", value = 95f, onValueChange = { ForkTestHooks.record("slider:velocity-h=$it") }, min = 0f, max = 100f, step = 1f, testKey = "slider-velocity-h")
            ForkSliderRow(label = "Vertical", valueText = "100%", value = 100f, onValueChange = { ForkTestHooks.record("slider:velocity-v=$it") }, min = 0f, max = 100f, step = 1f, testKey = "slider-velocity-v")
        }),
        MockModule(id = "reach", title = "Reach", category = "combat", keybind = "R", enabled = true, selectedDropdown = 2, bodyComposable = {
            ForkSettingRow(label = "Mode", control = { ForkDropdown(options = listOf("Silent", "Packet", "Entity"), selectedIndex = 2, onSelected = { ForkTestHooks.record("dropdown:reach=$it") }, testKey = "dropdown-reach") })
        }),
        MockModule(id = "timer", title = "Timer", category = "world", keybind = null, enabled = false, sliderValue = 20f, bodyComposable = {
            ForkSliderRow(label = "Tick duration", valueText = "20 t/s", value = 20f, onValueChange = { ForkTestHooks.record("slider:timer=$it") }, min = 1f, max = 100f, step = 5f, testKey = "slider-timer")
        }),
        MockModule(id = "waypoint", title = "Waypoint", category = "world", keybind = null, enabled = true, multiSelectFlags = booleanArrayOf(true, false, true, false), bodyComposable = {
            ForkSettingRow(label = "Dimensions", control = { ForkMultiSelectDropdown(options = listOf("Overworld", "Nether", "End", "Custom"), selectedFlags = booleanArrayOf(true, false, true, false), onToggle = { ForkTestHooks.record("multiselect:waypoint=$it") }, testKey = "multiselect-waypoint") })
        }),
        MockModule(id = "scaffold", title = "Scaffold", category = "movement", keybind = null, enabled = true, bodyComposable = {
            ForkSettingRow(label = "Safe walk", control = { ForkCheckbox(checked = true, onCheckedChange = { ForkTestHooks.record("checkbox:scaffold=$it") }, testKey = "checkbox-scaffold") })
        }),
        MockModule(id = "esp", title = "ESP", category = "render", keybind = null, enabled = false, bodyComposable = {
            ForkSettingRow(label = "Box color", control = { ForkDropdown(options = listOf("Red", "Green", "Blue"), selectedIndex = 1, onSelected = { ForkTestHooks.record("dropdown:esp=$it") }, testKey = "dropdown-esp") })
        }),
    )
}
