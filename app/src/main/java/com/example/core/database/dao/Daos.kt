package com.example.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.core.database.entity.NotificationEntity
import com.example.core.database.entity.SessionEntity
import com.example.core.database.entity.SyncMetadataEntity
import com.example.core.database.entity.VenueEntity
import com.example.core.model.DayOfWeekEnum
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM timetable_sessions WHERE cohortId = :cohortId AND dayOfWeek = :day ORDER BY startTime ASC")
    fun getSessionsForDay(cohortId: String, day: DayOfWeekEnum): Flow<List<SessionEntity>>

    @Query("SELECT * FROM timetable_sessions WHERE cohortId = :cohortId ORDER BY dayOfWeek ASC, startTime ASC")
    fun getAllSessionsForCohort(cohortId: String): Flow<List<SessionEntity>>

    @Query("SELECT * FROM timetable_sessions WHERE id = :id")
    suspend fun getSessionById(id: String): SessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSessions(sessions: List<SessionEntity>)

    @Query("DELETE FROM timetable_sessions WHERE cohortId = :cohortId")
    suspend fun clearSessionsForCohort(cohortId: String)

    @Transaction
    suspend fun replaceCohortSessions(cohortId: String, sessions: List<SessionEntity>) {
        clearSessionsForCohort(cohortId)
        upsertSessions(sessions)
    }

    @Query("SELECT COUNT(*) FROM timetable_sessions WHERE cohortId = :cohortId")
    suspend fun getSessionCount(cohortId: String): Int
}

@Dao
interface VenueDao {
    @Query("SELECT * FROM venues ORDER BY code ASC")
    fun getAllVenues(): Flow<List<VenueEntity>>

    @Query("SELECT * FROM venues WHERE code LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%' OR building LIKE '%' || :query || '%' ORDER BY code ASC")
    fun searchVenues(query: String): Flow<List<VenueEntity>>

    @Query("SELECT * FROM venues WHERE id = :id")
    suspend fun getVenueById(id: String): VenueEntity?

    @Query("SELECT * FROM venues WHERE code = :code LIMIT 1")
    suspend fun getVenueByCode(code: String): VenueEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertVenues(venues: List<VenueEntity>)

    @Query("DELETE FROM venues")
    suspend fun clearAllVenues()
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE isRead = 0 ORDER BY timestamp DESC")
    fun getUnreadNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertNotifications(notifications: List<NotificationEntity>)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: String)
}

@Dao
interface SyncMetadataDao {
    @Query("SELECT * FROM sync_metadata WHERE key = :key LIMIT 1")
    suspend fun getMetadata(key: String): SyncMetadataEntity?

    @Query("SELECT * FROM sync_metadata WHERE key = :key LIMIT 1")
    fun observeMetadata(key: String): Flow<SyncMetadataEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setMetadata(metadata: SyncMetadataEntity)
}
