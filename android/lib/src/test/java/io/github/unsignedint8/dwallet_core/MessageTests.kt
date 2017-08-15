package io.github.unsignedint8.dwallet_core

import io.github.unsignedint8.dwallet_core.bitcoin.protocol.Message
import org.junit.Test
import org.junit.Assert.*

/**
 * Created by unsignedint8 on 8/15/17.
 */

class MessageTests {
    @Test
    fun testMagics() {
        assertEquals(0xD9B4BEF9, Message.Magic.Bitcoin.main)
    }
}