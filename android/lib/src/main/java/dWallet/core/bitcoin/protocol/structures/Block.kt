package dWallet.core.bitcoin.protocol.structures

import dWallet.core.extensions.*
import dWallet.core.utils.MerkleTree

/**
 * Created by unsignedint8 on 8/21/17.
 */

class Block(version: Int, preBlockHash: String, merkleRootHash: String, timestamp: Int, bits: Int, nonce: Int, val txs: List<Transaction>, override var hash: String) : BlockHeader(version, preBlockHash, merkleRootHash, timestamp, bits, nonce) {

    companion object {

        fun fromBytes(data: ByteArray): Block {
            val header = BlockHeader.fromBytes(data)
            val txs = data.sliceArray(80).readVarList { bytes -> Transaction.fromBytes2(bytes) }

            return Block(header.version, header.preBlockHash, header.merkleRootHash, header.timestamp, header.bits, header.nonce, txs, header.hash!!)
        }

        const val message = "block"
    }

    override fun toBytes() = super.toBytes() + txs.size.toVarIntBytes() + txs.reduce(ByteArray(0), { item, acc -> acc + item.toBytes() })

    fun isValidMerkleRoot() = MerkleTree.generateRoot(txs.map { it.id }) == merkleRootHash
}