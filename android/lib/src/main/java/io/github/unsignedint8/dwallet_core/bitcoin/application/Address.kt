package io.github.unsignedint8.dwallet_core.bitcoin.application

import io.github.unsignedint8.dwallet_core.crypto.*
import io.github.unsignedint8.dwallet_core.utils.BaseX

/**
 * Created by unsignedint8 on 8/22/17.
 */

class Address(val pubkey: ByteArray, val netId: ByteArray = BTC.Main.pubkeyHash) {

    companion object Network {

        fun pubkeyHashToMultisignatureAddress(pubkeyHash: ByteArray, netId: ByteArray = BTC.Main.scriptHash) = pubkeyHashToBase58Checking(pubkeyHash, netId)

        private fun pubkeyHashToBase58Checking(pubkeyHash: ByteArray, netId: ByteArray): String {
            val ex = netId + pubkeyHash
            val checksum = hash256(ex).take(4).toByteArray()
            val addr = ex + checksum
            return BaseX.base58.encode(addr)
        }

        fun validate(address: String): Boolean {
            if (address.length !in 26..35) return false
            val decoded = BaseX.base58.decode(address)
            val hash = sha256(decoded, 0, 21, 2)
            return hash.sliceArray(0..3).contentEquals(decoded.sliceArray(21..24))
        }

        object BTC {

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

        }

        object LTC {

            object  Main {
                val pubkeyHash = byteArrayOf(0x30)
                val scriptHash = byteArrayOf(0x05)
            }

        }
    }

    override fun toString() = pubkeyHashToBase58Checking(hash160(pubkey), netId)
}