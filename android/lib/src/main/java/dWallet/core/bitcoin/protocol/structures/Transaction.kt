package dWallet.core.bitcoin.protocol.structures

import dWallet.core.crypto.*
import dWallet.core.extensions.*

/**
 * Created by unsignedint8 on 8/20/17.
 */

class Transaction(val version: Int, val txIns: List<TxIn>, val txOuts: List<TxOut>, val lockTime: Int) {

    private constructor(version: Int, txIns: List<TxIn>, txOuts: List<TxOut>, lockTime: Int, hash: String) : this(version, txIns, txOuts, lockTime) {
        id = hash
    }

    lateinit var id: String

    companion object {

        fun fromBytes(data: ByteArray): Transaction = fromBytes2(data).first

        fun fromBytes2(data: ByteArray): Pair<Transaction, Int> {

            val version = data.readInt32LE()
            val (txIns, txInsLength) = data.readVarListAndSize(4) { bytes ->
                val (txIn, len) = TxIn.fromBytes(bytes)
                Pair(txIn, len)
            }

            val (txOuts, txOutsLength) = data.readVarListAndSize(4 + txInsLength.toInt()) { bytes ->
                val (txOut, len) = TxOut.fromBytes(bytes)
                Pair(txOut, len)
            }

            val lockTime = data.readInt32LE((4 + txInsLength + txOutsLength).toInt())

            return Pair(Transaction(version, txIns, txOuts, lockTime, hash256(data).toHashString()), (4 + txInsLength + txOutsLength + 4).toInt())
        }

        const val message = "tx"
    }

    fun toBytes() = version.toInt32LEBytes() + txIns.size.toVarIntBytes() + txIns.reduce(ByteArray(0), { item, acc -> acc + item.toBytes() }) + txOuts.size.toVarIntBytes() + txOuts.reduce(ByteArray(0), { item, acc -> acc + item.toBytes() }) + lockTime.toInt32LEBytes()

    class TxIn(val prevOutput: OutputPoint, val signatureScript: ByteArray, val sequence: Int) {

        class OutputPoint(val referencedTxHash: String, val index: Int) {

            companion object {
                fun fromBytes(data: ByteArray): OutputPoint {
                    val refTxHash = data.sliceArray(0, 32).toHashString()
                    val index = data.readInt32LE(32)
                    return OutputPoint(refTxHash, index)
                }
            }

            fun toBytes() = referencedTxHash.hashToBytes() + index.toInt32LEBytes()
        }

        companion object {

            fun fromBytes(data: ByteArray): Pair<TxIn, Int> {
                val outPoint = OutputPoint.fromBytes(data)

                val (scriptLength, varIntSize) = data.readVarIntValueSize(36)
                val signatureScript = data.sliceArray(36 + varIntSize.toInt(), (36 + varIntSize + scriptLength).toInt())

                val seq = data.readInt32LE((36 + varIntSize + scriptLength).toInt())

                val length = (36 + varIntSize + scriptLength + 4).toInt()

                return Pair(TxIn(outPoint, signatureScript, seq), length)
            }
        }

        fun toBytes() = prevOutput.toBytes() + signatureScript.size.toVarIntBytes() + signatureScript + sequence.toInt32LEBytes()

        val txId: String
            get() = prevOutput.referencedTxHash

        val vout: Int
            get() = prevOutput.index
    }

    class TxOut(val value: Long, val pubkeyScript: ByteArray) {

        companion object {

            fun fromBytes(data: ByteArray): Pair<TxOut, Int> {
                val value = data.readInt64LE()
                val (scriptLength, varIntSize) = data.readVarIntValueSize(8)
                val offset = (8 + varIntSize).toInt()
                val len = (offset + scriptLength).toInt()
                val pubkeyScript = data.sliceArray(offset, len)

                return Pair(TxOut(value, pubkeyScript), len)
            }
        }

        fun toBytes() = value.toInt64LEBytes() + pubkeyScript.size.toVarIntBytes() + pubkeyScript
    }
}