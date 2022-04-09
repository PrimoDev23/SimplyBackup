package com.simplyteam.simplybackup.data.services

import android.content.Context
import androidx.annotation.WorkerThread
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.RemoteFile
import com.simplyteam.simplybackup.data.utils.FileUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SFTPService {

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun UploadFile(
        connection: Connection,
        file: File
    ): Result<Boolean> {
        return suspendCoroutine { continuation ->
            val client = JSch()
            val session = client.getSession(
                connection.Username,
                connection.URL,
                22
            )
            var channel: ChannelSftp? = null

            try {
                session.setConfig(
                    "StrictHostKeyChecking",
                    "no"
                )
                session.setPassword(connection.Password)
                session.connect()

                channel = session.openChannel("sftp") as ChannelSftp
                channel.connect()

                FileInputStream(file).use { stream ->
                    channel.put(
                        stream,
                        "${connection.RemotePath}/${file.name}"
                    )
                }

                channel.exit()
                session.disconnect()

                continuation.resume(Result.success(true))
            } catch (ex: Exception) {
                channel?.exit()
                session.disconnect()

                continuation.resume(Result.failure(ex))
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun GetFilesForConnection(
        connection: Connection
    ): List<RemoteFile> {
        return suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                val client = JSch()
                val session = client.getSession(
                    connection.Username,
                    connection.URL,
                    22
                )
                var channel: ChannelSftp? = null

                try {
                    session.setConfig(
                        "StrictHostKeyChecking",
                        "no"
                    )
                    session.setPassword(connection.Password)
                    session.connect()

                    channel = session.openChannel("sftp") as ChannelSftp
                    channel.connect()

                    val files = mutableListOf<RemoteFile>()

                    for (obj in channel.ls(
                        connection.RemotePath
                    )) {
                        val entry = obj as ChannelSftp.LsEntry

                        if (entry.filename.contains("-${connection.Name}-") && entry.filename.endsWith(".zip")) {
                            files.add(
                                RemoteFile(
                                    "${connection.RemotePath}/${entry.filename}",
                                    entry.attrs.mTime.toLong(),
                                    entry.attrs.size
                                )
                            )
                        }
                    }

                    channel.exit()
                    session.disconnect()

                    continuation.resume(files)
                } catch (ex: Exception) {
                    channel?.exit()
                    session.disconnect()

                    throw ex
                }
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun DeleteFile(
        connection: Connection,
        remotePath: String
    ): Boolean {
        return suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                val client = JSch()
                val session = client.getSession(
                    connection.Username,
                    connection.URL,
                    22
                )
                var channel: ChannelSftp? = null

                try {
                    session.setConfig(
                        "StrictHostKeyChecking",
                        "no"
                    )
                    session.setPassword(connection.Password)
                    session.connect()

                    channel = session.openChannel("sftp") as ChannelSftp
                    channel.connect()

                    channel.rm(
                        remotePath
                    )

                    channel.exit()
                    session.disconnect()

                    continuation.resume(true)
                } catch (ex: Exception) {
                    channel?.exit()
                    session.disconnect()

                    throw ex
                }
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun DownloadFile(
        context: Context,
        connection: Connection,
        remotePath: String
    ): File {
        return suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                val client = JSch()
                val session = client.getSession(
                    connection.Username,
                    connection.URL,
                    22
                )
                var channel: ChannelSftp? = null

                try {
                    session.setConfig(
                        "StrictHostKeyChecking",
                        "no"
                    )
                    session.setPassword(connection.Password)
                    session.connect()

                    channel = session.openChannel("sftp") as ChannelSftp
                    channel.connect()

                    val file = File(
                        context.filesDir,
                        FileUtil.ExtractFileNameFromRemotePath(
                            connection,
                            remotePath
                        )
                    )

                    FileOutputStream(file).use { stream ->
                        channel.get(
                            remotePath,
                            stream
                        )
                    }

                    channel.exit()
                    session.disconnect()

                    continuation.resume(file)
                } catch (ex: Exception) {
                    channel?.exit()
                    session.disconnect()

                    throw ex
                }
            }
        }
    }

}