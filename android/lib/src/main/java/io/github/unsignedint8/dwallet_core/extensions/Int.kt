package io.github.unsignedint8.dwallet_core.extensions

/**
 * Created by unsignedint8 on 8/15/17.
 */

fun Int.toVarIntBytes(): ByteArray {

    when {
        this < 0xfd -> {
            return byteArrayOf(this.toByte())
        }

        this < 0xffff -> {
            val number = ByteArray(2)
            number.writeInt16LE(this.toShort())
            return byteArrayOf(0xfd.toByte()) + number
        }

        this < 0xffffffff -> {
            val number = ByteArray(4)
            number.writeInt32LE(this)
            return byteArrayOf(0xfe.toByte()) + number
        }
    }

    val number = this.toShort().toInt16LEBytes() + ByteArray(6)
    return byteArrayOf(0xff.toByte()) + number
}

fun Int.toInt32LEBytes(): ByteArray {
    val data = ByteArray(4)
    data.writeInt32LE(this)
    return data
}

fun Int.toInt32BEBytes(): ByteArray {
    val data = ByteArray(4)
    data.writeInt32BE(this)
    return data
}