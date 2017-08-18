package io.github.unsignedint8.dwallet_core.extensions

import kotlin.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Created by unsignedint8 on 8/14/17.
 */

fun ByteArray.sliceArray(start: Int, endExclusive: Int = this.size) = sliceArray(IntRange(start, endExclusive - 1))

// https://gist.github.com/fabiomsr/845664a9c7e92bafb6fb0ca70d4e44fd

val HEX_CHARS = "0123456789abcdef".toCharArray()

fun ByteArray.toHexString(): String {
    val result = StringBuffer()

    forEach {
        val octet = it.toInt()
        val firstIndex = (octet and 0xF0).ushr(4)
        val secondIndex = octet and 0x0F
        result.append(HEX_CHARS[firstIndex])
        result.append(HEX_CHARS[secondIndex])
    }

    return result.toString()
}

fun ByteArray.readInt16LE(offset: Int = 0) = ByteBuffer.wrap(this).order(ByteOrder.LITTLE_ENDIAN).getShort(offset)
fun ByteArray.readInt16BE(offset: Int = 0) = ByteBuffer.wrap(this).order(ByteOrder.BIG_ENDIAN).getShort(offset)
fun ByteArray.writeInt16LE(n: Short, offset: Int = 0) = ByteBuffer.wrap(this).order(ByteOrder.LITTLE_ENDIAN).putShort(offset, n)
fun ByteArray.writeInt16BE(n: Short, offset: Int = 0) = ByteBuffer.wrap(this).order(ByteOrder.BIG_ENDIAN).putShort(offset, n)

fun ByteArray.readInt32LE(offset: Int = 0) = ByteBuffer.wrap(this).order(ByteOrder.LITTLE_ENDIAN).getInt(offset)
fun ByteArray.readInt32BE(offset: Int = 0) = ByteBuffer.wrap(this).order(ByteOrder.BIG_ENDIAN).getInt(offset)
fun ByteArray.writeInt32LE(n: Int, offset: Int = 0) = ByteBuffer.wrap(this).order(ByteOrder.LITTLE_ENDIAN).putInt(offset, n)
fun ByteArray.writeInt32BE(n: Int, offset: Int = 0) = ByteBuffer.wrap(this).order(ByteOrder.BIG_ENDIAN).putInt(offset, n)

fun ByteArray.readInt64LE(offset: Int = 0) = ByteBuffer.wrap(this).order(ByteOrder.LITTLE_ENDIAN).getLong(offset)
fun ByteArray.readInt64BE(offset: Int = 0) = ByteBuffer.wrap(this).order(ByteOrder.BIG_ENDIAN).getLong(offset)
fun ByteArray.writeInt64LE(n: Long, offset: Int = 0) = ByteBuffer.wrap(this).order(ByteOrder.LITTLE_ENDIAN).putLong(offset, n)
fun ByteArray.writeInt64BE(n: Long, offset: Int = 0) = ByteBuffer.wrap(this).order(ByteOrder.BIG_ENDIAN).putLong(offset, n)

/**
 * read the real value and size of it + tag
 */
fun ByteArray.readVarIntValueSize(): Pair<Long, Long> {
    val tag = this[0].toInt()
    var value: Long = tag.toLong()
    var size: Long = 1

    when (tag) {
        0xfd -> {
            value = this.readInt16LE(1).toLong()
            size += 2
        }

        0xfe -> {
            value = this.readInt32LE(1).toLong()
            size += 4
        }

        0xff -> {
            value = this.readInt64LE(1)
            size += 8
        }
    }

    return Pair(value, size)
}

/**
 * return the start offset of string, and length of it
 */
fun ByteArray.readVarStringOffsetLength(): Pair<Int, Long> {
    var offset = 1
    var length: Long = this[0].toLong()
    val tag = this[0].toInt()

    when (tag) {

        0xfd -> {
            offset = 3
            length = this.readInt16LE(1).toLong()
        }

        0xfe -> {
            offset = 5
            length = this.readInt32LE(1).toLong()
        }

        0xff -> {
            offset = 9
            val first = this.readInt32LE(1).toLong()
            val second = this.readInt32LE(5).toLong()
            length = (first * 0x100000000) + second
        }

    }

    return Pair(offset, length)
}

fun ByteArray.readVarString(): String {
    val (offset, len) = readVarStringOffsetLength()
    return String(this, offset, len.toInt())
}

fun <T> ByteArray.readVarList(deserializer: (bytes: ByteArray) -> Pair<T, Int>): List<T> {
    var (value, offset) = readVarIntValueSize()
    var data = this.sliceArray(offset.toInt())
    val list = mutableListOf<T>()

    kotlin.repeat(value.toInt()) {
        val (obj, length) = deserializer(data)
        offset += length
        data = this.sliceArray(offset.toInt())

        list.add(obj)
    }

    return list
}

fun ByteArray.toHashString(): String {
    val data = this.clone()
    data.reverse()
    return data.toHexString()
}