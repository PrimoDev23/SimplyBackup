package com.simplyteam.simplybackup.data.models

import com.owncloud.android.lib.resources.files.model.RemoteFile

data class BackupDetail(
    val Connection: Connection,
    val RemotePath: String,
    val Size: String,
    val Date: String
)
