package com.simplyteam.simplybackup.data.models

import androidx.room.Entity

@Entity(
    tableName = "Accounts",
    primaryKeys = ["Type", "Username"]
)
data class Account(
    val Type: ConnectionType,
    val Username: String
)
