package io.github.unsignedint8.dwallet_core.bitcoin.application.spv

import io.github.unsignedint8.dwallet_core.bitcoin.application.wallet.*
import io.github.unsignedint8.dwallet_core.bitcoin.p2p.Node

/**
 * Created by unsignedint8 on 8/26/17.
 */

class SPVNode(network: Network, val wallet: Wallet, latestBlockHash: String, latestHeight: Int) {

    val node = Node(network.magic, latestHeight)

}