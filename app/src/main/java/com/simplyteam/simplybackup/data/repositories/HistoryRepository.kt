package com.simplyteam.simplybackup.data.repositories

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.simplyteam.simplybackup.data.daos.HistoryDao
import com.simplyteam.simplybackup.data.databases.SimplyBackupDatabase
import com.simplyteam.simplybackup.data.models.HistoryEntry
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect

class HistoryRepository {
    private lateinit var _historyDao : HistoryDao

    val History = mutableStateOf<List<HistoryEntry>>(listOf())

    @OptIn(InternalCoroutinesApi::class)
    suspend fun Init(context: Context){
        _historyDao = SimplyBackupDatabase.getDatabase(context).historyDao()

        _historyDao.GetHistory().collect {
            History.value = it
        }
    }

    fun ContextOnlyInit(context: Context){
        _historyDao = SimplyBackupDatabase.getDatabase(context).historyDao()
    }

    suspend fun InsertHistoryEntry(entry: HistoryEntry) : Result<Long> {
        try {
            return Result.success(_historyDao.InsertHistoryEntry(entry))
        }catch (ex: Exception){
            return Result.failure(ex)
        }
    }

}