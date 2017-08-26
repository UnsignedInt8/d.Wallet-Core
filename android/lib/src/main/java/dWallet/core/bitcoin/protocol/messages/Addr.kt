package dWallet.core.bitcoin.protocol.messages

import dWallet.core.bitcoin.protocol.structures.*
import dWallet.core.extensions.*

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
    }

    val count = addrs.size
}