package io.github.unsignedint8.dwallet_core

import io.github.unsignedint8.dwallet_core.bitcoin.protocol.Message
import io.github.unsignedint8.dwallet_core.extensions.hexToByteArray
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

    @Test
    fun testFrom() {
        val raw = "F9BEB4D976657273696F6E0000000000640000003B648D5A62EA0000010000000000000011B2D05000000000010000000000000000000000000000000000FFFF000000000000010000000000000000000000000000000000FFFF0000000000003B2EB35D8CE617650F2F5361746F7368693A302E372E322FC03E0300".hexToByteArray()
        val msg = Message.fromBytes(raw)
        assertEquals("version", msg.command)
        assertArrayEquals("f9beb4d9".hexToByteArray(), msg.magic)
        assertArrayEquals("3b648d5a".hexToByteArray(), msg.checksum)
    }
}