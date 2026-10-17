package com.example.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequestDto(
    @Json(name = "admissionNo") val admissionNo: String,
    @Json(name = "password") val password: String,
    @Json(name = "deviceToken") val deviceToken: String? = null
)

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id") val id: String,
    @Json(name = "regNo") val regNo: String,
    @Json(name = "name") val name: String,
    @Json(name = "email") val email: String = "",
    @Json(name = "cohortId") val cohortId: String,
    @Json(name = "cohortName") val cohortName: String,
    @Json(name = "role") val role: String = "STUDENT"
)

@JsonClass(generateAdapter = true)
data class LoginResponseDto(
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "refreshToken") val refreshToken: String,
    @Json(name = "user") val user: UserDto
)

@JsonClass(generateAdapter = true)
data class RefreshTokenRequestDto(
    @Json(name = "refreshToken") val refreshToken: String
)

@JsonClass(generateAdapter = true)
data class RefreshTokenResponseDto(
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "refreshToken") val refreshToken: String? = null
)

@JsonClass(generateAdapter = true)
data class SessionDto(
    @Json(name = "id") val id: String,
    @Json(name = "unitCode") val unitCode: String,
    @Json(name = "unitTitle") val unitTitle: String,
    @Json(name = "lecturer") val lecturer: String,
    @Json(name = "venue") val venue: String,
    @Json(name = "originalVenue") val originalVenue: String? = null,
    @Json(name = "startTime") val startTime: String,
    @Json(name = "endTime") val endTime: String,
    @Json(name = "originalStartTime") val originalStartTime: String? = null,
    @Json(name = "originalEndTime") val originalEndTime: String? = null,
    @Json(name = "dayOfWeek") val dayOfWeek: String,
    @Json(name = "status") val status: String = "NORMAL",
    @Json(name = "cancellationReason") val cancellationReason: String? = null,
    @Json(name = "cohortId") val cohortId: String = "SE-Y3S1",
    @Json(name = "lastUpdated") val lastUpdated: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class TimetableResponseDto(
    @Json(name = "cohortId") val cohortId: String,
    @Json(name = "cohortName") val cohortName: String,
    @Json(name = "semester") val semester: String? = null,
    @Json(name = "sessions") val sessions: List<SessionDto>,
    @Json(name = "serverTimestamp") val serverTimestamp: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class NotificationDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "message") val message: String,
    @Json(name = "type") val type: String = "GENERAL",
    @Json(name = "unitCode") val unitCode: String? = null,
    @Json(name = "timestamp") val timestamp: Long = System.currentTimeMillis(),
    @Json(name = "isRead") val isRead: Boolean = false
)

@JsonClass(generateAdapter = true)
data class NotificationsResponseDto(
    @Json(name = "notifications") val notifications: List<NotificationDto>,
    @Json(name = "unreadCount") val unreadCount: Int = 0
)

@JsonClass(generateAdapter = true)
data class VenueDto(
    @Json(name = "id") val id: String,
    @Json(name = "code") val code: String,
    @Json(name = "name") val name: String,
    @Json(name = "building") val building: String,
    @Json(name = "capacity") val capacity: Int,
    @Json(name = "status") val status: String = "AVAILABLE",
    @Json(name = "currentUnitCode") val currentUnitCode: String? = null,
    @Json(name = "availableUntil") val availableUntil: String? = null,
    @Json(name = "nextSessionTime") val nextSessionTime: String? = null,
    @Json(name = "lastVerified") val lastVerified: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class VenuesResponseDto(
    @Json(name = "venues") val venues: List<VenueDto>
)

@JsonClass(generateAdapter = true)
data class VenueAvailabilityDto(
    @Json(name = "venueId") val venueId: String,
    @Json(name = "code") val code: String,
    @Json(name = "status") val status: String,
    @Json(name = "currentUnitCode") val currentUnitCode: String? = null,
    @Json(name = "availableUntil") val availableUntil: String? = null,
    @Json(name = "nextSessionTime") val nextSessionTime: String? = null,
    @Json(name = "lastVerified") val lastVerified: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class MarkReadResponseDto(
    @Json(name = "success") val success: Boolean,
    @Json(name = "id") val id: String
)
