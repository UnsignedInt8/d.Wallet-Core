package io.github.unsignedint8.dwallet_core

import org.junit.Test
import org.spongycastle.crypto.digests.RIPEMD160Digest
import io.github.unsignedint8.dwallet_core.crypto.*
import io.github.unsignedint8.dwallet_core.extensions.hexToByteArray
import io.github.unsignedint8.dwallet_core.extensions.toHexString
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
}