package com.simplyteam.simplybackup.common

import java.time.format.DateTimeFormatter

object Constants {

    val PackagingFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    val HumanReadableFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

}