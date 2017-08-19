package io.github.unsignedint8.dwallet_core.bitcoin.p2p

import io.github.unsignedint8.dwallet_core.bitcoin.protocol.messages.*
import io.github.unsignedint8.dwallet_core.bitcoin.protocol.structures.*
import io.github.unsignedint8.dwallet_core.crypto.*
import io.github.unsignedint8.dwallet_core.extensions.*
import io.github.unsignedint8.dwallet_core.infrastructure.Callback
import io.github.unsignedint8.dwallet_core.infrastructure.Event
import io.github.unsignedint8.dwallet_core.network.*
import io.github.unsignedint8.dwallet_core.utils.*
import kotlinx.coroutines.experimental.*
import java.security.*

/**
 * Created by unsignedint8 on 8/18/17.
 */

class Node : Event() {

    private val msgHandlers: Map<String, (payload: ByteArray) -> Unit>
    private val socket = SocketEx()

    init {
        msgHandlers = mutableMapOf()
        msgHandlers[Version.text] = fun(payload: ByteArray) { handleVersion(payload) }
        msgHandlers[Version.verack] = fun(_: ByteArray) { handleVerack() }
        msgHandlers[Ping.text] = fun(d: ByteArray) { handlePing(d) }
        msgHandlers[Reject.text] = fun(d: ByteArray) { handleReject(d) }
        msgHandlers[GetHeaders.headers] = fun(d: ByteArray) { handleHeaders(d) }
        msgHandlers[InventoryVector.inv] = fun(d: ByteArray) { handleInv(d) }
    }

    val peerAddress: String
        get() = socket.inetAddress.hostAddress

    val peerPort: Int
        get() = socket.port

    var versionVerified = false
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

        if (socket.isClosed) return

        fun runNext() = async(CommonPool) { beginReceivingData() }

        try {
            var data = socket.readAsync(Message.standardSize).await()
            if (data == null || data.size != Message.standardSize) {
                println("data size are not equal")
                return
            }

            val msg = Message.fromBytes(data)
            if (!magic.contentEquals(msg.magic)) {
                println("magic numbers are not equal")
                return
            }

            data = socket.readAsync(msg.length).await()
            if (data == null) return

            while (data.size < msg.length) {
                val part = socket.readAsync(msg.length - data.size).await() ?: return
                data += part
            }

            if (!msg.verifyChecksum(data)) {
                println("checksum are not equal")
                return
            }

            if (msg.command != Version.text && !versionVerified) {
                println("verack has not been received - ${msg.command}")
                return
            }

            println(msg.command)

            val handler = msgHandlers[msg.command] ?: return
            handler(data)

        } finally {
            runNext()
        }
    }

    private fun sendMessage(command: String, payload: ByteArray = ByteArray(0)) {
        socket.writeAsync(Message(magic, command, payload).toBytes())
    }

    private fun sendVersion() {
        val emptyAddr = NetworkAddress("::0", 0, addTimestamp = false)
        sendMessage(Version.text, Version(version, toAddr = emptyAddr, fromAddr = emptyAddr, nonce = nodeId, ua = ua, startHeight = startHeight).toBytes())
    }

    private fun handleVersion(payload: ByteArray) {
        versionVerified = true

        val v = Version.fromBytes(payload)
        peerBlockchainHeight = v.startHeight
        peerVersion = v.version
        isFullNode = v.services[0] >= 1.toByte()
        sendVerack()
    }

    private fun sendVerack() {
        sendMessage(Version.verack)
    }

    private fun handleVerack() {
        sendFilterLoad()
    }

    private fun sendFilterLoad() {
        if (filter == null) return
        sendMessage(FilterLoad.text, FilterLoad(filter!!.data, filter!!.nHashFuncs, filter!!.nTweak, filter!!.nFlags).toBytes())
    }

    fun sendFilterClear() {
        sendMessage(FilterLoad.filterclear)
    }

    fun sendGetHeaders(locatorHashes: List<String> = listOf(String.ZEROHASH), stopHash: String = String.ZEROHASH) {
        sendMessage(GetHeaders.text, GetHeaders(locatorHashes, stopHash).toBytes())
    }

    private fun handleHeaders(payload: ByteArray) {
        val headers = payload.readVarList { bytes -> Pair(BlockHeader.fromBytes(bytes), BlockHeader.standardSize) }
        super.trigger(GetHeaders.headers, this, headers)
    }

    fun onHeaders(callback: (sender: Node, headers: List<BlockHeader>) -> Unit) {
        super.register(GetHeaders.headers, callback as Callback)
    }

    fun sendPing() {
        sendMessage(Ping.text, Ping(SecureRandom().nextLong()).toBytes())
    }

    private fun handlePing(payload: ByteArray) {
        sendMessage(Ping.pong, Ping.fromBytes(payload).toBytes())
    }

    private fun handleReject(data: ByteArray) {
        val reject = Reject.fromBytes(data)
        super.trigger(Reject.text, this, reject)
    }

    fun onReject(callback: (sender: Node, reject: Reject) -> Unit) {
        super.register(Reject.text, callback as Callback)
    }

    fun sendGetBlocks(locatorHashes: List<String> = listOf(String.ZEROHASH), stopHash: String = String.ZEROHASH) {
        sendMessage(GetHeaders.getblocks, GetHeaders(locatorHashes, stopHash).toBytes())
    }

    private fun handleInv(data: ByteArray) {
        val invs = data.readVarList { bytes -> Pair(InventoryVector.fromBytes(bytes), InventoryVector.standardSize) }
        super.trigger(InventoryVector.inv, this, invs)
    }

    fun onInv(callback: (sender: Node, invs: List<InventoryVector>) -> Unit) {
        super.register(InventoryVector.inv, callback as Callback)
    }
}