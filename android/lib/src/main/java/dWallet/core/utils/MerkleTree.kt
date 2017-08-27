package dWallet.core.utils

import dWallet.core.crypto.hash256
import dWallet.core.extensions.hashToBytes

/**
 * Created by unsignedint8 on 8/27/17.
 */

class MerkleTree {

    companion object {

        fun generateRoot(hashes: List<String>): ByteArray? {
            return fromByteArray(hashes.map { it.hashToBytes() })
        }

        private fun fromByteArray(hashes: List<ByteArray>): ByteArray? {
            if (hashes.size == 1) return hashes.first()

            val items = mutableListOf<ByteArray>()
            hashes.forEach { items.add(it) }
            if (hashes.size % 2 != 0) items.add(hashes.last())

            val concatHashes = mutableListOf<ByteArray>()
            (0 until items.size step 2).mapTo(concatHashes) { items[it] + items[it + 1] }

            val upperLevel = concatHashes.map { hash256(it) }

            return if (upperLevel.size > 1) {
                fromByteArray(upperLevel)
            } else {
                upperLevel.firstOrNull()
            }
        }

    }
}