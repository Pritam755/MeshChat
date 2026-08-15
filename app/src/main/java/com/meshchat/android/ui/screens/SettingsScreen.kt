package com.meshchat.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meshchat.android.BuildConfig
import com.meshchat.android.ui.ChatViewModel

/**
 * Settings / Profile screen.
 *
 * Sections:
 *  1. Profile — editable display name
 *  2. Emergency wipe — panic mode explainer + trigger button
 *  3. Mesh network — live peer count, connection type, peer ID
 *  4. About — version, license notice (GPL-3.0)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ChatViewModel,
    onBack:    () -> Unit,
    modifier:  Modifier = Modifier,
) {
    val nickname      by viewModel.nickname.collectAsState()
    val peerCount     by viewModel.peerCount.collectAsState()
    val meshStatus    by viewModel.meshStatusText.collectAsState()
    val myPeerId      = remember { viewModel.myPeerID }

    var nameInput       by remember(nickname) { mutableStateOf(nickname) }
    var showPanicDialog by remember { mutableStateOf(false) }

    if (showPanicDialog) {
        PanicWipeDialog(
            onConfirm = {
                showPanicDialog = false
                viewModel.handlePanicWipe()
            },
            onDismiss = { showPanicDialog = false },
        )
    }

    Scaffold(
        modifier  = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar    = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor         = MaterialTheme.colorScheme.surface,
                    titleContentColor      = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // ── 1. Profile ────────────────────────────────────────────────────
            SettingsSection(title = "Profile") {
                OutlinedTextField(
                    value         = nameInput,
                    onValueChange = { nameInput = it },
                    label         = { Text("Display name") },
                    singleLine    = true,
                    trailingIcon  = {
                        Icon(Icons.Default.Edit, contentDescription = null,
                             tint = MaterialTheme.colorScheme.primary)
                    },
                    modifier      = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick   = { viewModel.setNickname(nameInput.trim()) },
                    enabled   = nameInput.isNotBlank() && nameInput.trim() != nickname,
                    modifier  = Modifier.fillMaxWidth(),
                ) {
                    Text("Save name")
                }
            }

            // ── 2. Emergency Wipe ─────────────────────────────────────────────
            SettingsSection(title = "Emergency Wipe") {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                    ),
                    shape  = RoundedCornerShape(12.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Panic Mode",
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text  = "Triple-tap the avatar in any conversation header to instantly wipe all messages, keys, and settings from this device. You can also trigger it here.\n\nThis action is immediate and irreversible.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { showPanicDialog = true },
                            colors  = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Trigger Emergency Wipe")
                        }
                    }
                }
            }

            // ── 3. Mesh Network ───────────────────────────────────────────────
            SettingsSection(title = "Mesh Network") {
                SettingsInfoRow(
                    icon  = Icons.Default.People,
                    label = "Connected peers",
                    value = peerCount.toString(),
                )
                SettingsInfoRow(
                    icon  = Icons.Default.Bluetooth,
                    label = "Status",
                    value = meshStatus,
                )
                SettingsInfoRow(
                    icon  = Icons.Default.Info,
                    label = "My peer ID",
                    value = myPeerId.take(16) + "…",
                    monospace = true,
                )
            }

            // ── 4. About ──────────────────────────────────────────────────────
            SettingsSection(title = "About") {
                SettingsInfoRow(
                    icon  = Icons.Default.Info,
                    label = "Version",
                    value = try { BuildConfig.VERSION_NAME } catch (_: Exception) { "dev" },
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = "MeshChat is free software released under the GNU General Public License v3.0. It is based on the bitchat-android project (permissionlesstech/bitchat-android).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ─── Sub-components ───────────────────────────────────────────────────────────

@Composable
private fun SettingsSection(
    title:   String,
    content: @Composable () -> Unit,
) {
    Column {
        Text(
            text      = title.uppercase(),
            style     = MaterialTheme.typography.labelSmall,
            color     = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.2.sp,
            modifier  = Modifier.padding(bottom = 8.dp),
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape  = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) { content() }
        }
    }
}

@Composable
private fun SettingsInfoRow(
    icon:      ImageVector,
    label:     String,
    value:     String,
    monospace: Boolean = false,
) {
    Row(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment   = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null,
                 tint = MaterialTheme.colorScheme.onSurfaceVariant,
                 modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium,
                 color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            text  = value,
            style = MaterialTheme.typography.bodyMedium.let {
                if (monospace) it.copy(fontFamily = FontFamily.Monospace) else it
            },
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun PanicWipeDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.Warning,
                 contentDescription = null,
                 tint = MaterialTheme.colorScheme.error,
                 modifier = Modifier.size(36.dp))
        },
        title = { Text("Emergency Wipe", fontWeight = FontWeight.Bold) },
        text  = {
            Text(
                "This will immediately erase ALL messages, keys, and settings. " +
                "The app will reset to factory state. This cannot be undone.",
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors  = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                ),
            ) { Text("Wipe now") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
