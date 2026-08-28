package org.polyfrost.oneconfig.internal.ui.layout.fork

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme
import org.polyfrost.oneconfig.internal.ui.themes.withOpacityPercent

/** Fork-specific spacing tokens. */
object ForkSpacing {
    val cardGap = 16.dp
}

/**
 * 2-column responsive grid host for [ModuleCard] components.
 *
 * Gutters on both axes use spacing.cardGap (16dp). The grid is vertically
 * scrollable when the total content height exceeds the viewport. Odd card
 * counts are left-aligned with no placeholder in the second column.
 */
@Composable
fun ModuleGrid(
    modules: List<MockModule>,
    onKeybindCapture: (MockModule, String) -> Unit,
    onModuleToggle: (MockModule, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalTheme.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(theme.pageBackground.withOpacityPercent(88f))
            .verticalScroll(rememberScrollState())
            .padding(ForkSpacing.cardGap),
        verticalArrangement = Arrangement.spacedBy(ForkSpacing.cardGap),
    ) {
        // Chunk into rows of 2
        modules.chunked(2).forEach { rowModules ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(ForkSpacing.cardGap),
            ) {
                rowModules.forEach { module ->
                    Box(modifier = Modifier.weight(1f)) {
                        ModuleCard(
                            title = module.title,
                            keybind = module.keybind,
                            onKeybindCapture = { onKeybindCapture(module, it) },
                            enabled = module.enabled,
                            onEnabledChange = { onModuleToggle(module, it) },
                            body = module.bodyComposable,
                        )
                    }
                }
            }
        }
    }
}
