package io.github.unsignedint8.dwallet_core.bitcoin.protocol.structures

import io.github.unsignedint8.dwallet_core.extensions.*

/**
 * Created by unsignedint8 on 8/18/17.
 */

open class BlockHeader(val version: Int, val preBlockHash: String, val merkleRootHash: String, val timestamp: Int, val bits: Int, val nonce: Int) {

    companion object {
        fun fromBytes(data: ByteArray): BlockHeader {
            val ver = data.readInt32LE()
            val preBlockHash = data.sliceArray(4, 36).toHashString()
            val merkleRoot = data.sliceArray(36, 68).toHashString()
            val timestamp = data.readInt32LE(68)
            val bits = data.readInt32LE(72)
            val nonce = data.readInt32LE(76)

            return BlockHeader(ver, preBlockHash, merkleRoot, timestamp, bits, nonce)
        }

        const val naturalSize = 80
        const val standardSize = 81
    }

    fun toBytes() = version.toInt32LEBytes() + preBlockHash.hashToBytes() + merkleRootHash.hashToBytes() + timestamp.toInt32LEBytes() + bits.toInt32LEBytes() + nonce.toInt32LEBytes()
}