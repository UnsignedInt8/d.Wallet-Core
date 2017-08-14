package io.github.unsignedint8.dwallet_core.utils

import java.security.MessageDigest

/**
 * Created by unsignedint8 on 8/14/17.
 */

fun sha256(data: ByteArray) = MessageDigest.getInstance("sha-256").digest(data)

fun hash256(data: ByteArray) = sha256(sha256(data))

