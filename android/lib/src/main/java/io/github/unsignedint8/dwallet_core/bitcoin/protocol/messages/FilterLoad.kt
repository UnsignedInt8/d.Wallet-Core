package io.github.unsignedint8.dwallet_core.bitcoin.protocol.messages

import io.github.unsignedint8.dwallet_core.extensions.*

/**
 * Created by unsignedint8 on 8/18/17.
 */

class FilterLoad(val filter: ByteArray, val nHashFuncs: Int, val nTweak: Int, val nFlags: Int) {
    companion object {
        const val text = "filterload"
        const val filterclear = "filterclear"
    }

    fun toBytes() = filter.size.toVarIntBytes() + filter + nHashFuncs.toInt32LEBytes() + nTweak.toInt32LEBytes() + nFlags.toByte()
}