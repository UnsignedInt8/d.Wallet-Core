package dWallet.u8

import dWallet.core.bitcoin.protocol.structures.*
import dWallet.core.extensions.*
import org.junit.Assert.*
import org.junit.Test

/**
 * Created by unsignedint8 on 8/21/17.
 */

class MerkleBlockTests {

    @Test
    fun testMerkleBlock() {
        val raw = "000000203110b9caef379f0131f8a7b490316625d7f53308fb47ac352e2e3803180000005dcad525acc0d9cc4fc483e999ddd0ff01b9d5c7583958b84524adcd670996750511fb58ffff7f2008f50eb901000000015dcad525acc0d9cc4fc483e999ddd0ff01b9d5c7583958b84524adcd670996750100".hexToByteArray()
        val b = MerkleBlock.fromBytes(raw)

        assertEquals("0000001803382e2e35ac47fb0833f5d725663190b4a7f831019f37efcab91031", b.preBlockHash)
        assertEquals(1, b.totalTxs)
        assertArrayEquals(arrayOf(0.toByte()), b.flags.toTypedArray())

        assertEquals(raw.toHexString(), b.toBytes().toHexString())

        println(b.hash)
    }

    @Test
    fun testMerkleBlock2() {
        val raw = "000000207f026bea3f187657d0da0165b5122484cb91b292f00cf76323617bc841c8f26997e9e1dc840597b09b0718c1e116e5073f2c3c0b3120e5d8c4585918942d481c35209959ffff7f20020000001500000015e998e96c234906c36554da9a65c89d3ad2f7465011bb5c0861bd4baf401b15aa8c0e129c7a25c70fe3743ca211360b78a014ce586c111e0bf3d2d1ee2bb2d76f2dcf64125fd10ecaafbc86712404c93cf424c2ec11ddd9d0b20a878e6bb6ec1e11b1693a38fff4dbd27162a6d3ecc36f196cd10fe4b00f07885cdbe2f65cb2ecff5091a2af58399b82af4bc3eac89dbf892113949c4c3527e62e098c6427d2d17e36d66a75e278f5dbd626293a25501599edef09d39678abd72d7a8ae989a3d5a238c93c61f322e53c9b8016f6871fd44fad5fe303cb68e4bbd667abc8f38f1e21df783456fb0218f56277ecda58293e380714f72c82ad58e7c6960973dc88ee4321e4d948a7760584c2c27ce16d8845a778563fee74828774ca9e323ca4c0fac33ace425b352babe3b44abeab7115d8d405753d6d144c04ec1666b6dd2f2fe7c78946387fabd470392078079423f8129015e20a49463e60302e872ea2a0dcf667fb551aadd906cc72e00232f818697cd6ad9e6cd934024cb6831dff9bc53e84d0604b75b6535ad9db1da5441d62e85e64bc4a0daef663d62bcb37e9a7676c225b8a2b900f84ec0ab057b3aaf64ceb8149e8b0baa9fe19758135543b15cf7d2a2bde61d37725a0f4bbe47d7468df286fbcdf353c17e95bb4ac6024219a7ad97959996671d568ca3724b5a2b39aa82970137033979bc8407085207aeae3081932b177835c43c0bf7399f2c9d4753f7017069d7eedd190babaeee03e7021a5374d8427d50f76af03f137a0a71a7c88a90a2dca9cba8994942f4b1797a06b648c7c5469d69f770c6ef4fafdd7d9ebf7cecb30f9115393397f8445fb9da95cd7e6dfb84efabf1687bba6b5e106d560c4a25029c556123c970cfdf50351abfebaf5b32a05e3197b56082e3ea102c91184511c3a8d4154d154b0c05b122d84f7dc6d9806dfffffffff0f".hexToByteArray()
        val b = MerkleBlock.fromBytes(raw)

        assertEquals("69f2c841c87b612363f70cf092b291cb842412b56501dad05776183fea6b027f", b.preBlockHash)
        assertEquals(21, b.totalTxs)
        assertArrayEquals(arrayOf(0xdf.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0x0f.toByte()), b.flags.toTypedArray())

        assertEquals(raw.toHexString(), b.toBytes().toHexString())

        println(b.hash)
    }

    @Test
    fun testMerkleBlock3() {
        val raw = "00000020fc0485f19747fa25a4c2620a017daddf2dd80040f2bc4e45c3b578db3198fe6438461f7e3a8a8356ac53c80ff99c6e088fade188cefc0c9fdb01443f9b6fe5a6d4b1dc58ffff7f2000000000010000000138461f7e3a8a8356ac53c80ff99c6e088fade188cefc0c9fdb01443f9b6fe5a60100"
        val b = MerkleBlock.fromBytes(raw.hexToByteArray())

        assertEquals("01e97e5a81d8f81044fb861f4487f55dd86041f210f843c31c24455590c57b6b", b.hash)
    }
}