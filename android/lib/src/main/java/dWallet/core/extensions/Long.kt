package dWallet.core.extensions

/**
 * Created by unsignedint8 on 8/15/17.
 */

fun Long.toInt64LEBytes(): ByteArray {
    val data = ByteArray(8)
    data.writeInt64LE(this)
    return data
}

fun Long.toInt64BEBytes(): ByteArray {
    val data = ByteArray(8)
    data.writeInt64BE(this)
    return data
}