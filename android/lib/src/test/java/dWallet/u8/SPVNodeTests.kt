package dWallet.u8

import dWallet.core.bitcoin.application.spv.Network
import dWallet.core.bitcoin.application.spv.SPVNode
import dWallet.core.bitcoin.application.wallet.Wallet
import dWallet.core.bitcoin.p2p.Node
import dWallet.core.crypto.Crypto
import dWallet.core.extensions.format
import dWallet.core.infrastructure.SocketEx
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
    fun testSync() = runBlocking {
//        Wallet.changeKeysAmount = 2
//        Wallet.externalKeysAmount = 1
        val w = Wallet.fromMnemonic("parade skill social future veteran cigar chef bleak federal benefit steel such car air embark music solid adult setup walk leader engage filter spider") // Wallet.create().first
        w.insertWIF("cQqABdMtNTk894GxuAJWJfF2S7Ln31LnzkUsvLiCznLaSEvkwR9y") //mwT5FhANpkurDKBVXVyAH1b6T3rz9T1owr
        w.insertWIF("cTCjVfRp8oAXX7RGyszhghBhSatTD1mEgtiyeBsrkB6qcuw4PrE4") //miwTG5Wt5t188CXh8oT3bTjGvbzS6kcgqT
        w.onBalanceChanged { _, balance -> println("balance: ${balance}") }

        val spv = SPVNode(Network.BitcoinTestnet, w)
        spv.onTx { _, tx -> println("progress: ${spv.progress.format(2)}") }
        spv.onMerkleblock { _, merkleblock -> println("progress: ${spv.progress.format(2)}") }
        spv.onAddr { _, addr -> println(addr) }

        if (!spv.connectAsync("localhost", 19000)) {
            println("not connected")
            return@runBlocking
        }

        delay(20 * 1000)
    }

}