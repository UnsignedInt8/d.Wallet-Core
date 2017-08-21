package io.github.unsignedint8.dwallet_core

import io.github.unsignedint8.dwallet_core.bitcoin.protocol.structures.*
import io.github.unsignedint8.dwallet_core.extensions.*
import org.junit.Test
import org.junit.Assert.*

/**
 * Created by unsignedint8 on 8/18/17.
 */

class BlockHeaderTests {
    @Test
    fun testBlockHeader() {
        val raw = "000000207efc10a9a0e343ccd56cbff8a203361ec4d36e30561b3b00bab6414616a9046b24bc71d07b4a9b1cc3dbb7ef28587faf1dc3bfaf65eae3736d31841c0b5a681f24489659ffff7f200400000001020000000001010000000000000000000000000000000000000000000000000000000000000000ffffffff0502ef090101ffffffff02052a01000000000023210316b2aa472599f676d74835f907035925d9fdff76be24c0a0f10bf017de04a809ac0000000000000000266a24aa21a9ede2f61c3f71d1defd3fa999dfa36953755c690689799962b48bebd836974e8cf90120000000000000000000000000000000000000000000000000000000000000000000000000".hexToByteArray()
        val header = BlockHeader.fromBytes(raw)
        assertEquals("6b04a9164641b6ba003b1b56306ed3c41e3603a2f8bf6cd5cc43e3a0a910fc7e", header.preBlockHash)
        assertEquals("1f685a0b1c84316d73e3ea65afbfc31daf7f5828efb7dbc31c9b4a7bd071bc24", header.merkleRootHash)
        assertEquals(1503021092, header.timestamp)
        assertEquals(4, header.nonce)
        assertEquals(536870912, header.version)
        println(header.hash)
        assertArrayEquals(raw.take(BlockHeader.naturalSize).toByteArray(), header.toBytes())
    }
}