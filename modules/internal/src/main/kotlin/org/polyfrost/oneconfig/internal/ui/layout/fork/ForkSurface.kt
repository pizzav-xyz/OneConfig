package org.polyfrost.oneconfig.internal.ui.layout.fork

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme

/**
 * Fork-scoped config surface.
 *
 * Composes [IconSidebar] + [ModuleGrid] into a single dark-orange surface.
 * This file replaces navigation-routed per-mod screens within the Fork's scope.
 */
@Composable
fun ForkConfigSurface(
    onBack: (() -> Unit)? = null,
) {
    var selectedCategory by remember { mutableStateOf("Combat") }

    val categories = remember {
        listOf(
            SidebarEntry("combat", "settings"),
            SidebarEntry("player", "profiles"),
            SidebarEntry("movement", "activity"),
            SidebarEntry("render", "eye"),
            SidebarEntry("world", "box"),
            SidebarEntry("misc", "help-circle"),
        )
    }

    Row(modifier = Modifier) {
        // Left rail
        IconSidebar(
            entries = categories,
            selectedId = selectedCategory,
            onSelected = { selectedCategory = it },
        )

        // Right content: 2-column module grid for the selected category
        ModuleGrid(
            modules = MockModules.modules,
            onKeybindCapture = { module, key ->
                // TODO: persist via config service
                println("${module.id} -> $key")
            },
            onModuleToggle = { module, enabled ->
                // TODO: persist via config service
                println("${module.id} enabled=$enabled")
            },
        )
    }
}
