package com.meshchat.android.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.meshchat.android.ui.theme.MeshChatShapes

/**
 * Top app bar for the Chat List screen.
 *
 * - Wordmark / logo on the left
 * - Search icon → expands inline search field (animated)
 * - Settings icon on the right
 */
@Composable
fun MeshChatTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchOpen by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(searchOpen) {
        if (searchOpen) focusRequester.requestFocus()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .height(56.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // ── Left: logo or back-from-search icon ──────────────────────────
            if (searchOpen) {
                IconButton(onClick = {
                    searchOpen = false
                    onSearchQueryChange("")
                }) {
                    Icon(
                        imageVector    = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Close search",
                        tint           = MaterialTheme.colorScheme.onSurface,
                    )
                }
            } else {
                Spacer(Modifier.width(12.dp))
                Text(
                    text  = "MeshChat",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            // ── Centre: animated search field ────────────────────────────────
            AnimatedVisibility(
                visible = searchOpen,
                enter   = expandHorizontally() + fadeIn(),
                exit    = shrinkHorizontally() + fadeOut(),
                modifier = Modifier.weight(1f),
            ) {
                BasicTextField(
                    value               = searchQuery,
                    onValueChange       = onSearchQueryChange,
                    singleLine          = true,
                    textStyle           = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    cursorBrush         = SolidColor(MaterialTheme.colorScheme.primary),
                    keyboardOptions     = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions     = KeyboardActions(onSearch = {}),
                    modifier            = Modifier
                        .focusRequester(focusRequester)
                        .padding(horizontal = 8.dp),
                    decorationBox       = { inner ->
                        Box {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text  = "Search conversations…",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.alpha(0.6f),
                                )
                            }
                            inner()
                        }
                    },
                )
            }

            if (!searchOpen) Spacer(Modifier.weight(1f))

            // ── Right: clear search / search icon ────────────────────────────
            if (searchOpen && searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(
                        imageVector    = Icons.Default.Close,
                        contentDescription = "Clear search",
                        tint           = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier       = Modifier.size(20.dp),
                    )
                }
            } else if (!searchOpen) {
                IconButton(onClick = { searchOpen = true }) {
                    Icon(
                        imageVector    = Icons.Default.Search,
                        contentDescription = "Search",
                        tint           = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector    = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint           = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
