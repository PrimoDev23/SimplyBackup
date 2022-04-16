package com.simplyteam.simplybackup.data.models

data class HistoryData(
    val Connection: Connection,
    val LastBackup: String,
    val NextBackup: String,
    val LastBackupSize: String,
    val TotalBackedUpSize: String
)