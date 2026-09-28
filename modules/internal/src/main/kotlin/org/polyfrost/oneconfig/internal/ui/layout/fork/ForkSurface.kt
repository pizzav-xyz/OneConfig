package org.polyfrost.oneconfig.internal.ui.layout.fork

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkTestHooks
import org.polyfrost.oneconfig.internal.ui.themes.withOpacityPercent

/**
 * Dark-orange config surface (reference §1 Shell).
 *
 * Fullscreen dim scrim (black @45%, game visible behind) with the config drawn
 * as a centered floating [ForkPanel] only — never a full-bleed dashboard.
 * The rail stays fixed; the grid scrolls inside the panel.
 *
 * No header bar — the reference carries no title/search chrome inside the
 * panel; the rail selection is the only navigation.
 */
@Composable
fun ForkConfigSurface() {
    var selectedCategory by remember { mutableStateOf("combat") }
    var toggles by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
    val modules = remember(toggles) {
        MockModules.modules.map { it.copy(enabled = toggles[it.id] ?: it.enabled) }
    }

    val categories = remember {
        listOf(
            SidebarEntry("combat", "combat"),
            SidebarEntry("player", "profiles"),
            SidebarEntry("movement", "chevrons"),
            SidebarEntry("render", "eye"),
            SidebarEntry("world", "grid"),
            SidebarEntry("misc", "close"),
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.withOpacityPercent(45f)),
        )

        ForkPanel {
            Row {
                IconSidebar(
                    entries = categories,
                    selectedId = selectedCategory,
                    onSelected = { selectedCategory = it },
                    modifier = Modifier,
                )

                ModuleGrid(
                    modules = modules,
                    onKeybindCapture = { module, key ->
                        ForkTestHooks.record("keybind:${module.id}=$key")
                    },
                    onKeybindCancel = { module ->
                        ForkTestHooks.record("keybind-cancel:${module.id}")
                    },
                    onModuleToggle = { module, enabled ->
                        toggles = toggles + (module.id to enabled)
                        ForkTestHooks.record("toggle:${module.id}=$enabled")
                    },
                    selectedCategory = selectedCategory,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
