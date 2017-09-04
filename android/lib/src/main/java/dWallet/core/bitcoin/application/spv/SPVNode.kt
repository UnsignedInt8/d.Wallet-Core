package dwallet.core.bitcoin.application.spv

import dwallet.core.bitcoin.p2p.Node
import dwallet.core.bitcoin.protocol.messages.Addr
import dwallet.core.bitcoin.protocol.structures.*
import dwallet.core.extensions.*
import dwallet.core.infrastructure.Event
import dwallet.core.infrastructure.EventCallback

/**
 * Created by unsignedint8 on 8/26/17.
 *
 * The default implementation code of a SPV Node.
 * You can implement your spv node by referring this code.
 *
 */

open class SPVNode(network: Network, filterItems: Iterable<ByteArray>, private val latestBlockHeight: Int = 0, latestBlockHash: String = String.ZEROHASH, knownBlockHashes: List<String> = listOf(), knownTxHashes: List<String> = listOf()) : Node(network.magic, latestBlockHeight) {

    //    private val node = Node(network.magic, latestBlockHeight)
    private val knownBlocks = mutableSetOf<String>()
    private val knownTxs = mutableSetOf<String>()

    init {
        knownBlockHashes.forEach { knownBlocks.add(it) }
        knownTxHashes.forEach { knownTxs.add(it) }

        initBloomFilter(filterItems, 0.001, 0)

        onVerack { sender, _ ->
            sender.sendGetBlocks(listOf(latestBlockHash))
            sender.sendMempool()
            sender.sendGetAddr()
        }

        onInv { sender, items ->
            val blocks = items.filter { it.type == InvTypes.MSG_BLOCK && !knownBlocks.contains(it.hash) }.map { InventoryVector(InvTypes.MSG_FILTERED_BLOCK, it.hash) }
            val txs = items.filter { it.type == InvTypes.MSG_TX && !knownTxs.contains(it.hash) }

            if (txs.isNotEmpty()) sender.sendGetData(txs)
            if (blocks.isEmpty()) return@onInv

            sender.sendGetData(blocks)
            sender.sendGetBlocks(listOf(blocks.last().hash)) // continue requesting the latest block
        }

        onTx { _, tx ->
            knownTxs.add(tx.id)
        }

        onMerkleblock { _, merkleblock ->
            knownBlocks.add(merkleblock.hash)
        }
    }

    val progress: Double
        get() = Math.min((latestBlockHeight + knownBlocks.size).toDouble() / peerBlockchainHeight.toDouble() * 100.0, 100.0)

}