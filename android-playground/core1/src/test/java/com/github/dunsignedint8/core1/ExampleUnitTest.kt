package com.github.dunsignedint8.core1

import org.junit.Test

import org.junit.Assert.*
import java.net.Inet6Address
import java.net.InetAddress

/**
 * Example local unit test, which will execute on the development machine (host).

 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {
        assertEquals(4, (2 + 2).toLong())
    }

    @Test
    fun testSocketEx() {
        val ex = SocketEx()
        assert(ex.connect("baidu.com", 80))
    }

    @Test
    fun testKeyPair() {
        val pair = generateKeyPair()
        println(pair)
        println(pair?.private ?: "")
        println(pair?.public ?: "")
    }


    @Test
    fun testInetAddr() {
        val addr = InetAddress.getByName("::1")
        println(addr.hostAddress)
        println(addr.address.size)

        val addr6 = Inet6Address.getByName("::ffff:a00:1")
        println(addr6.hostAddress)
        println(addr6.address.size)


    }
}