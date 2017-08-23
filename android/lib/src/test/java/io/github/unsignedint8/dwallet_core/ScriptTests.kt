package io.github.unsignedint8.dwallet_core

import io.github.unsignedint8.dwallet_core.bitcoin.application.Address
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

        // answer https://blockchain.info/rawtx/09f691b2263260e71f363d1db51ff3100d285956a40cc0e4f8c8c2c4a80559b1
        var signScript = "4c500100000000000000000000000000000000000000000000000000000000000000000000003ba3edfd7a7b12b27ac72c3e67768f617fc81bc3888a51323a9fb8aa4b1e5e4a29ab5f49ffff001d1dac2b7c"
        val ops3 = Interpreter.scriptToOps(signScript.hexToByteArray())
        assertEquals("0100000000000000000000000000000000000000000000000000000000000000000000003BA3EDFD7A7B12B27AC72C3E67768F617FC81BC3888A51323A9FB8AA4B1E5E4A29AB5F49FFFF001D1DAC2B7C".toLowerCase(), ops3[0].second!!.toHexString())
        assertEquals(1, ops3.size)

        // puzzle https://blockchain.info/rawtx/a4bfa8ab6435ae5f25dae9d89e4eb67dfa94283ca751f393c1ddc5a837bbc31b

        signScript = "493046022100bc4add188fd3f3857b66b71f83c558b84ccae792aa549c90a93dc7b0842ac2d1022100e2648f9998a32a01a6d6c25e62d6f385b42b6bed896539ce95547a9ee76473d101210368e828b31aeddf82a53bdf803065aac378b7c330299189d277c1ff182724c967"
        val ops2 = Interpreter.scriptToOps(signScript.hexToByteArray())
        assertEquals("3046022100bc4add188fd3f3857b66b71f83c558b84ccae792aa549c90a93dc7b0842ac2d1022100e2648f9998a32a01a6d6c25e62d6f385b42b6bed896539ce95547a9ee76473d101", ops2.first().second!!.toHexString())
        assertEquals("1FMb8Jnn1jSh7yjDFfonC8xCCH3ittoEzB", Address(ops2.last().second!!).toString())
        assertEquals(true, Interpreter.checkOpsValidity(ops2.map { it.first }))

        val pubkeyScript = "aa206fe28c0ab6f1b372c1a6a246ae63f74f931e8365e15a089c68d619000000000087".hexToByteArray()
        val ops = Interpreter.scriptToOps(pubkeyScript)

        assertEquals(3, ops.size)
        assertArrayEquals(arrayOf(Words.Crypto.OP_HASH256.raw, 32.toByte(), Words.Bitwise.OP_EQUAL.raw), ops.map { it.first }.toTypedArray())
    }
}