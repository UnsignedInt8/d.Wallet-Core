package dwallet.core.bitcoin.protocol.messages

import dwallet.core.extensions.readInt64LE
import dwallet.core.extensions.toInt64LEBytes

/**
 * Created by unsignedint8 on 8/16/17.
 */

class Ping(val nonce: Long) {

    companion object {

        fun fromBytes(bytes: ByteArray): Ping {
            val nonce = bytes.readInt64LE()
            return Ping(nonce)
        }

        val text = "ping"
        val pong = "pong"
    }

    fun toBytes() = nonce.toInt64LEBytes()
}