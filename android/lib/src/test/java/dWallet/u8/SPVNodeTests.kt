package dWallet.u8

import dWallet.core.bitcoin.application.spv.Network
import dWallet.core.bitcoin.application.spv.SPVNode
import dWallet.core.bitcoin.application.wallet.Wallet
import dWallet.core.crypto.Crypto
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test

/**
 * Created by unsignedint8 on 8/27/17.
 */

class SPVNodeTests {

    init {
        Crypto.setupCryptoProvider()
    }

    @Test
    fun testConnection() = runBlocking {

        val w = Wallet.fromMnemonic("parade skill social future veteran cigar chef bleak federal benefit steel such car air embark music solid adult setup walk leader engage filter spider") // Wallet.create().first
        val spv = SPVNode(Network.BitcoinTestnet, w)
        spv.connectAsync("localhost", 19000).await()

        delay(30*1000)
    }
}