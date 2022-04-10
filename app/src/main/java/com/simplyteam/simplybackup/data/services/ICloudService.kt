package com.simplyteam.simplybackup.data.services

import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.RemoteFile
import java.io.File

interface ICloudService {

    suspend fun UploadFile(
        connection: Connection,
        file: File
    ): Result<Boolean>

    suspend fun GetFilesForConnection(
        connection: Connection
    ): List<RemoteFile>

    suspend fun DeleteFile(
        connection: Connection,
        remotePath: String
    ): Boolean

    suspend fun DownloadFile(
        connection: Connection,
        remotePath: String
    ): File

}