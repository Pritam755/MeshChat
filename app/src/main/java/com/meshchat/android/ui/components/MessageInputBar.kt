package com.meshchat.android.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.meshchat.android.ui.theme.ShapeInput

/**
 * Bottom input bar for the conversation screen.
 *
 * - Attachment icon on the left (opens media picker)
 * - Expanding text field (up to 6 lines) with placeholder
 * - Right button: morphs from **mic** (empty) → **send** (has text)
 *
 * The component is stateless — the caller holds the [inputText] state and
 * supplies [onSendClick] and [onAttachClick] callbacks.
 */
@Composable
fun MessageInputBar(
    inputText:      String,
    onInputChange:  (String) -> Unit,
    onSendClick:    () -> Unit,
    onAttachClick:  () -> Unit,
    onMicClick:     () -> Unit = {},
    modifier:       Modifier = Modifier,
) {
    val hasText = inputText.isNotBlank()

    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
    ) {
        // ── Attachment icon ───────────────────────────────────────────────────
        IconButton(
            onClick  = onAttachClick,
            modifier = Modifier.padding(bottom = 4.dp),
        ) {
            Icon(
                imageVector        = Icons.Default.AttachFile,
                contentDescription = "Attach file",
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // ── Text field ────────────────────────────────────────────────────────
        BasicTextField(
            value           = inputText,
            onValueChange   = onInputChange,
            textStyle       = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            cursorBrush     = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
            ),
            maxLines        = 6,
            modifier        = Modifier
                .weight(1f)
                .heightIn(min = 40.dp)
                .clip(ShapeInput)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            decorationBox   = { innerTextField ->
                Box {
                    if (inputText.isEmpty()) {
                        Text(
                            text     = "Message…",
                            style    = MaterialTheme.typography.bodyLarge,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.alpha(0.5f),
                        )
                    }
                    innerTextField()
                }
            },
        )

        // ── Send / mic button ─────────────────────────────────────────────────
        IconButton(
            onClick  = if (hasText) onSendClick else onMicClick,
            modifier = Modifier
                .padding(bottom = 4.dp, start = 4.dp)
                .size(44.dp)
                .clip(ShapeInput)
                .background(
                    if (hasText) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant,
                ),
        ) {
            AnimatedContent(
                targetState   = hasText,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label         = "sendMicTransition",
            ) { isSend ->
                Icon(
                    imageVector        = if (isSend) Icons.Default.Send else Icons.Default.Mic,
                    contentDescription = if (isSend) "Send" else "Voice message",
                    tint               = if (isSend) MaterialTheme.colorScheme.onPrimary
                                         else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier           = Modifier.size(22.dp),
                )
            }
        }
    }
}
