package com.github.dunsignedint8.core1

/**
 * Created by unsignedint8 on 8/14/17.
 */

import java.net.*
import kotlinx.coroutines.experimental.*
import java.io.IOException
import java.util.concurrent.TimeoutException

class SocketEx() {

    private var socket: Socket = Socket()

    constructor(host: String, port: Int) : this() {
        socket = Socket()
    }

    var lastException: Exception? = null
        private set

    val port: Int
        get() = socket.port

    fun connect(endpoint: SocketAddress, timeout: Int = 10 * 1000): Boolean {
        try {
            socket.connect(endpoint, timeout)
            lastException = null
            return true
        } catch (io: IOException) {
            lastException = io
            return false
        } catch (timeout: TimeoutException) {
            lastException = timeout
            return false
        } catch (e: Exception) {
            lastException = e
            return false
        }
    }

    fun connect(host: String, port: Int, timeout: Int = 10 * 1000): Boolean {
        return connect(InetSocketAddress(host, port), timeout)
    }

    fun connectAsync(host: String, port: Int, timeout: Int = 10 * 1000) = async(CommonPool) {
          connect(host, port, timeout)
    }
}