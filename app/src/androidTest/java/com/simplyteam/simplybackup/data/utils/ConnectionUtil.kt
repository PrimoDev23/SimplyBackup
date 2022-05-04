package com.simplyteam.simplybackup.data.utils

import com.simplyteam.simplybackup.BuildConfig
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.models.Path
import com.simplyteam.simplybackup.data.models.PathType
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import kotlinx.coroutines.runBlocking

object ConnectionUtil {

    fun InsertConnection(
        connectionType: ConnectionType,
        connectionRepository: ConnectionRepository
    ): Connection {
        val connection = when (connectionType) {
            ConnectionType.NextCloud -> {
                Connection(
                    ConnectionType = ConnectionType.NextCloud,
                    Name = "AndroidInstrumentationTest",
                    Host = BuildConfig.NEXTCLOUD_HOST,
                    Username = BuildConfig.NEXTCLOUD_USERNAME,
                    Password = BuildConfig.NEXTCLOUD_PASSWORD,
                    Paths = listOf(
                        Path(
                            "/sdcard/TestFolder",
                            PathType.DIRECTORY
                        )
                    )
                )
            }
            ConnectionType.SFTP -> {
                Connection(
                    ConnectionType = ConnectionType.SFTP,
                    Name = "AndroidInstrumentationTest",
                    Host = BuildConfig.SFTP_HOST,
                    Username = BuildConfig.SFTP_USERNAME,
                    Password = BuildConfig.SFTP_PASSWORD,
                    RemotePath = BuildConfig.SFTP_REMOTEPATH,
                    Paths = listOf(
                        Path(
                            "/sdcard/TestFolder",
                            PathType.DIRECTORY
                        )
                    )
                )
            }
            ConnectionType.GoogleDrive -> {
                Connection(
                    ConnectionType = ConnectionType.GoogleDrive,
                    Name = "AndroidInstrumentationTest",
                    Username = BuildConfig.GOOGLE_DRIVE_USER,
                    Paths = listOf(
                        Path(
                            "/sdcard/TestFolder",
                            PathType.DIRECTORY
                        )
                    )
                )
            }
            ConnectionType.SeaFile -> {
                Connection(
                    ConnectionType = ConnectionType.SeaFile,
                    Name = "AndroidInstrumentationTest",
                    Host = BuildConfig.SEAFILE_HOST,
                    Username = BuildConfig.SEAFILE_USER,
                    Password = BuildConfig.SEAFILE_PASSWORD,
                    RepoId = BuildConfig.SEAFILE_REPOID,
                    RemotePath = "/"
                )
            }
        }

        val id = runBlocking {
            connectionRepository.InsertConnection(connection)
        }

        Thread.sleep(1000)

        return connection.copy(
            Id = id
        )
    }

}