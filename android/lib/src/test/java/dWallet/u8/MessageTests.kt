package dwallet.u8

import dwallet.core.bitcoin.protocol.messages.*
import dwallet.core.bitcoin.protocol.structures.*
import dwallet.core.extensions.*
import org.junit.Test
import org.junit.Assert.*

/**
 * Created by unsignedint8 on 8/15/17.
 */

class MessageTests {
    @Test
    fun testMagics() {
        assertArrayEquals(0xD9B4BEF9.toInt64LEBytes().take(4).toByteArray(), Message.Magic.Bitcoin.main.toInt32LEBytes())
    }

    @Test
    fun testMessageFrom() {
        val raw = "F9BEB4D976657273696F6E0000000000640000003B648D5A62EA0000010000000000000011B2D05000000000010000000000000000000000000000000000FFFF000000000000010000000000000000000000000000000000FFFF0000000000003B2EB35D8CE617650F2F5361746F7368693A302E372E322FC03E0300".hexToByteArray()
        val msg = Message.fromBytes(raw)
        assertEquals("version", msg.command)
        assertArrayEquals("f9beb4d9".hexToByteArray(), msg.magic)
        assertArrayEquals("3b648d5a".hexToByteArray(), msg.checksum)

        val mmm = Message(Message.Magic.Bitcoin.main.toInt32LEBytes(), "version", msg.payload)
        assertEquals("3b648d5a", mmm.checksum.toHexString())
        assertEquals(100, mmm.length)
        assertArrayEquals(raw, mmm.toBytes())
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
        val netaddr = NetworkAddress("0.0.0.0", 0, service, addTimestamp = false)

        val ver = Version(60002, byteArrayOf(1, 0, 0, 0, 0, 0, 0, 0), 1355854353, netaddr, netaddr, 7284544412836900411, "/Satoshi:0.7.2/", 212672)
        assertEquals(60002, ver.version)
        assertEquals(true, ver.isFullNode)
        assertArrayEquals("62EA0000010000000000000011B2D05000000000010000000000000000000000000000000000FFFF000000000000010000000000000000000000000000000000FFFF0000000000003B2EB35D8CE617650F2F5361746F7368693A302E372E322FC03E0300".hexToByteArray(), ver.toBytes())

        val msg = Message(Message.Magic.Bitcoin.main, "version", ver.toBytes())
        assertEquals(100, msg.length)
        assertEquals(124, msg.toBytes().size)
        assertArrayEquals("3B648D5A".hexToByteArray(), msg.checksum)

        val verack = Message(Message.Magic.Bitcoin.main, "verack", ByteArray(0))
        assertEquals("5DF6E0E2", verack.checksum.toHexString().toUpperCase())

    }

    @Test
    fun testAddr() {
        val raw = "02E215104D010000000000000000000000000000000000FFFF0A000001208DE215104D010000000000000000000000000000000000FFFF0A000001208D".hexToByteArray()
        val addrs = Addr.fromBytes(raw)

        assertEquals(true, addrs.addrs.all { it.ip == "10.0.0.1" && it.port == 8333.toShort() })
        assertEquals(2, addrs.count)
    }

}