package com.simplyteam.simplybackup.data.services.cloudservices.seafile

import android.content.Context
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.RemoteFile
import com.simplyteam.simplybackup.data.models.seafile.User
import com.simplyteam.simplybackup.data.services.cloudservices.ICloudService
import com.simplyteam.simplybackup.data.services.cloudservices.seafile.api.SeaFileService
import com.simplyteam.simplybackup.data.utils.FileUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class SeaFileService @Inject constructor(
    @ApplicationContext private val _context: Context
) : ICloudService {

    private fun BuildRetrofitInstance(connection: Connection) =
        Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .readTimeout(Duration.ZERO)
                    .writeTimeout(Duration.ZERO)
                    .build()
            )
            .baseUrl(
                if (connection.Host.endsWith("/")) {
                    connection.Host
                } else {
                    "${connection.Host}/"
                }
            )
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    private suspend fun GetAuthToken(
        seaFileService: SeaFileService,
        connection: Connection
    ) =
        "Token ${
            seaFileService.GetAuthToken(
                User(
                    connection.Username,
                    connection.Password
                )
            ).Token
        }"

    override suspend fun UploadFile(
        connection: Connection,
        file: File
    ): Result<Boolean> {
        try {
            val seaFileService =
                BuildRetrofitInstance(connection).create(SeaFileService::class.java)

            val authToken = GetAuthToken(
                seaFileService,
                connection
            )

            val uploadLink = seaFileService.GetUploadLink(
                authToken,
                connection.RepoId,
                connection.RemotePath
            )

            val requestFile = file
                .asRequestBody(
                    "application/octet-stream".toMediaType()
                )

            val fileToUpload = MultipartBody.Part.createFormData(
                "file",
                file.name,
                requestFile
            )

            seaFileService.UploadFile(
                uploadLink,
                authToken,
                connection.RemotePath.toRequestBody(),
                fileToUpload
            )

            return Result.success(true)
        } catch (ex: Exception) {
            return Result.failure(ex)
        }
    }

    override suspend fun GetFilesForConnection(connection: Connection): List<RemoteFile> {
        val seaFileService = BuildRetrofitInstance(connection).create(SeaFileService::class.java)

        val authToken = GetAuthToken(
            seaFileService,
            connection
        )

        val files =
            seaFileService.GetItemsInDirectory(
                authToken,
                connection.RepoId,
                connection.RemotePath
            )

        val remoteFiles = mutableListOf<RemoteFile>()

        for (file in files.filter {
            FileUtil.CheckFilenameIsBackup(
                it.name,
                connection
            )
        }) {
            remoteFiles.add(
                RemoteFile(
                    file.id,
                    "${connection.RemotePath}/${file.name}",
                    Instant.ofEpochMilli(file.mtime).epochSecond,
                    file.size
                )
            )
        }

        return remoteFiles
    }

    override suspend fun DeleteFile(
        connection: Connection,
        remotePath: String
    ): Boolean {
        val seaFileService = BuildRetrofitInstance(connection).create(SeaFileService::class.java)

        val authToken = GetAuthToken(
            seaFileService,
            connection
        )

        seaFileService.DeleteFile(
            authToken,
            connection.RepoId,
            remotePath
        )

        return true
    }

    override suspend fun DownloadFile(
        connection: Connection,
        remotePath: String
    ): File {
        val seaFileService = BuildRetrofitInstance(connection).create(SeaFileService::class.java)

        val authToken = GetAuthToken(
            seaFileService,
            connection
        )

        val downloadLink = seaFileService.GetDownloadLink(
            authToken,
            connection.RepoId,
            remotePath
        )

        val body = seaFileService.DownloadFile(
            downloadLink,
            authToken
        )

        return SaveBodyToFile(
            FileUtil.ExtractFileNameFromRemotePath(
                connection,
                remotePath
            ),
            body
        )
    }

    private fun SaveBodyToFile(
        filename: String,
        body: ResponseBody
    ): File {
        val file = File(
            _context.cacheDir,
            "backup.zip"
        )
        FileOutputStream(file).use { fos ->
            fos.write(body.bytes())
        }
        return file
    }
}