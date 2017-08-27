package dWallet.core.extensions

/**
 * Created by unsignedint8 on 8/27/17.
 */

fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)