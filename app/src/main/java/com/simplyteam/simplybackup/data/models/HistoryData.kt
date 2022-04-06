package com.simplyteam.simplybackup.data.models

data class HistoryData(
    val Name: String,
    val Type: ConnectionType,
    val LastBackup: String,
    val NextBackup: String,
    val LastBackupSize: String,
    val TotalBackedUpSize: String
)