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
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkTokens
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme
import org.polyfrost.oneconfig.internal.ui.themes.withOpacityPercent

/**
 * 2-column responsive grid host for [ModuleCard] components.
 *
 * Gutters on both axes use [ForkTokens.Spacing.cardGap] (16dp). The grid is vertically
 * scrollable when the total content height exceeds the viewport. Odd card
 * counts are left-aligned with no placeholder in the second column.
 *
 * Single shadow token is handled by [ModuleCard]; the grid itself owns
 * no shadow brush — it only owns spacing.
 */
@Composable
fun ModuleGrid(
    modules: List<MockModule>,
    onKeybindCapture: (MockModule, String) -> Unit,
    onModuleToggle: (MockModule, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalTheme.current
    val gap = ForkTokens.Spacing.cardGap

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(theme.pageBackground.withOpacityPercent(90f))
            .verticalScroll(rememberScrollState())
            .padding(gap),
        verticalArrangement = Arrangement.spacedBy(gap),
    ) {
        modules.chunked(2).forEach { rowModules ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(gap),
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
