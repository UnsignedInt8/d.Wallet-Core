package io.github.unsignedint8.dwallet_core

import io.github.unsignedint8.dwallet_core.bitcoin.application.PrivateKey
import io.github.unsignedint8.dwallet_core.bitcoin.script.Interpreter
import io.github.unsignedint8.dwallet_core.crypto.hash256
import io.github.unsignedint8.dwallet_core.extensions.*
import io.github.unsignedint8.dwallet_core.utils.BaseX
import org.junit.Test
import java.math.BigInteger
import org.junit.Assert.*

/**
 * Created by unsignedint8 on 8/24/17.
 */

class PrivateKeyTests {

    @Test
    fun testRandomPrivKey() {
        val scriptSign = "483045022100d68415c728150daa0abf58d1c530eb4e82ab50be73bcd60745eba2f856bcae02022011732cbdfbe0b811dae554c239e42227ee367d72faf707d4af6e321d0e69c4960121034bf9a0bd44d38531e1d714c0d9ccf77c1a1a92cda24b44e403e274ac8d424076"
        val ops = Interpreter.scriptToOps(scriptSign.hexToByteArray())
//        PrivateKey

        val wif = "cTf3uEpv9UuKLLmvvR3Zr2riiJ53FjjsUJZ35CWfT5ehiyc78uoW"
        var priv = BaseX.base58.decode(wif)
        val checksum = hash256(priv.take(priv.size - 4).toByteArray()).take(4).toByteArray().toHexString()
        val org = priv.skip(priv.size - 4).toHexString()
        priv = priv.take(priv.size - 4).toByteArray().skip(1)

//        var obj2 = PrivateKey(BigInteger("a8b0be69c813c976db90f4c074d7207b12a57a71e3fb04b0186871073f8ab618".hexToByteArray()))
//        val obj = PrivateKey(BigInteger(priv))

        val pp = PrivateKey(BigInteger("f554cfb1b5833c314b893275eddf53343bd7e4a82c5666c4d08077dff17b105f", 16))
        assertEquals(true, pp.priv == BigInteger("f554cfb1b5833c314b893275eddf53343bd7e4a82c5666c4d08077dff17b105f", 16))
        assertEquals("0271e80b9425e4116cb9bea13792f690d956788aefb4f39632f6cecf8a59508972", pp.pubKey.toHexString())
    }


}