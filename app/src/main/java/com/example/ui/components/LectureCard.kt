package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.SessionStatus
import com.example.core.model.TimetableSession
import com.example.ui.theme.XBlue
import com.example.ui.theme.XGreen
import com.example.ui.theme.XRed
import com.example.ui.theme.XYellow

@Composable
fun LectureCard(
    session: TimetableSession,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val statusBorderColor = when (session.status) {
        SessionStatus.ACTIVE -> XGreen.copy(alpha = 0.7f)
        SessionStatus.RESCHEDULED -> XYellow.copy(alpha = 0.7f)
        SessionStatus.CANCELLED -> XRed.copy(alpha = 0.7f)
        SessionStatus.NORMAL -> MaterialTheme.colorScheme.outlineVariant
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = if (session.status == SessionStatus.NORMAL) 0.75.dp else 1.25.dp,
                color = statusBorderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag("lecture_card_${session.unitCode.replace(" ", "_")}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Left: Course Avatar / Badge (X style author avatar)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when (session.status) {
                            SessionStatus.ACTIVE -> XGreen.copy(alpha = 0.15f)
                            SessionStatus.RESCHEDULED -> XYellow.copy(alpha = 0.15f)
                            SessionStatus.CANCELLED -> XRed.copy(alpha = 0.15f)
                            SessionStatus.NORMAL -> XBlue.copy(alpha = 0.12f)
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = when (session.status) {
                            SessionStatus.ACTIVE -> XGreen.copy(alpha = 0.4f)
                            SessionStatus.RESCHEDULED -> XYellow.copy(alpha = 0.4f)
                            SessionStatus.CANCELLED -> XRed.copy(alpha = 0.4f)
                            SessionStatus.NORMAL -> XBlue.copy(alpha = 0.3f)
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (session.status) {
                        SessionStatus.ACTIVE -> Icons.Default.CheckCircle
                        SessionStatus.RESCHEDULED -> Icons.Default.ChangeCircle
                        SessionStatus.CANCELLED -> Icons.Default.Cancel
                        SessionStatus.NORMAL -> Icons.Default.School
                    },
                    contentDescription = null,
                    tint = when (session.status) {
                        SessionStatus.ACTIVE -> XGreen
                        SessionStatus.RESCHEDULED -> XYellow
                        SessionStatus.CANCELLED -> XRed
                        SessionStatus.NORMAL -> XBlue
                    },
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right: Content body (like an X tweet layout)
            Column(modifier = Modifier.weight(1f)) {
                // Header row: Unit Code, verified badge, time, status pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = session.unitCode,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified session",
                            tint = XBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "· ${session.startTime}",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Status Pill
                    when (session.status) {
                        SessionStatus.ACTIVE -> {
                            Row(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(XGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 7.dp, vertical = 2.5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(XGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "ACTIVE",
                                    color = XGreen,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        SessionStatus.RESCHEDULED -> {
                            Row(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(XYellow.copy(alpha = 0.15f))
                                    .padding(horizontal = 7.dp, vertical = 2.5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Repeat,
                                    contentDescription = null,
                                    tint = XYellow,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "MOVED",
                                    color = XYellow,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        SessionStatus.CANCELLED -> {
                            Row(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(XRed.copy(alpha = 0.15f))
                                    .padding(horizontal = 7.dp, vertical = 2.5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = XRed,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "CANCELLED",
                                    color = XRed,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        SessionStatus.NORMAL -> {
                            Text(
                                text = "until ${session.endTime}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                // Lecturer handle style
                Text(
                    text = "@${session.lecturer.replace(" ", "")}",
                    fontSize = 12.sp,
                    color = XBlue,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Course title
                Text(
                    text = session.unitTitle,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = if (session.status == SessionStatus.CANCELLED) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (session.status == SessionStatus.CANCELLED) TextDecoration.LineThrough else null,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Venue & Location Row (like tweet action footer)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Venue",
                            tint = when (session.status) {
                                SessionStatus.RESCHEDULED -> XYellow
                                SessionStatus.CANCELLED -> XRed
                                SessionStatus.ACTIVE -> XGreen
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))

                        if (session.status == SessionStatus.RESCHEDULED && session.originalVenue != null) {
                            Text(
                                text = session.originalVenue,
                                textDecoration = TextDecoration.LineThrough,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.5.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "→",
                                color = XYellow,
                                fontSize = 12.5.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        Text(
                            text = session.venue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = when (session.status) {
                                SessionStatus.RESCHEDULED -> XYellow
                                SessionStatus.CANCELLED -> XRed
                                SessionStatus.ACTIVE -> XGreen
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }

                    Text(
                        text = "${session.startTime} – ${session.endTime}",
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Official Notice box (like an X quoted post)
                if (session.status == SessionStatus.CANCELLED && session.cancellationReason != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(XRed.copy(alpha = 0.1f))
                            .border(0.75.dp, XRed.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Official Reason: ${session.cancellationReason}",
                            color = XRed,
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp
                        )
                    }
                } else if (session.status == SessionStatus.RESCHEDULED && session.cancellationReason != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(XYellow.copy(alpha = 0.1f))
                            .border(0.75.dp, XYellow.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Update: ${session.cancellationReason}",
                            color = XYellow,
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}
