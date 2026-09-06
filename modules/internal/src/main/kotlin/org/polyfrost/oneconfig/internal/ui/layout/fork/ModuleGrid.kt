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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.polyfrost.oneconfig.internal.ui.components.Text
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
    searchQuery: String = "",
    selectedCategory: String = "combat",
    modifier: Modifier = Modifier,
) {
    val theme = LocalTheme.current
    val gap = ForkTokens.Spacing.cardGap
    val normalizedQuery = searchQuery.trim().lowercase()
    val visibleModules = remember(modules, normalizedQuery, selectedCategory) {
        modules.filter { m ->
            val categoryMatch = m.category == selectedCategory
            val searchMatch = normalizedQuery.isBlank() || m.title.lowercase().contains(normalizedQuery)
            categoryMatch && searchMatch
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(theme.pageBackground.withOpacityPercent(90f))
            .verticalScroll(rememberScrollState())
            .padding(gap),
        verticalArrangement = Arrangement.spacedBy(gap),
    ) {
        if (visibleModules.isEmpty()) {
            Text(
                text = "No modules match \"$searchQuery\"",
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
                rowModules.forEach { item ->
                    Box(modifier = Modifier.weight(1f)) {
                        ModuleCard(
                            title = item.title,
                            category = item.category,
                            keybind = item.keybind,
                            onKeybindCapture = { onKeybindCapture(item, it) },
                            enabled = item.enabled,
                            onEnabledChange = { onModuleToggle(item, it) },
                            body = item.bodyComposable,
                        )
                    }
                }
            }
        }
    }
}
