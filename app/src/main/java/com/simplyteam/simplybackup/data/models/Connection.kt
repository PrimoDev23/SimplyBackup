package com.simplyteam.simplybackup.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Connections")
data class Connection(
    @PrimaryKey(autoGenerate = true)
    var Id: Long = 0,
    val Name : String,
    val ConnectionType : ConnectionType,
    val URL : String,
    val Username : String,
    val Password : String,
    val RemotePath: String,
    val WifiOnly: Boolean,
    val Paths : List<Path>,
    val ScheduleType : ScheduleType
) : Serializable