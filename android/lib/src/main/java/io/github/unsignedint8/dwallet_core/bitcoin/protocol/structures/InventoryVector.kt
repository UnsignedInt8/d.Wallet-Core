package io.github.unsignedint8.dwallet_core.bitcoin.protocol.structures

import io.github.unsignedint8.dwallet_core.extensions.*

/**
 * Created by unsignedint8 on 8/16/17.
 */

class InventoryVector(val type: InvTypes, val hash: String) {

    companion object {
        fun fromBytes(bytes: ByteArray): InventoryVector {
            val type = InvTypes.values().firstOrNull { it.value == bytes.readInt32LE() } ?: InvTypes.ERROR
            val hash = String(bytes.sliceArray(4))
            return InventoryVector(type, hash)
        }

        const val standardSize = 36
        const val inv = "inv"
    }

    fun toBytes() {
        type.value.toInt32LEBytes()
    }
}
