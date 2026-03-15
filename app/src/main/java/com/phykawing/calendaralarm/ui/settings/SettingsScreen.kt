package com.phykawing.calendaralarm.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phykawing.calendaralarm.ui.components.AlarmSettingRow
import com.phykawing.calendaralarm.ui.components.AlarmTypePicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Global alarm toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Global Alarm",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Enable alarm for all events",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Automatically set alarms for all calendar events using the default settings below",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.globalAlarmEnabled,
                            onCheckedChange = { viewModel.toggleGlobalAlarm(it) }
                        )
                    }

                }
            }

            // Default alarm settings
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Default Alarm Settings",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = "Time before event",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    AlarmSettingRow(
                        offsetValue = uiState.defaultOffsetValue,
                        onOffsetValueChange = { viewModel.updateDefaultOffset(it) },
                        selectedUnit = uiState.defaultOffsetUnit,
                        onUnitChange = { viewModel.updateDefaultUnit(it) }
                    )

                    HorizontalDivider()

                    Text(
                        text = "Default alarm type",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    AlarmTypePicker(
                        selectedType = uiState.defaultAlarmType,
                        onTypeChange = { viewModel.updateDefaultAlarmType(it) }
                    )

                    HorizontalDivider()

                    Text(
                        text = "Default snooze: ${uiState.defaultSnoozeDuration} min",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Slider(
                        value = uiState.defaultSnoozeDuration.toFloat(),
                        onValueChange = { viewModel.updateDefaultSnoozeDuration(it.toInt()) },
                        valueRange = 1f..30f,
                        steps = 28
                    )
                }
            }

            // Sync settings
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Synchronization",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Auto-sync calendar",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Sync every 15 minutes",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.isSyncEnabled,
                            onCheckedChange = { viewModel.toggleSync(it) }
                        )
                    }
                }
            }

            // Apply to all button
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Apply to All Events",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Override all existing alarm settings with the default settings above. Useful for first-time setup or resetting all alarms.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = { viewModel.showApplyAllDialog() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isApplying,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        if (uiState.isApplying) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(end = 8.dp),
                                color = MaterialTheme.colorScheme.onError,
                                strokeWidth = 2.dp
                            )
                        }
                        Text(
                            text = if (uiState.isApplying) "Applying..." else "Apply Global Settings to All Events"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Confirmation dialog
    if (uiState.showApplyAllDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissApplyAllDialog() },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Apply to All Events?") },
            text = {
                Text(
                    "This will override all existing alarm settings for every event " +
                    "with the current default settings. Any individually customized " +
                    "alarm settings will be lost.\n\nThis action cannot be undone."
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.applyGlobalSettingsToAll() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Apply to All")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissApplyAllDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }
}
