package io.github.unsignedint8.dwallet_core

import io.github.unsignedint8.dwallet_core.extensions.*
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
}