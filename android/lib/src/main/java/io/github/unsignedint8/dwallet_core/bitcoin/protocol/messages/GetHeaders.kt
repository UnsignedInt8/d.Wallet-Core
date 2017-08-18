package io.github.unsignedint8.dwallet_core.bitcoin.protocol.messages

import io.github.unsignedint8.dwallet_core.extensions.*

/**
 * Created by unsignedint8 on 8/18/17.
 */

class GetHeaders(val version: Int, val count: Int, val locatorHash: String, val stopHash: String) {

    companion object {
        const val text = "getheaders"
    }

    constructor(count: Int, locatorHash: String = ByteArray(32).toHexString(), stopHash: String = ByteArray(32).toHexString()) : this(70001, count, locatorHash, stopHash)

    fun toBytes() {

    }
}