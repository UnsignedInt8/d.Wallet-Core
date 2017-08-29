package dwallet.core.bitcoin.protocol.structures

import dwallet.core.extensions.*

/**
 * Created by unsignedint8 on 8/16/17.
 */

class InventoryVector(var type: InvTypes, val hash: String) {

    companion object {
        fun fromBytes(bytes: ByteArray): InventoryVector {
            val type = InvTypes.values().firstOrNull { it.value == bytes.readInt32LE() } ?: InvTypes.ERROR
            val hash = bytes.sliceArray(4, standardSize).toHashString()
            return InventoryVector(type, hash)
        }

        const val standardSize = 36
        const val inv = "inv"
    }

    fun toBytes() = type.value.toInt32LEBytes() + hash.hashToBytes()
}
