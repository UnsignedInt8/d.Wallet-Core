package dwallet.core.extensions

import java.math.BigInteger

/**
 * Created by unsignedint8 on 8/19/17.
 */

val Byte.Companion.ZERO: Byte
    get() = 0.toByte()

val Byte.Companion.FF: Byte
    get() = 0xff.toByte()

fun Byte.toBigInteger(): BigInteger {
    val unsigned = (if (this < 0) 256 + this.toInt() else this.toInt()) // why can't java have unsigned type!!!??? miaow miaow miaow???
    return BigInteger.valueOf(unsigned.toLong())
}
