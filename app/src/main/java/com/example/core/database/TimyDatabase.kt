package com.example.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.core.database.dao.NotificationDao
import com.example.core.database.dao.SessionDao
import com.example.core.database.dao.SyncMetadataDao
import com.example.core.database.dao.VenueDao
import com.example.core.database.entity.NotificationEntity
import com.example.core.database.entity.SessionEntity
import com.example.core.database.entity.SyncMetadataEntity
import com.example.core.database.entity.VenueEntity

@Database(
    entities = [
        SessionEntity::class,
        VenueEntity::class,
        NotificationEntity::class,
        SyncMetadataEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TimyDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun venueDao(): VenueDao
    abstract fun notificationDao(): NotificationDao
    abstract fun syncMetadataDao(): SyncMetadataDao

    companion object {
        @Volatile
        private var INSTANCE: TimyDatabase? = null

        fun getInstance(context: Context): TimyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TimyDatabase::class.java,
                    "timy_academic.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
