package io.github.unsignedint8.dwallet_core.crypto

import java.security.MessageDigest
import kotlin.experimental.*

/**
 * Created by unsignedint8 on 8/14/17.
 */

fun sha256(data: ByteArray) = MessageDigest.getInstance("sha-256").digest(data)

fun hash256(data: ByteArray) = sha256(sha256(data))

/**
 * source from https://github.com/bitpay/bloom-filter/blob/master/lib/murmurhash3.js
 */
fun murmurHash3(seed: Int, data: ByteArray): Int {

    var c1 = 0xcc9e2d51.toInt()
    var c2 = 0x1b873593
    var r1 = 15
    var r2 = 13
    var m = 5
    var n = 0x6b64e654

    var hash = seed

    fun mul32(a: Int, b: Int): Int {
        return (a and 0xffff) * b + (((a ushr 16) * b and 0xffff) shl 16) and 0xffffffff.toInt()
    }

    fun sum32(a: Int, b: Int): Int {
        return (a and 0xffff) + (b ushr 16) + (((a ushr 16) + b and 0xffff) shl 16) and 0xffffffff.toInt()
    }

    fun rotl32(a: Int, b: Int): Int {
        return (a shl b) or (a ushr (32 - b))
    }

    var k1 = 0

    for (i in 0..data.size step 4) {
        k1 = (data[i] or
                (data[i + 1].toInt() shl 8).toByte() or
                (data[i + 2].toInt() shl 16).toByte() or
                (data[i + 3].toInt() shl 24).toByte()).toInt()

        k1 = mul32(k1, c1)
        k1 = rotl32(k1, r1)
        k1 = mul32(k1, c2)

        hash = hash xor k1
        hash = rotl32(hash, r2)
        hash = mul32(hash, m)
        hash = sum32(hash, n)
    }

    k1 = 0

    val bit = data.size and 3
    val i = data.size / 4 * 4

    if (bit <= 3) {
        k1 = k1 xor (data[i + 2].toInt() shl 16)
    }

    if (bit <= 2) {
        k1 = k1 xor (data[i + 1].toInt() shl 8)
    }

    if (bit <= 1) {
        k1 = k1 xor data[i].toInt()
        k1 = mul32(k1, c1)
        k1 = rotl32(k1, r1)
        k1 = mul32(k1, c2)
        hash = hash xor k1
    }

    hash = hash xor data.size
    hash = hash xor (hash ushr 16)
    hash = mul32(hash, 0x85ebca6b.toInt())
    hash = hash xor (hash ushr 13)
    hash = mul32(hash, 0xc2b2ae35.toInt())
    hash = hash xor (hash ushr 16)

    return hash ushr 0
}
