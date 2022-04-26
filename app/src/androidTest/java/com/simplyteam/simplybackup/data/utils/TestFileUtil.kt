package com.simplyteam.simplybackup.data.utils

import com.simplyteam.simplybackup.data.models.Connection
import java.io.File

object TestFileUtil {

    fun GetFilesRecursively(connection: Connection): MutableList<File> {
        val files = mutableListOf<File>()

        for (path in connection.Paths) {
            val file = File(path.Path)

            if (file.isDirectory) {
                val innerFiles = GetFilesForDirectory(file)
                files.addAll(innerFiles)
            } else {
                files.add(file)
            }
        }

        return files
    }

    private fun GetFilesForDirectory(dir: File): MutableList<File> {
        val list = mutableListOf<File>()

        dir.listFiles()
            ?.let { files ->
                for (file in files) {
                    if (file.isDirectory) {
                        list.addAll(GetFilesForDirectory(file))
                    } else {
                        list.add(file)
                    }
                }
            }

        return list
    }

}