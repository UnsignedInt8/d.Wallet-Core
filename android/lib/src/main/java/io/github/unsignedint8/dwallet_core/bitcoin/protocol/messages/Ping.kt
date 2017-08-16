package io.github.unsignedint8.dwallet_core.bitcoin.protocol.messages

import io.github.unsignedint8.dwallet_core.extensions.readInt64LE

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
}