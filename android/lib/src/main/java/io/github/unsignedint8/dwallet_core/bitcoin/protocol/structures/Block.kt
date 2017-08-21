package io.github.unsignedint8.dwallet_core.bitcoin.protocol.structures

import io.github.unsignedint8.dwallet_core.extensions.*

/**
 * Created by unsignedint8 on 8/21/17.
 */

class Block(version: Int, preBlockHash: String, merkleRootHash: String, timestamp: Int, bits: Int, nonce: Int, val txs: List<Transaction>) : BlockHeader(version, preBlockHash, merkleRootHash, timestamp, bits, nonce) {

    companion object {

        fun fromBytes(data: ByteArray): Block {
            val header = BlockHeader.fromBytes(data)
            val txs = data.sliceArray(80).readVarList { bytes -> Transaction.fromBytes2(bytes) }

            return Block(header.version, header.preBlockHash, header.merkleRootHash, header.timestamp, header.bits, header.nonce, txs)
        }
    }
}