package io.github.unsignedint8.dwallet_core

import io.github.unsignedint8.dwallet_core.extensions.*
import org.junit.Test
import org.junit.Assert.*

/**
 * Created by unsignedint8 on 8/15/17.
 */

class BytesTest {

    @Test
    fun testInt32LE() {
        assertArrayEquals("05000000".hexToByteArray(), 5.toInt32LEBytes())
    }

    @Test
    fun testInt32BE() {
        assertArrayEquals("00000005".hexToByteArray(), 5.toInt32BEBytes())
    }

    @Test
    fun testInt16LE() {
        assertArrayEquals("0004".hexToByteArray(), 1024.toShort().toInt16LEBytes())
    }

    @Test
    fun testInt16BE() {
        assertArrayEquals("208D".hexToByteArray(), 8333.toShort().toInt16BEBytes())
    }

    @Test
    fun testVarString() {
        val varbytes = "/Satoshi:0.7.2/".toVarBytes()
        println(varbytes.toHexString())

        assertArrayEquals("0F2F5361746F7368693A302E372E322F".hexToByteArray(), varbytes)
    }
}