package io.github.unsignedint8.dwallet_core.bitcoin.protocol

import io.github.unsignedint8.dwallet_core.crypto.hash256
import io.github.unsignedint8.dwallet_core.crypto.sha256
import io.github.unsignedint8.dwallet_core.extensions.readInt32LE
import io.github.unsignedint8.dwallet_core.extensions.sliceArray
import io.github.unsignedint8.dwallet_core.extensions.toHexString
import java.nio.ByteBuffer

/**
 * Created by unsignedint8 on 8/15/17.
 */

class Message(val magic: ByteArray, command: ByteArray, val length: Int, val checksum: ByteArray, val payload: ByteArray) {

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

        fun fromBytes(bytes: ByteArray): Message {
            val magic = bytes.take(4).toByteArray()
            val command = String(bytes.sliceArray(4, 16).takeWhile { it != 0.toByte() }.toByteArray())
            val length = bytes.readInt32LE(16)
            val checksum = bytes.sliceArray(20, 24)
            val payload = bytes.sliceArray(24)

            return Message(magic, command, length, checksum, payload)
        }
    }

    constructor(magic: ByteArray, command: String, length: Int, checksum: ByteArray, payload: ByteArray) : this(
            magic,
            command.toByteArray().plus(kotlin.ByteArray(12 - command.length)),
            length,
            checksum,
            payload)

    constructor(magic: String, command: String, payload: ByteArray) : this(
            magic.toByteArray(),
            command.toByteArray().plus(ByteArray(12 - command.length)),
            payload.size,
            hash256(payload).take(4).toByteArray(),
            payload)

    val command: String = String(command.takeWhile { it != 0.toByte() }.toByteArray())
}