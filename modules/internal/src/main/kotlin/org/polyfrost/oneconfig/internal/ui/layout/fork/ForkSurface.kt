package org.polyfrost.oneconfig.internal.ui.layout.fork

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.polyfrost.oneconfig.internal.ui.components.Text
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkTokens
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme
import org.polyfrost.oneconfig.internal.ui.themes.withOpacityPercent

@Composable
fun ForkConfigSurface(
    onBack: (() -> Unit)? = null,
) {
    var selectedCategory by remember { mutableStateOf("Combat") }
    var searchQuery by remember { mutableStateOf("") }

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

    val theme = LocalTheme.current
    val headerShape = RoundedCornerShape(ForkTokens.Radii.card)

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(headerShape)
                    .background(theme.modCardBackground.withOpacityPercent(92f), headerShape)
                    .padding(ForkTokens.Padding.card),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "OneConfig",
                    color = theme.textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(ForkTokens.Spacing.controlRowGap))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ForkTokens.controlShape)
                    .background(theme.componentBackground.withOpacityPercent(85f), ForkTokens.controlShape)
                    .padding(horizontal = ForkTokens.Padding.dropdownPadding, vertical = ForkTokens.Spacing.controlRowGap),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = when {
                        searchQuery.isBlank() -> "Search settings…"
                        else -> searchQuery
                    },
                    color = if (searchQuery.isBlank()) theme.textColorSecondary else theme.textColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(ForkTokens.Spacing.cardGap))

            ModuleGrid(
                modules = MockModules.modules,
                onKeybindCapture = { module, key ->
                    println("${module.id} -> $key")
                },
                onModuleToggle = { module, enabled ->
                    println("${module.id} enabled=$enabled")
                },
            )
        }
    }
}
