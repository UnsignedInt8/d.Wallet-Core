package io.github.unsignedint8.dwallet_core.extensions

/**
 * Created by unsignedint8 on 8/14/17.
 */

// https://gist.github.com/fabiomsr/845664a9c7e92bafb6fb0ca70d4e44fd

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