package com.simplyteam.simplybackup.data.daos

import androidx.room.*
import com.simplyteam.simplybackup.data.models.QueueItem

@Dao
interface QueueDao {

    @Query("SELECT * FROM Queue")
    fun GetQueueItems() : List<QueueItem>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun InsertQueueItem(item: QueueItem)

    @Delete
    suspend fun DeleteQueueItem(item: QueueItem) : Int

}