package io.github.unsignedint8.dwallet_core

import org.junit.Test
import org.spongycastle.crypto.digests.RIPEMD160Digest
import io.github.unsignedint8.dwallet_core.crypto.*
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
}