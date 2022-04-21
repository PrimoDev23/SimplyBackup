package com.simplyteam.simplybackup.data.services

import com.simplyteam.simplybackup.common.Constants
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.PathType
import net.lingala.zip4j.io.inputstream.ZipInputStream
import net.lingala.zip4j.io.outputstream.ZipOutputStream
import net.lingala.zip4j.model.LocalFileHeader
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.AesKeyStrength
import net.lingala.zip4j.model.enums.EncryptionMethod
import java.io.*
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.io.path.Path


class PackagingService @Inject constructor(

) {

    private fun BuildZipParameters(encrypted: Boolean): ZipParameters {
        val zipParameters = ZipParameters()

        if (encrypted) {
            zipParameters.isEncryptFiles = true
            zipParameters.encryptionMethod = EncryptionMethod.AES
            zipParameters.aesKeyStrength = AesKeyStrength.KEY_STRENGTH_256
        }

        return zipParameters
    }

    fun CreatePackage(
        filesDir: String,
        connection: Connection
    ): Result<File> {
        try {
            val file = File(
                filesDir,
                "backup-${connection.Name}-${
                    LocalDateTime.now()
                        .format(Constants.PackagingFormatter)
                }.zip"
            )

            val zipParameters = BuildZipParameters(connection.BackupPassword.isNotEmpty())

            if (connection.BackupPassword.isNotEmpty()) {
                ZipOutputStream(
                    BufferedOutputStream(FileOutputStream(file)),
                    connection.BackupPassword.toCharArray()
                )
            } else {
                ZipOutputStream(BufferedOutputStream(FileOutputStream(file)))
            }.use { stream ->
                //Add every entry
                for (path in connection.Paths) {
                    val backupFile = File(path.Path)

                    when (path.Type) {
                        PathType.FILE -> {
                            AddFileToZip(
                                stream,
                                zipParameters,
                                backupFile
                            )
                        }
                        PathType.DIRECTORY -> {
                            AddDirectoryToZip(
                                stream,
                                zipParameters,
                                backupFile
                            )
                        }
                    }
                }
            }
            return Result.success(file)
        } catch (ex: Exception) {
            return Result.failure(ex)
        }
    }

    fun RestorePackage(
        zipFile: File,
        connection: Connection
    ) {
        if (connection.BackupPassword.isNotEmpty()) {
            ZipInputStream(
                FileInputStream(zipFile),
                connection.BackupPassword.toCharArray()
            )
        } else {
            ZipInputStream(FileInputStream(zipFile))
        }.use { stream ->
            var localFileHeader: LocalFileHeader? = stream.nextEntry
            while (localFileHeader != null) {
                val file = File("/${localFileHeader.fileName}")

                CreateDirectories(file)

                Files.copy(
                    stream,
                    Path(file.absolutePath),
                    StandardCopyOption.REPLACE_EXISTING
                )

                localFileHeader = stream.nextEntry
            }
        }
    }

    private fun AddFileToZip(
        stream: ZipOutputStream,
        zipParameters: ZipParameters,
        file: File
    ) {
        val bytes = file.readBytes()

        val path = if (file.absolutePath.startsWith("/")) {
            file.absolutePath.removePrefix("/")
        } else {
            file.absolutePath
        }

        zipParameters.fileNameInZip = path
        zipParameters.lastModifiedFileTime = file.lastModified()

        stream.putNextEntry(zipParameters)
        stream.write(
            bytes
        )
        stream.closeEntry()
    }

    private fun AddDirectoryToZip(
        stream: ZipOutputStream,
        zipParameters: ZipParameters,
        dir: File
    ) {
        val dirFiles = dir.listFiles()

        dirFiles?.let { files ->
            for (file in files) {
                if (file.isDirectory) {
                    AddDirectoryToZip(
                        stream,
                        zipParameters,
                        file
                    )
                } else {
                    AddFileToZip(
                        stream,
                        zipParameters,
                        file
                    )
                }
            }
        }
    }

    private fun CreateDirectories(file: File) {
        file.parent?.let { parent ->
            val parentDir = File(parent)

            if (!parentDir.exists()) {
                Files.createDirectories(Path(parentDir.absolutePath))
            }
        }
    }
}