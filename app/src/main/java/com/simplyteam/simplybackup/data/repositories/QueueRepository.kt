package com.simplyteam.simplybackup.data.repositories

import android.content.Context
import com.simplyteam.simplybackup.data.daos.QueueDao
import com.simplyteam.simplybackup.data.databases.SimplyBackupDatabase
import com.simplyteam.simplybackup.data.models.QueueItem

class QueueRepository() {

    private lateinit var QueueDao: QueueDao

    fun Init(context: Context){
        QueueDao = SimplyBackupDatabase.getDatabase(context).queueDao()
    }

    fun GetQueueItems(queueItem: QueueItem) : Result<List<QueueItem>> {
        try {
            return Result.success(QueueDao.GetQueueItems())
        }catch (ex: Exception){
            return Result.failure(ex)
        }
    }

    suspend fun InsertQueueItem(queueItem: QueueItem) : Result<Boolean> {
        try {
            QueueDao.InsertQueueItem(queueItem)

            return Result.success(true)
        }catch (ex: Exception){
            return Result.failure(ex)
        }
    }

    suspend fun DeleteQueueItem(queueItem: QueueItem) : Result<Boolean> {
        try {
            return Result.success(QueueDao.DeleteQueueItem(queueItem) > 0)
        }catch (ex: Exception){
            return Result.failure(ex)
        }
    }

}