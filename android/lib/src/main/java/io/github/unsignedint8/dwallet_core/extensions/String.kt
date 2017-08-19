package io.github.unsignedint8.dwallet_core.extensions

import java.nio.charset.Charset

/**
 * Created by unsignedint8 on 8/15/17.
 */

fun String.hexToByteArray(): ByteArray {

    val len = this.length
    val data = ByteArray(len / 2)
    var i = 0
    while (i < len) {
        data[i / 2] = ((Character.digit(this[i], 16) shl 4) + Character.digit(this[i + 1], 16)).toByte()
        i += 2
    }

    return data
}

fun String.toVarBytes(charset: Charset = Charset.defaultCharset()): ByteArray {
    return this.length.toVarIntBytes() + this.toByteArray(charset)
}

fun String.hashToBytes(): ByteArray {
    val data = this.hexToByteArray()
    data.reverse()
    return data
}

val String.Companion.ZEROHASH: String
    get() = ByteArray(32).toHexString()