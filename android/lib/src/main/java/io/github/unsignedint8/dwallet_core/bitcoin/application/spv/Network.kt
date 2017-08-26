package io.github.unsignedint8.dwallet_core.bitcoin.application.spv

import io.github.unsignedint8.dwallet_core.bitcoin.protocol.structures.Message
import io.github.unsignedint8.dwallet_core.extensions.toInt32LEBytes

/**
 * Created by unsignedint8 on 8/26/17.
 */

enum class Network : NetworkType {

    BitcoinMain {
        override val magic = Message.Magic.Bitcoin.main.toInt32LEBytes()
    },

    BitcoinTestnet {
        override val magic = Message.Magic.Bitcoin.testnet.toInt32LEBytes()
    },

    Litecoin {
        override val magic = Message.Magic.Litecoin.main.toInt32LEBytes()
    },
}