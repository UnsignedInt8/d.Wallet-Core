package dwallet.core.bitcoin.protocol.messages

import dwallet.core.bitcoin.protocol.structures.InventoryVector
import dwallet.core.extensions.*

/**
 * Created by unsignedint8 on 8/19/17.
 */

class GetData(val items: List<InventoryVector>) {

    companion object {
        const val text = "getdata"
    }

    fun toBytes() = items.size.toVarIntBytes() + items.reduce(ByteArray(0), { item, acc -> acc + item.toBytes() })
}