package io.github.unsignedint8.dwallet_core.bitcoin.application

import io.github.unsignedint8.dwallet_core.crypto.*
import io.github.unsignedint8.dwallet_core.utils.BaseX

/**
 * Created by unsignedint8 on 8/22/17.
 */

class Address(val pubkey: ByteArray, val netId: ByteArray = Main.pubkeyHash) {

    companion object Network {

        object Main {
            val pubkeyHash = byteArrayOf(0x00)
            val scriptHash = byteArrayOf(0x05)
            val privateKey = byteArrayOf(0x80.toByte())
            val xpubKey = byteArrayOf(0x04, 0x88.toByte(), 0xB2.toByte(), 0x1E)
            val xprvKey = byteArrayOf(0x04, 0x88.toByte(), 0xAD.toByte(), 0xE4.toByte())
        }

        object Testnet {
            val pubkeyHash = byteArrayOf(0x6f)
            val scriptHash = byteArrayOf(0xc4.toByte())
            val privateKey = byteArrayOf(0xef.toByte())
            val tpubKey = byteArrayOf(0x04, 0x35, 0x87.toByte(), 0xCF.toByte())
            val tprvKey = byteArrayOf(0x04, 0x35, 0x83.toByte(), 0x94.toByte())
        }

        fun pubkeyHashToMultisignatureAddress(pubkeyHash: ByteArray, netId: ByteArray = Main.scriptHash) = pubkeyHashToBase58Checking(pubkeyHash, netId)

        private fun pubkeyHashToBase58Checking(pubkeyHash: ByteArray, netId: ByteArray): String {
            val ex = netId + pubkeyHash
            val checksum = hash256(ex).take(4).toByteArray()
            val addr = ex + checksum
            return BaseX.base58.encode(addr)
        }
    }

    override fun toString() = pubkeyHashToBase58Checking(hash160(pubkey), netId)
}