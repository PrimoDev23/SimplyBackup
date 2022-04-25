package com.simplyteam.simplybackup.data.models

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Immutable
@Suppress("RemoveRedundantQualifierName")
@Entity(tableName = "Connections")
data class Connection(
    @PrimaryKey(autoGenerate = true)
    val Id: Long = 0,
    val ConnectionType : ConnectionType,
    val Name : String,
    val Host : String = "",
    val Username : String,
    val Password : String = "",
    val RemotePath: String = "",
    val BackupPassword: String = "",
    val WifiOnly: Boolean = false,
    val Paths : List<Path> = emptyList(),
    val ScheduleType : ScheduleType = com.simplyteam.simplybackup.data.models.ScheduleType.DAILY,
    val TemporarilyDeleted: Boolean = false
) : Serializable