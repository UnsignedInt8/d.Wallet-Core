package io.github.unsignedint8.dwallet_core.bitcoin.protocol.structures

import io.github.unsignedint8.dwallet_core.extensions.*

/**
 * Created by unsignedint8 on 8/20/17.
 */

class Transaction(val version: Int, val txIns: List<TxIn>, val txOuts: List<TxOut>, val lockTime: Int) {

    companion object {

        fun fromBytes(data: ByteArray) {
            val version = data.readInt32LE()

        }

        const val message = "tx"
    }

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

            fun fromBytes(data: ByteArray): TxIn {
                val outPoint = OutputPoint.fromBytes(data)

                val (scriptLength, varIntSize) = data.readVarIntValueSize(36)
                val signatureScript = data.sliceArray(36 + varIntSize.toInt(), (36 + varIntSize + scriptLength).toInt())

                val seq = data.readInt32LE((36 + varIntSize + scriptLength).toInt())

                return TxIn(outPoint, signatureScript, seq)
            }
        }

        fun toBytes() = prevOutput.toBytes() + signatureScript.size.toVarIntBytes() + signatureScript + sequence.toInt32LEBytes()
    }

    class TxOut(val value: Long, val pubkeyScript: ByteArray) {

        companion object {

            fun fromBytes(data: ByteArray): TxOut {
                val value = data.readInt64LE()
                val (scriptLength, varIntSize) = data.readVarIntValueSize(8)
                val pubkeyScript = data.sliceArray((8 + scriptLength + varIntSize).toInt())

                return TxOut(value, pubkeyScript)
            }
        }

        fun toBytes() = value.toInt64LEBytes() + pubkeyScript.size.toVarIntBytes() + pubkeyScript
    }
}