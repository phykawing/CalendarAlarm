package com.phykawing.calendaralarm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.phykawing.calendaralarm.domain.model.CalendarEvent
import com.phykawing.calendaralarm.ui.theme.GradientEnd
import com.phykawing.calendaralarm.ui.theme.GradientMid
import com.phykawing.calendaralarm.ui.theme.GradientStart
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EventCard(
    event: CalendarEvent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    alarmTime: Long? = null
) {
    val dateFormat = SimpleDateFormat("EEE, MMM d · h:mm a", Locale.getDefault())

    val accentColor = when {
        event.isSnoozed -> MaterialTheme.colorScheme.secondary
        event.isAlarmEnabled -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.height(intrinsicSize = androidx.compose.foundation.layout.IntrinsicSize.Min)) {
            // Left accent strip
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(MaterialTheme.shapes.small)
                    .background(
                        if (event.isAlarmEnabled || event.isSnoozed) {
                            Brush.verticalGradient(listOf(GradientStart, GradientMid))
                        } else {
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                                )
                            )
                        }
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black.copy(alpha = 0.85f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = dateFormat.format(Date(event.startTime)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (alarmTime != null) {
                        val alarmFormat = SimpleDateFormat("EEE, MMM d · h:mm a", Locale.getDefault())
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = alarmFormat.format(Date(alarmTime)),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    if (!event.location.isNullOrBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.padding(end = 2.dp)
                            )
                            Text(
                                text = event.location,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    if (event.isSnoozed) {
                        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Snooze,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "Snoozed until ${timeFormat.format(Date(event.snoozedUntil!!))}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                Icon(
                    imageVector = when {
                        event.isSnoozed -> Icons.Default.Snooze
                        event.isAlarmEnabled -> Icons.Default.Alarm
                        else -> Icons.Default.AlarmOff
                    },
                    contentDescription = when {
                        event.isSnoozed -> "Snoozed"
                        event.isAlarmEnabled -> "Alarm set"
                        else -> "No alarm"
                    },
                    tint = accentColor
                )
            }
        }
    }
}
