package com.simplyteam.simplybackup.data.services.cloudservices

import android.content.Context
import android.net.Uri
import android.os.Handler
import com.owncloud.android.lib.common.OwnCloudClient
import com.owncloud.android.lib.common.OwnCloudClientFactory
import com.owncloud.android.lib.common.OwnCloudCredentialsFactory
import com.owncloud.android.lib.resources.files.*
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.RemoteFile
import com.simplyteam.simplybackup.data.utils.FileUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.DelicateCoroutinesApi
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class NextCloudService @Inject constructor(
    @ApplicationContext private val _context: Context
) : ICloudService {

    private fun CreateClient(
        connection: Connection
    ): OwnCloudClient {
        val serverUri = Uri.parse(connection.Host)

        //Create a client
        val client = OwnCloudClientFactory.createOwnCloudClient(
            serverUri,
            _context,
            true
        )
        client.credentials = OwnCloudCredentialsFactory.newBasicCredentials(
            connection.Username,
            connection.Password
        )

        //This has to be set to compensate an error
        client.userId = connection.Username

        return client
    }

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun UploadFile(
        connection: Connection,
        file: File
    ): Result<Boolean> {
        return suspendCoroutine { continuation ->
            try {
                val client = CreateClient(
                    connection
                )

                val remotePath = connection.RemotePath + FileUtils.PATH_SEPARATOR + file.name

                // Get the last modification date of the file from the file system
                val timeStampLong: Long = file.lastModified() / 1000
                val timeStamp = timeStampLong.toString()

                val operation = UploadFileRemoteOperation(
                    file.absolutePath,
                    remotePath,
                    "application/zip",
                    timeStamp
                )

                val handler = Handler(_context.mainLooper)

                operation.execute(
                    client,
                    { _, p1 ->
                        Timber.d("Upload to ${connection.Host} succeed - ${p1.isSuccess}")

                        if (p1.isSuccess) {
                            continuation.resume(Result.success(true))
                        } else {
                            continuation.resume(
                                Result.failure(
                                    p1.exception ?: Exception(
                                        "${p1.code.name} (${p1.httpPhrase})"
                                    )
                                )
                            )
                        }
                    },
                    handler
                )
            } catch (ex: Exception) {
                continuation.resume(Result.failure(ex))
            }
        }
    }

    override suspend fun GetFilesForConnection(
        connection: Connection
    ): List<RemoteFile> {
        return suspendCoroutine { continuation ->
            val client = CreateClient(
                connection
            )

            val operation = ReadFolderRemoteOperation(connection.RemotePath)

            val handler = Handler(_context.mainLooper)

            operation.execute(
                client,
                { _, p1 ->
                    if (p1.isSuccess) {
                        val files = mutableListOf<RemoteFile>()

                        for (obj in p1.data) {
                            val file =
                                obj as com.owncloud.android.lib.resources.files.model.RemoteFile

                            if (
                                FileUtil.CheckFilenameIsBackup(
                                    file.remotePath,
                                    connection
                                )
                            ) {
                                files.add(
                                    RemoteFile(
                                        file.localId,
                                        file.remotePath,
                                        file.uploadTimestamp,
                                        file.size
                                    )
                                )
                            }
                        }

                        continuation.resume(files)
                    } else {
                        continuation.resumeWithException(
                            p1.exception ?: Exception(
                                "${p1.code.name} (${p1.httpPhrase})"
                            )
                        )
                    }
                },
                handler
            )
        }
    }

    override suspend fun DeleteFile(
        connection: Connection,
        remotePath: String
    ): Boolean {
        return suspendCoroutine { continuation ->
            val client = CreateClient(
                connection
            )

            val operation = RemoveFileRemoteOperation(remotePath)

            val handler = Handler(_context.mainLooper)

            operation.execute(
                client,
                { _, p1 ->
                    if (p1.isSuccess) {
                        continuation.resume(true)
                    } else {
                        continuation.resumeWithException(
                            p1.exception ?: Exception(
                                "${p1.code.name} (${p1.httpPhrase})"
                            )
                        )
                    }
                },
                handler
            )
        }
    }

    override suspend fun DownloadFile(
        connection: Connection,
        remotePath: String
    ): File {
        return suspendCoroutine { continuation ->
            val client = CreateClient(
                connection
            )

            val operation = DownloadFileRemoteOperation(
                remotePath,
                _context.cacheDir.absolutePath
            )

            val handler = Handler(_context.mainLooper)

            operation.execute(
                client,
                { _, p1 ->
                    if (p1.isSuccess) {
                        continuation.resume(
                            File(
                                _context.filesDir.absolutePath,
                                FileUtil.ExtractFileNameFromRemotePath(
                                    connection,
                                    remotePath
                                )
                            )
                        )
                    } else {
                        continuation.resumeWithException(
                            p1.exception ?: Exception(
                                "${p1.code.name} (${p1.httpPhrase})"
                            )
                        )
                    }
                },
                handler
            )
        }
    }
}