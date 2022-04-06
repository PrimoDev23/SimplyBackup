package com.simplyteam.simplybackup.data.utils

import kotlin.math.roundToInt

object MathUtil {

    private const val _divider = 1024.0
    private const val _kiloBytes = _divider
    private const val _megaBytes = _kiloBytes * _divider
    private const val _gigaBytes = _megaBytes * _divider

    //Number of 0s is the number of decimals
    private const val _decimals = 100.0

    fun GetBiggestFileSizeString(bytes: Long): String {
        //0 -1024 B
        if (bytes < _kiloBytes) {
            return "$bytes B"
        }

        //0.01 - 999.99 KB
        if (bytes < _megaBytes) {
            return "${((bytes / _kiloBytes) * _decimals).roundToInt() / _decimals} KB"
        }

        if (bytes < _gigaBytes) {
            return "${((bytes / _megaBytes) * _decimals).roundToInt() / _decimals} MB"
        }

        return "${((bytes / _gigaBytes) * _decimals).roundToInt() / _decimals} GB"
    }

}