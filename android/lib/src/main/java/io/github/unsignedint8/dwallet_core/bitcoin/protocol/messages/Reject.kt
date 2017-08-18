package io.github.unsignedint8.dwallet_core.bitcoin.protocol.messages

import io.github.unsignedint8.dwallet_core.extensions.*

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
    }

    override fun toString(): String {
        return "Reject: $message $reason $code"
    }
}