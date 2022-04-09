package com.simplyteam.simplybackup.common

import com.simplyteam.simplybackup.BuildConfig
import java.time.format.DateTimeFormatter

object Constants {

    val PackagingFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    val HumanReadableFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    const val NOTIFICATION_ID_OFFSET = 1000

}