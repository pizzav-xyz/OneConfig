package org.polyfrost.oneconfig.internal.ui.layout.fork

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import org.polyfrost.oneconfig.internal.ThemeConfig
import org.polyfrost.oneconfig.internal.ui.api.settings.ColorOptionData
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkDropdown
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkSliderRow
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkTestHooks
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkToggle
import org.polyfrost.oneconfig.internal.ui.components.fork.testBounds
import org.polyfrost.oneconfig.internal.ui.components.settings.ColorOption
import org.polyfrost.oneconfig.internal.ui.themes.ThemeRegistry

@Composable
fun AppearanceCard(modifier: Modifier = Modifier) {
    val themesTree = remember { ForkAppearance.themesTree() }
    val configTree = remember { ForkAppearance.configTree() }
    var customSize by remember { mutableStateOf(ForkAppearance.readCustomSize(configTree)) }
    var pixelSize by remember { mutableFloatStateOf(ForkAppearance.readPixelSize(configTree)) }

    LaunchedEffect(configTree) {
        customSize = ForkAppearance.readCustomSize(configTree)
        pixelSize = ForkAppearance.readPixelSize(configTree)
    }
    LaunchedEffect(Unit) {
        ForkAppearance.reapplyPersistedAccent()
    }

    val names = remember { ForkAppearance.themeNames() }
    val currentName = ThemeRegistry.activeTheme?.name ?: ThemeConfig.activeTheme

    ModuleCard(
        title = "Appearance",
        keybind = null,
        onKeybindCapture = { ForkTestHooks.record("keybind:appearance=$it") },
        onKeybindCancel = { ForkTestHooks.record("keybind-cancel:appearance") },
        enabled = customSize,
        onEnabledChange = {
            customSize = it
            ForkAppearance.writeScale(configTree, it, pixelSize)
        },
        testKey = ForkAppearance.TEST_KEY,
        body = {
            ForkSettingRow(
                label = "Accent colour",
                control = {
                    val prop = themesTree?.getProp(ForkAppearance.ACCENT_PROP)
                    if (prop != null) {
                        Box(
                            modifier = Modifier.testBounds("${ForkAppearance.TEST_KEY}-accent")
                        ) {
                            ColorOption(ColorOptionData(prop).apply {
                                pickerTestKey = "${ForkAppearance.TEST_KEY}-picker"
                                onPickerCommitArgb = { argb ->
                                    ForkAppearance.writeAccent(
                                        ForkAppearance.themesTree(),
                                        androidx.compose.ui.graphics.Color(argb),
                                    )
                                }
                            })
                        }
                    }
                },
            )
            val selectedIndex = names.indexOf(currentName).coerceAtLeast(0)
            ForkSettingRow(
                label = "Theme",
                control = {
                    ForkDropdown(
                        options = names,
                        selectedIndex = selectedIndex,
                        onSelected = {
                            if (ForkAppearance.activateTheme(names[it])) {
                                ForkTestHooks.record("dropdown:${ForkAppearance.TEST_KEY}-theme=${names[it]}")
                            }
                        },
                        testKey = "${ForkAppearance.TEST_KEY}-theme",
                    )
                },
            )
            ForkSliderRow(
                label = "UI pixel size",
                valueText = pixelSize.toString(),
                value = pixelSize,
                onValueChange = {
                    pixelSize = it
                    ForkAppearance.writeScale(configTree, true, it)
                    customSize = true
                },
                min = 1f,
                max = 4f,
                step = 0.5f,
                testKey = "${ForkAppearance.TEST_KEY}-scale",
            )
        },
        modifier = modifier,
    )
}
