package io.github.unsignedint8.dwallet_core.extensions

/**
 * Created by unsignedint8 on 8/14/17.
 */

private val HEX_CHARS = "0123456789abcdef".toCharArray()

fun ByteArray.toHexString(): String {
    val result = StringBuffer()

    forEach {
        val octet = it.toInt()
        val firstIndex = (octet and 0xF0).ushr(4)
        val secondIndex = octet and 0x0F
        result.append(HEX_CHARS[firstIndex])
        result.append(HEX_CHARS[secondIndex])
    }

    return result.toString()
}