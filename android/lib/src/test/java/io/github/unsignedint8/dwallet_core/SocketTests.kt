package io.github.unsignedint8.dwallet_core

/**
 * Created by unsignedint8 on 8/14/17.
 */


import io.github.unsignedint8.dwallet_core.network.SocketEx
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import org.junit.Assert.*
import java.net.ServerSocket

class SocketTests {
    @Test
    fun testConnect() {
        val s = SocketEx()
        assert(s.connect("baidu.com", 80))
        assertEquals(null, s.lastException)
    }

    @Test
    fun testConnectAsync() = runBlocking {
        val s = SocketEx()
        assert(s.connectAsync("baidu.com", 80).await())
        assertEquals(null, s.lastException)
    }

    @Test
    fun testWrite() = runBlocking {

        async(CommonPool) {
            val server = ServerSocket(9999)
            val client = server.accept()
            val msg = client.getInputStream().bufferedReader().readLine()
            assertEquals("hello\n", msg)
        }

        delay(1000)
        val c1 = SocketEx()
        assert(c1.connectAsync("localhost", 9999).await())
        assertEquals(6, c1.writeAsync("hello\n".toByteArray()).await())
        delay(1000)
    }
}
