package com.simplyteam.simplybackup.common

import java.time.format.DateTimeFormatter

object Constants {

    val PackagingFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    val HumanReadableFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    const val PENDING_INTENT_REQUEST_CODE_OFFSET = 1000
    const val FINISH_NOTIFICATION_OFFSET = 2000

}