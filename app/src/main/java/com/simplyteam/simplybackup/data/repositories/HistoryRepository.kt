package com.simplyteam.simplybackup.data.repositories

import androidx.compose.runtime.mutableStateOf
import com.simplyteam.simplybackup.data.daos.HistoryDao
import com.simplyteam.simplybackup.data.models.HistoryEntry
import kotlinx.coroutines.InternalCoroutinesApi

class HistoryRepository(
    private val _historyDao: HistoryDao
) {
    val History = mutableStateOf<List<HistoryEntry>>(listOf())

    @OptIn(InternalCoroutinesApi::class)
    suspend fun Init(){
        _historyDao.GetHistory().collect {
            History.value = it
        }
    }

    suspend fun InsertHistoryEntry(entry: HistoryEntry) : Result<Long> {
        return try {
            Result.success(_historyDao.InsertHistoryEntry(entry))
        }catch (ex: Exception){
            Result.failure(ex)
        }
    }

}