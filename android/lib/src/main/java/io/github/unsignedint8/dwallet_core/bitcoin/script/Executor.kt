package io.github.unsignedint8.dwallet_core.bitcoin.script

import io.github.unsignedint8.dwallet_core.crypto.*
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

    fun OP_PUSH(value: BigInteger) = stack.push(value)

    fun OP_PUSH(data: ByteArray) = stack.push(BigInteger(data))

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

    fun OP_1SUB() {
        stack.push(stack.pop().subtract(BigInteger.ONE))
    }

    fun OP_NEGATE() {
        stack.push(stack.pop().multiply(BigInteger.valueOf(-1)))
    }

    fun OP_ABS() {
        stack.push(stack.pop().abs())
    }

    fun OP_NOT() {
        if (stack.pop().compareTo(BigInteger.ZERO) == 0) {
            OP_1()
        } else {
            OP_0()
        }
    }

    fun OP_0NOTEQUAL() {
        if (stack.pop().compareTo(BigInteger.ZERO) == 0) {
            OP_0()
        } else {
            OP_1()
        }
    }

    fun OP_ADD() {
        val a = stack.pop()
        val b = stack.pop()
        stack.push(a.add(b))
    }

    fun OP_SUB() {
        val a = stack.pop()
        val b = stack.pop()
        stack.push(b.minus(a))
    }

    fun OP_MUL() = stack.push(stack.pop().multiply(stack.pop()))

    fun OP_DIV() {
        val divisor = stack.pop()
        val dividend = stack.pop()
        stack.push(dividend.divide(divisor))
    }

    fun OP_MOD() {
        val divisor = stack.pop()
        val dividend = stack.pop()
        stack.push(dividend.mod(divisor))
    }

    fun OP_LSHIFT() {
        val n = stack.pop()
        val value = stack.pop()
        stack.push(value.shiftLeft(value.toInt()))
    }

    fun OP_RSHIFT() {
        val n = stack.pop()
        val value = stack.pop()
        stack.push(value.shiftRight(n.toInt()))
    }

    fun OP_BOOLAND() {
        val result = stack.pop().compareTo(BigInteger.ZERO) != 0 && stack.pop().compareTo(BigInteger.ZERO) != 0
        if (result)
            OP_1()
        else
            OP_0()
    }

    fun OP_BOOLOR() {
        val result = stack.pop().compareTo(BigInteger.ZERO) != 0 || stack.pop().compareTo(BigInteger.ZERO) != 0
        if (result)
            OP_1()
        else
            OP_0()
    }

    fun OP_NUMEQUAL() = OP_EQUAL()

    fun OP_NUMNOTEQUAL() {
        val a = stack.pop()
        val b = stack.pop()
        if (a.compareTo(b) != 0) {
            OP_1()
        } else {
            OP_0()
        }
    }

    fun OP_LESSTHAN() {
        val b = stack.pop()
        val a = stack.pop()
        if (a < b) {
            OP_1()
        } else {
            OP_0()
        }
    }

    fun OP_GREATERTHAN() {
        val b = stack.pop()
        val a = stack.pop()

        if (a > b) {
            OP_1()
        } else {
            OP_0()
        }
    }

    fun OP_LESSTHANOREQUAL() {
        val b = stack.pop()
        val a = stack.pop()

        if (a <= b) {
            OP_1()
        } else {
            OP_0()
        }
    }

    fun OP_GREATERTHANOREQUAL() {
        val b = stack.pop()
        val a = stack.pop()

        if (a >= b) {
            OP_1()
        } else {
            OP_0()
        }
    }

    fun OP_MIN() {
        val b = stack.pop()
        val a = stack.pop()

        if (a > b) {
            stack.push(b)
        } else {
            stack.push(a)
        }
    }

    fun OP_MAX() {
        val b = stack.pop()
        val a = stack.pop()

        if (a > b) {
            stack.push(a)
        } else {
            stack.push(b)
        }
    }

    fun OP_WITHIN() {
        val max = stack.pop()
        val min = stack.pop()
        val x = stack.pop()

        if (x >= min && x < max) {
            OP_1()
        } else {
            OP_0()
        }
    }

    fun OP_RIPEMD160() {
        OP_PUSH(ripemd160(stack.pop().toByteArray()))
    }

    fun OP_SHA1() {
        OP_PUSH(sha1(stack.pop().toByteArray()))
    }

    fun OP_SHA256() {
        OP_PUSH(sha256(stack.pop().toByteArray()))
    }

    fun OP_HASH160() {
        OP_PUSH(ripemd160(sha256(stack.pop().toByteArray())))
    }

    fun OP_HASH256() {
        OP_PUSH(sha256(sha256(stack.pop().toByteArray())))
    }


}