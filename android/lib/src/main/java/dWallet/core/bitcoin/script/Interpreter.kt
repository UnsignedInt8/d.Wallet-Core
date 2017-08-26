package dWallet.core.bitcoin.script

import dWallet.core.extensions.*

/**
 * Created by unsignedint8 on 8/23/17.
 */

class Interpreter {

    companion object {

        private val disabledCodes = Words.Disabled.Arithmetic.values().map { it.raw } + Words.Disabled.Bitwise.values().map { it.raw } + Words.Disabled.Pseudo.values().map { it.raw } + Words.Disabled.Reserved.values().map { it.raw } + Words.Disabled.Splice.values().map { it.raw }

        fun checkValidity(opcode: Byte) = !disabledCodes.any { it == opcode }

        fun checkOpsValidity(opcodes: List<Byte>) = opcodes.all { checkValidity(it) }

        fun scriptToOps(data: ByteArray): List<Pair<Byte, ByteArray?>> {
            var offset = 0
            val ops = mutableListOf<Pair<Byte, ByteArray?>>()

            while (offset < data.size) {
                val (opcode, operand, length) = parse(data, offset)
                ops.add(Pair(opcode, operand))
                offset += length
            }

            return ops
        }

        fun isP2PKHOutScript(opcodes: List<Byte>) = arrayOf(Words.Stack.OP_DUP.raw, Words.Crypto.OP_HASH160.raw, 20.toByte(), Words.Bitwise.OP_EQUALVERIFY.raw, Words.Crypto.OP_CHECKSIG.raw).contentEquals(opcodes.toTypedArray())

        fun isP2SHOutScript(opcodes: List<Byte>) = arrayOf(Words.Crypto.OP_HASH160.raw, 20.toByte(), Words.Bitwise.OP_EQUAL.raw).contentEquals(opcodes.toTypedArray())

        private fun parse(data: ByteArray, offset: Int = 0): Triple<Byte, ByteArray?, Int> {
            val opcode = data[offset]
            var operand: ByteArray? = null
            var totalLength = 1
            var offset = offset

            when (opcode) {

                in Words.Constants.OP_2.raw..Words.Constants.OP_16.raw -> {
                    operand = byteArrayOf((opcode - Words.Constants.OP_2.raw + 2.toByte()).toByte())
                }

                in Words.Constants.NA_LOW.raw..Words.Constants.NA_HIGH.raw -> {
                    offset += 1
                    operand = data.sliceArray(offset, offset + opcode)
                    totalLength += opcode.toInt()
                }

                in Words.Constants.NA_LOW.raw..Words.Constants.NA_HIGH.raw -> {
                    offset += 1
                    operand = data.sliceArray(offset, offset + opcode.toInt())
                    totalLength += opcode.toInt()
                }

                Words.Constants.OP_PUSHDATA1.raw -> {
                    val dataLength = data[offset + 1].toInt()
                    offset += 2
                    operand = data.sliceArray(offset, offset + dataLength)
                    totalLength += 1 + dataLength
                }

                Words.Constants.OP_PUSHDATA2.raw -> {
                    val dataLength = data.readInt16LE(offset + 1).toInt()
                    offset += 3
                    operand = data.sliceArray(offset, offset + dataLength)
                    totalLength += 2 + dataLength
                }

                Words.Constants.OP_PUSHDATA4.raw -> {
                    val dataLength = data.readInt32LE(offset + 1)
                    offset += 5
                    operand = data.sliceArray(offset, offset + dataLength)
                    totalLength += 4 + dataLength
                }
            }

            return Triple(opcode, operand, totalLength)
        }
    }
}