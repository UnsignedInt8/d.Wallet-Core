package io.github.unsignedint8.dwallet_core

import io.github.unsignedint8.dwallet_core.extensions.*
import io.github.unsignedint8.dwallet_core.utils.*
import org.junit.Test
import org.junit.Assert.*

/**
 * Created by unsignedint8 on 8/14/17.
 */

class HashTests {
    @Test
    fun testToHex() {
        assertEquals("616263",  "abc".toByteArray().toHexString())
    }

    @Test
    fun testSHA256() {
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", sha256("abc".toByteArray()).toHexString())
    }
}