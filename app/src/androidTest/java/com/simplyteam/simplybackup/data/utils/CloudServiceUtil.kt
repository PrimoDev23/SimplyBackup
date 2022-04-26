package com.simplyteam.simplybackup.data.utils

import com.simplyteam.simplybackup.common.TestConstants
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.models.RemoteFile
import com.simplyteam.simplybackup.data.services.PackagingService
import com.simplyteam.simplybackup.data.services.cloudservices.GoogleDriveService
import com.simplyteam.simplybackup.data.services.cloudservices.NextCloudService
import com.simplyteam.simplybackup.data.services.cloudservices.SFTPService
import kotlinx.coroutines.runBlocking

object CloudServiceUtil {
    suspend fun UploadTestPackage(
        connection: Connection,
        packagingService: PackagingService,
        basePath: String,
        nextCloudService: NextCloudService,
        sftpService: SFTPService,
        googleDriveService: GoogleDriveService
    ): Result<Boolean> {
        packagingService.CreatePackage(
            basePath,
            connection
        )
            .onSuccess {
                val result = when (connection.ConnectionType) {
                    ConnectionType.NextCloud -> {
                        nextCloudService.UploadFile(
                            connection,
                            it
                        )
                    }
                    ConnectionType.SFTP -> {
                        sftpService.UploadFile(
                            connection,
                            it
                        )
                    }
                    ConnectionType.GoogleDrive -> {
                        googleDriveService.UploadFile(
                            connection,
                            it
                        )
                    }
                }
                return result
            }
            .onFailure {
                throw it
            }
        return Result.failure(Exception())
    }

    suspend fun GetFilesForConnection(
        connection: Connection,
        nextCloudService: NextCloudService,
        sftpService: SFTPService,
        googleDriveService: GoogleDriveService
    ): List<RemoteFile> {
        return when (TestConstants.TestConnectionType) {
            ConnectionType.NextCloud -> {
                nextCloudService.GetFilesForConnection(
                    connection
                )
            }
            ConnectionType.SFTP -> {
                sftpService.GetFilesForConnection(
                    connection
                )
            }
            ConnectionType.GoogleDrive -> {
                googleDriveService.GetFilesForConnection(
                    connection
                )
            }
        }
    }

    suspend fun CleanupServer(
        connection: Connection,
        nextCloudService: NextCloudService,
        sftpService: SFTPService,
        googleDriveService: GoogleDriveService
    ) {
        val files = when (connection.ConnectionType) {
            ConnectionType.NextCloud -> {
                nextCloudService.GetFilesForConnection(
                    connection
                )
            }
            ConnectionType.SFTP -> {
                sftpService.GetFilesForConnection(
                    connection
                )
            }
            ConnectionType.GoogleDrive -> {
                googleDriveService.GetFilesForConnection(
                    connection
                )
            }
        }

        for (file in files) {
            when (connection.ConnectionType) {
                ConnectionType.NextCloud -> {
                    nextCloudService.DeleteFile(
                        connection,
                        file.RemotePath
                    )
                }
                ConnectionType.SFTP -> {
                    sftpService.DeleteFile(
                        connection,
                        file.RemotePath
                    )
                }
                ConnectionType.GoogleDrive -> {
                    googleDriveService.DeleteFile(
                        connection,
                        file.RemoteId
                    )
                }
            }
        }
    }
}