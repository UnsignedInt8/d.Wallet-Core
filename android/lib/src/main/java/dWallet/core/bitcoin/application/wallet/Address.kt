package dwallet.core.bitcoin.application.wallet

import dwallet.core.crypto.*
import dwallet.core.extensions.sliceArray
import dwallet.core.utils.BaseX

/**
 * Created by unsignedint8 on 8/22/17.
 */

class Address(val pubkey: ByteArray, val netId: ByteArray = Coins.Bitcoin.pubkeyHashId) {

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

        fun stringToPubkeyHash(address: String): ByteArray {
            val decoded = BaseX.base58.decode(address)
            return decoded.sliceArray(4, 24)
        }
    }

    override fun toString() = pubkeyHashToBase58Checking(hash160(pubkey), netId)

    val pubkeyHash by lazy { hash160(pubkey) }
}