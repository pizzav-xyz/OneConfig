package org.polyfrost.oneconfig.internal.ui.layout.fork

import androidx.compose.foundation.layout.Arrangement
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
            .padding(ForkTokens.Padding.gridOuter),
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
                    .padding(top = ForkTokens.Padding.gridEmptyTop),
            )
            return@Column
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(gap),
            verticalAlignment = Alignment.Top,
        ) {
            val left = visibleModules.filterIndexed { index, _ -> index % 2 == 0 }
            val right = visibleModules.filterIndexed { index, _ -> index % 2 == 1 }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(gap),
            ) {
                left.forEach { module ->
                    ModuleGridCard(
                        module = module,
                        onKeybindCapture = onKeybindCapture,
                        onKeybindCancel = onKeybindCancel,
                        onModuleToggle = onModuleToggle,
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(gap),
            ) {
                right.forEach { module ->
                    ModuleGridCard(
                        module = module,
                        onKeybindCapture = onKeybindCapture,
                        onKeybindCancel = onKeybindCancel,
                        onModuleToggle = onModuleToggle,
                    )
                }
            }
        }
    }
}

@Composable
private fun ModuleGridCard(
    module: MockModule,
    onKeybindCapture: (MockModule, String) -> Unit,
    onKeybindCancel: (MockModule) -> Unit,
    onModuleToggle: (MockModule, Boolean) -> Unit,
) {
    ModuleCard(
        title = module.title,
        keybind = module.keybind,
        onKeybindCapture = { onKeybindCapture(module, it) },
        onKeybindCancel = { onKeybindCancel(module) },
        enabled = module.enabled,
        onEnabledChange = { onModuleToggle(module, it) },
        testKey = module.id,
        disabled = module.disabled,
        body = module.bodyComposable,
    )
}
