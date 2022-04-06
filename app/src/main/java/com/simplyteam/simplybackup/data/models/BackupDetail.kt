package com.simplyteam.simplybackup.data.models

import com.owncloud.android.lib.resources.files.model.RemoteFile

data class BackupDetail(
    val Connection: Connection,
    val RemoteFile: RemoteFile,
    val Size: String,
    val Date: String
)
