package com.simplyteam.simplybackup.data.utils

import org.junit.Assert.*
import org.junit.Test

class MathUtilTest {

    @Test
    fun SizeConversionTest(){
        val byteTest = 200L
        val kiloByteTest = 4096L
        val megaByteTest = 5242880L
        val gigaByteTest = 6442450944L

        assertEquals(MathUtil.GetBiggestFileSizeString(byteTest), "200 B")
        assertEquals(MathUtil.GetBiggestFileSizeString(kiloByteTest), "4.0 KB")
        assertEquals(MathUtil.GetBiggestFileSizeString(megaByteTest), "5.0 MB")
        assertEquals(MathUtil.GetBiggestFileSizeString(gigaByteTest), "6.0 GB")
    }

}