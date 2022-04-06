package com.simplyteam.simplybackup.data.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.simplyteam.simplybackup.data.converter.Converters
import com.simplyteam.simplybackup.data.daos.ConnectionDao
import com.simplyteam.simplybackup.data.daos.HistoryDao
import com.simplyteam.simplybackup.data.daos.QueueDao
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.HistoryEntry
import com.simplyteam.simplybackup.data.models.QueueItem

@Database(entities = [Connection::class, HistoryEntry::class, QueueItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class SimplyBackupDatabase : RoomDatabase() {

    abstract fun connectionDao() : ConnectionDao
    abstract fun historyDao() : HistoryDao
    abstract fun queueDao() : QueueDao

    companion object {
        @Volatile
        private var INSTANCE: SimplyBackupDatabase? = null

        fun getDatabase(context: Context): SimplyBackupDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SimplyBackupDatabase::class.java,
                    "simplybackup_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}