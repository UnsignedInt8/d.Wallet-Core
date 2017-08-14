package io.github.unsignedint8.dwallet_core

/**
 * Created by unsignedint8 on 8/14/17.
 */


import io.github.unsignedint8.dwallet_core.network.SocketEx
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import org.junit.Assert.*

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
}
