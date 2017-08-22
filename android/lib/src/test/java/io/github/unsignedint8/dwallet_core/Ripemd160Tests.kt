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
    fun testHash160(){
        val input = "0450863AD64A87AE8A2FE83C1AF1A8403CB53F53E486D8511DAD8A04887E5B23522CD470243453A299FA9E77237716103ABC11A1DF38855ED6F2EE187E9C582BA6".hexToByteArray()

        val sha256 = sha256(input)
        assertEquals("600FFE422B4E00731A59557A5CCA46CC183944191006324A447BDB2D98D4B408".toLowerCase(), sha256.toHexString())

        val hash160 = ripemd160(sha256)
        assertEquals("010966776006953D5567439E5E39F86A0D273BEE".toLowerCase(), hash160.toHexString())

        assertEquals("010966776006953D5567439E5E39F86A0D273BEE".toLowerCase(), hash160(input).toHexString())
    }
}