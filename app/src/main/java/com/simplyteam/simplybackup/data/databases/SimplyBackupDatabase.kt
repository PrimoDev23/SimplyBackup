package com.simplyteam.simplybackup.data.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.simplyteam.simplybackup.data.converter.Converters
import com.simplyteam.simplybackup.data.daos.ConnectionDao
import com.simplyteam.simplybackup.data.daos.HistoryDao
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.HistoryEntry

@Database(entities = [Connection::class, HistoryEntry::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class SimplyBackupDatabase : RoomDatabase() {

    abstract val connectionDao : ConnectionDao
    abstract val historyDao : HistoryDao

    companion object {
        const val DATABASE_NAME = "simplybackup_database"
    }

}