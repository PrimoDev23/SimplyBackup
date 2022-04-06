package com.simplyteam.simplybackup.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Queue")
data class QueueItem(
    @PrimaryKey
    val connectionId: Long
)
