package io.github.unsignedint8.dwallet_core.bitcoin.protocol.structures

/**
 * Created by unsignedint8 on 8/15/17.
 */

import io.github.unsignedint8.dwallet_core.extensions.*
import java.net.InetAddress
import kotlin.*

class NetworkAddress(val ip: String, val port: Short, val services: ByteArray = ByteArray(8), val time: Int = (System.currentTimeMillis() / 1000).toInt(), val addTimestamp: Boolean = true) {

    companion object {

        fun fromBytes(bytes: ByteArray, hasTimestamp: Boolean = true): NetworkAddress {
            val time = if (hasTimestamp) bytes.readInt32LE(0) else 0
            var offset = if (hasTimestamp) 4 else 0

            val services = bytes.sliceArray(offset, offset + 8)
            offset += 8

            val ip = InetAddress.getByAddress(bytes.sliceArray(offset, offset + 16))
            offset += 16

            val port = bytes.readInt16BE(offset)

            return NetworkAddress(ip.hostAddress, port, services, time)
        }

        fun fromBytes2(bytes: ByteArray): Pair<NetworkAddress, Int> {
            return Pair(fromBytes(bytes), specifiedSize)
        }

        const val specifiedSize = 30
    }

    fun toBytes(): ByteArray {
        val inetAddr = InetAddress.getByName(ip)
        val addr = if (inetAddr.address.size == 4) "00000000000000000000FFFF".hexToByteArray() + inetAddr.address else inetAddr.address

        return (if (addTimestamp) time.toInt32LEBytes() else ByteArray(0)) + services + addr + port.toInt16BEBytes()
    }

}