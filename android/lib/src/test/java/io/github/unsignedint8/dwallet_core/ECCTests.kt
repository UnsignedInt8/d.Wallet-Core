package io.github.unsignedint8.dwallet_core

import io.github.unsignedint8.dwallet_core.crypto.ECC
import org.junit.Test

/**
 * Created by unsignedint8 on 8/23/17.
 */

class ECCTests {

    @Test
    fun testSeed() {
        println(ECC.instance.generateKeyPair(byteArrayOf(0)).private)
        println(ECC.instance.generateKeyPair().private)
    }
}