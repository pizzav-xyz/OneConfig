package org.polyfrost.oneconfig.internal.ui.components.fork

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.polyfrost.oneconfig.internal.ui.components.Icon
import org.polyfrost.oneconfig.internal.ui.components.Text
import org.polyfrost.oneconfig.internal.ui.components.onClick
import org.polyfrost.oneconfig.internal.ui.components.rememberInteractionSource
import org.polyfrost.oneconfig.internal.ui.themes.Accent
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import org.polyfrost.oneconfig.internal.ui.themes.fork.ForkRadii

private val BadgeShape = RoundedCornerShape(ForkRadii.control)

/**
 * Fork-scoped keybind badge, adapted from KeybindOption.
 *
 * Compact chip showing bound key or placeholder. Click to enter recording
 * state; Escape cancels and retains previous value.
 */
@Composable
fun ForkKeybindBadge(
    keyName: String?,
    onKeyCapture: (String) -> Unit,
    onCancel: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val theme = LocalTheme.current
    val interactionSource = rememberInteractionSource()
    val isHovered by interactionSource.collectIsHoveredAsState()
    var recording by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val bgColor by animateColorAsState(
        when {
            recording -> Accent.copy(alpha = 0.2f)
            isHovered -> theme.componentBackground.copy(alpha = 0.8f)
            else -> theme.componentBackground
        }
    )
    val borderColor by animateColorAsState(
        when {
            recording -> Accent
            isHovered -> theme.textColor.copy(alpha = 0.2f)
            else -> theme.borderColor
        }
    )
    val textColor by animateColorAsState(
        if (recording) Accent else theme.textColor
    )

    LaunchedEffect(recording) {
        if (recording) focusRequester.requestFocus()
    }

    Row(
        modifier = modifier
            .widthIn(min = 60.dp)
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
            .focusable()
            .background(bgColor, BadgeShape)
            .border(1.dp, borderColor, BadgeShape)
            .onClick(interactionSource) { recording = !recording }
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            "keyboard",
            color = textColor,
            modifier = Modifier.size(14.dp),
        )
        Text(
            when {
                recording -> "Press a key…"
                keyName != null -> keyName
                else -> "—"
            },
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
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
