package io.github.unsignedint8.dwallet_core.bitcoin.protocol.messages

import io.github.unsignedint8.dwallet_core.bitcoin.protocol.structures.*
import io.github.unsignedint8.dwallet_core.extensions.*

/**
 * Created by unsignedint8 on 8/15/17.
 */

class Version(val version: Int = 70001, val services: ByteArray = ByteArray(8), val timestamp: Long = System.currentTimeMillis() / 1000, val toAddr: NetworkAddress, val fromAddr: NetworkAddress, val nonce: Long, val ua: String, val startHeight: Int, val relay: Boolean = false) {

    companion object {
        fun fromBytes(bytes: ByteArray): Version {
            val version = bytes.readInt32LE(0)
            val services = bytes.sliceArray(4, 12)
            val timestamp = bytes.readInt64LE(12)
            val toAddr = NetworkAddress.fromBytes(bytes.sliceArray(16, 16 + NetworkAddress.standardSize - 4), false)
            val fromAddr = NetworkAddress.fromBytes(bytes.sliceArray(42, 46 + NetworkAddress.standardSize - 4), false)
            val nonce = bytes.readInt64LE(72)
            val uaSize = bytes.sliceArray(80).readVarStringOffsetLength()
            val ua = bytes.sliceArray(80).readVarString()
            val startHeight = bytes.readInt32LE((80 + uaSize.first + uaSize.second).toInt())
            val relay = if (version >= 70001) bytes[bytes.lastIndex] == 1.toByte() else false

            return Version(version, services, timestamp, toAddr, fromAddr, nonce, ua, startHeight, relay)
        }

        val text = "version"
    }

    fun toBytes() = version.toInt32LEBytes() +
            services +
            timestamp.toInt64LEBytes() +
            toAddr.toBytes() +
            fromAddr.toBytes() +
            nonce.toInt64LEBytes() +
            ua.toVarBytes() +
            startHeight.toInt32LEBytes() +
            (if (version >= 70001) byteArrayOf(if (relay) 1 else 0) else ByteArray(0))

    val isFullNode: Boolean = services[0] == 1.toByte()
    val canGetUTXO: Boolean = services[1] == 1.toByte()
    val canSetBloom: Boolean = services[2] == 1.toByte()
}