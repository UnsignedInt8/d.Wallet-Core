package dwallet.core.crypto

import dwallet.core.extensions.*
import java.math.BigInteger

/**
 * Created by unsignedint8 on 8/17/17.
 */

private fun BigInteger.unShiftRight(n: Int): BigInteger {
    return BigInteger.valueOf((this.toInt() ushr n).toLong())
}

/**
 * source from https://github.com/bitpay/bloom-filter/blob/master/lib/murmurhash3.js
 */
fun murmurHash3(seed: Int, data: ByteArray): BigInteger {

    val c1 = BigInteger.valueOf(0xcc9e2d51)
    val c2 = BigInteger.valueOf(0x1b873593)
    val r1 = BigInteger.valueOf(15)
    val r2 = BigInteger.valueOf(13)
    val m = BigInteger.valueOf(5)
    val n = BigInteger.valueOf(0x6b64e654)

    var hash = BigInteger.valueOf(seed.toLong())

    fun mul32(a: BigInteger, b: BigInteger): BigInteger {
        return BigInteger.valueOf((((a.and(BigInteger.valueOf(0xffff)))).multiply(b) + (((a.unShiftRight(16))).multiply(b).and(BigInteger.valueOf(0xffff))).shiftLeft(16).and(BigInteger.valueOf(0xffffffff))).toInt().toLong())
    }

    fun sum32(a: BigInteger, b: BigInteger): BigInteger {
        return (a.and(BigInteger.valueOf(0xffff))).plus(b.unShiftRight(16)) + (((a.unShiftRight(16)).plus(b.and(BigInteger.valueOf(0xffff)))).shiftLeft(16)).and(BigInteger.valueOf(0xffffffff))
    }

    fun rotl32(a: Int, b: Int): BigInteger {
        return BigInteger.valueOf(((a shl b) or (a ushr (32 - b))).toLong())
    }

    var k1 = BigInteger.valueOf(0)

    var i = 0
    while (i + 4 <= data.size) {

        k1 = BigInteger.valueOf(data[i].toBigInteger().or(data[i + 1].toBigInteger().shiftLeft(8)).or(data[i + 2].toBigInteger().shiftLeft(16)).or(data[i + 3].toBigInteger().shiftLeft(24)).toInt().toLong())

        k1 = mul32(k1, c1)
        k1 = rotl32(k1.toInt(), r1.toInt())
        k1 = mul32(k1, c2)

        hash = hash.xor(k1)
        hash = rotl32(hash.toInt(), r2.toInt())
        hash = mul32(hash, m)
        hash = sum32(hash, n)

        i += 4
    }

    k1 = BigInteger.valueOf(0)

    val bit = data.size and 3

    if (bit == 3) {
        k1 = k1.xor(BigInteger.valueOf((data[i + 2].toInt() shl 16).toLong()))
    }

    if (bit in 2..3) {
        k1 = k1.xor(BigInteger.valueOf((data[i + 1].toInt() shl 8).toLong()))
    }

    if (bit in 1..3) {
        k1 = k1.xor(data[i].toBigInteger())
        k1 = mul32(k1, c1)
        k1 = rotl32(k1.toInt(), r1.toInt())
        k1 = mul32(k1, c2)
        hash = hash.xor(k1)
    }

    hash = hash.xor(BigInteger.valueOf(data.size.toLong()))
    hash = hash.xor(hash.unShiftRight(16))
    hash = mul32(hash, BigInteger.valueOf(0x85ebca6b))
    hash = hash.xor(hash.unShiftRight(13))
    hash = mul32(hash, BigInteger.valueOf(0xc2b2ae35))
    hash = hash.xor(hash.unShiftRight(16))

    return if (hash >= BigInteger.ZERO) hash else hash.add(BigInteger.valueOf(4294967296))
}
