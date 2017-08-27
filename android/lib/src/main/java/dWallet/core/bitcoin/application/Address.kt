package dWallet.core.bitcoin.application

import dWallet.core.crypto.*
import dWallet.core.utils.BaseX

/**
 * Created by unsignedint8 on 8/22/17.
 */

class Address(val pubkey: ByteArray, val netId: ByteArray) {

    companion object {

        fun pubkeyHashToMultisignatureAddress(pubkeyHash: ByteArray, netId: ByteArray) = pubkeyHashToBase58Checking(pubkeyHash, netId)

        fun pubkeyHashToBase58Checking(pubkeyHash: ByteArray, netId: ByteArray): String {
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

        fun fromString(address: String) {

        }
    }

    override fun toString() = pubkeyHashToBase58Checking(hash160(pubkey), netId)

    val pubkeyHash by lazy { hash160(pubkey) }
}