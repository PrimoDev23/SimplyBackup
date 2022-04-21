package com.simplyteam.simplybackup.data.services.cloudservices

import android.accounts.Account
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension
import com.google.android.gms.common.AccountPicker
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.RemoteFile
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton
import javax.security.auth.login.LoginException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GoogleDriveService @Inject constructor(
    @ApplicationContext private val _context: Context
) : ICloudService {

    private suspend fun GetClientForUsername(mail: String): GoogleSignInAccount {
        return suspendCoroutine { continuation ->
            try {
                val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(Scope(Scopes.DRIVE_FILE))
                    .setAccountName(mail)
                    .build()

                GoogleSignIn.getClient(
                    _context,
                    options
                )
                    .silentSignIn()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            continuation.resume(task.result)
                        } else {
                            continuation.resumeWithException(task.exception!!)
                        }
                    }
            } catch (ex: Exception) {
                continuation.resumeWithException(ex)
            }
        }
    }

    fun GetSignInIntent(): Intent {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(Scopes.DRIVE_FILE))
            .build()

        val client = GoogleSignIn.getClient(
            _context,
            options
        )

        client.signOut()
        return client.signInIntent
    }

    suspend fun GetAccountFromIntent(data: Intent): GoogleSignInAccount {
        return suspendCoroutine { continuation ->
            try {
                GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            continuation.resume(it.result)
                        } else {
                            continuation.resumeWithException(it.exception!!)
                        }
                    }
            } catch (ex: Exception) {
                continuation.resumeWithException(ex)
            }
        }
    }

    private fun GetDriveService(account: Account): Drive? {
        val creds = GoogleAccountCredential.usingOAuth2(
            _context,
            listOf(
                Scopes.DRIVE_FILE
            )
        )
        creds.selectedAccount = account

        return Drive.Builder(
            NetHttpTransport(),
            JacksonFactory.getDefaultInstance(),
            creds
        )
            .setApplicationName(_context.getString(R.string.app_name))
            .build()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun UploadFile(
        connection: Connection,
        file: File
    ): Result<Boolean> {
        try {
            GetClientForUsername(connection.Username).account?.let { account ->
                GetDriveService(account)?.let { drive ->
                    val fileContent = FileContent(
                        "application/zip",
                        file
                    )
                    val remoteFile = com.google.api.services.drive.model.File()
                    remoteFile.name = file.name

                    if (connection.RemotePath.isNotEmpty()) {
                        remoteFile.parents = listOf(
                            GetParentIdFromPath(
                                connection.RemotePath
                            )
                        )
                    }

                    drive.files()
                        .create(
                            remoteFile,
                            fileContent
                        )
                        .execute()

                    return Result.success(true)
                }
            }

            throw LoginException()
        } catch (ex: Exception) {
            return Result.failure(ex)
        }
    }

    private fun GetParentIdFromPath(
        remotePath: String
    ): String {
        //Either link or folderId is provided
        return remotePath.split("/")
            .last()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun GetFilesForConnection(
        connection: Connection
    ): List<RemoteFile> {
        GetClientForUsername(connection.Username).account?.let { account ->
            GetDriveService(account)?.let { drive ->
                val fileList = mutableListOf<RemoteFile>()
                val result = drive.files()
                    .list()
                    .setQ("mimeType='application/zip' and name contains '${connection.Name}'")
                    .setSpaces("drive")
                    .setFields("files(id, name, createdTime, size, parents)")
                    .execute()

                for (file in result.files) {
                    fileList.add(
                        RemoteFile(
                            file.id,
                            file.name,
                            file.createdTime.value,
                            file.getSize()
                        )
                    )
                }

                return fileList
            }
        }
        throw LoginException()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun DeleteFile(
        connection: Connection,
        remotePath: String
    ): Boolean {
        GetClientForUsername(connection.Username).account?.let { account ->
            GetDriveService(account)?.let { drive ->
                drive.files()
                    .delete(remotePath)
                    .execute()

                return true
            }
        }
        throw LoginException()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun DownloadFile(
        connection: Connection,
        remotePath: String
    ): File {
        GetClientForUsername(connection.Username).account?.let { account ->
            GetDriveService(account)?.let { drive ->
                val file = File(
                    _context.filesDir,
                    "backup.zip"
                )

                FileOutputStream(
                    file
                ).use { os ->
                    drive.files()
                        .get(remotePath)
                        .executeMediaAndDownloadTo(
                            os
                        )
                }

                return file
            }
        }

        throw LoginException()
    }
}