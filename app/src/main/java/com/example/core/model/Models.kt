package com.example.core.model

enum class SessionStatus {
    NORMAL,
    ACTIVE,
    RESCHEDULED,
    CANCELLED
}

enum class DayOfWeekEnum(val displayName: String, val shortName: String, val dayNumber: Int) {
    MONDAY("Monday", "Mon", 1),
    TUESDAY("Tuesday", "Tue", 2),
    WEDNESDAY("Wednesday", "Wed", 3),
    THURSDAY("Thursday", "Thu", 4),
    FRIDAY("Friday", "Fri", 5),
    SATURDAY("Saturday", "Sat", 6);

    companion object {
        fun fromDayNumber(number: Int): DayOfWeekEnum =
            entries.find { it.dayNumber == number } ?: MONDAY

        fun fromShortName(name: String): DayOfWeekEnum =
            entries.find { it.shortName.equals(name, ignoreCase = true) } ?: MONDAY
    }
}

enum class VenueStatus {
    AVAILABLE,
    OCCUPIED,
    MAINTENANCE
}

enum class NotificationType {
    VENUE_CHANGE,
    CANCELLATION,
    RESCHEDULED,
    GENERAL
}

enum class SyncState {
    LIVE,
    SYNCING,
    OFFLINE,
    SYNC_FAILED
}

data class TimetableSession(
    val id: String,
    val unitCode: String,
    val unitTitle: String,
    val lecturer: String,
    val venue: String,
    val originalVenue: String? = null,
    val startTime: String, // e.g. "08:00"
    val endTime: String,   // e.g. "11:00"
    val originalStartTime: String? = null,
    val originalEndTime: String? = null,
    val dayOfWeek: DayOfWeekEnum,
    val status: SessionStatus = SessionStatus.NORMAL,
    val cancellationReason: String? = null,
    val cohortId: String = "SE-Y3S1",
    val lastUpdated: Long = System.currentTimeMillis()
)

data class Venue(
    val id: String,
    val code: String,
    val name: String,
    val building: String,
    val capacity: Int,
    val status: VenueStatus,
    val currentUnitCode: String? = null,
    val availableUntil: String? = null,
    val nextSessionTime: String? = null,
    val lastVerified: Long = System.currentTimeMillis()
)

data class TimyNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val unitCode: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

data class UserSession(
    val id: String,
    val regNo: String,
    val name: String,
    val email: String,
    val cohortId: String,
    val cohortName: String,
    val role: String,
    val accessToken: String,
    val refreshToken: String
)
