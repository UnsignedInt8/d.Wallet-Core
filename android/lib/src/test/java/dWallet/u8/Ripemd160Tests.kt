package dwallet.u8

import dwallet.core.bitcoin.application.wallet.Address
import dwallet.core.bitcoin.application.wallet.Coins
import org.junit.Test
import org.spongycastle.crypto.digests.RIPEMD160Digest
import dwallet.core.crypto.*
import dwallet.core.extensions.*
import dwallet.core.utils.BaseX
import java.nio.charset.Charset
import org.junit.Assert.*

/**
 * Created by unsignedint8 on 8/22/17.
 */

class Ripemd160Tests {

    @Test
    fun testDigest() {
        val input = "Rosetta Code".toByteArray(Charset.defaultCharset())
        val output = RIPEMD160Digest().digest(input)
        assertEquals("b3be159860842cebaa7174c8fff0aa9e50a5199f", output.toHexString())

        val out2 = RIPEMD160Digest().digest("UnsignedInt8".toByteArray())
        assertEquals("3dcdbd6079da245fd256a601127f1d9b9cf4c931", out2.toHexString())
    }

    @Test
    fun testHash160() {
        val input = "0450863AD64A87AE8A2FE83C1AF1A8403CB53F53E486D8511DAD8A04887E5B23522CD470243453A299FA9E77237716103ABC11A1DF38855ED6F2EE187E9C582BA6".hexToByteArray()

        val sha256 = sha256(input)
        assertEquals("600FFE422B4E00731A59557A5CCA46CC183944191006324A447BDB2D98D4B408".toLowerCase(), sha256.toHexString())

        val hash160 = ripemd160(sha256)
        assertEquals("010966776006953D5567439E5E39F86A0D273BEE".toLowerCase(), hash160.toHexString())

        assertEquals("010966776006953D5567439E5E39F86A0D273BEE".toLowerCase(), hash160(input).toHexString())
    }

    @Test
    fun testAddress() {
        val input = "0450863AD64A87AE8A2FE83C1AF1A8403CB53F53E486D8511DAD8A04887E5B23522CD470243453A299FA9E77237716103ABC11A1DF38855ED6F2EE187E9C582BA6".hexToByteArray()
        assertEquals("16UwLL9Risc3QfPqBUvKofHmBQ7wMtjvM", Address(input, Coins.Bitcoin.pubkeyHashId).toString())
        assertEquals(true, Address.validate("16UwLL9Risc3QfPqBUvKofHmBQ7wMtjvM"))
        assertEquals(false, Address.validate("16UwLL9Risc3QfPqBUvKofHmBQ7wMtjxM"))
    }

    @Test
    fun testBase58() {
        assertEquals("RPGNSU3bqTX", BaseX.base58.encode("91c81cbfdd58bbd2".hexToByteArray()))
        assertEquals("01111", BaseX.base2.encode("000f".hexToByteArray()))
        assertEquals("011111111", BaseX.base2.encode("00ff".hexToByteArray()))
        assertEquals("111111111111", BaseX.base2.encode("0fff".hexToByteArray()))
        assertEquals("11111111000000001111111100000000", BaseX.base2.encode("ff00ff00".hexToByteArray()))
        assertEquals("000f", BaseX.base16.encode("0000000f".hexToByteArray()))
        assertEquals("0fff", BaseX.base16.encode("000fff".hexToByteArray()))
        assertEquals("", BaseX.base58.encode("".hexToByteArray()))
        assertEquals("2g", BaseX.base58.encode("61".hexToByteArray()))
        assertEquals("a3gV", BaseX.base58.encode("626262".hexToByteArray()))
        assertEquals("aPEr", BaseX.base58.encode("636363".hexToByteArray()))
        assertEquals("2cFupjhnEsSn59qHXstmK2ffpLv2", BaseX.base58.encode("73696d706c792061206c6f6e6720737472696e67".hexToByteArray()))
        assertEquals("1NS17iag9jJgTHD1VXjvLCEnZuQ3rJDE9L", BaseX.base58.encode("00eb15231dfceb60925886b67d065299925915aeb172c06647".hexToByteArray()))
        assertEquals("ABnLTmg", BaseX.base58.encode("516b6fcd0f".hexToByteArray()))
        assertEquals("3SEo3LWLoPntC", BaseX.base58.encode("bf4f89001e670274dd".hexToByteArray()))
        assertEquals("3EFU7m", BaseX.base58.encode("572e4794".hexToByteArray()))
        assertEquals("EJDM8drfXA6uyA", BaseX.base58.encode("ecac89cad93923c02321".hexToByteArray()))
        assertEquals("Rt5zm", BaseX.base58.encode("10c8511e".hexToByteArray()))
        assertEquals("FPBt6CHo3fovdL", BaseX.base58.encode("ffffffffffffffffffff".hexToByteArray()))
        assertEquals("YcVfxkQb6JRzqk5kF2tNLv", BaseX.base58.encode("ffffffffffffffffffffffffffffffff".hexToByteArray()))
        assertEquals("5Hx15HFGyep2CfPxsJKe2fXJsCVn5DEiyoeGGF6JZjGbTRnqfiD", BaseX.base58.encode("801184cd2cdd640ca42cfc3a091c51d549b2f016d454b2774019c2b2d2e08529fd206ec97e".hexToByteArray()))
    }

    @Test
    fun testBase58decode() {
        assertEquals("91c81cbfdd58bbd2", BaseX.base58.decode("RPGNSU3bqTX").toHexString())
        assertEquals("000f", BaseX.base2.decode("01111").toHexString())
        assertEquals("00ff", BaseX.base2.decode("011111111").toHexString())
        assertEquals("0fff", BaseX.base2.decode("111111111111").toHexString())
        assertEquals("ff00ff00", BaseX.base2.decode("11111111000000001111111100000000").toHexString())
        assertEquals("0000000f", BaseX.base16.decode("000f").toHexString())
        assertEquals("000fff", BaseX.base16.decode("0fff").toHexString())
        assertEquals("", BaseX.base58.decode("").toHexString())
        assertEquals("61", BaseX.base58.decode("2g").toHexString())
        assertEquals("626262", BaseX.base58.decode("a3gV").toHexString())
        assertEquals("636363", BaseX.base58.decode("aPEr").toHexString())
        assertEquals("73696d706c792061206c6f6e6720737472696e67", BaseX.base58.decode("2cFupjhnEsSn59qHXstmK2ffpLv2").toHexString())
        assertEquals("00eb15231dfceb60925886b67d065299925915aeb172c06647", BaseX.base58.decode("1NS17iag9jJgTHD1VXjvLCEnZuQ3rJDE9L").toHexString())
        assertEquals("516b6fcd0f", BaseX.base58.decode("ABnLTmg").toHexString())
        assertEquals("bf4f89001e670274dd", BaseX.base58.decode("3SEo3LWLoPntC").toHexString())
        assertEquals("572e4794", BaseX.base58.decode("3EFU7m").toHexString())
        assertEquals("ecac89cad93923c02321", BaseX.base58.decode("EJDM8drfXA6uyA").toHexString())
        assertEquals("10c8511e", BaseX.base58.decode("Rt5zm").toHexString())
        assertEquals("ffffffffffffffffffff", BaseX.base58.decode("FPBt6CHo3fovdL").toHexString())
        assertEquals("ffffffffffffffffffffffffffffffff", BaseX.base58.decode("YcVfxkQb6JRzqk5kF2tNLv").toHexString())
        assertEquals("801184cd2cdd640ca42cfc3a091c51d549b2f016d454b2774019c2b2d2e08529fd206ec97e", BaseX.base58.decode("5Hx15HFGyep2CfPxsJKe2fXJsCVn5DEiyoeGGF6JZjGbTRnqfiD").toHexString())
    }

    @Test
    fun testPrivWIF() {
        val priv = "1E99423A4ED27608A15A2616A2B0E9E52CED330AC530EDCC32C8FFC6A526AEDD".hexToByteArray()
        val wif = BaseX.base58.encode(priv)
        println(wif)
    }
}