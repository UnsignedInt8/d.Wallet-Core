package dwallet.core.bitcoin.application.spv

import dwallet.core.bitcoin.protocol.structures.Message
import dwallet.core.extensions.toInt32LEBytes

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