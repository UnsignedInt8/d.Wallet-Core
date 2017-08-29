package dwallet.u8

import dwallet.core.bitcoin.application.wallet.Address
import dwallet.core.bitcoin.application.spv.Network
import dwallet.core.bitcoin.application.spv.SPVNode
import dwallet.core.bitcoin.application.wallet.Coins
import dwallet.core.bitcoin.application.wallet.Wallet
import dwallet.core.bitcoin.script.Interpreter
import dwallet.core.crypto.Crypto
import dwallet.core.extensions.format
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
        //        w.insertWIF("cQqABdMtNTk894GxuAJWJfF2S7Ln31LnzkUsvLiCznLaSEvkwR9y") //mwT5FhANpkurDKBVXVyAH1b6T3rz9T1owr
//        w.insertWIF("cTCjVfRp8oAXX7RGyszhghBhSatTD1mEgtiyeBsrkB6qcuw4PrE4") //miwTG5Wt5t188CXh8oT3bTjGvbzS6kcgqT
//        w.insertWIF("cTf3uEpv9UuKLLmvvR3Zr2riiJ53FjjsUJZ35CWfT5ehiyc78uoW") // test wallet no.3 mnpbqSLQ3r293VHSjN82Ht63zf3PD8gBmm

        Wallet.changeKeysAmount = 200
        Wallet.externalKeysAmount = 200
        val w = Wallet.fromMnemonic("parade skill social future veteran cigar chef bleak federal benefit steel such car air embark music solid adult setup walk leader engage filter spider") // Wallet.create().first

        w.insertWIF("cQarDhhBPExGHBb34rX1SYgcPTchCNPnamagXyUGpsnWFS1cmM5e") // test wallet no.4 mmqZxGUW1sK2kYSsijC5jy85tqocCs8onj
        w.insertWIF("cUAXYdADW7ipvPu17MRuanwMJFTdu4Xeqk5i6BF3cbbNB58H57if") // test wallet no.4 change address mk9So7AzhuMhR8CCL2WbPTzBVPH32i63GH
        println(Address(w.importedPrivKeys[0].public!!, Coins.BitcoinTestnet.pubkeyHashId))

        w.onBalanceChanged { _, balance -> println("balance: ${balance}") }
        w.onUtxoAdded { _, utxo -> println("new utxo: ${utxo.id}") }
        w.onUtxosRemoved { _, utxos -> println("utxos removed: ${utxos.map { it.id }}") }

        val spv = SPVNode(Network.BitcoinTestnet, w.dumpKeysToFilterItems())
        spv.onTx { _, tx ->
            println(tx.id)
            w.insertUtxo(tx)
            println(tx.txOuts.map { Address.pubkeyHashToBase58Checking(Interpreter.scriptToOps(it.pubkeyScript)[2].second!!, Coins.BitcoinTestnet.pubkeyHashId) })
        }
        spv.onMerkleblock { _, merkleblock -> println("progress: ${spv.progress.format(2)}") }
        spv.onAddr { _, addr -> println(addr) }

        if (!spv.connectAsync("localhost", 19000)) {
            println("not connected")
            return@runBlocking
        }

        delay(30 * 1000)
    }

}