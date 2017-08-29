package dwallet.core.crypto

/**
 * Created by unsignedint8 on 8/22/17.
 */

import org.spongycastle.crypto.digests.RIPEMD160Digest

fun RIPEMD160Digest.digest(input: ByteArray): ByteArray {
    val output = ByteArray(digestSize)

    update(input, 0, input.size)
    doFinal(output, 0)

    return output
}

