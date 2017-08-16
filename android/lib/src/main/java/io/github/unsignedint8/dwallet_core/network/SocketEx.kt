package io.github.unsignedint8.dwallet_core.network

import kotlinx.coroutines.experimental.*
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Created by unsignedint8 on 8/14/17.
 */

class SocketEx : Socket() {

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

    suspend fun connectAsync(host: String, port: Int, timeout: Int = Int.MAX_VALUE) = async(CommonPool) { connect(host, port, timeout) }

    fun read(size: Int = DEFAULT_BUFFER_SIZE): ByteArray {
        val data = ByteArray(size)
        val readBytes = inputStream.read(data)

        return data.take(readBytes).toByteArray()
    }

    suspend fun readAsync(size: Int = DEFAULT_BUFFER_SIZE) = async(CommonPool) { read(size) }

    fun write(data: ByteArray): Int {
        lastException = null

        return try {
            this.outputStream.write(data)
            this.outputStream.flush()
            data.size
        } catch (e: Exception) {
            lastException = e
            0
        }
    }

    fun writeAsync(data: ByteArray) = async(CommonPool) { write(data) }
}