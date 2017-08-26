package dWallet.core.bitcoin.protocol.messages

import dWallet.core.extensions.*

/**
 * Created by unsignedint8 on 8/18/17.
 */

class Reject(val message: String, val code: Byte, val reason: String) {

    companion object {
        fun fromBytes(data: ByteArray): Reject {
            val (msg, offset, len) = data.readVarStringComponents()
            val code = data[(offset + len).toInt()]
            val reason = data.sliceArray((offset + len + 1).toInt()).readVarString()
            return Reject(msg, code, reason)
        }

        const val text = "reject"

        const val REJECT_MALFORMED = 0x01.toByte()
        const val REJECT_INVALID = 0x10.toByte()
        const val REJECT_OBSOLETE = 0x11.toByte()
        const val REJECT_DUPLICATE = 0x12.toByte()
        const val REJECT_NONSTANDARD = 0x40.toByte()
        const val REJECT_DUST = 0x41.toByte()
        const val REJECT_INSUFFICIENTFEE = 0x42.toByte()
        const val REJECT_CHECKPOINT = 0x43.toByte()
    }

    override fun toString(): String {
        return "Reject: $message $reason $code"
    }
}