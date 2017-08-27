package dWallet.core.bitcoin.p2p

import dWallet.core.bitcoin.protocol.messages.*
import dWallet.core.bitcoin.protocol.structures.*
import dWallet.core.extensions.*
import dWallet.core.infrastructure.*
import dWallet.core.utils.*
import kotlinx.coroutines.experimental.*
import java.security.*
import kotlin.experimental.and

/**
 * Created by unsignedint8 on 8/18/17.
 */

class Node(var magic: ByteArray, var startHeight: Int = 0) : Event() {

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
        msgHandlers[MerkleBlock.message] = fun(d: ByteArray) { handleMerkleblock(d) }
        msgHandlers[Transaction.message] = fun(d: ByteArray) { handleTx(d) }
        msgHandlers[Block.message] = fun(d: ByteArray) { handleBlock(d) }
    }

    val peerAddress: String
        get() = socket.inetAddress.hostAddress

    val peerPort: Int
        get() = socket.port

    val localPort: Int
        get() = socket.localPort

    var versionVerified = false
        private set

    var peerId: Long = 0
        private set

    val nodeId = SecureRandom().nextLong()

    var isFullNode = false
        private set

    var isSegwit2xNode = false
        private set

    var isBCHNode = false
        private set

    var peerBlockchainHeight = 0
        private set

    var peerVersion = 0
        private set

    var version = 70001

    var ua = "/Wallet:0.0.1/"

    private var filter: BloomFilter? = null

    fun initBloomFilter(elements: Iterable<ByteArray>, falsePositiveRate: Double, nTweak: Int = 0, nFlags: Int = BloomFilter.BLOOM_UPDATE_NONE) {
        filter = BloomFilter.create(elements.count(), falsePositiveRate, nTweak, nFlags)
        elements.forEach { filter?.insert(it) }
    }

    fun insertFilterElement(element: ByteArray) {
        filter?.insert(element)
    }

    fun connectAsync(host: String, port: Int) = async(CommonPool) {
        socket.keepAlive = true

        val result = socket.connectAsync(host, port, 10 * 1000).await()
        if (!result) return@async false

        sendVersion()
        beginReceivingData()

        return@async result
    }

    private suspend fun beginReceivingData() {

        if (socket.isClosed) return

        fun runNext() = async(CommonPool) { beginReceivingData() }
        var shutdown = false

        try {
            var data = ByteArray(0)

            while (data.size < Message.standardSize) {
                val buf = socket.readAsync(Message.standardSize).await()
                if (buf == null) {
                    println("data is null, socket connected: ${socket.isConnected}")
                    shutdown = true
                    return
                }

                data += buf
            }

            if (data.size < Message.standardSize) {
                println("data size are not equal, actual: ${data?.size} expected: ${Message.standardSize}")
                return
            }

            val msg = Message.fromBytes(data.take(24).toByteArray())
            if (!magic.contentEquals(msg.magic)) {
                println("magic numbers are not equal")
                return
            }

            println("cmd: ${msg.command}")

            data = data.takeLast(data.size - 24).toByteArray()
            while (data.size < msg.length) {
                val part = socket.readAsync(msg.length - data.size).await()

                if (part == null) {
                    shutdown = true
                    return
                }

                data += part
            }

            if (!msg.verifyChecksum(data)) {
                println("both checksum are not equal")
                return
            }

            if (msg.command != Version.text && !versionVerified) {
                println("verack has not been received - ${msg.command}")
                return
            }

            val handler = msgHandlers[msg.command] ?: return
            handler(data)

        } finally {

            if (!shutdown) runNext()
            if (shutdown) super.trigger("socket-shutdown", this, socket)
        }
    }

    fun onSocketClosed(callback: (sender: Node, socket: SocketEx) -> Unit) {
        super.register("socket-shutdown", callback as EventCallback)

        try {
            this.socket.close()
        } catch (e: Exception) {
        }
    }

    private fun sendMessage(command: String, payload: ByteArray = ByteArray(0)) {
        socket.writeAsync(Message(magic, command, payload).toBytes())
    }

    private fun sendVersion(relayTx: Boolean = false) {
        val emptyAddr = NetworkAddress("::0", 0, addTimestamp = false)
        sendMessage(Version.text, Version(version, toAddr = emptyAddr, fromAddr = emptyAddr, nonce = nodeId, ua = ua, startHeight = startHeight, relay = relayTx).toBytes())
    }

    private fun handleVersion(payload: ByteArray) {
        versionVerified = true

        val v = Version.fromBytes(payload)
        peerBlockchainHeight = v.startHeight
        peerVersion = v.version
        isFullNode = v.services[0] and 0x01 != Byte.ZERO

        sendVerack()
    }

    private fun sendVerack() {
        sendMessage(Version.verack)
    }

    private fun handleVerack() {
        sendFilterLoad()
        super.trigger(Version.verack, this, this.peerVersion)
    }

    fun onVerack(callback: (sender: Node, version: Int) -> Unit) {
        super.register(Version.verack, callback as EventCallback)
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
        super.register(GetHeaders.headers, callback as EventCallback)
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
        super.register(Reject.text, callback as EventCallback)
    }

    fun sendGetBlocks(locatorHashes: List<String> = listOf(String.ZEROHASH), stopHash: String = String.ZEROHASH) {
        sendMessage(GetHeaders.getblocks, GetHeaders(locatorHashes, stopHash).toBytes())
    }

    private fun handleInv(data: ByteArray) {
        val items = data.readVarList { bytes -> Pair(InventoryVector.fromBytes(bytes), InventoryVector.standardSize) }
        super.trigger(InventoryVector.inv, this, items)
    }

    fun onInv(callback: (sender: Node, items: List<InventoryVector>) -> Unit) {
        super.register(InventoryVector.inv, callback as EventCallback)
    }

    fun sendGetData(items: List<InventoryVector>) {
        sendMessage(GetData.text, GetData(items).toBytes())
    }

    fun sendGetMerkleblocks(hashes: List<String>) {
        val blocks = hashes.map { InventoryVector(InvTypes.MSG_FILTERED_BLOCK, it) }
        sendGetData(blocks)
    }

    private fun handleMerkleblock(data: ByteArray) {
        val block = MerkleBlock.fromBytes(data)
        super.trigger(MerkleBlock.message, this, block)
    }

    fun onMerkleblocks(callback: (sender: Node, merkleblock: MerkleBlock) -> Unit) {
        super.register(MerkleBlock.message, callback as EventCallback)
    }

    fun sendMempool() {
        sendMessage("mempool")
    }

    fun sendTx(tx: Transaction) {
        sendMessage(Transaction.message, tx.toBytes())
    }

    private fun handleTx(data: ByteArray) {
        val tx = Transaction.fromBytes(data)
        super.trigger(Transaction.message, this, tx)
    }

    fun onTx(callback: (sender: Node, tx: Transaction) -> Unit) {
        super.register(Transaction.message, callback as EventCallback)
    }

    private fun handleBlock(data: ByteArray) {
        val block = Block.fromBytes(data)
        super.trigger(Block.message, this, block)
    }

    fun onBlock(callback: (sender: Node, block: Block) -> Unit) {
        super.register(Block.message, callback as EventCallback)
    }
}