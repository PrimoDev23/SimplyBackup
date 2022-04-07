package com.simplyteam.simplybackup.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Suppress("RemoveRedundantQualifierName")
@Entity(tableName = "Connections")
data class Connection(
    @PrimaryKey(autoGenerate = true)
    var Id: Long = 0,
    val ConnectionType : ConnectionType,
    val Name : String,
    val URL : String,
    val Username : String,
    val Password : String,
    var RemotePath: String = "",
    var WifiOnly: Boolean = false,
    var Paths : List<Path> = listOf(),
    var ScheduleType : ScheduleType = com.simplyteam.simplybackup.data.models.ScheduleType.DAILY
) : Serializable