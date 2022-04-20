package com.simplyteam.simplybackup.data.models

data class RemoteFile(
    val RemoteId: String,
    val RemotePath: String,
    val TimeStamp: Long,
    val Size: Long
)
