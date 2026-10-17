package com.example.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.core.model.DayOfWeekEnum
import com.example.core.model.NotificationType
import com.example.core.model.SessionStatus
import com.example.core.model.VenueStatus

@Entity(
    tableName = "timetable_sessions",
    indices = [
        Index(value = ["cohortId", "dayOfWeek"]),
        Index(value = ["unitCode"]),
        Index(value = ["venue"])
    ]
)
data class SessionEntity(
    @PrimaryKey val id: String,
    val unitCode: String,
    val unitTitle: String,
    val lecturer: String,
    val venue: String,
    val originalVenue: String?,
    val startTime: String,
    val endTime: String,
    val originalStartTime: String?,
    val originalEndTime: String?,
    val dayOfWeek: DayOfWeekEnum,
    val status: SessionStatus,
    val cancellationReason: String?,
    val cohortId: String,
    val lastUpdated: Long
)

@Entity(
    tableName = "venues",
    indices = [
        Index(value = ["code"], unique = true),
        Index(value = ["building"])
    ]
)
data class VenueEntity(
    @PrimaryKey val id: String,
    val code: String,
    val name: String,
    val building: String,
    val capacity: Int,
    val status: VenueStatus,
    val currentUnitCode: String?,
    val availableUntil: String?,
    val nextSessionTime: String?,
    val lastVerified: Long
)

@Entity(
    tableName = "notifications",
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["isRead"])
    ]
)
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val unitCode: String?,
    val timestamp: Long,
    val isRead: Boolean
)

@Entity(tableName = "sync_metadata")
data class SyncMetadataEntity(
    @PrimaryKey val key: String,
    val lastSyncTimestamp: Long,
    val status: String,
    val errorMessage: String? = null
)
