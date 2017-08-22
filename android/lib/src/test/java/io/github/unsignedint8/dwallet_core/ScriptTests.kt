package io.github.unsignedint8.dwallet_core

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
}