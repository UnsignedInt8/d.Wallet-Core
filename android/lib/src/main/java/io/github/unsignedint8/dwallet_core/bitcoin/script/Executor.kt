package io.github.unsignedint8.dwallet_core.bitcoin.script

import io.github.unsignedint8.dwallet_core.extensions.*
import java.math.BigInteger
import java.util.*

/**
 * Created by unsignedint8 on 8/22/17.
 */

/**
 * No any exceptions check
 */
class Executor {

    private val stack = Stack<BigInteger>()
    private val altStack = Stack<BigInteger>()

    private fun checkValidity(bytes: ByteArray) {

    }

    private fun parseToOpcodes(data: ByteArray, offset: Int = 0) {

    }

    fun parse(data: ByteArray) {

    }

    /**
     * Constants
     */
    fun OP_0() = stack.push(BigInteger.ZERO)

    fun OP_FALSE() = OP_0()

    fun OP_PUSHDATA1(data: ByteArray) {
        if (data.size > 255) throw IllegalArgumentException("size too large")
        stack.push(BigInteger(data))
    }

    fun OP_PUSHDATA2(data: ByteArray) {
        if (data.size > 65535) throw IllegalArgumentException("size too large")
        stack.push(BigInteger(data))
    }

    fun OP_PUSHDATA4(data: ByteArray) = stack.push(BigInteger(data))

    fun OP_PUSH(value: BigInteger) = stack.push(value)

    fun OP_1() = stack.push(BigInteger.ONE)

    fun OP_TRUE() = OP_1()

    /**
     * Flow control
     */
    fun OP_VERIFY() = stack.pop().compareTo(BigInteger.ZERO) !== 0

    /**
     * Stack ops
     */
    fun OP_TOALTSTACK() = altStack.push(stack.pop())

    fun OP_FROMALTSTACK() {
        if (altStack.empty()) return
        stack.push(altStack.pop())
    }

    fun OP_IFDUP() {
        if (stack.peek()?.compareTo(BigInteger.ZERO) == 0) return
        stack.push(stack.peek())
    }

    fun OP_DEPTH() = stack.push(BigInteger.valueOf(stack.size.toLong()))

    fun OP_DROP(): BigInteger? = stack.pop()

    fun OP_2DROP() = kotlin.repeat(2) { OP_DROP() }

    fun OP_DUP(count: Int = 1) = stack.skip(stack.size - count).take(count).forEach { stack.push(it) }

    fun OP_2DUP() = OP_DUP(2)

    fun OP_3DUP() = OP_DUP(3)

    fun OP_NIP() {
        val top = OP_DROP()
        OP_DROP()
        if (top != null) OP_PUSH(top)
    }

    fun OP_OVER() {
        val second = stack.size - 2
        stack.push(stack[stack.size - 2])
    }

    fun OP_PICK() {
        if (stack.empty()) return

        val n = stack.pop()
        val pos = stack.size - n.toInt()

        stack.push(stack[pos])
    }

    fun OP_ROT() {
        val items = stack.skip(stack.size - 3).take(3)
        kotlin.repeat(3) { stack.pop() }
        items.skip(1).forEach { stack.push(it) }
        stack.push(items[0])
    }

    fun OP_SWAP() {
        val top = stack.pop()
        val second = stack.pop()
        stack.push(top)
        stack.push(second)
    }

    /**
     * Bitwise
     */
    fun OP_EQUAL() {
        val a = stack.pop()
        val b = stack.pop()

        if (a.compareTo(b) == 0) {
            OP_1()
        } else {
            OP_0()
        }
    }

    fun OP_EQUALVERIFY(): Boolean {
        OP_EQUAL()
        return OP_VERIFY()
    }

    /**
     * Arithmetic
     */
    fun OP_1ADD() {
        stack.push(stack.pop().add(BigInteger.ONE))
    }
}