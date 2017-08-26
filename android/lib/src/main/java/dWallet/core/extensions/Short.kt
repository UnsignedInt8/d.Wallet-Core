package dWallet.core.extensions

/**
 * Created by unsignedint8 on 8/15/17.
 */

fun Short.toInt16LEBytes(): ByteArray {
    val data = ByteArray(2)
    data.writeInt16LE(this)
    return data
}

fun Short.toInt16BEBytes(): ByteArray {
    val data = ByteArray(2)
    data.writeInt16BE(this)
    return data
}