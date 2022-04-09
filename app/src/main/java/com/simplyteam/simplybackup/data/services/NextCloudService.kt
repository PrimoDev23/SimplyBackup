package com.simplyteam.simplybackup.data.services

import android.content.Context
import android.net.Uri
import android.os.Handler
import com.owncloud.android.lib.common.OwnCloudClient
import com.owncloud.android.lib.common.OwnCloudClientFactory
import com.owncloud.android.lib.common.OwnCloudCredentialsFactory
import com.owncloud.android.lib.common.operations.RemoteOperationResult
import com.owncloud.android.lib.resources.files.*
import com.owncloud.android.lib.resources.files.model.RemoteFile
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.exceptions.FolderOperationException
import kotlinx.coroutines.DelicateCoroutinesApi
import timber.log.Timber
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NextCloudService {

    private fun CreateClient(
        context: Context,
        connection: Connection
    ): OwnCloudClient {
        val serverUri = Uri.parse(connection.URL)

        //Create a client
        val client = OwnCloudClientFactory.createOwnCloudClient(
            serverUri,
            context,
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
    suspend fun UploadFile(
        context: Context,
        connection: Connection,
        file: File
    ): Result<RemoteOperationResult<*>> {
        return suspendCoroutine { continuation ->
            try {
                val client = CreateClient(
                    context,
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

                val handler = Handler(context.mainLooper)

                operation.execute(
                    client,
                    { _, p1 ->
                        Timber.d("Upload to ${connection.URL} succeed - ${p1.isSuccess}")

                        continuation.resume(Result.success(p1))
                    },
                    handler
                )
            } catch (ex: Exception) {
                continuation.resume(Result.failure(ex))
            }
        }
    }

    suspend fun GetFilesForConnection(
        context: Context,
        connection: Connection
    ): List<RemoteFile> {
        return suspendCoroutine { continuation ->
            val client = CreateClient(
                context,
                connection
            )

            val operation = ReadFolderRemoteOperation(connection.RemotePath)

            val handler = Handler(context.mainLooper)

            operation.execute(
                client,
                { _, p1 ->
                    if (p1.isSuccess) {
                        val files = mutableListOf<RemoteFile>()

                        for (obj in p1.data) {
                            val file = obj as RemoteFile

                            if (file.mimeType == "application/zip" && file.remotePath.contains("-${connection.Name}-")) {
                                files.add(file)
                            }
                        }

                        continuation.resume(files)
                    } else {
                        throw p1.exception ?: FolderOperationException(
                            "${p1.code.name} (${p1.httpPhrase})"
                        )
                    }
                },
                handler
            )
        }
    }

    suspend fun DeleteFile(
        context: Context,
        connection: Connection,
        file: RemoteFile
    ): Boolean {
        return suspendCoroutine { continuation ->
            val client = CreateClient(
                context,
                connection
            )

            val operation = RemoveFileRemoteOperation(file.remotePath)

            val handler = Handler(context.mainLooper)

            operation.execute(
                client,
                { _, p1 ->
                    if (p1.isSuccess) {
                        continuation.resume(true)
                    } else {
                        throw
                        p1.exception ?: FolderOperationException(
                            "${p1.code.name} (${p1.httpPhrase})"
                        )
                    }
                },
                handler
            )
        }
    }

    suspend fun DownloadFile(
        context: Context,
        connection: Connection,
        file: RemoteFile
    ): File {
        return suspendCoroutine { continuation ->
            val client = CreateClient(
                context,
                connection
            )

            val operation = DownloadFileRemoteOperation(
                file.remotePath,
                context.filesDir.absolutePath
            )

            val handler = Handler(context.mainLooper)

            operation.execute(
                client,
                { _, p1 ->
                    if (p1.isSuccess) {
                        continuation.resume(
                            File(
                                context.filesDir.absolutePath,
                                ExtractFileNameFromRemotePath(
                                    connection,
                                    file
                                )
                            )
                        )
                    } else {
                        throw
                        p1.exception ?: FolderOperationException(
                            "${p1.code.name} (${p1.httpPhrase})"
                        )
                    }
                },
                handler
            )
        }
    }

    private fun ExtractFileNameFromRemotePath(
        connection: Connection,
        file: RemoteFile
    ): String {
        return file.remotePath.removeRange(
            0,
            connection.RemotePath.length + 1
        )
    }
}