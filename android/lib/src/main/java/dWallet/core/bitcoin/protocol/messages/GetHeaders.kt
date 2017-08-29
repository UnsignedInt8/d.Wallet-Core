package dwallet.core.bitcoin.protocol.messages

import dwallet.core.extensions.*

/**
 * Created by unsignedint8 on 8/18/17.
 */

class GetHeaders(val version: Int, val locatorHashes: List<String>, val stopHash: String) {

    companion object {
        const val text = "getheaders"
        const val headers = "headers"
        const val getblocks = "getblocks"
    }

    constructor(locatorHashes: List<String>, stopHash: String = String.ZEROHASH) : this(Version.number, locatorHashes, stopHash)

    fun toBytes() = version.toInt32LEBytes() + locatorHashes.size.toVarIntBytes() + locatorHashes.reduce(ByteArray(0), { acc, seed -> seed + acc.hashToBytes() }) + stopHash.hashToBytes()
}