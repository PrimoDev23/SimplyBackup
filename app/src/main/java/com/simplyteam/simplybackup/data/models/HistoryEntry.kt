package com.simplyteam.simplybackup.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "History")
data class HistoryEntry(
    @PrimaryKey(autoGenerate = true)
    val Id : Long = 0,
    val ConnectionId: Long,
    val Time: String,
    val Size: Long,
    val Succeed: Boolean
)