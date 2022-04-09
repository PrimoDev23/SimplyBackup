package com.simplyteam.simplybackup.data.utils

import com.simplyteam.simplybackup.common.Constants
import com.simplyteam.simplybackup.data.models.Connection
import java.time.LocalDateTime

object FileUtil {

    fun ExtractFileNameFromRemotePath(
        connection: Connection,
        path: String
    ): String =
        path.removeRange(
            0,
            connection.RemotePath.length + 1
        )

    fun ExtractDateFromFileName(fileName: String): String {
        val originalDate = fileName.split('-')
            .last()
            .removeSuffix(".zip")

        val date = LocalDateTime.parse(
            originalDate,
            Constants.PackagingFormatter
        )
        return date.format(Constants.HumanReadableFormatter)
    }

}