package dWallet.core.bitcoin.application.spv

import dWallet.core.bitcoin.application.wallet.*
import dWallet.core.bitcoin.p2p.Node
import dWallet.core.bitcoin.protocol.structures.InvTypes
import dWallet.core.bitcoin.protocol.structures.InventoryVector
import dWallet.core.bitcoin.protocol.structures.MerkleBlock

/**
 * Created by unsignedint8 on 8/26/17.
 *
 * The default implementation code of a SPV Node.
 * You can implement your spv node by referring this code.
 *
 */

open class SPVNode(network: Network, val wallet: Wallet, latestBlockHash: String, latestHeight: Int, knownBlockHashes: List<String> = listOf(), knownTxHashes: List<String> = listOf()) {

    private val node = Node(network.magic, latestHeight)
    private val knownBlocks = mutableSetOf<String>()
    private val knownTxs = mutableSetOf<String>()

    init {
        knownBlockHashes.forEach { knownBlocks.add(it) }
        knownTxHashes.forEach { knownTxs.add(it) }

        node.initBloomFilter(wallet.allPrivKeys.map { it.publicKeyHash!! } + wallet.allPrivKeys.map { it.public!! }, 0.0001)
        node.onVerack { sender, _ -> sender.sendGetBlocks(listOf(latestBlockHash)) }

        node.onInv { sender, items ->
            val blocks = items.filter { it.type == InvTypes.MSG_BLOCK && !knownBlocks.contains(it.hash) }.map { InventoryVector(InvTypes.MSG_FILTERED_BLOCK, it.hash) }
            val txs = items.filter { it.type == InvTypes.MSG_TX && !knownTxs.contains(it.hash) }

            if (txs.isNotEmpty()) sender.sendGetData(txs)

            if (blocks.isNotEmpty()) {
                sender.sendGetData(blocks)
                sender.sendGetBlocks(listOf(blocks.last().hash))
                println("latest block hash ${blocks.last().hash}")
            }
        }
    }

    fun connectAsync(host: String, port: Int) = node.connectAsync(host, port)

}