package org.polyfrost.oneconfig.internal.ui.layout.fork

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkCheckbox
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkDropdown
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkEnumOptions
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkMultiSelectDropdown
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkSliderRow
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkTestHooks
import org.polyfrost.oneconfig.internal.ui.components.fork.countSelectedFlags
import org.polyfrost.oneconfig.internal.ui.components.fork.formatMultiSelectLabel

data class MockModule(
    val id: String,
    val title: String,
    val category: String,
    val keybind: String?,
    val enabled: Boolean,
    val bodyComposable: @Composable () -> Unit,
    val disabled: Boolean = false,
)

object MockModules {
    /** Stateful selection overrides so E2E interactions persist visually. */
    val dropdownSelection = mutableStateMapOf<String, Int>()
    val enumSelection = mutableStateMapOf<String, Int>()

    val modules: List<MockModule> = listOf(
        MockModule(id = "auto-clicker", title = "Auto Clicker", category = "combat", keybind = "L", enabled = true, bodyComposable = {
            ForkSettingRow(label = "Check invisible", control = { ForkCheckbox(checked = true, onCheckedChange = { ForkTestHooks.record("checkbox:auto-clicker=$it") }, testKey = "checkbox-auto-clicker") })
            ForkSliderRow(label = "Clicks per second", valueText = "12", value = 12f, onValueChange = { ForkTestHooks.record("slider:auto-clicker=$it") }, min = 1f, max = 50f, step = 1f, testKey = "slider-auto-clicker")
            ForkStackedRow(label = "Target entity", control = { ForkDropdown(options = listOf("Players", "Mobs", "All"), selectedIndex = dropdownSelection["auto-clicker"] ?: 1, onSelected = { dropdownSelection["auto-clicker"] = it; ForkTestHooks.record("dropdown:auto-clicker=$it") }, testKey = "dropdown-auto-clicker") })
            ForkEnumOptions(options = listOf("Hypixel", "Matrix", "Vanilla"), selectedIndex = enumSelection["auto-clicker"] ?: 0, onSelected = { enumSelection["auto-clicker"] = it; ForkTestHooks.record("enum:auto-clicker=$it") }, label = "Detection mode", testKeyPrefix = "enum-auto-clicker")
        }),
        MockModule(id = "auto-eat", title = "Auto Eat", category = "player", keybind = null, enabled = false, disabled = true, bodyComposable = {
            ForkSliderRow(label = "Hunger threshold", valueText = "80%", value = 80f, onValueChange = { ForkTestHooks.record("slider:auto-eat=$it") }, min = 0f, max = 100f, step = 5f, testKey = "slider-auto-eat")
        }),
        MockModule(id = "auto-tool", title = "Auto Tool", category = "player", keybind = "G", enabled = true, bodyComposable = {
            val autoToolFlags = booleanArrayOf(true, true, false, false)
            ForkStackedRow(label = "Preferred materials", valueText = formatMultiSelectLabel(countSelectedFlags(autoToolFlags), autoToolFlags.size), control = { ForkMultiSelectDropdown(options = listOf("Netherite", "Diamond", "Iron", "Stone"), selectedFlags = autoToolFlags, onToggle = { ForkTestHooks.record("multiselect:auto-tool=$it") }, testKey = "multiselect-auto-tool") })
        }),
        MockModule(id = "auto-heal", title = "Auto Heal", category = "player", keybind = "H", enabled = true, bodyComposable = {
            ForkSettingRow(label = "Use gapples", control = { ForkCheckbox(checked = true, onCheckedChange = { ForkTestHooks.record("checkbox:auto-heal=$it") }, testKey = "checkbox-auto-heal") })
            ForkSliderRow(label = "Health threshold", valueText = "12", value = 12f, onValueChange = { ForkTestHooks.record("slider:auto-heal=$it") }, min = 1f, max = 20f, step = 1f, testKey = "slider-auto-heal")
        }),
        MockModule(id = "auto-respawn", title = "Auto Respawn", category = "player", keybind = null, enabled = true, bodyComposable = {
            ForkSettingRow(label = "Instant respawn", control = { ForkCheckbox(checked = false, onCheckedChange = { ForkTestHooks.record("checkbox:auto-respawn=$it") }, testKey = "checkbox-auto-respawn") })
        }),
        MockModule(id = "chams", title = "Chams", category = "render", keybind = "X", enabled = true, bodyComposable = {
            ForkSettingRow(label = "Show entities through walls", control = { ForkCheckbox(checked = true, onCheckedChange = { ForkTestHooks.record("checkbox:chams=$it") }, testKey = "checkbox-chams") })
            ForkEnumOptions(options = listOf("Wireframe", "Solid"), selectedIndex = enumSelection["chams"] ?: 0, onSelected = { enumSelection["chams"] = it; ForkTestHooks.record("enum:chams=$it") }, label = "Mode", testKeyPrefix = "enum-chams")
        }),
        MockModule(id = "velocity", title = "Velocity", category = "movement", keybind = null, enabled = false, bodyComposable = {
            ForkSliderRow(label = "Horizontal", valueText = "95%", value = 95f, onValueChange = { ForkTestHooks.record("slider:velocity-h=$it") }, min = 0f, max = 100f, step = 1f, testKey = "slider-velocity-h")
            ForkSliderRow(label = "Vertical", valueText = "100%", value = 100f, onValueChange = { ForkTestHooks.record("slider:velocity-v=$it") }, min = 0f, max = 100f, step = 1f, testKey = "slider-velocity-v")
        }),
        MockModule(id = "reach", title = "Reach", category = "combat", keybind = "R", enabled = true, bodyComposable = {
            ForkSettingRow(label = "Legit mode", control = { ForkCheckbox(checked = false, onCheckedChange = { ForkTestHooks.record("checkbox:reach=$it") }, testKey = "checkbox-reach") })
            ForkStackedRow(label = "Mode", control = { ForkDropdown(options = listOf("Silent", "Packet", "Entity"), selectedIndex = dropdownSelection["reach"] ?: 2, onSelected = { dropdownSelection["reach"] = it; ForkTestHooks.record("dropdown:reach=$it") }, testKey = "dropdown-reach") })
        }),
        MockModule(id = "anti-bot", title = "Anti Bot", category = "combat", keybind = "B", enabled = true, bodyComposable = {
            ForkSettingRow(label = "Remove bots", control = { ForkCheckbox(checked = true, onCheckedChange = { ForkTestHooks.record("checkbox:anti-bot=$it") }, testKey = "checkbox-anti-bot") })
            ForkSliderRow(label = "Entity limit", valueText = "20", value = 20f, onValueChange = { ForkTestHooks.record("slider:anti-bot=$it") }, min = 1f, max = 100f, step = 1f, testKey = "slider-anti-bot")
        }),
        MockModule(id = "auto-armor", title = "Auto Armor", category = "combat", keybind = "N", enabled = true, bodyComposable = {
            ForkSettingRow(label = "Prioritize enchantments", control = { ForkCheckbox(checked = true, onCheckedChange = { ForkTestHooks.record("checkbox:auto-armor=$it") }, testKey = "checkbox-auto-armor") })
            ForkSliderRow(label = "Equip delay", valueText = "2.6", value = 2.6f, onValueChange = { ForkTestHooks.record("slider:auto-armor=$it") }, min = 0f, max = 10f, step = 0.1f, testKey = "slider-auto-armor")
        }),
        MockModule(id = "timer", title = "Timer", category = "world", keybind = null, enabled = false, bodyComposable = {
            ForkSliderRow(label = "Tick duration", valueText = "20 t/s", value = 20f, onValueChange = { ForkTestHooks.record("slider:timer=$it") }, min = 1f, max = 100f, step = 5f, testKey = "slider-timer")
        }),
        MockModule(id = "waypoint", title = "Waypoint", category = "world", keybind = null, enabled = true, bodyComposable = {
            val waypointFlags = booleanArrayOf(true, false, true, false)
            ForkStackedRow(label = "Dimensions", valueText = formatMultiSelectLabel(countSelectedFlags(waypointFlags), waypointFlags.size), control = { ForkMultiSelectDropdown(options = listOf("Overworld", "Nether", "End", "Custom"), selectedFlags = waypointFlags, onToggle = { ForkTestHooks.record("multiselect:waypoint=$it") }, testKey = "multiselect-waypoint") })
        }),
        MockModule(id = "scaffold", title = "Scaffold", category = "movement", keybind = null, enabled = true, bodyComposable = {
            ForkSettingRow(label = "Safe walk", control = { ForkCheckbox(checked = true, onCheckedChange = { ForkTestHooks.record("checkbox:scaffold=$it") }, testKey = "checkbox-scaffold") })
        }),
        MockModule(id = "esp", title = "ESP", category = "render", keybind = null, enabled = false, bodyComposable = {
            ForkStackedRow(label = "Box color", control = { ForkDropdown(options = listOf("Red", "Green", "Blue"), selectedIndex = dropdownSelection["esp"] ?: 1, onSelected = { dropdownSelection["esp"] = it; ForkTestHooks.record("dropdown:esp=$it") }, testKey = "dropdown-esp") })
        }),
        MockModule(id = "speed", title = "Speed", category = "movement", keybind = "C", enabled = true, bodyComposable = {
            ForkSettingRow(label = "Strafe only", control = { ForkCheckbox(checked = false, onCheckedChange = { ForkTestHooks.record("checkbox:speed=$it") }, testKey = "checkbox-speed") })
            ForkSliderRow(label = "Speed", valueText = "1.5", value = 1.5f, onValueChange = { ForkTestHooks.record("slider:speed=$it") }, min = 0.5f, max = 5f, step = 0.1f, testKey = "slider-speed")
        }),
        MockModule(id = "fly", title = "Fly", category = "movement", keybind = "F", enabled = true, bodyComposable = {
            ForkStackedRow(label = "Mode", control = { ForkDropdown(options = listOf("Vanilla", "Creative", "Packet"), selectedIndex = dropdownSelection["fly"] ?: 0, onSelected = { dropdownSelection["fly"] = it; ForkTestHooks.record("dropdown:fly=$it") }, testKey = "dropdown-fly") })
        }),
        MockModule(id = "nametags", title = "Nametags", category = "render", keybind = null, enabled = true, bodyComposable = {
            ForkSettingRow(label = "Show health", control = { ForkCheckbox(checked = true, onCheckedChange = { ForkTestHooks.record("checkbox:nametags=$it") }, testKey = "checkbox-nametags") })
            ForkSliderRow(label = "Scale", valueText = "1.0", value = 1f, onValueChange = { ForkTestHooks.record("slider:nametags=$it") }, min = 0.5f, max = 2f, step = 0.1f, testKey = "slider-nametags")
        }),
        MockModule(id = "tracers", title = "Tracers", category = "render", keybind = "T", enabled = false, bodyComposable = {
            ForkStackedRow(label = "Target", control = { ForkDropdown(options = listOf("Players", "Mobs", "All"), selectedIndex = dropdownSelection["tracers"] ?: 0, onSelected = { dropdownSelection["tracers"] = it; ForkTestHooks.record("dropdown:tracers=$it") }, testKey = "dropdown-tracers") })
        }),
        MockModule(id = "auto-mine", title = "Auto Mine", category = "world", keybind = null, enabled = true, bodyComposable = {
            ForkSliderRow(label = "Range", valueText = "4", value = 4f, onValueChange = { ForkTestHooks.record("slider:auto-mine=$it") }, min = 1f, max = 8f, step = 1f, testKey = "slider-auto-mine")
        }),
        MockModule(id = "chest-finder", title = "Chest Finder", category = "world", keybind = null, enabled = true, bodyComposable = {
            ForkSettingRow(label = "Highlight chests", control = { ForkCheckbox(checked = true, onCheckedChange = { ForkTestHooks.record("checkbox:chest-finder=$it") }, testKey = "checkbox-chest-finder") })
            ForkStackedRow(label = "Color", control = { ForkDropdown(options = listOf("Orange", "Green", "White"), selectedIndex = dropdownSelection["chest-finder"] ?: 0, onSelected = { dropdownSelection["chest-finder"] = it; ForkTestHooks.record("dropdown:chest-finder=$it") }, testKey = "dropdown-chest-finder") })
        }),
        MockModule(id = "w-tap", title = "W-Tap", category = "combat", keybind = "W", enabled = true, bodyComposable = {
            ForkSettingRow(label = "Only in combat", control = { ForkCheckbox(checked = true, onCheckedChange = { ForkTestHooks.record("checkbox:w-tap=$it") }, testKey = "checkbox-w-tap") })
            ForkSliderRow(label = "Range", valueText = "3.0", value = 3f, onValueChange = { ForkTestHooks.record("slider:w-tap=$it") }, min = 2f, max = 6f, step = 0.5f, testKey = "slider-w-tap")
        }),
        MockModule(id = "hit-select", title = "Hit Select", category = "combat", keybind = null, enabled = false, bodyComposable = {
            ForkSettingRow(label = "Reset sprint", control = { ForkCheckbox(checked = false, onCheckedChange = { ForkTestHooks.record("checkbox:hit-select=$it") }, testKey = "checkbox-hit-select") })
            ForkStackedRow(label = "Mode", control = { ForkDropdown(options = listOf("Off", "Hypixel", "Matrix"), selectedIndex = dropdownSelection["hit-select"] ?: 0, onSelected = { dropdownSelection["hit-select"] = it; ForkTestHooks.record("dropdown:hit-select=$it") }, testKey = "dropdown-hit-select") })
        }),
        MockModule(id = "keep-sprint", title = "Keep Sprint", category = "player", keybind = "K", enabled = true, bodyComposable = {
            ForkSettingRow(label = "Sprint in all directions", control = { ForkCheckbox(checked = true, onCheckedChange = { ForkTestHooks.record("checkbox:keep-sprint=$it") }, testKey = "checkbox-keep-sprint") })
        }),
        MockModule(id = "no-fall", title = "No Fall", category = "player", keybind = null, enabled = true, bodyComposable = {
            ForkSettingRow(label = "Disable fall damage", control = { ForkCheckbox(checked = false, onCheckedChange = { ForkTestHooks.record("checkbox:no-fall=$it") }, testKey = "checkbox-no-fall") })
        }),
        MockModule(id = "step", title = "Step", category = "movement", keybind = null, enabled = false, bodyComposable = {
            ForkSliderRow(label = "Height", valueText = "1", value = 1f, onValueChange = { ForkTestHooks.record("slider:step=$it") }, min = 1f, max = 3f, step = 1f, testKey = "slider-step")
        }),
        MockModule(id = "no-slow", title = "No Slow", category = "movement", keybind = "V", enabled = true, bodyComposable = {
            ForkSettingRow(label = "No slowdown", control = { ForkCheckbox(checked = true, onCheckedChange = { ForkTestHooks.record("checkbox:no-slow=$it") }, testKey = "checkbox-no-slow") })
        }),
        MockModule(id = "fullbright", title = "Fullbright", category = "render", keybind = "U", enabled = true, bodyComposable = {
            ForkSliderRow(label = "Brightness", valueText = "100%", value = 100f, onValueChange = { ForkTestHooks.record("slider:fullbright=$it") }, min = 0f, max = 100f, step = 5f, testKey = "slider-fullbright")
        }),
        MockModule(id = "block-overlay", title = "Block Overlay", category = "render", keybind = "O", enabled = false, bodyComposable = {
            ForkSettingRow(label = "Highlight ores", control = { ForkCheckbox(checked = false, onCheckedChange = { ForkTestHooks.record("checkbox:block-overlay=$it") }, testKey = "checkbox-block-overlay") })
        }),
        MockModule(id = "fast-break", title = "Fast Break", category = "world", keybind = null, enabled = true, bodyComposable = {
            ForkSliderRow(label = "Speed", valueText = "2", value = 2f, onValueChange = { ForkTestHooks.record("slider:fast-break=$it") }, min = 1f, max = 5f, step = 1f, testKey = "slider-fast-break")
        }),
        MockModule(id = "auto-farm", title = "Auto Farm", category = "world", keybind = "M", enabled = true, bodyComposable = {
            ForkSettingRow(label = "Replant", control = { ForkCheckbox(checked = true, onCheckedChange = { ForkTestHooks.record("checkbox:auto-farm=$it") }, testKey = "checkbox-auto-farm") })
            ForkStackedRow(label = "Crop", control = { ForkDropdown(options = listOf("Wheat", "Carrot", "Potato"), selectedIndex = dropdownSelection["auto-farm"] ?: 0, onSelected = { dropdownSelection["auto-farm"] = it; ForkTestHooks.record("dropdown:auto-farm=$it") }, testKey = "dropdown-auto-farm") })
        }),
    )
}
