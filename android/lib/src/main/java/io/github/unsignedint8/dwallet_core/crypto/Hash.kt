package io.github.unsignedint8.dwallet_core.crypto

import org.spongycastle.crypto.digests.RIPEMD160Digest
import java.security.MessageDigest

/**
 * Created by unsignedint8 on 8/14/17.
 */

fun sha1(data: ByteArray) = MessageDigest.getInstance("sha1").digest(data)

fun sha256(data: ByteArray) = MessageDigest.getInstance("sha-256").digest(data)

internal fun sha256(data: ByteArray, start: Int, len: Int, recursion: Int): ByteArray {
    if (recursion == 0) return data
    val md = MessageDigest.getInstance("SHA-256")
    md.update(data.sliceArray(start until start + len))
    return sha256(md.digest(), 0, 32, recursion - 1)
}

fun hash256(data: ByteArray) = sha256(sha256(data))

fun ripemd160(data: ByteArray) = RIPEMD160Digest().digest(data)

fun hash160(data: ByteArray) = ripemd160(sha256(data))