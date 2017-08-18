package io.github.unsignedint8.dwallet_core.bitcoin.p2p

import io.github.unsignedint8.dwallet_core.bitcoin.protocol.messages.*
import io.github.unsignedint8.dwallet_core.infrastructure.Event
import io.github.unsignedint8.dwallet_core.network.*
import io.github.unsignedint8.dwallet_core.utils.BloomFilter
import java.security.*

/**
 * Created by unsignedint8 on 8/18/17.
 */

class Node : Event() {

    companion object {

        val Events = object {
            val version = Version.text
            val ping = Ping.text
            val pong = Ping.pong
            val getheaders = GetHeaders.text
            val addr = Addr.text
        }

    }

    private val socket = SocketEx()

    val peerAddress: String
        get() = socket.inetAddress.hostAddress

    val peerPort: Int
        get() = socket.port

    var verackVerified = false
        private set

    var peerId: Long = 0
        private set

    val nodeId: Long = SecureRandom().nextLong()

    var isFullNode: Boolean = false
        private set

    var canSetBloomFilter: Boolean = false
        private set

    var canGetUtxo: Boolean = false
        private set

    var isSegwit2xNode: Boolean = false
        private set

    var isBCHNode: Boolean = false
        private set

    private var filter: BloomFilter? = null

    fun initBloomFilter(elements: Array<ByteArray>, falsePositiveRate: Double) {
        filter = BloomFilter.create(elements.size, falsePositiveRate, 0, BloomFilter.BLOOM_UPDATE_ALL)
        elements.forEach { filter?.insert(it) }
    }

    fun insertFilterElement(element: ByteArray) {
        filter?.insert(element)
    }

    
}