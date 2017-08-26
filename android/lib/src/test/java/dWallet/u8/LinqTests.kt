package dWallet.u8

import dWallet.core.extensions.*
import org.junit.Test
import org.junit.Assert.*

/**
 * Created by unsignedint8 on 8/19/17.
 */

class LinqTests {
    @Test
    fun testReduceSeed() {
        assertEquals("12345", listOf(1, 2, 3, 4, 5).reduce("", { item, seed -> seed + item.toString(10) }))
    }

    @Test
    fun testSkip() {
        assertArrayEquals(arrayOf(0, 2, 3), listOf(1, 2, 0, 2, 3).skip(2).toTypedArray())
        assertArrayEquals(arrayOf(1, 2, 0, 2, 3), listOf(1, 2, 0, 2, 3).skip(0).toTypedArray())
    }
}