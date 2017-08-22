package io.github.unsignedint8.dwallet_core.crypto

/**
 * Created by unsignedint8 on 8/22/17.
 * https://github.com/cryptocoinjs/base-x/blob/master/index.js
 */

class BaseX(val alpha: String) {

    private val alphaMap = mutableMapOf<Char, Int>()
    private val base = alpha.length
    private val leader = alpha.first()

    init {
        alpha.forEachIndexed { index, c -> alphaMap[c] = index }
    }

    

    companion object {
        const val base2 = "01"
        const val base8 = "01234567"
        const val base11 = "0123456789a"
        const val base16 = "0123456789abcdef"
        const val base32 = "0123456789ABCDEFGHJKMNPQRSTVWXYZ"
        const val base36 = "0123456789abcdefghijklmnopqrstuvwxyz"
        const val base58 = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
        const val base62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        const val base64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
        const val base66 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.!~"
    }
}