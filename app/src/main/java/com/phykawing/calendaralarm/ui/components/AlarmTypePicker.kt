package com.phykawing.calendaralarm.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.phykawing.calendaralarm.domain.model.AlarmType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlarmTypePicker(
    selectedType: AlarmType,
    onTypeChange: (AlarmType) -> Unit,
    modifier: Modifier = Modifier
) {
    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primary,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
    )

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedType == AlarmType.SOUND,
            onClick = { onTypeChange(AlarmType.SOUND) },
            label = { Text("Sound") },
            leadingIcon = {
                Icon(Icons.Default.MusicNote, contentDescription = null)
            },
            colors = chipColors
        )
        FilterChip(
            selected = selectedType == AlarmType.VIBRATION,
            onClick = { onTypeChange(AlarmType.VIBRATION) },
            label = { Text("Vibration") },
            leadingIcon = {
                Icon(Icons.Default.Vibration, contentDescription = null)
            },
            colors = chipColors
        )
        FilterChip(
            selected = selectedType == AlarmType.SOUND_VIBRATION,
            onClick = { onTypeChange(AlarmType.SOUND_VIBRATION) },
            label = { Text("Sound + Vibration") },
            leadingIcon = {
                Icon(Icons.Default.MusicNote, contentDescription = null)
            },
            colors = chipColors
        )
    }
}
