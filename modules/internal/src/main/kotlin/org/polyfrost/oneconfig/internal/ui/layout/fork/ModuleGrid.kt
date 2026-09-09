package org.polyfrost.oneconfig.internal.ui.layout.fork

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.polyfrost.oneconfig.internal.ui.components.Text
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkTokens
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme

/**
 * 2-column dense grid host for [ModuleCard] components.
 *
 * Gutters on both axes use [ForkTokens.Spacing.cardGap]. The grid is vertically
 * scrollable and does not draw its own background fill. Odd card counts are
 * left-aligned with no placeholder in the second column.
 */
@Composable
fun ModuleGrid(
    modules: List<MockModule>,
    onKeybindCapture: (MockModule, String) -> Unit,
    onKeybindCancel: (MockModule) -> Unit = {},
    onModuleToggle: (MockModule, Boolean) -> Unit,
    selectedCategory: String = "combat",
    modifier: Modifier = Modifier,
) {
    val theme = LocalTheme.current
    val gap = ForkTokens.Spacing.cardGap
    val visibleModules = remember(modules, selectedCategory) {
        modules.filter { m -> m.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(gap),
        verticalArrangement = Arrangement.spacedBy(gap),
    ) {
        if (visibleModules.isEmpty()) {
            Text(
                text = "No modules in ${selectedCategory.replaceFirstChar { it.uppercase() }}",
                color = theme.textColorSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
            )
            return@Column
        }

        visibleModules.chunked(2).forEach { rowModules ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(gap),
            ) {
                val first = rowModules.first()
                Box(modifier = Modifier.weight(1f)) {
                    ModuleCard(
                        title = first.title,
                        category = first.category,
                        keybind = first.keybind,
                        onKeybindCapture = { onKeybindCapture(first, it) },
                        onKeybindCancel = { onKeybindCancel(first) },
                        enabled = first.enabled,
                        onEnabledChange = { onModuleToggle(first, it) },
                        testKey = first.id,
                        body = first.bodyComposable,
                    )
                }
                if (rowModules.size > 1) {
                    val second = rowModules[1]
                    Box(modifier = Modifier.weight(1f)) {
                        ModuleCard(
                            title = second.title,
                            category = second.category,
                            keybind = second.keybind,
                            onKeybindCapture = { onKeybindCapture(second, it) },
                            onKeybindCancel = { onKeybindCancel(second) },
                            enabled = second.enabled,
                            onEnabledChange = { onModuleToggle(second, it) },
                            testKey = second.id,
                            body = second.bodyComposable,
                        )
                    }
                }
            }
        }
    }
}
