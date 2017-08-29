package dwallet.core.bitcoin.protocol.structures

import dwallet.core.crypto.*
import dwallet.core.extensions.*

/**
 * Created by unsignedint8 on 8/15/17.
 */

class Message(val magic: ByteArray, command: ByteArray, val length: Int, val checksum: ByteArray, val payload: ByteArray) {

    companion object Magic {
        object Bitcoin {
            const val main = 0xD9B4BEF9.toInt()
            const val testnet = 0xDAB5BFFA.toInt()
            const val regtest = 0xDAB5BFFA.toInt()
            const val testnet3 = 0x0709110B
        }

        object Litecoin {
            const val main = 0xFBC0B6DB.toInt()
            const val testnet = 0xFCC1B7DC.toInt()
        }

        fun fromBytes(bytes: ByteArray): Message {
            val magic = bytes.take(4).toByteArray()
            val command = String(bytes.sliceArray(4, 16).takeWhile { it != Byte.ZERO }.toByteArray())
            val length = bytes.readInt32LE(16)
            val checksum = bytes.sliceArray(20, 24)
            val payload = bytes.sliceArray(24)

            return Message(magic, command, length, checksum, payload)
        }

        const val standardSize = 24
    }

    constructor(magic: ByteArray, command: String, length: Int, checksum: ByteArray, payload: ByteArray) : this(
            magic,
            command.toByteArray().plus(ByteArray(12 - command.length)),
            length,
            checksum,
            payload)

    constructor(magic: ByteArray, command: String, payload: ByteArray) : this(
            magic,
            command.toByteArray().plus(ByteArray(12 - command.length)),
            payload.size,
            hash256(payload).take(4).toByteArray(),
            payload)

    constructor(magic: Int, command: String, payload: ByteArray) : this(magic.toInt32LEBytes(), command, payload)

    val command: String = String(command.takeWhile { it != Byte.ZERO }.toByteArray())

    fun toBytes() = magic + command.toByteArray().plus(ByteArray(12 - command.length)) + length.toInt32LEBytes() + checksum + payload

    fun verifyChecksum(data: ByteArray) = checksum.contentEquals(hash256(data).take(4).toByteArray())
}

