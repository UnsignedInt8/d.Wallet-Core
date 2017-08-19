package io.github.unsignedint8.dwallet_core

import io.github.unsignedint8.dwallet_core.bitcoin.p2p.Node
import io.github.unsignedint8.dwallet_core.bitcoin.protocol.messages.Version
import io.github.unsignedint8.dwallet_core.bitcoin.protocol.structures.*
import io.github.unsignedint8.dwallet_core.extensions.hexToByteArray
import io.github.unsignedint8.dwallet_core.extensions.toInt32LEBytes
import io.github.unsignedint8.dwallet_core.network.*
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
        val node = Node()
        node.magic = Message.Magic.Bitcoin.regtest.toInt32LEBytes()
        node.initBloomFilter(arrayOf("02cee8043452a0e2e9dc75526a6ed2ce2f53269bd5460b0694fb9619e073c819f3".hexToByteArray()), 0.01)

        async(CommonPool) {
            node.connectAsync("localhost", 19000)
            node.sendGetheaders()
        }

        runBlocking { delay(1000 * 1000) }
        println(node.peerBlockchainHeight)
        println(node.peerVersion)
    }
}