package io.github.unsignedint8.dwallet_core.bitcoin.application.bip32

import org.spongycastle.crypto.digests.RIPEMD160Digest
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Created by unsignedint8 on 8/24/17.
 */

internal class Hash(private val input: ByteArray, private val rounds: Int = 50000, private val func: String = SHA256) {

    constructor(input: String, rounds: Int = 50000, func: String = SHA256) : this(input.toByteArray(), rounds, func)

    @Throws(Exception::class)
    fun hash(): ByteArray? {
        return if (func == SHA256) {
            sha256Hash(rounds)
        } else if (func == HmacSHA256) {
            hmacHash(rounds)
        } else {
            throw Error("Hashing function not supported")
        }
    }

    @Throws(Exception::class)
    private fun hmacHash(rounds: Int): ByteArray {
        val key = SecretKeySpec(input, HmacSHA256)
        val mac = Mac.getInstance(HmacSHA256)
        mac.init(key)
        var last = input
        for (i in 1..rounds) {
            last = mac.doFinal(last)
        }
        return last
    }

    /**
     * Used to generate a maser's key hash (using "Bitcoin seed" string as key)
     *
     * @param keyStr - hashing key
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getHmacSHA512(keyStr: String = Seed.BITCOIN_SEED): ByteArray {
        val key = SecretKeySpec(keyStr.toByteArray(), HmacSHA512)
        val mac = Mac.getInstance(HmacSHA512, "SC")
        mac.init(key)
        return mac.doFinal(this.input)
    }

    @Throws(Exception::class)
    fun getHmacSHA256(keyBytes: ByteArray): ByteArray {
        val key = SecretKeySpec(keyBytes, HmacSHA256)
        val mac = Mac.getInstance(HmacSHA256, "SC")
        mac.init(key)
        return mac.doFinal(this.input)
    }

    @Throws(Exception::class)
    private fun sha256Hash(rounds: Int): ByteArray? {
        val md = MessageDigest.getInstance(SHA256)
        var last: ByteArray? = null
        for (i in 1..rounds) {
            md.update(if (last == null) input else last)
            last = md.digest()
        }
        return last
    }

    /**
     * BIP32 Extended Key Public hash
     */
    fun keyHash(): ByteArray {
        val ph = ByteArray(20)
        try {
            val sha256 = MessageDigest.getInstance(SHA256).digest(input)
            val digest = RIPEMD160Digest()
            digest.update(sha256, 0, sha256.size)
            digest.doFinal(ph, 0)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }

        return ph
    }

    fun sha256(): ByteArray {
        var out = ByteArray(0)
        try {
            out = MessageDigest.getInstance(SHA256).digest(input)
        } catch (e: NoSuchAlgorithmException) {

        }

        return out
    }

    companion object {

        private val SHA256 = "SHA-256"
        private val HmacSHA256 = "HmacSHA256"
        private val HmacSHA512 = "HmacSHA512"

        /**
         * Used by Base58 with Checksum encoding for extended keys
         */
        @JvmOverloads
        fun hash(data: ByteArray, offset: Int = 0, len: Int = data.size): ByteArray {
            try {
                val a = MessageDigest.getInstance(SHA256)
                a.update(data, offset, len)
                return a.digest(a.digest())
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e)
            }

        }
    }
}