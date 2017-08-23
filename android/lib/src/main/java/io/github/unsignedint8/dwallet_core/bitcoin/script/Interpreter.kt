package io.github.unsignedint8.dwallet_core.bitcoin.script

import io.github.unsignedint8.dwallet_core.extensions.*

/**
 * Created by unsignedint8 on 8/23/17.
 */

class Interpreter {

    companion object {

        private val disabledCodes = Words.Disabled.Arithmetic.values().map { it.raw } + Words.Disabled.Bitwise.values().map { it.raw } + Words.Disabled.Pseudo.values().map { it.raw } + Words.Disabled.Reserved.values().map { it.raw } + Words.Disabled.Splice.values().map { it.raw }

        fun checkValidity(opcode: Byte) = !disabledCodes.any { it == opcode }

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

        fun parse(data: ByteArray, offset: Int = 0): Triple<Byte, ByteArray?, Int> {
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