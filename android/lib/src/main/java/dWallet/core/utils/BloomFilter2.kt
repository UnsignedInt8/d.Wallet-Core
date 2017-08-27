package dWallet.core.utils

import dWallet.core.crypto.Crypto
import java.lang.Math.*

/**
 * Created by unsignedint8 on 8/27/17.
 */

class BloomFilter2(val elements: Int, falsePositiveRate: Double, randomNonce: Long = 0, updateFlag: BloomUpdate = BloomUpdate.UPDATE_NONE) {

    enum class BloomUpdate(val raw: Int) {
        UPDATE_NONE(0), // 0
        UPDATE_ALL(1), // 1
        UPDATE_P2PUBKEY_ONLY(2), //2 Only adds outpoints to the filter if the output is a pay-to-pubkey/pay-to-multisig script
    }


    // Same value as Bitcoin Core
    // A filter of 20,000 items and a false positive rate of 0.1% or one of 10,000 items and 0.0001% is just under 36,000 bytes
    private val MAX_FILTER_SIZE = 36000
    // There is little reason to ever have more hash functions than 50 given a limit of 36,000 bytes
    private val MAX_HASH_FUNCS = 50L

    var data: ByteArray? = null
        private set

    var nHashFuncs: Long = 0
        private set

    var nTweak: Long = 0
        private set

    var nFlags: Byte = 0
        private set

    init {
        var size = (-1 / pow(log(2.0), 2.0) * elements * log(falsePositiveRate)).toInt()
        size = max(1, min(size, MAX_FILTER_SIZE * 8) / 8)
        data = ByteArray(size)
        // Optimal number of hash functions for a given filter size and element count.
        nHashFuncs = ((data!!.size * 8 / elements).toDouble() * log(2.0)).toLong()
        nHashFuncs = max(1, Math.min(nHashFuncs, MAX_HASH_FUNCS)).toLong()
        this.nTweak = randomNonce
        this.nFlags = (0xff and updateFlag.raw).toByte()
    }

    fun getFalsePositiveRate(elements: Int): Double {
        return pow(1 - pow(E, -1.0 * (nHashFuncs * elements) / (data!!.size * 8)), nHashFuncs.toDouble())
    }


    @Synchronized
    fun insert(`object`: ByteArray) {
        for (i in 0 until nHashFuncs)
            setBitLE(data!!,  murmurHash3(data!!, nTweak, i.toInt(), `object`))
    }

    @Synchronized operator fun contains(`object`: ByteArray): Boolean {
        return (0 until nHashFuncs).any { checkBitLE(data!!, murmurHash3(data!!, nTweak, it.toInt(), `object`)) }
    }

    companion object {
        fun rotateLeft32(x: Int, r: Int): Int {
            return x shl r or x.ushr(32 - r)
        }

        /**
         * Applies the MurmurHash3 (x86_32) algorithm to the given data.
         * See this [C++ code for the original.](https://github.com/aappleby/smhasher/blob/master/src/MurmurHash3.cpp)
         */
        fun murmurHash3(data: ByteArray, nTweak: Long, hashNum: Int, `object`: ByteArray): Int {
            var h1 = (hashNum * 0xFBA4C795L + nTweak).toInt()
            val c1 = 0xcc9e2d51.toInt()
            val c2 = 0x1b873593

            val numBlocks = `object`.size / 4 * 4
            // body
            var i = 0
            while (i < numBlocks) {
                var k1 = (`object`[i].toInt() and 0xFF) or
                        ((`object`[i + 1].toInt() and 0xFF) shl 8) or
                        ((`object`[i + 2].toInt() and 0xFF) shl 16) or
                        ((`object`[i + 3].toInt() and 0xFF) shl 24)

                k1 *= c1
                k1 = rotateLeft32(k1, 15)
                k1 *= c2

                h1 = h1 xor k1
                h1 = rotateLeft32(h1, 13)
                h1 = h1 * 5 + 0xe6546b64.toInt()
                i += 4
            }

            var k1 = 0
            when (`object`.size and 3) {
                3 -> {
                    k1 = k1 xor ((`object`[numBlocks + 2].toInt() and 0xff) shl 16)
                    k1 = k1 xor ((`object`[numBlocks + 1].toInt() and 0xff) shl 8)
                    k1 = k1 xor (`object`[numBlocks].toInt() and 0xff)
                    k1 *= c1
                    k1 = rotateLeft32(k1, 15)
                    k1 *= c2
                    h1 = h1 xor k1
                }
            // Fall through.
                2 -> {
                    k1 = k1 xor ((`object`[numBlocks + 1].toInt() and 0xff) shl 8)
                    k1 = k1 xor (`object`[numBlocks].toInt() and 0xff)
                    k1 *= c1
                    k1 = rotateLeft32(k1, 15)
                    k1 *= c2
                    h1 = h1 xor k1
                }
            // Fall through.
                1 -> {
                    k1 = k1 xor (`object`[numBlocks].toInt() and 0xff)
                    k1 *= c1
                    k1 = rotateLeft32(k1, 15)
                    k1 *= c2
                    h1 = h1 xor k1
                }
            // Fall through.
                else -> {
                }
            }// Do nothing.

            // finalization
            h1 = h1 xor `object`.size
            h1 = h1 xor h1.ushr(16)
            h1 *= 0x85ebca6b.toInt()
            h1 = h1 xor h1.ushr(13)
            h1 *= 0xc2b2ae35.toInt()
            h1 = h1 xor h1.ushr(16)

            return ((h1 and 0xFFFFFFFFL.toInt()) % (data.size * 8)).toInt()
        }

        // 00000001, 00000010, 00000100, 00001000, ...
        private val bitMask = intArrayOf(0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80)

        /** Checks if the given bit is set in data, using little endian (not the same as Java native big endian)  */
        private fun checkBitLE(data: ByteArray, index: Int): Boolean {
            var bit = index.ushr(3)
            println(index)
//            if (bit !in 0 until data.size) bit = 0
            return data[bit].toInt() and bitMask[7 and index] != 0
        }

        /** Sets the given bit in data to one, using little endian (not the same as Java native big endian)  */
        private fun setBitLE(data: ByteArray, index: Int) {
            var bit = index.ushr(3)
//            if (bit !in 0 until data.size) bit = 0
            data[bit] = (data[bit].toInt() or bitMask[7 and index]).toByte()
        }
    }
}