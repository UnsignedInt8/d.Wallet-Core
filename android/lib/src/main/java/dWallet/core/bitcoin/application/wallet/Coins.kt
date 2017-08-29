package dwallet.core.bitcoin.application.wallet

/**
 * Created by unsignedint8 on 8/25/17.
 */

enum class Coins : CoinType {

    Bitcoin {
        override val privateKeyId: ByteArray
            get() = byteArrayOf(0x80.toByte())

        override val scriptHashId: ByteArray
            get() = byteArrayOf(0x05)

        override val pubkeyHashId: ByteArray
            get() = byteArrayOf(0x00)

        override val hdCoinId: Int
            get() = 0
    },

    BitcoinTestnet {
        override val privateKeyId: ByteArray
            get() = byteArrayOf(0xef.toByte())

        override val scriptHashId: ByteArray
            get() = byteArrayOf(0xc4.toByte())

        override val pubkeyHashId: ByteArray
            get() = byteArrayOf(0x6f)

        override val hdCoinId: Int
            get() = 1

        override val xprvKeyId: ByteArray
            get() = byteArrayOf(0x04, 0x35, 0x83.toByte(), 0x94.toByte())

        override val xpubKeyId: ByteArray
            get() = byteArrayOf(0x04, 0x35, 0x87.toByte(), 0xCF.toByte())
    },

    Litecoin {
        override val privateKeyId: ByteArray
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

        override val scriptHashId: ByteArray
            get() = byteArrayOf(0x05)

        override val pubkeyHashId: ByteArray
            get() = byteArrayOf(0x30)

        override val hdCoinId: Int
            get() = 2
    },


}

