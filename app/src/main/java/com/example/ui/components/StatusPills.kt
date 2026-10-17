package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.SyncState
import com.example.core.model.VenueStatus
import com.example.ui.theme.XBlue
import com.example.ui.theme.XGreen
import com.example.ui.theme.XRed
import com.example.ui.theme.XYellow

@Composable
fun SyncStatusPill(
    syncState: SyncState,
    modifier: Modifier = Modifier
) {
    val (dotColor, label, bgColor, borderColor) = when (syncState) {
        SyncState.LIVE -> Quad(XGreen, "LIVE", XGreen.copy(alpha = 0.15f), XGreen.copy(alpha = 0.4f))
        SyncState.SYNCING -> Quad(XBlue, "SYNCING", XBlue.copy(alpha = 0.15f), XBlue.copy(alpha = 0.4f))
        SyncState.OFFLINE -> Quad(Color(0xFF71767B), "OFFLINE", Color(0xFF71767B).copy(alpha = 0.12f), Color(0xFF71767B).copy(alpha = 0.3f))
        SyncState.SYNC_FAILED -> Quad(XRed, "SYNC FAILED", XRed.copy(alpha = 0.15f), XRed.copy(alpha = 0.4f))
    }

    Row(
        modifier = modifier
            .testTag("sync_status_pill")
            .clip(CircleShape)
            .background(bgColor)
            .border(0.75.dp, borderColor, CircleShape)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun VenueStatusBadge(
    status: VenueStatus,
    modifier: Modifier = Modifier
) {
    val (color, label, bgColor) = when (status) {
        VenueStatus.AVAILABLE -> Triple(XGreen, "AVAILABLE", XGreen.copy(alpha = 0.12f))
        VenueStatus.OCCUPIED -> Triple(XYellow, "OCCUPIED", XYellow.copy(alpha = 0.12f))
        VenueStatus.MAINTENANCE -> Triple(XRed, "MAINTENANCE", XRed.copy(alpha = 0.12f))
    }

    Row(
        modifier = modifier
            .testTag("venue_status_badge_${status.name.lowercase()}")
            .clip(CircleShape)
            .background(bgColor)
            .border(0.75.dp, color.copy(alpha = 0.5f), CircleShape)
            .padding(horizontal = 8.dp, vertical = 2.5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
