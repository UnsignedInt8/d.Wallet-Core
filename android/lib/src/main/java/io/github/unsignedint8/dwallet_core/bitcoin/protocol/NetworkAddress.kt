package io.github.unsignedint8.dwallet_core.bitcoin.protocol

/**
 * Created by unsignedint8 on 8/15/17.
 */

import io.github.unsignedint8.dwallet_core.extensions.*
import java.net.InetAddress
import kotlin.*

class NetworkAddress(val ip: String, val port: Short, val services: ByteArray = ByteArray(8), val time: Int = (System.currentTimeMillis() / 1000).toInt()) {

    companion object {
        fun fromBytes(bytes: ByteArray): NetworkAddress {
            val time = bytes.readInt32LE(0)
            val services = bytes.sliceArray(4, 12)
            val ip = InetAddress.getByAddress(bytes.sliceArray(12, 28))
            val port = bytes.readInt16BE(28)

            return NetworkAddress(ip.hostAddress, port, services, time)
        }
    }

    fun toBytes(): ByteArray {
        val inetAddr = InetAddress.getByName(ip)
        val addr = if (inetAddr.address.size == 4) "00000000000000000000FFFF".hexToByteArray() + inetAddr.address else inetAddr.address

        return time.toInt32LEBytes() + services + addr + port.toInt16BEBytes()
    }

}