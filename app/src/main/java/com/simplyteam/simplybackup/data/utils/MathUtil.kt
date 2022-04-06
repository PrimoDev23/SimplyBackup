package com.simplyteam.simplybackup.data.utils

import kotlin.math.roundToInt

object MathUtil {

    private const val DIVIDER = 1024.0
    private const val KILOBYTES = DIVIDER
    private const val MEGABYTES = KILOBYTES * DIVIDER
    private const val GIGABYTES = MEGABYTES * DIVIDER

    //Number of 0s is the number of decimals
    private const val DECIMALS = 100.0

    fun GetBiggestFileSizeString(bytes: Long): String {
        //0 -1024 B
        if (bytes < KILOBYTES) {
            return "$bytes B"
        }

        //0.01 - 999.99 KB
        if (bytes < MEGABYTES) {
            return "${((bytes / KILOBYTES) * DECIMALS).roundToInt() / DECIMALS} KB"
        }

        if (bytes < GIGABYTES) {
            return "${((bytes / MEGABYTES) * DECIMALS).roundToInt() / DECIMALS} MB"
        }

        return "${((bytes / GIGABYTES) * DECIMALS).roundToInt() / DECIMALS} GB"
    }

}