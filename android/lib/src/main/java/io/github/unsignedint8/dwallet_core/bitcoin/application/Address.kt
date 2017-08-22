package io.github.unsignedint8.dwallet_core.bitcoin.application

import io.github.unsignedint8.dwallet_core.crypto.*
import io.github.unsignedint8.dwallet_core.extensions.*

/**
 * Created by unsignedint8 on 8/22/17.
 */

class Address(val pubkey: ByteArray, val netId: ByteArray = main) {

    companion object Network {
        val main = byteArrayOf(0, 0)
    }

    fun toAddress() {
        val ripemd160Ex = netId + hash160(pubkey)
        val checksum = hash256(ripemd160Ex).take(4).toByteArray()
        val address = ripemd160Ex + checksum
    }
}