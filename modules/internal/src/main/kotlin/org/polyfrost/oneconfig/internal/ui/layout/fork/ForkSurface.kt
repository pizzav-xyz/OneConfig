package org.polyfrost.oneconfig.internal.ui.layout.fork

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkTokens
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme
import org.polyfrost.oneconfig.internal.ui.themes.withOpacityPercent

/**
 * Dark-orange config surface: icon rail + module grid.
 *
 * No header bar — the reference carries no title/search chrome inside the
 * panel; the rail selection is the only navigation.
 */
@Composable
fun ForkConfigSurface(
    onBack: (() -> Unit)? = null,
) {
    var selectedCategory by remember { mutableStateOf("combat") }

    val categories = remember {
        listOf(
            SidebarEntry("combat", "combat"),
            SidebarEntry("player", "profiles"),
            SidebarEntry("movement", "move"),
            SidebarEntry("render", "paintbrush"),
            SidebarEntry("world", "layers"),
            SidebarEntry("misc", "qol"),
        )
    }

    val theme = LocalTheme.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(theme.pageBackground.withOpacityPercent(90f)),
    ) {
        IconSidebar(
            entries = categories,
            selectedId = selectedCategory,
            onSelected = { selectedCategory = it },
            modifier = Modifier,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(ForkTokens.Spacing.cardGap),
        ) {
            ModuleGrid(
                modules = MockModules.modules,
                onKeybindCapture = { module, key ->
                    println("${module.id} -> $key")
                },
                onModuleToggle = { module, enabled ->
                    println("${module.id} enabled=$enabled")
                },
                selectedCategory = selectedCategory,
            )
        }
    }
}
