package io.github.unsignedint8.dwallet_core

import io.github.unsignedint8.dwallet_core.bitcoin.protocol.*
import io.github.unsignedint8.dwallet_core.bitcoin.protocol.messages.*
import io.github.unsignedint8.dwallet_core.extensions.*
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
    fun testMessageFrom() {
        val raw = "F9BEB4D976657273696F6E0000000000640000003B648D5A62EA0000010000000000000011B2D05000000000010000000000000000000000000000000000FFFF000000000000010000000000000000000000000000000000FFFF0000000000003B2EB35D8CE617650F2F5361746F7368693A302E372E322FC03E0300".hexToByteArray()
        val msg = Message.fromBytes(raw)
        assertEquals("version", msg.command)
        assertArrayEquals("f9beb4d9".hexToByteArray(), msg.magic)
        assertArrayEquals("3b648d5a".hexToByteArray(), msg.checksum)
    }

    @Test
    fun testNetworkAddress() {
        val addr = NetworkAddress("10.0.0.1", 8333, byteArrayOf(1, 0, 0, 0, 0, 0, 0, 0)).toBytes().sliceArray(4)
        assertArrayEquals("010000000000000000000000000000000000FFFF0A000001208D".hexToByteArray(), addr)

        val addr2 = NetworkAddress.fromBytes("00000000010000000000000000000000000000000000FFFF0A000001208D".hexToByteArray())
        assertEquals(0, addr2.time)
        assertEquals("10.0.0.1", addr2.ip)
        assertEquals(8333.toShort(), addr2.port)

    }

    @Test
    fun testVersion() {
        val service = byteArrayOf(1, 0, 0, 0, 0, 0, 0, 0)
        val netaddr = NetworkAddress("::1", 8333, service)
        netaddr.version = 60002

        val ver = Version(60002, byteArrayOf(1, 0, 0, 0, 0, 0, 0, 0), 1355854353, netaddr, netaddr, 7284544412836901000, "/Satoshi:0.7.2/", 212672)
        println(ver.toBytes().toHexString())
    }

}