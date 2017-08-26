package io.github.unsignedint8.dwallet_core.bitcoin.application.spv

import io.github.unsignedint8.dwallet_core.bitcoin.application.wallet.*
import io.github.unsignedint8.dwallet_core.bitcoin.p2p.Node

/**
 * Created by unsignedint8 on 8/26/17.
 *
 * The default implementation code of a SPV Node.
 * You can implement your node by referring the code.
 *
 */

class SPVNode(network: Network, val wallet: Wallet, private val latestBlockHash: String, latestHeight: Int) {

    private val node = Node(network.magic, latestHeight)

    init {
        node.initBloomFilter(wallet.allPrivKeys.map { it.publicKeyHash!! } + wallet.allPrivKeys.map { it.public!! }, 0.001)
        node.onVerack { sender, _ -> sender.sendGetBlocks(listOf(latestBlockHash)) }
    }
}