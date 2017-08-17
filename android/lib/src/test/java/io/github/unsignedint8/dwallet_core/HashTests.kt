package io.github.unsignedint8.dwallet_core

import io.github.unsignedint8.dwallet_core.crypto.murmurHash3
import io.github.unsignedint8.dwallet_core.crypto.sha256
import io.github.unsignedint8.dwallet_core.extensions.*
import io.github.unsignedint8.dwallet_core.utils.BloomFilter
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
                Triple(0x00000000.toInt(), 0x00000000.toInt(), byteArrayOf()),
                Triple(0x6a396f08.toInt(), 0xFBA4C795.toInt(), byteArrayOf()),
                Triple(0x81f16f39.toInt(), 0xffffffff.toInt(), byteArrayOf()),
                Triple(0x514e28b7.toInt(), 0x00000000.toInt(), byteArrayOf(0x00)),
                Triple(0xea3f0b17.toInt(), 0xFBA4C795.toInt(), byteArrayOf(0x00)),
                Triple(0xfd6cf10d.toInt(), 0x00000000.toInt(), byteArrayOf(0xff.toByte())),
                Triple(0x16c6b7ab.toInt(), 0x00000000.toInt(), byteArrayOf(0x00, 0x11)),
                Triple(0x8eb51c3d.toInt(), 0x00000000.toInt(), byteArrayOf(0x00, 0x11, 0x22)),
                Triple(0xb4471bf8.toInt(), 0x00000000.toInt(), byteArrayOf(0x00, 0x11, 0x22, 0x33)),
                Triple(0xe2301fa8.toInt(), 0x00000000.toInt(), byteArrayOf(0x00, 0x11, 0x22, 0x33, 0x44)),
                Triple(0xfc2e4a15.toInt(), 0x00000000.toInt(), byteArrayOf(0x00, 0x11, 0x22, 0x33, 0x44, 0x55)),
                Triple(0xb074502c.toInt(), 0x00000000.toInt(), byteArrayOf(0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66)),
                Triple(0x8034d2a0.toInt(), 0x00000000.toInt(), byteArrayOf(0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77)),
                Triple(0xb4698def.toInt(), 0x00000000.toInt(), byteArrayOf(0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x88.toByte()))
        )

        cases.forEach {
            //            println(it.first.toInt32LEBytes().toHexString())
//            println(murmurHash3(it.second, it.third).toInt().toInt32LEBytes().toHexString())
            assertEquals(it.first, murmurHash3(it.second, it.third).toInt())
        }
    }

    // test data from bitcoind
    // see: https://github.com/bitcoin/bitcoin/blob/master/src/test/bloom_tests.cpp
    val a = "99108ad8ed9bb6274d3980bab5a85c048f0950c8".hexToByteArray()
    var b = "19108ad8ed9bb6274d3980bab5a85c048f0950c8".hexToByteArray()
    var c = "b5a2c786d9ef4658287ced5914b37a1b4aa32eee".hexToByteArray()
    var d = "b9300670b4c5366e95b2699e8b18bc75e5f729c5".hexToByteArray()

    @Test
    fun testCreateBloomFilter() {
        val f = BloomFilter.create(100, 0.1)
        assert(f.data.isNotEmpty())
        assert(f.nHashFuncs > 0)
    }

    @Test
    fun testBloomFilterSize() {
        val cases = arrayOf(
                Triple(Pair(2, 0.001), 3, 8),
                Triple(Pair(3, 0.01), 3, 5),
                Triple(Pair(10, 0.2), 4, 2),
                Triple(Pair(100, 0.2), 41, 2),
                Triple(Pair(10000, 0.3), 3132, 1)
        )

        cases.forEach {
            val f = BloomFilter.create(it.first.first, it.first.second)

            assertEquals(it.second, f.data.size)
            assertEquals(it.third, f.nHashFuncs)
        }
    }

    @Test
    fun testInsertingBloomFilter() {
        val f = BloomFilter.create(3, 0.01)
        f.insert(a)
        assert(f.contains(a))
        assert(!f.contains(b))
        f.insert(c)
        assert(f.contains(c))
        f.insert(d)
        assert(f.contains(d))
    }
}