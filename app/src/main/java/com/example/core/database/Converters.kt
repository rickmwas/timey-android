package com.example.core.database

import androidx.room.TypeConverter
import com.example.core.model.DayOfWeekEnum
import com.example.core.model.NotificationType
import com.example.core.model.SessionStatus
import com.example.core.model.VenueStatus

class Converters {
    @TypeConverter
    fun fromDayOfWeek(value: DayOfWeekEnum): String = value.name

    @TypeConverter
    fun toDayOfWeek(value: String): DayOfWeekEnum = try {
        DayOfWeekEnum.valueOf(value)
    } catch (e: Exception) {
        DayOfWeekEnum.MONDAY
    }

    @TypeConverter
    fun fromSessionStatus(value: SessionStatus): String = value.name

    @TypeConverter
    fun toSessionStatus(value: String): SessionStatus = try {
        SessionStatus.valueOf(value)
    } catch (e: Exception) {
        SessionStatus.NORMAL
    }

    @TypeConverter
    fun fromVenueStatus(value: VenueStatus): String = value.name

    @TypeConverter
    fun toVenueStatus(value: String): VenueStatus = try {
        VenueStatus.valueOf(value)
    } catch (e: Exception) {
        VenueStatus.AVAILABLE
    }

    @TypeConverter
    fun fromNotificationType(value: NotificationType): String = value.name

    @TypeConverter
    fun toNotificationType(value: String): NotificationType = try {
        NotificationType.valueOf(value)
    } catch (e: Exception) {
        NotificationType.GENERAL
    }
}
