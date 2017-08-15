package io.github.unsignedint8.dwallet_core.extensions

import java.nio.charset.Charset

/**
 * Created by unsignedint8 on 8/15/17.
 */

fun String.hexToByteArray(): ByteArray {
    val data = if (startsWith("0x", true)) this.substring(2) else this
    val result = ByteArray(data.length / 2)

    for (i in 0 until data.length step 2) {
        val firstIndex = HEX_CHARS.indexOf(data[i])
        val secondIndex = HEX_CHARS.indexOf(data[i + 1])

        val octet = firstIndex.shl(4).or(secondIndex)
        result[i.shr(1)] = octet.toByte()
    }

    return result
}

fun String.toVarBytes(charset: Charset = Charset.defaultCharset()) {
    this.toByteArray(charset)
}