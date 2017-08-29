package dwallet.core.bitcoin.protocol.messages

import dwallet.core.bitcoin.protocol.structures.*
import dwallet.core.extensions.*

/**
 * Created by unsignedint8 on 8/16/17.
 */

class Addr(val addrs: List<NetworkAddress>) {

    companion object {
        fun fromBytes(bytes: ByteArray): Addr {
            val addrs = bytes.readVarList { NetworkAddress.fromBytes2(it) }
            return Addr(addrs)
        }

        val text = "addr"
        val getaddr = "getaddr"
    }

    val count = addrs.size
}