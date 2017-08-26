package io.github.unsignedint8.dwallet_core.u8

import io.github.unsignedint8.dwallet_core.bitcoin.application.wallet.Wallet
import io.github.unsignedint8.dwallet_core.bitcoin.protocol.structures.Transaction
import org.junit.Test

/**
 * Created by unsignedint8 on 8/26/17.
 */

class WalletTests {

    private val wallet: Wallet

    init {
        Wallet.changeKeysAmount = 10
        Wallet.externalKeysAmount = 10
        wallet = Wallet.fromMnemonic("parade skill social future veteran cigar chef bleak federal benefit steel such car air embark music solid adult setup walk leader engage filter spider")
//        println(wallet.)
    }

    @Test
    fun testIsValid() {
//        val tx = Transaction.fromBytes("".toByteArray())
//        wallet.isIncomeTx(tx)
    }
}