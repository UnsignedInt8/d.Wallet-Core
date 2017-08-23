package io.github.unsignedint8.dwallet_core

import io.github.unsignedint8.dwallet_core.bitcoin.script.*
import io.github.unsignedint8.dwallet_core.extensions.*
import org.junit.Test
import java.util.*
import org.junit.Assert.*

/**
 * Created by unsignedint8 on 8/22/17.
 */

class ScriptTests {

    @Test
    fun testStack() {
        val s = Stack<Int>()
        kotlin.repeat(5) { s.push(it) }
        assertArrayEquals(arrayOf(2, 1, 0), s.take(3).reversed().toTypedArray())
    }

    @Test
    fun testPuzzle() {
        // https://blockchain.info/rawtx/a4bfa8ab6435ae5f25dae9d89e4eb67dfa94283ca751f393c1ddc5a837bbc31b
        val pubkeyScript = "aa206fe28c0ab6f1b372c1a6a246ae63f74f931e8365e15a089c68d619000000000087".hexToByteArray()
        val ops = Interpreter.scriptToOps(pubkeyScript)

        assertEquals(3, ops.size)
        assertArrayEquals(arrayOf(Words.Crypto.OP_HASH256.raw, 32.toByte(), Words.Bitwise.OP_EQUAL.raw), ops.map { it.first }.toTypedArray())

        val signScript = "493046022100bc4add188fd3f3857b66b71f83c558b84ccae792aa549c90a93dc7b0842ac2d1022100e2648f9998a32a01a6d6c25e62d6f385b42b6bed896539ce95547a9ee76473d101210368e828b31aeddf82a53bdf803065aac378b7c330299189d277c1ff182724c967"
//        Interp
    }
}