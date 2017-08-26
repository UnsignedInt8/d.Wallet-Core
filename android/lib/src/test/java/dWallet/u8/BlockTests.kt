package dWallet.u8

import dWallet.core.bitcoin.protocol.structures.*
import dWallet.core.extensions.*
import org.junit.Test
import org.junit.Assert.*

/**
 * Created by unsignedint8 on 8/21/17.
 */

class BlockTests {
    @Test
    fun testDeserialization() {
        val raw = "000000200e4c46cc007ba9c361fd8377fbe6215805d14d3830fe0fe5e01fe8200b000000b2d493bf5d448d0ec5d323fdc559806ede120dd6683a9ece6268370f6b069b3e0411fb58ffff7f2000015ffe0101000000010000000000000000000000000000000000000000000000000000000000000000ffffffff4b02f101040411fb5808fabe6d6d0000000000000000000000000000000000000000000000000000000000000000010000000000000049cfb314000000000d4d696e65642062792052696365000000000140be4025000000001976a9145f16616eea6ba068480c49b0201011e0d158a36c88ac00000000"
        val b = Block.fromBytes(raw.hexToByteArray())

        assertEquals("0000000b20e81fe0e50ffe30384dd1055821e6fb7783fd61c3a97b00cc464c0e", b.preBlockHash)
        assertEquals("0000001803382e2e35ac47fb0833f5d725663190b4a7f831019f37efcab91031", b.hash)
        assertEquals(1, b.txs.size)
        assertEquals("3e9b066b0f376862ce9e3a68d60d12de6e8059c5fd23d3c50e8d445dbf93d4b2", b.txs.first().id)
        assertEquals("3e9b066b0f376862ce9e3a68d60d12de6e8059c5fd23d3c50e8d445dbf93d4b2", b.merkleRootHash)
        assertEquals(4267639040.toInt(), b.nonce)
        assertEquals("76a9145f16616eea6ba068480c49b0201011e0d158a36c88ac", b.txs.first().txOuts.first().pubkeyScript.toHexString())
        assertEquals("02f101040411fb5808fabe6d6d0000000000000000000000000000000000000000000000000000000000000000010000000000000049cfb314000000000d4d696e65642062792052696365", b.txs.first().txIns.first().signatureScript.toHexString())

        assertEquals(raw, b.toBytes().toHexString())
    }
}