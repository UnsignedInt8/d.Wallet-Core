package io.github.unsignedint8.dwallet_core.bitcoin.protocol.structures

import io.github.unsignedint8.dwallet_core.extensions.*

/**
 * Created by unsignedint8 on 8/20/17.
 */

class MerkleBlock(version: Int, preBlockHash: String, merkleRootHash: String, timestamp: Int, bits: Int, nonce: Int, val totalTxs: Int, val hashes: List<String>, val flags: List<Byte>) :
        BlockHeader(version, preBlockHash, merkleRootHash, timestamp, bits, nonce) {

    companion object {
        fun fromBytes(data: ByteArray): MerkleBlock {
            val header = BlockHeader.fromBytes(data)

            val totalTxs = data.readInt32LE(80)
            val (hashes , size) = data.readVarListAndSize(84) { bytes ->  Pair(bytes.sliceArray(0, 32).toHashString(), 32) }

            val flags = data.sliceArray(84 + size.toInt()).readVarList { bytes -> Pair(bytes[0], 1) }

            return MerkleBlock(header.version, header.preBlockHash, header.merkleRootHash, header.timestamp, header.bits, header.nonce, totalTxs, hashes, flags)
        }

        const val text = "merkleblock"
    }

    override fun toBytes() = super.toBytes() + totalTxs.toInt32LEBytes() + hashes.size.toVarIntBytes() + hashes.reduce(ByteArray(0), { item, acc -> acc + item.hashToBytes() }) + flags.size.toVarIntBytes() + flags.toByteArray()
}