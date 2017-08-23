package io.github.unsignedint8.dwallet_core.crypto

import org.spongycastle.crypto.digests.RIPEMD160Digest
import java.security.MessageDigest

/**
 * Created by unsignedint8 on 8/14/17.
 */

fun sha1(data: ByteArray) = MessageDigest.getInstance("sha1").digest(data)

fun sha256(data: ByteArray) = MessageDigest.getInstance("sha-256").digest(data)

fun hash256(data: ByteArray) = sha256(sha256(data))

fun ripemd160(data: ByteArray) = RIPEMD160Digest().digest(data)

fun hash160(data: ByteArray) = ripemd160(sha256(data))