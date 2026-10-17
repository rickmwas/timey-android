package com.example.data.repository

import com.example.core.model.DayOfWeekEnum
import com.example.core.model.NotificationType
import com.example.core.model.SessionStatus
import com.example.core.model.SyncState
import com.example.core.model.TimetableSession
import com.example.core.model.TimyNotification
import com.example.core.model.UserSession
import com.example.core.model.Venue
import kotlinx.coroutines.flow.Flow

interface TimyRepository {
    val syncState: Flow<SyncState>
    val lastSyncTimestamp: Flow<Long>

    fun getSessionsForDay(cohortId: String, day: DayOfWeekEnum): Flow<List<TimetableSession>>
    fun getAllSessionsForCohort(cohortId: String): Flow<List<TimetableSession>>
    fun getAllVenues(): Flow<List<Venue>>
    fun searchVenues(query: String): Flow<List<Venue>>
    fun getAllNotifications(): Flow<List<TimyNotification>>
    fun getUnreadNotificationsCount(): Flow<Int>

    suspend fun login(admissionNo: String, password: String): Result<UserSession>
    suspend fun syncTimetable(cohortId: String): Result<Unit>
    suspend fun refreshVenues(): Result<Unit>
    suspend fun fetchNotifications(): Result<Unit>
    suspend fun markNotificationAsRead(id: String): Result<Unit>
    suspend fun markAllNotificationsAsRead(): Result<Unit>
    suspend fun initializeSeedDataIfEmpty(cohortId: String)
}
