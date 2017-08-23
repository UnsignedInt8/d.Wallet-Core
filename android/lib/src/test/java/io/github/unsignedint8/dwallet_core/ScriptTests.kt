package io.github.unsignedint8.dwallet_core

import io.github.unsignedint8.dwallet_core.bitcoin.application.*
import io.github.unsignedint8.dwallet_core.bitcoin.script.*
import io.github.unsignedint8.dwallet_core.crypto.hash160
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

    @Test
    fun testStandardP2PKH() {
        var script = "48304502207fba64e8b5a5027ecc3acb6df9a666bc72feb668622b3b49f120967fd4b67a26022100978e3954c8ac40f19a80f787ad3ecce4f54b9a6155c7720246cfbf43c66e9a5f01410453e4880d737f41e8ece50b99f4524bad7ac10a6403fad016b7f606a09d0574bb13d5ae3ef3993f2c5f130987ce85a7796804f314b64a642a224c0c525b389dac"
        var ops = Interpreter.scriptToOps(script.hexToByteArray())
        assertEquals("19c7JHZoNK2XmvB1rznrjgpDfmxMvx2EWc", Address(ops.last().second!!).toString())

        script = "76a914494294730abf03c846988654f15d1864469c737a88ac"
        ops = Interpreter.scriptToOps(script.hexToByteArray())
        assertArrayEquals(arrayOf(Words.Stack.OP_DUP.raw, Words.Crypto.OP_HASH160.raw, 20.toByte(), Words.Bitwise.OP_EQUALVERIFY.raw, Words.Crypto.OP_CHECKSIG.raw), ops.map { it.first }.toTypedArray())
        assertEquals(true, Interpreter.isP2PKHScript(ops.map { it.first }))
    }

    @Test
    fun testStandardP2SH() {
        var script = "a91419a7d869032368fd1f1e26e5e73a4ad0e474960e87"
        var ops = Interpreter.scriptToOps(script.hexToByteArray())
        assertEquals(3, ops.size)
        assertEquals(true, Interpreter.isP2SHScript(ops.map { it.first }))
        assertEquals("342ftSRCvFHfCeFFBuz4xwbeqnDw6BGUey", Address.pubkeyHashToMultisignatureAddress("19a7d869032368fd1f1e26e5e73a4ad0e474960e".hexToByteArray()))
    }

    @Test
    fun testP2SHSignScript() {
        // answer https://blockchain.info/tx/6a26d2ecb67f27d1fa5524763b49029d7106e91e3cc05743073461a719776192
        // question https://blockchain.info/tx/9c08a4d78931342b37fd5f72900fb9983087e6f46c4a097d8a1f52c74e28eaf6

        val pubkeyScript = "a91419a7d869032368fd1f1e26e5e73a4ad0e474960e87"
        val pubkeyOps = Interpreter.scriptToOps(pubkeyScript.hexToByteArray())

        val signScript = "5121029b6d2c97b8b7c718c325d7be3ac30f7c9d67651bce0c929f55ee77ce58efcf8451ae"
        val signOps = Interpreter.scriptToOps(signScript.hexToByteArray())

        println(signOps[1].second!!.toHexString())
        println(hash160(signOps.last().second!!).toHexString())
    }
}