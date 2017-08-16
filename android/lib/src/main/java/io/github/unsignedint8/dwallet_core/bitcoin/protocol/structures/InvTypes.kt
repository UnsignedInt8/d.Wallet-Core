package io.github.unsignedint8.dwallet_core.bitcoin.protocol.structures

/**
 * Created by unsignedint8 on 8/16/17.
 */

enum class InvTypes(val value: Int) {
    ERROR(0),
    MSG_TX(1),
    MSG_BLOCK(2),
    MSG_FILTERED_BLOCK(3),
    MSG_CMPCT_BLOCK(4),
}