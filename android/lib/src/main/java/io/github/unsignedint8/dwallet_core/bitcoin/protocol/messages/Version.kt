package io.github.unsignedint8.dwallet_core.bitcoin.protocol.messages

import io.github.unsignedint8.dwallet_core.bitcoin.protocol.*
import io.github.unsignedint8.dwallet_core.extensions.toInt32LEBytes
import io.github.unsignedint8.dwallet_core.extensions.toInt64LEBytes
import io.github.unsignedint8.dwallet_core.extensions.toVarBytes

/**
 * Created by unsignedint8 on 8/15/17.
 */

class Version(val version: Int, val services: ByteArray, val timestamp: Long, val toAddr: NetworkAddress, val fromAddr: NetworkAddress, val nonce: Long, val ua: String, val startHeight: Int, val relay: Boolean = false) {

    fun toBytes() {
        version.toInt32LEBytes()
        services
        timestamp.toInt64LEBytes()
        toAddr.toBytes()
        fromAddr.toBytes()
        nonce.toInt64LEBytes()
        ua.toVarBytes()
    }

    val isFullNode: Boolean = services[0] == 1.toByte()
    val canGetUTXO: Boolean = services[1] == 1.toByte()
    val canSetBloom: Boolean = services[2] == 1.toByte()
}