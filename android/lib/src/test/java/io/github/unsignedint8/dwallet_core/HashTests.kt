package io.github.unsignedint8.dwallet_core

import io.github.unsignedint8.dwallet_core.crypto.murmurHash3
import io.github.unsignedint8.dwallet_core.crypto.sha256
import io.github.unsignedint8.dwallet_core.extensions.*
import org.junit.Test
import org.junit.Assert.*

/**
 * Created by unsignedint8 on 8/14/17.
 */

class HashTests {
    @Test
    fun testToHex() {
        assertEquals("616263", "abc".toByteArray().toHexString())
    }

    @Test
    fun testHexToByteArray() {
        assertEquals("616263", "616263".hexToByteArray().toHexString())
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad".hexToByteArray().toHexString())
    }

    @Test
    fun testSHA256() {
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", sha256("abc".toByteArray()).toHexString())
    }

    @Test
    fun testMurmurhash() {
        val cases = arrayOf(
//                Triple(0x00000000.toLong(), 0x00000000.toLong(), byteArrayOf()),
//                Triple(0x6a396f08.toLong(), 0xFBA4C795.toLong(), byteArrayOf()),
                Triple(0x81f16f39.toLong(), 0xffffffff.toLong(), byteArrayOf())
//                Triple(0x514e28b7.toLong(), 0x00000000.toLong(), byteArrayOf(0x00)),
//                Triple(0xea3f0b17.toLong(), 0xFBA4C795.toLong(), byteArrayOf(0x00)),
//                Triple(0xfd6cf10d.toLong(), 0x00000000.toLong(), byteArrayOf(0xff.toByte())),
//                Triple(0x16c6b7ab.toLong(), 0x00000000.toLong(), byteArrayOf(0x00, 0x11)),
//                Triple(0x8eb51c3d.toLong(), 0x00000000.toLong(), byteArrayOf(0x00, 0x11, 0x22)),
//                Triple(0xb4471bf8.toLong(), 0x00000000.toLong(), byteArrayOf(0x00, 0x11, 0x22, 0x33))
        )

        cases.forEach {
            println(it.first.toInt64LEBytes().toHexString())
            println(murmurHash3(it.second.toInt(), it.third).toInt32LEBytes().toHexString())
        }
    }
}