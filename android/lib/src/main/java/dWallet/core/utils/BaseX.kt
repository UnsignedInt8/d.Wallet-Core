package dwallet.core.utils

import dwallet.core.extensions.ZERO
import dwallet.core.extensions.toBigInteger

/**
 * Created by unsignedint8 on 8/22/17.
 * https://github.com/cryptocoinjs/base-x/blob/master/index.js
 */

class BaseX(val alphabet: String) {

    private val alphaMap = mutableMapOf<Char, Int>()
    private val base = alphabet.length
    private val leader = alphabet.first()

    init {
        alphabet.forEachIndexed { index, c -> alphaMap[c] = index }
    }

    fun encode(source: ByteArray): String {

        if (source.isEmpty()) return ""

        val digits = mutableListOf(0)

        source.forEachIndexed { i, byte ->
            var carry = byte.toBigInteger().toInt()

            digits.forEachIndexed { j, digit ->
                carry += digit shl 8
                digits[j] = carry % base
                carry /= base
            }

            while (carry > 0) {
                digits.add(carry % base)
                carry /= base
            }
        }

        var string = ""

        var k = 0
        while (k < source.size && source[k] == Byte.ZERO) {
            string += alphabet[0]
            ++k
        }

        var q = digits.size - 1
        while (q >= 0) {
            string += alphabet[digits[q]]
            --q
        }

        return string
    }

    fun decode(string: String): ByteArray {
        if (string.isEmpty()) return ByteArray(0)

        var bytes = mutableListOf(0)
        string.forEachIndexed { i, c ->
            var carry = alphaMap[c] ?: return@forEachIndexed

            bytes.forEachIndexed { i, byte ->
                carry += byte * base
                bytes[i] = carry.and(0xff)
                carry = carry.shr(8)
            }

            while (carry > 0) {
                bytes.add(carry.and(0xff))
                carry = carry.shr(8)
            }
        }

        var k = 0
        while (k < string.length - 1 && string[k] == leader){
            bytes.add(0)
            k++
        }

        return bytes.map { it.toByte() }.reversed().toByteArray()
    }

    companion object {
        val base2 by lazy { BaseX("01") }
        val base8 by lazy { BaseX("01234567") }
        val base11 by lazy { BaseX("0123456789a") }
        val base16 by lazy { BaseX("0123456789abcdef") }
        val base32 by lazy { BaseX("0123456789ABCDEFGHJKMNPQRSTVWXYZ") }
        val base36 by lazy { BaseX("0123456789abcdefghijklmnopqrstuvwxyz") }
        val base58 by lazy { BaseX("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz") }
        val base62 by lazy { BaseX("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ") }
        val base64 by lazy { BaseX("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/") }
        val base66 by lazy { BaseX("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.!~") }
    }
}