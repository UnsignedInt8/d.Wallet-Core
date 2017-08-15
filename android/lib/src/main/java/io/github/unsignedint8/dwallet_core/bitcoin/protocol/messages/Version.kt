package io.github.unsignedint8.dwallet_core.bitcoin.protocol.messages

import io.github.unsignedint8.dwallet_core.bitcoin.protocol.*
import io.github.unsignedint8.dwallet_core.extensions.*

/**
 * Created by unsignedint8 on 8/15/17.
 */

class Version(val version: Int = 70001, val services: ByteArray = ByteArray(8), val timestamp: Long = System.currentTimeMillis() / 1000, val toAddr: NetworkAddress, val fromAddr: NetworkAddress, val nonce: Long, val ua: String, val startHeight: Int, val relay: Boolean = false) {

    fun toBytes(): ByteArray {
        return version.toInt32LEBytes() +
                services +
                timestamp.toInt64LEBytes() +
                toAddr.toBytes() +
                fromAddr.toBytes() +
                nonce.toInt64LEBytes() +
                ua.toVarBytes() +
                startHeight.toInt32LEBytes() +
                (if (version >= 70001) byteArrayOf(if (relay) 1 else 0) else ByteArray(0))
    }

    val isFullNode: Boolean = services[0] == 1.toByte()
    val canGetUTXO: Boolean = services[1] == 1.toByte()
    val canSetBloom: Boolean = services[2] == 1.toByte()
}