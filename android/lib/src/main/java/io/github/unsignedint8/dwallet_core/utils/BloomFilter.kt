package io.github.unsignedint8.dwallet_core.utils

import io.github.unsignedint8.dwallet_core.crypto.murmurHash3
import kotlin.experimental.*


/**
 * Created by unsignedint8 on 8/17/17.
 * source from https://github.com/bitpay/bloom-filter/blob/master/lib/filter.js
 */

class BloomFilter private constructor(var data: ByteArray, val nHashFuncs: Int, val nTweak: Int, val nFlags: Int) {

    companion object {
        const val BLOOM_UPDATE_NONE = 0
        const val BLOOM_UPDATE_ALL = 1
        const val BLOOM_UPDATE_P2PUBKEY_ONLY = 2
        const val MAX_BLOOM_FILTER_SIZE = 36000
        const val MAX_HASH_FUNCS = 50
        const val MIN_HASH_FUNCS = 1

        val LN2 = Math.log(2.0)
        val LN2SQUARED = Math.pow(Math.log(2.0), 2.0)

        fun create(elements: Int, falsePositiveRate: Double, nTweak: Int = 0, nFlags: Int = BLOOM_UPDATE_NONE): BloomFilter {

            val size = -1.0 / LN2SQUARED * elements * Math.log(falsePositiveRate)
            var filterSize = Math.floor(size / 8).toInt()

            val max = MAX_BLOOM_FILTER_SIZE * 8
            if (filterSize > max) {
                filterSize = max
            }

            val data = ByteArray(filterSize)

            // The ideal number of hash functions is:
            // filter size * ln(2) / number of elements
            // See: https://github.com/bitcoin/bitcoin/blob/master/src/bloom.cpp
            var nHashFuncs = Math.floor(data.size * 8 / elements * LN2).toInt()
            if (nHashFuncs > MAX_HASH_FUNCS) {
                nHashFuncs = MAX_HASH_FUNCS
            }

            if (nHashFuncs < MIN_HASH_FUNCS) {
                nHashFuncs = MIN_HASH_FUNCS
            }

            return BloomFilter(data, nHashFuncs, nTweak, nFlags)
        }
    }

    fun hash(data: ByteArray, nHashFuncs: Int): Int {
        val hash = murmurHash3((nHashFuncs * 0xFBA4C795.toInt() + nTweak) and 0xFFFFFFFF.toInt(), data)
        return hash % (data.size / 8)
    }

    fun insert(data: ByteArray) {
        kotlin.repeat(nHashFuncs) {
            val index = hash(data, it)
            val position = 1 shl (7 and index)
            if (index shr 3 !in 0..data.size) return@repeat
            this.data[index shr 3] = this.data[index shr 3] or position.toByte()
        }
    }

    fun contains(data: ByteArray): Boolean {
        kotlin.repeat(nHashFuncs) {
            val index = this.hash(data, it)
            val bit = index shr 3
            if (bit in 0..this.data.size && (this.data[bit] and (1 shl (7 and index)).toByte()) == 1.toByte()) {
                return true
            }
        }

        return false
    }

    fun clear() {
        data = ByteArray(data.size)
    }

    override fun toString(): String {
        return "<BloomFilter: $data nHashFuncs: $nHashFuncs nTweak: $nTweak nFlags: $nFlags>"
    }
}