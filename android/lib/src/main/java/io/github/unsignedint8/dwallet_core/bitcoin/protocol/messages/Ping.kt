package io.github.unsignedint8.dwallet_core.bitcoin.protocol.messages

/**
 * Created by unsignedint8 on 8/16/17.
 */

class Ping(val nonce: Long) {

    companion object {

        fun fromBytes(bytes: ByteArray) {

        }

        val text = "ping"
    }
}