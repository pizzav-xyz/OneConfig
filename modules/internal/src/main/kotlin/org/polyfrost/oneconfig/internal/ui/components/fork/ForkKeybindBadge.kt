package org.polyfrost.oneconfig.internal.ui.components.fork

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import org.polyfrost.oneconfig.internal.ui.components.Text
import org.polyfrost.oneconfig.internal.ui.components.onClick
import org.polyfrost.oneconfig.internal.ui.components.rememberInteractionSource
import org.polyfrost.oneconfig.internal.ui.themes.Accent
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme

/**
 * Fork-scoped keybind badge.
 *
 * Compact chip showing bound key or placeholder. Click to enter recording
 * state; Escape cancels and retains previous value. Dimensions come from [ForkTokens].
 */
@Composable
fun ForkKeybindBadge(
    keyName: String?,
    onKeyCapture: (String) -> Unit,
    onCancel: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testKey: String? = null,
) {
    val theme = LocalTheme.current
    val interactionSource = rememberInteractionSource()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()
    var recording by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val bgColor by animateColorAsState(
        when {
            recording -> theme.accentColor
            isHovered -> theme.componentBackground.copy(alpha = 0.8f)
            enabled -> Accent.copy(alpha = ForkTokens.Alpha.controlFill)
            else -> Color.White.copy(alpha = ForkTokens.Alpha.disabledFill)
        }
    )
    val textColor by animateColorAsState(
        if (recording) theme.shadowColor else theme.textColor
    )
    val ringColor by animateColorAsState(if (isFocused) Accent else Color.Transparent)

    LaunchedEffect(recording) {
        if (recording) focusRequester.requestFocus()
    }

    Row(
        modifier = modifier
            .widthIn(min = ForkTokens.Size.keybindMinWidth)
            .heightIn(min = ForkTokens.Size.keybindHeight)
            .testBounds(testKey)
            .onKeyEvent { event ->
                if (!recording) return@onKeyEvent false
                when (event.type) {
                    KeyEventType.KeyDown -> {
                        if (event.key == Key.Escape) {
                            onCancel?.invoke()
                            recording = false
                            return@onKeyEvent true
                        }
                        val name = keyNameFor(event.key)
                        if (name != null) {
                            onKeyCapture(name)
                            recording = false
                            return@onKeyEvent true
                        }
                        return@onKeyEvent true
                    }
                    else -> return@onKeyEvent true
                }
            }
            .focusRequester(focusRequester)
            .focusable(interactionSource = interactionSource)
            .background(bgColor, ForkTokens.pillShape)
            .border(
                ForkTokens.Size.focusRing,
                ringColor,
                ForkTokens.pillShape,
            )
            .onClick(interactionSource) { recording = !recording }
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(horizontal = ForkTokens.Padding.keybindHorizontal, vertical = ForkTokens.Padding.keybindVertical),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        val label = when {
            recording -> "KEY…"
            else -> keyName
        }
        if (label != null) {
            Text(
                label,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        } else {
            Box(
                modifier = Modifier.size(
                    ForkTokens.Size.keybindDashWidth,
                    ForkTokens.Size.keybindDashHeight,
                ).background(
                    if (enabled) Accent
                    else Color.White.copy(alpha = ForkTokens.Alpha.disabledContent),
                ),
            )
        }
    }
}

private fun keyNameFor(key: Key): String? = when (key) {
    Key.Spacebar -> "SPACE"
    Key.Tab -> "TAB"
    Key.Enter -> "ENTER"
    Key.Backspace -> "BKSP"
    Key.Delete -> "DEL"
    Key.Escape -> "ESC"
    Key.DirectionUp -> "UP"
    Key.DirectionDown -> "DOWN"
    Key.DirectionLeft -> "LEFT"
    Key.DirectionRight -> "RIGHT"
    Key.CtrlLeft, Key.CtrlRight -> "CTRL"
    Key.ShiftLeft, Key.ShiftRight -> "SHIFT"
    Key.AltLeft, Key.AltRight -> "ALT"
    else -> key.toString().takeLastWhile { it.isLetterOrDigit() }.takeIf { it.isNotBlank() }
}
