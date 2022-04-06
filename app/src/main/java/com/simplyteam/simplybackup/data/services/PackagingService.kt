package com.simplyteam.simplybackup.data.services

import android.content.Context
import com.simplyteam.simplybackup.common.Constants
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.PathType
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class PackagingService {

    fun CreatePackage(context: Context, connection: Connection) : Result<File> {
        try {
            val file = File(context.dataDir, "backup-${connection.Name}-${LocalDateTime.now().format(Constants.PackagingFormatter)}.zip")

            //Create a new zip file
            ZipOutputStream(BufferedOutputStream(FileOutputStream(file))).use { stream ->

                //Add every entry
                for (path in connection.Paths) {
                    val backupFile = File(path.Path)

                    when (path.Type) {
                        PathType.FILE -> {
                            AddFileToZip(stream, file)
                        }
                        PathType.DIRECTORY -> {
                            AddDirectoryToZip(stream, backupFile)
                        }
                    }
                }
                stream.finish()
            }
            return Result.success(file)
        }catch (ex: Exception){
            return Result.failure(ex)
        }
    }

    private fun AddFileToZip(stream: ZipOutputStream, file: File){
        val entry = ZipEntry(file.absolutePath)
        val bytes = file.readBytes()

        stream.putNextEntry(entry)
        stream.write(bytes, 0, bytes.size)
        stream.closeEntry()
    }

    private fun AddDirectoryToZip(stream: ZipOutputStream, dir: File) {
        val dirFiles = dir.listFiles()

        dirFiles?.let { files ->
            for(file in files){
                if(file.isDirectory){
                    AddDirectoryToZip(stream, file)
                }else{
                    AddFileToZip(stream, file)
                }
            }
        }
    }

}