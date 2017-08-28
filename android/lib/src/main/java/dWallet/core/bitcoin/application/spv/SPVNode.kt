package dWallet.core.bitcoin.application.spv

import dWallet.core.bitcoin.p2p.Node
import dWallet.core.bitcoin.protocol.messages.Addr
import dWallet.core.bitcoin.protocol.structures.*
import dWallet.core.extensions.*
import dWallet.core.infrastructure.Event
import dWallet.core.infrastructure.EventCallback

/**
 * Created by unsignedint8 on 8/26/17.
 *
 * The default implementation code of a SPV Node.
 * You can implement your spv node by referring this code.
 *
 */

open class SPVNode(network: Network, filterItems: Iterable<ByteArray>, private val latestBlockHeight: Int = 0, latestBlockHash: String = String.ZEROHASH, knownBlockHashes: List<String> = listOf(), knownTxHashes: List<String> = listOf()) : Event() {

    private val node = Node(network.magic, latestBlockHeight)
    private val knownBlocks = mutableSetOf<String>()
    private val knownTxs = mutableSetOf<String>()

    init {
        knownBlockHashes.forEach { knownBlocks.add(it) }
        knownTxHashes.forEach { knownTxs.add(it) }

        node.initBloomFilter(filterItems, 0.001, 0)

        node.onVerack { sender, _ ->
            sender.sendGetBlocks(listOf(latestBlockHash))
            sender.sendMempool()
            sender.sendGetAddr()
        }

        node.onInv { sender, items ->
            val blocks = items.filter { it.type == InvTypes.MSG_BLOCK && !knownBlocks.contains(it.hash) }.map { InventoryVector(InvTypes.MSG_FILTERED_BLOCK, it.hash) }
            val txs = items.filter { it.type == InvTypes.MSG_TX && !knownTxs.contains(it.hash) }

            if (txs.isNotEmpty()) sender.sendGetData(txs)
            if (blocks.isEmpty()) return@onInv

            sender.sendGetData(blocks)
            sender.sendGetBlocks(listOf(blocks.last().hash)) // continue requesting the latest block
        }

        node.onTx { _, tx ->
            this.trigger(Transaction.message, this, tx)
            knownTxs.add(tx.id)
        }

        node.onMerkleblock { _, merkleblock ->
            knownBlocks.add(merkleblock.hash)
            this.trigger(MerkleBlock.message, this, merkleblock)
        }

        node.onSocketClosed { _, _ -> super.trigger("ConnectionLost", this, 0) }

        node.onAddr { _, addrs -> super.trigger(Addr.text, this, addrs) }
    }

    suspend fun connectAsync(host: String, port: Int) = node.connectAsync(host, port).await()

    fun onTx(callback: (sender: SPVNode, tx: Transaction) -> Unit) = super.register(Transaction.message, callback as EventCallback)

    fun onMerkleblock(callback: (sender: SPVNode, merkleblock: MerkleBlock) -> Unit) = super.register(MerkleBlock.message, callback as EventCallback)

    fun onAddr(callback: (sender: SPVNode, addrs: List<NetworkAddress>) -> Unit) = super.register(Addr.text, callback as EventCallback)

    fun onConnectionLost(callback: (sender: SPVNode, placeholder: Any) -> Unit) = super.register("ConnectionLost", callback as EventCallback)

    val progress: Double
        get() = Math.min((latestBlockHeight + knownBlocks.size).toDouble() / peerHeight.toDouble() * 100.0, 100.0)

    val peerHeight: Int
        get() = node.peerBlockchainHeight

    val peerAddress: String
        get() = node.peerAddress
}