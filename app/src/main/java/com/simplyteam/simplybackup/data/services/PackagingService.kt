package com.simplyteam.simplybackup.data.services

import com.simplyteam.simplybackup.common.Constants
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.PathType
import java.io.*
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.LocalDateTime
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.io.path.Path

class PackagingService {

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

            //Create a new zip file
            ZipOutputStream(BufferedOutputStream(FileOutputStream(file))).use { stream ->

                //Add every entry
                for (path in connection.Paths) {
                    val backupFile = File(path.Path)

                    when (path.Type) {
                        PathType.FILE -> {
                            AddFileToZip(
                                stream,
                                file
                            )
                        }
                        PathType.DIRECTORY -> {
                            AddDirectoryToZip(
                                stream,
                                backupFile
                            )
                        }
                    }
                }
                stream.finish()
            }
            return Result.success(file)
        } catch (ex: Exception) {
            return Result.failure(ex)
        }
    }

    fun RestorePackage(zipFile: File) {
        ZipInputStream(BufferedInputStream(FileInputStream(zipFile))).use {
            var zipEntry = it.nextEntry

            while (zipEntry != null) {

                val file = File(zipEntry.name)

                CreateDirectories(file)

                Files.copy(
                    it,
                    Path(file.absolutePath),
                    StandardCopyOption.REPLACE_EXISTING
                )

                zipEntry = it.nextEntry
            }
        }
    }

    private fun AddFileToZip(
        stream: ZipOutputStream,
        file: File
    ) {
        val entry = ZipEntry(file.absolutePath)
        val bytes = file.readBytes()

        stream.putNextEntry(entry)
        stream.write(
            bytes,
            0,
            bytes.size
        )
        stream.closeEntry()
    }

    private fun AddDirectoryToZip(
        stream: ZipOutputStream,
        dir: File
    ) {
        val dirFiles = dir.listFiles()

        dirFiles?.let { files ->
            for (file in files) {
                if (file.isDirectory) {
                    AddDirectoryToZip(
                        stream,
                        file
                    )
                } else {
                    AddFileToZip(
                        stream,
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