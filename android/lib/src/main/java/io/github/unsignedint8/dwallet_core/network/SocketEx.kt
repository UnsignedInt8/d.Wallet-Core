package io.github.unsignedint8.dwallet_core.network

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.TimeoutException

/**
 * Created by unsignedint8 on 8/14/17.
 */

class SocketEx() : Socket() {

    var lastException: Exception? = null

    fun connect(host: String, port: Int, timeout: Int = Int.MAX_VALUE): Boolean {
        try {
            super.connect(InetSocketAddress(host, port), timeout)
            lastException = null
            return true
        } catch (e: Exception) {
            lastException = e
        }

        return false
    }

    suspend fun connectAsync(host: String, port: Int, timeout: Int = Int.MAX_VALUE): Deferred<Boolean> {
        return async(CommonPool) { connect(host, port, timeout) }
    }


}