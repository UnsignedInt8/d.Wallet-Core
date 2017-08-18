package io.github.unsignedint8.dwallet_core.bitcoin.p2p

import io.github.unsignedint8.dwallet_core.bitcoin.protocol.messages.*
import io.github.unsignedint8.dwallet_core.bitcoin.protocol.structures.*
import io.github.unsignedint8.dwallet_core.crypto.hash256
import io.github.unsignedint8.dwallet_core.extensions.toInt32LEBytes
import io.github.unsignedint8.dwallet_core.infrastructure.Event
import io.github.unsignedint8.dwallet_core.network.*
import io.github.unsignedint8.dwallet_core.utils.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.security.*

/**
 * Created by unsignedint8 on 8/18/17.
 */

class Node() : Event() {

    companion object {

        val Events = object {
            val version = Version.text
            val ping = Ping.text
            val pong = Ping.pong
            val getheaders = GetHeaders.text
            val addr = Addr.text
        }

    }

    private val msgHandlers: Map<String, (payload: ByteArray) -> Unit>
    private val socket = SocketEx()

    init {
        msgHandlers = mutableMapOf()
        msgHandlers[Version.text] = fun(payload: ByteArray) { handleVersion(payload) }
        msgHandlers[Version.verack] = fun(_: ByteArray) { handleVerack() }
        msgHandlers[Ping.text] = fun(d: ByteArray) { handlePing(d) }
    }


    val peerAddress: String
        get() = socket.inetAddress.hostAddress

    val peerPort: Int
        get() = socket.port

    var verackVerified = false
        private set

    var peerId: Long = 0
        private set

    val nodeId = SecureRandom().nextLong()

    var isFullNode = false
        private set

    var canSetBloomFilter = false
        private set

    var canGetUtxo = false
        private set

    var isSegwit2xNode = false
        private set

    var isBCHNode = false
        private set

    var peerBlockchainHeight = 0
        private set

    var peerVersion = 0
        private set

    var magic = Message.Magic.Bitcoin.main.toInt32LEBytes()

    var version = 70001

    var ua = "/d.Wallet:0.0.1/"

    var startHeight = 0

    private var filter: BloomFilter? = null

    fun initBloomFilter(elements: Array<ByteArray>, falsePositiveRate: Double) {
        filter = BloomFilter.create(elements.size, falsePositiveRate, 0, BloomFilter.BLOOM_UPDATE_ALL)
        elements.forEach { filter?.insert(it) }
    }

    fun insertFilterElement(element: ByteArray) {
        filter?.insert(element)
    }

    suspend fun connectAsync(host: String, port: Int): Boolean {
        socket.keepAlive = true

        val result = socket.connectAsync(host, port, 10 * 1000).await()
        if (!result) return false

        sendVersion()
        beginReceivingData()

        return result
    }

    private suspend fun beginReceivingData() {

        fun runNext() = async(CommonPool) { beginReceivingData() }

        var data = socket.readAsync(Message.standardSize).await()

        if (data == null || data.size != Message.standardSize) {
            runNext()
            return
        }

        val msg = Message.fromBytes(data)

        if (!magic.contentEquals(msg.magic)) {
            println("magic numbers are not equal")
            runNext()
            return
        }

        data = socket.readAsync(msg.length).await()

        if (data == null || data.size != msg.length) {
            runNext()
            return
        }

        if (!hash256(data).take(4).toByteArray().contentEquals(msg.checksum)) {
            println("checksum data are not equal")
            runNext()
            return
        }

        val handler = msgHandlers[msg.command]
        if (handler == null) {
            runNext()
            return
        }

        println(msg.command)
        handler(data)
        runNext()
    }

    private fun sendMessage(command: String, payload: ByteArray = ByteArray(0)) {
        socket.writeAsync(Message(magic, command, payload).toBytes())
    }

    private fun sendVersion() {
        val emptyAddr = NetworkAddress("::0", 0, addTimestamp = false)
        sendMessage(Version.text, Version(version, toAddr = emptyAddr, fromAddr = emptyAddr, nonce = nodeId, ua = ua, startHeight = startHeight).toBytes())
    }

    private fun sendVerack() {
        sendMessage(Version.verack)
    }

    private fun sendFilterLoad() {
        if (filter == null) return
        sendMessage(FilterLoad.text, FilterLoad(filter!!.data, filter!!.nHashFuncs, filter!!.nTweak, filter!!.nFlags).toBytes())
    }

    fun sendPing() {
        sendMessage(Ping.text, Ping(SecureRandom().nextLong()).toBytes())
    }

    private fun handleVersion(payload: ByteArray) {
        val v = Version.fromBytes(payload)
        peerBlockchainHeight = v.startHeight
        peerVersion = v.version
        isFullNode = v.services[0] >= 1.toByte()
        sendVerack()
    }

    private fun handleVerack() {
        verackVerified = true
        sendFilterLoad()
    }

    private fun handlePing(payload: ByteArray) {
        sendMessage(Ping.pong, Ping.fromBytes(payload).toBytes())
    }
}