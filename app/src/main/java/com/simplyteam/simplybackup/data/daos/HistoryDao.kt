package com.simplyteam.simplybackup.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.simplyteam.simplybackup.data.models.HistoryEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM History")
    fun GetHistoryFlow() : Flow<List<HistoryEntry>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun InsertHistoryEntry(historyEntry: HistoryEntry) : Long

}