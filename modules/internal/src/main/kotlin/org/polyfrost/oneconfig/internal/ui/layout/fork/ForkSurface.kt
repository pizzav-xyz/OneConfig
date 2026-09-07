package org.polyfrost.oneconfig.internal.ui.layout.fork

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.polyfrost.oneconfig.internal.ui.components.Icon
import org.polyfrost.oneconfig.internal.ui.components.Text
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkTokens
import org.polyfrost.oneconfig.internal.ui.components.onClick
import org.polyfrost.oneconfig.internal.ui.components.rememberInteractionSource
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme
import org.polyfrost.oneconfig.internal.ui.themes.withOpacityPercent

@Composable
fun ForkConfigSurface(
    onBack: (() -> Unit)? = null,
) {
    var selectedCategory by remember { mutableStateOf("combat") }
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
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "OneConfig",
                        color = theme.textColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Dark Orange",
                        color = theme.accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = org.polyfrost.oneconfig.internal.ui.shell.ShellState.versionLabel?.takeIf { it.isNotBlank() } ?: "Fabric 1.21.1",
                        color = theme.textColorSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }
                Row(
                    modifier = Modifier
                        .width(220.dp)
                        .clip(ForkTokens.controlShape)
                        .background(theme.componentBackground.withOpacityPercent(85f), ForkTokens.controlShape)
                        .padding(horizontal = ForkTokens.Padding.dropdownPadding, vertical = ForkTokens.Spacing.controlRowGap),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon("settings-search", color = theme.textColorSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = theme.textColor,
                            fontSize = 13.sp,
                            fontFamily = theme.typography.family,
                        ),
                        cursorBrush = SolidColor(theme.textColor),
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isBlank()) {
                                Text(
                                    text = "Search…",
                                    color = theme.textColorSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            innerTextField()
                        },
                    )
                    if (searchQuery.isNotBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            "x",
                            color = theme.textColorSecondary,
                            modifier = Modifier
                                .size(14.dp)
                                .clickable { searchQuery = "" },
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${MockModules.modules.count { it.category == selectedCategory && (searchQuery.isBlank() || it.title.lowercase().contains(searchQuery.lowercase())) }} in ${selectedCategory.replaceFirstChar { c -> c.uppercase() }}",
                    color = theme.textColorSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                )
                if (searchQuery.isNotBlank()) {
                    Text(
                        text = "for \"${searchQuery}\"",
                        color = theme.accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ModuleGrid(
                modules = MockModules.modules,
                onKeybindCapture = { module, key ->
                    println("${module.id} -> $key")
                },
                onModuleToggle = { module, enabled ->
                    println("${module.id} enabled=$enabled")
                },
                searchQuery = searchQuery,
                selectedCategory = selectedCategory,
            )
        }
    }
}
