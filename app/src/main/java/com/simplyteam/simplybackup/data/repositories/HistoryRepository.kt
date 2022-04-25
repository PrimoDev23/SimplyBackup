package com.simplyteam.simplybackup.data.repositories

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.simplyteam.simplybackup.common.Constants
import com.simplyteam.simplybackup.data.daos.HistoryDao
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.HistoryData
import com.simplyteam.simplybackup.data.models.HistoryEntry
import com.simplyteam.simplybackup.data.utils.MathUtil
import com.simplyteam.simplybackup.data.utils.SchedulerUtil
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val _historyDao: HistoryDao
) {
    var HistoryEntries = listOf<HistoryEntry>()

    fun GetFlow() = _historyDao.GetHistoryFlow()

    suspend fun InsertHistoryEntry(entry: HistoryEntry): Result<Long> {
        return try {
            Result.success(_historyDao.InsertHistoryEntry(entry))
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

}