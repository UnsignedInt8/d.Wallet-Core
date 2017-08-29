package dwallet.u8

import dwallet.core.bitcoin.p2p.Node
import dwallet.core.bitcoin.protocol.messages.Version
import dwallet.core.bitcoin.protocol.structures.*
import dwallet.core.extensions.hexToByteArray
import dwallet.core.extensions.toInt32LEBytes
import dwallet.core.infrastructure.SocketEx
import dwallet.core.utils.BloomFilter3
import dwallet.core.utils.MerkleTree
import kotlinx.coroutines.experimental.*
import org.junit.Test

/**
 * Created by unsignedint8 on 8/16/17.
 */

class PeerTests {
    @Test
    fun testVersion() = runBlocking {
        val host = "localhost"

        val s = SocketEx()
        if (!s.connectAsync(host, 19000).await()) return@runBlocking

        val service = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)

        val ver3 = Version(services = service, toAddr = NetworkAddress("::0", 0, addTimestamp = false), fromAddr = NetworkAddress("::0", 0, services = service, addTimestamp = false), nonce = 7284544412836900411, ua = "/zz/", startHeight = 1)
//        println(ver3.toBytes().toHexString())
        val m3 = Message(Message.Magic.Bitcoin.testnet, "version", ver3.toBytes())
//        println(m3.toBytes().toHexString())

        s.writeAsync(m3.toBytes()).await()
        val data = s.readAsync().await()

        val m4 = Message.fromBytes(data!!)
        val v4 = Version.fromBytes(m4.payload)
        assert(v4.ua.contains("satoshi", ignoreCase = true))
    }

    @Test
    fun testNodeVersion() {
        var gotcount = 0
        val host = "localhost"// "120.77.42.241"
        val port = 19000
        val magic = Message.Magic.Bitcoin.regtest.toInt32LEBytes()

        val node = Node(Message.Magic.Bitcoin.main.toInt32LEBytes())
        node.magic = magic
        node.initBloomFilter(listOf("bc7662ecd3c4e0024d00e8647fb9ff6539a7b379".hexToByteArray()),
                0.0001, nFlags = BloomFilter3.BloomUpdate.UPDATE_ALL)

        node.onHeaders { _, headers ->
            println(headers.size)
            println(headers.first().preBlockHash)

            if (gotcount++ == 2) return@onHeaders

            node.sendGetHeaders(listOf(headers.last().preBlockHash))
        }

        node.onInv { _, invs ->
            println("inv ${invs.size} ${invs.all { it.type == InvTypes.MSG_BLOCK }}")
            println(invs.first().hash)
            node.sendGetMerkleblocks(invs.map { it.hash })
//            node.sendGetData(invs.takeLast(5))
        }

        node.onTx { sender, tx -> println(tx.id) }

        node.onReject { _, reject -> println("${reject.message} ${reject.reason}") }

        node.onVerack { _, _ ->
            node.sendGetBlocks()
        }

        node.onBlock { sender, block ->

            if (!block.isValidMerkleRoot()) {
                println("block " + block.hash + " " + block.isValidMerkleRoot() + " " + block.txs.size)
                println(block.merkleRootHash)
                println(MerkleTree.generateRootHash(block.txs.map { it.id }))
                println(block.txs.map { it.id })
            }
        }

        node.onMerkleblock { _, block ->
            if (block.flags.isNotEmpty()) println("flags: " + block.flags)
            println(block.preBlockHash + " ")
        }

        async(CommonPool) {
            node.connectAsync(host, port).await()
            println("socket port: ${node.localPort}")
        }

        runBlocking { delay(5 * 1000) }

    }
}