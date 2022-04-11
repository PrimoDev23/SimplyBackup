package com.simplyteam.simplybackup.data.models

data class BackupDetail(
    val Connection: Connection,
    val RemotePath: String,
    val Size: String,
    val Date: String
)
