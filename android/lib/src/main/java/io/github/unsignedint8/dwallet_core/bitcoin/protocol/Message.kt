package io.github.unsignedint8.dwallet_core.bitcoin.protocol

/**
 * Created by unsignedint8 on 8/15/17.
 */

class Message(magic: ByteArray, command: ByteArray, length: Int, checksum: ByteArray, payload: ByteArray) {

    companion object Magic {
        object Bitcoin {
            const val main = 0xD9B4BEF9
            const val testnet = 0xDAB5BFFA
            const val regtest = 0xDAB5BFFA
            const val testnet3 = 0x0709110B
        }

        object Litecoin {
            const val main = 0xFBC0B6DB
            const val testnet = 0xFCC1B7DC
        }

        fun fromBytes(bytes: ByteArray) {

        }
    }

//    constructor(magic: String, command: String, length: Int, payload: ByteArray): this(magic.toByteArray(), ) {
//
//    }

}