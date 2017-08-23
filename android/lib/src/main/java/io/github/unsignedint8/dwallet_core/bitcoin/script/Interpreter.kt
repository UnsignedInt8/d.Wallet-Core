package io.github.unsignedint8.dwallet_core.bitcoin.script

import io.github.unsignedint8.dwallet_core.extensions.*

/**
 * Created by unsignedint8 on 8/23/17.
 */

class Interpreter {

    companion object {

        private val disabledCodes = Words.Disabled.Arithmetic.values().map { it.raw } + Words.Disabled.Bitwise.values().map { it.raw } + Words.Disabled.Pseudo.values().map { it.raw } + Words.Disabled.Reserved.values().map { it.raw } + Words.Disabled.Splice.values().map { it.raw }

        fun checkValidity(opcode: Byte) = !disabledCodes.any { it == opcode }

        fun parse(data: ByteArray): Triple<Byte, ByteArray?, Int> {
            val opcode = data.first()
            var operand: ByteArray? = null
            var totalLength = 1

            when (opcode) {

                in Words.Constants.OP_2.raw..Words.Constants.OP_16.raw -> {
                    operand = byteArrayOf((opcode - Words.Constants.OP_2.raw + 2.toByte()).toByte())
                }

                in Words.Constants.NA_LOW.raw..Words.Constants.NA_HIGH.raw -> {
                    operand = data.sliceArray(1, opcode.toInt())
                    totalLength += opcode.toInt()
                }

                Words.Constants.OP_PUSHDATA1.raw -> {
                    val dataLength = data[1].toInt()
                    operand = data.sliceArray(2, 2 + dataLength)
                    totalLength += 1 + dataLength
                }

                Words.Constants.OP_PUSHDATA2.raw -> {
                    val dataLength = data.readInt16LE(1).toInt()
                    operand = data.sliceArray(3, 3 + dataLength)
                    totalLength += 2 + dataLength
                }

                Words.Constants.OP_PUSHDATA4.raw -> {
                    val dataLength = data.readInt32LE(1)
                    operand = data.sliceArray(5, 5 + dataLength)
                    totalLength += 4 + dataLength
                }
            }

            return Triple(opcode, operand, totalLength)
        }
    }
}