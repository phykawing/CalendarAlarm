package com.phykawing.calendaralarm.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.phykawing.calendaralarm.domain.model.TimeUnit

@Composable
fun AlarmSettingRow(
    offsetValue: String,
    onOffsetValueChange: (String) -> Unit,
    selectedUnit: TimeUnit,
    onUnitChange: (TimeUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = offsetValue,
            onValueChange = { value ->
                if (value.isEmpty() || value.all { it.isDigit() }) {
                    onOffsetValueChange(value)
                }
            },
            label = { Text("Time before") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(100.dp),
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )

        TimeUnit.entries.forEach { unit ->
            FilterChip(
                selected = selectedUnit == unit,
                onClick = { onUnitChange(unit) },
                label = { Text(unit.label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}
