package com.simplyteam.simplybackup.common

import java.time.format.DateTimeFormatter

object Constants {

    val PackagingFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    val HumanReadableFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

}