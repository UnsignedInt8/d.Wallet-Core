package io.github.unsignedint8.dwallet_core.bitcoin.protocol.structures

/**
 * Created by unsignedint8 on 8/16/17.
 */

interface BitcoinSerializable<out T> {
    fun fromBytes(bytes: ByteArray): T
}