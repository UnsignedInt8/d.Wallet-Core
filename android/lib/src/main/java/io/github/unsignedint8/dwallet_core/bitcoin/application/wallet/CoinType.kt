package io.github.unsignedint8.dwallet_core.bitcoin.application.wallet

/**
 * Created by unsignedint8 on 8/26/17.
 */

interface CoinType {

    val hdCoinId: Int

    val pubkeyHashId: ByteArray
    val scriptHashId: ByteArray
    val privateKeyId: ByteArray

    val xpubKeyId: ByteArray
        get() = byteArrayOf(0x04, 0x88.toByte(), 0xB2.toByte(), 0x1E.toByte())

    val xprvKeyId: ByteArray
        get() = byteArrayOf(0x04, 0x88.toByte(), 0xAD.toByte(), 0xE4.toByte())
}