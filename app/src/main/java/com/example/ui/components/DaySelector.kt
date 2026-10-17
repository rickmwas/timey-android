package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.DayOfWeekEnum
import com.example.ui.theme.XBlue
import com.example.ui.theme.XYellow

@Composable
fun DaySelector(
    selectedDay: DayOfWeekEnum,
    today: DayOfWeekEnum,
    daysWithChanges: Set<DayOfWeekEnum> = emptySet(),
    onSelectDay: (DayOfWeekEnum) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = listOf(
        DayOfWeekEnum.MONDAY,
        DayOfWeekEnum.TUESDAY,
        DayOfWeekEnum.WEDNESDAY,
        DayOfWeekEnum.THURSDAY,
        DayOfWeekEnum.FRIDAY,
        DayOfWeekEnum.SATURDAY
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            days.forEach { day ->
                val isSelected = day == selectedDay
                val isToday = day == today
                val hasChange = daysWithChanges.contains(day)

                val textColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "text_color"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectDay(day) }
                        .padding(top = 10.dp, bottom = 4.dp)
                        .testTag("day_selector_${day.shortName.lowercase()}"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = day.shortName.uppercase(),
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                            fontFamily = FontFamily.SansSerif,
                            color = textColor
                        )
                        if (hasChange) {
                            Spacer(modifier = Modifier.size(3.dp))
                            Box(
                                modifier = Modifier
                                    .size(4.5.dp)
                                    .clip(CircleShape)
                                    .background(XYellow)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Active Tab Indicator line (X-style active tab underline)
                    Box(
                        modifier = Modifier
                            .height(3.dp)
                            .fillMaxWidth(if (isSelected) 0.65f else 0f)
                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                            .background(if (isSelected) XBlue else androidx.compose.ui.graphics.Color.Transparent)
                    )
                }
            }
        }

        // Hairline Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.75.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
    }
}
