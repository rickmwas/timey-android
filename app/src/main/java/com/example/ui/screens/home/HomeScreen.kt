package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DaySelector
import com.example.ui.components.HappeningNowHero
import com.example.ui.components.LectureCard
import com.example.ui.components.NotificationBanner
import com.example.ui.components.TimyHeader
import com.example.ui.theme.XBlue
import com.example.ui.theme.XYellow
import com.example.ui.viewmodels.DashboardViewModel

@Composable
fun HomeScreen(
    viewModel: DashboardViewModel,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // X-Style Header with Profile Avatar and Instant Theme Toggle
        TimyHeader(
            cohortName = uiState.cohortName,
            syncState = uiState.syncState,
            unreadNotificationCount = uiState.unreadCount,
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme,
            onNotificationsClick = onNotificationsClick,
            onProfileClick = onProfileClick
        )

        // Foreground In-App Schedule Change Banner
        NotificationBanner(
            notification = uiState.foregroundNotification,
            onDismiss = { viewModel.dismissForegroundNotification() },
            onClick = onNotificationsClick
        )

        // X-Style Weekly Day Selector
        DaySelector(
            selectedDay = uiState.selectedDay,
            today = uiState.today,
            daysWithChanges = uiState.daysWithChanges,
            onSelectDay = { viewModel.selectDay(it) }
        )

        // Main Agenda Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_agenda_list"),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // "Happening Now" or "Next Up" Hero Section
            item(key = "happening_now_hero") {
                HappeningNowHero(
                    activeSession = uiState.activeSession,
                    nextSession = uiState.nextSession,
                    remainingMinutes = uiState.remainingMinutes
                )
            }

            // Day Agenda Header
            item(key = "agenda_section_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${uiState.selectedDay.displayName.uppercase()} TIMELINE",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    IconButton(
                        onClick = { viewModel.refreshSchedule() },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("home_refresh_button")
                    ) {
                        if (uiState.isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(15.dp),
                                color = XBlue,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh schedule",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Lecture Cards List
            if (uiState.sessionsForDay.isEmpty()) {
                item(key = "empty_day_state") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(0.75.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                            .padding(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Coffee,
                                contentDescription = null,
                                tint = XYellow,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Free Academic Period",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "No lectures scheduled for ${uiState.selectedDay.displayName}. Great opportunity for revision and lab coursework.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(
                    items = uiState.sessionsForDay,
                    key = { it.id }
                ) { session ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.5.dp)) {
                        LectureCard(session = session)
                    }
                }
            }
        }
    }
}
