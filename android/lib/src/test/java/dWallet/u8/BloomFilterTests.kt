package dWallet.u8

import dWallet.core.crypto.murmurHash3
import dWallet.core.crypto.sha1
import dWallet.core.crypto.sha256
import dWallet.core.extensions.*
import dWallet.core.utils.BloomFilter
import org.junit.Test
import org.junit.Assert.*

/**
 * Created by unsignedint8 on 8/14/17.
 */

class BloomFilterTests {
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
    fun testSHA1() {
        assertEquals("6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2", sha1("111".toByteArray()).toHexString())
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
            assertEquals(it.first, murmurHash3(it.second, it.third).toInt())
        }

        val h = murmurHash3(0, "99108ad8ed9bb6274d3980bab5a85c048f0950c8".hexToByteArray())
        assertEquals(683397288, h.toInt())
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

    @Test
    fun testBloomFilterSerializeObjects() {
        val f = BloomFilter.create(3, 0.01, 0, BloomFilter.BLOOM_UPDATE_ALL)

        f.insert("99108ad8ed9bb6274d3980bab5a85c048f0950c8".hexToByteArray())
        assert(f.contains("99108ad8ed9bb6274d3980bab5a85c048f0950c8".hexToByteArray()))
//
        assert(!f.contains("19108ad8ed9bb6274d3980bab5a85c048f0950c8".hexToByteArray()))
//
        f.insert("b5a2c786d9ef4658287ced5914b37a1b4aa32eee".hexToByteArray())
        assert(f.contains("b5a2c786d9ef4658287ced5914b37a1b4aa32eee".hexToByteArray()))
//
        f.insert("b9300670b4c5366e95b2699e8b18bc75e5f729c5".hexToByteArray())
        assert(f.contains("b9300670b4c5366e95b2699e8b18bc75e5f729c5".hexToByteArray()))

        assertArrayEquals(byteArrayOf(97, 78, 155.toByte()), f.data)
        assertEquals(5, f.nHashFuncs)
        assertEquals(0, f.nTweak)
        assertEquals(1, f.nFlags)
    }

    @Test
    fun testBloomFilterTweak() {
        val f = BloomFilter.create(3, 0.01, 2147483649.toInt(), BloomFilter.BLOOM_UPDATE_ALL)
        f.insert("99108ad8ed9bb6274d3980bab5a85c048f0950c8".hexToByteArray())
        assert(f.contains("99108ad8ed9bb6274d3980bab5a85c048f0950c8".hexToByteArray()))

        assert(!f.contains("19108ad8ed9bb6274d3980bab5a85c048f0950c8".hexToByteArray()))

        f.insert("b5a2c786d9ef4658287ced5914b37a1b4aa32eee".hexToByteArray())
        assert(f.contains("b5a2c786d9ef4658287ced5914b37a1b4aa32eee".hexToByteArray()))

        f.insert("b9300670b4c5366e95b2699e8b18bc75e5f729c5".hexToByteArray())
        assert(f.contains("b9300670b4c5366e95b2699e8b18bc75e5f729c5".hexToByteArray()))

        assertArrayEquals(byteArrayOf(206.toByte(), 66, 153.toByte()), f.data)
        assertEquals(5, f.nHashFuncs)
        assertEquals(2147483649.toInt(), f.nTweak)
        assertEquals(1, f.nFlags)
    }

    @Test
    fun testBloomFilterPubkey() {
        val f = BloomFilter.create(2, 0.001, 0, BloomFilter.BLOOM_UPDATE_ALL)
        f.insert("045b81f0017e2091e2edcd5eecf10d5bdd120a5514cb3ee65b8447ec18bfc4575c6d5bf415e54e03b1067934a0f0ba76b01c6b9ab227142ee1d543764b69d901e0".hexToByteArray())
        f.insert("477abbacd4113f2e6b100526222eedd953c26a64".hexToByteArray())

        assertArrayEquals(byteArrayOf(143.toByte(), 193.toByte(), 107), f.data)
        assertEquals(8, f.nHashFuncs)
        assertEquals(0, f.nTweak)
    }

    @Test
    fun testBloomFilterThreshold() {
        val f = BloomFilter.create(10, 0.0000000000000001)
        assertEquals(BloomFilter.MAX_HASH_FUNCS, f.nHashFuncs)
    }
}