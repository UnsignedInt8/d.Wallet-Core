package io.github.unsignedint8.dwallet_core.bitcoin.protocol.messages

import io.github.unsignedint8.dwallet_core.bitcoin.protocol.structures.InventoryVector
import io.github.unsignedint8.dwallet_core.extensions.reduce
import io.github.unsignedint8.dwallet_core.extensions.toVarIntBytes

/**
 * Created by unsignedint8 on 8/19/17.
 */

class GetData(val items: List<InventoryVector>) {

    companion object {
        const val text = "getdata"
    }

    fun toBytes() = items.size.toVarIntBytes() + items.reduce(ByteArray(0), { item, acc -> acc + item.toBytes() })
}