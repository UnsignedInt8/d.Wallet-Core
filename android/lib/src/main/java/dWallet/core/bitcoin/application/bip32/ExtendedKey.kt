package dWallet.core.bitcoin.application.bip32

import dWallet.core.bitcoin.application.wallet.Address
import dWallet.core.extensions.toHexString
import org.spongycastle.util.Arrays

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.io.ByteArrayOutputStream
import kotlin.experimental.and
import kotlin.experimental.or

/**
 * Created by Jesion on 2015-01-14.
 *
 * ExtendedKey class represents BIP32 key, which is able to provide derived keys according to the spec.
 * It composes ECKey class which is responsible for providing Elliptic Curve transformations required to support key derivation.
 */
class ExtendedKey {

    var chainCode: ByteArray? = null
        private set

    var ecKey: ECKey? = null
        private set

    private var sequence: Int = 0
    private var depth: Int = 0
    private var parentFingerprint: Int = 0

    /**
     * Constructing a derived key
     *
     * @param keyHash - Derived key hash
     * @param compressed - Indicates if public key is compressed for EC calculations
     * @param sequence - Derivation sequence
     * @param depth - Derivation depth
     * @param parentFingerprint - Parent key fingerprint
     * @param ecKey - Parent ECKey
     */
    constructor(keyHash: ByteArray, compressed: Boolean = true, sequence: Int = 0, depth: Int = 0, parentFingerprint: Int = 0, ecKey: ECKey? = null) {

        //key hash left side, private key base
        val l = Arrays.copyOfRange(keyHash, 0, 32)
        //key hash right side, chaincode
        val r = Arrays.copyOfRange(keyHash, 32, 64)
        //r is chainCode bytes
        this.chainCode = r
        this.sequence = sequence
        this.depth = depth
        this.parentFingerprint = parentFingerprint

        if (ecKey != null) {
            this.ecKey = ECKey(l, ecKey)
        } else {
            this.ecKey = ECKey(l, compressed)
        }
    }

    /**
     * Constructing a parsed key
     *
     * @param chainCode
     * @param sequence
     * @param depth
     * @param parentFingerprint
     * @param ecKey
     */
    constructor(chainCode: ByteArray, sequence: Int, depth: Int, parentFingerprint: Int, ecKey: ECKey) {
        this.chainCode = chainCode
        this.sequence = sequence
        this.depth = depth
        this.parentFingerprint = parentFingerprint
        this.ecKey = ecKey
    }

    @Throws(Exception::class)
    fun serializePublic(): String {
        return ExtendedKeySerializer().serialize(xpub,
                this.depth,
                this.parentFingerprint,
                this.sequence,
                this.chainCode,
                this.ecKey!!.public!!
        )
    }

    @Throws(Exception::class)
    fun serializePrivate(): String {
        if (ecKey?.hasPrivate() == true) {
            return ExtendedKeySerializer().serialize(xprv,
                    this.depth,
                    this.parentFingerprint,
                    this.sequence,
                    this.chainCode,
                    this.ecKey!!.private!!
            )
        }
        throw Exception("This is a public key only. Can't serialize a private key")
    }

    /**
     * Derives a child key from a valid instance of key
     * Currently only supports simple derivation (m/i', where m is master and i is level-1 derivation of master)
     * Key derivation spec is much richer and includes accounts with internal/external key chains as well, due to be implemented
     * @return
     */
    @Throws(Exception::class)
    fun derive(i: Int): ExtendedKey {
        return getChild(i)
    }

    @Throws(Exception::class)
    private fun getChild(i: Int): ExtendedKey {

        //Hmac hashing algo, which is using parents chainCode as its key
        val mac = Mac.getInstance("HmacSHA512", "SC")
        val key = SecretKeySpec(chainCode, "HmacSHA512")
        mac.init(key)
        //treating master's pub key as base... not sure why but simple m/i derivation goes by pub only but has to be tested a lot
        val pub = this.ecKey!!.public!!
        val child = ByteArray(pub.size + 4)
        System.arraycopy(pub, 0, child, 0, pub.size)

        //now some byte shifting
        child[pub.size] = (i.ushr(24) and 0xff).toByte()
        child[pub.size + 1] = (i.ushr(16) and 0xff).toByte()
        child[pub.size + 2] = (i.ushr(8) and 0xff).toByte()
        child[pub.size + 3] = (i and 0xff).toByte()

        val keyHash = mac.doFinal(child)
        return ExtendedKey(keyHash, this.ecKey!!.isCompressed, i, this.depth + 1, fingerPrint, this.ecKey)
    }

    /**
     * Gets an Integer representation of master public key hash
     * @return
     */
    val fingerPrint: Int
        get() {
            var fingerprint = 0
            for (i in 0..3) {
                fingerprint = fingerprint shl 8
                fingerprint = fingerprint or ((this.ecKey!!.publicKeyHash!![i] and 0xff.toByte()).toInt())
            }
            return fingerprint
        }

    /**
     * Gets a Wallet Import Format - a Base58 String representation of private key that can be imported to
     * - Electrum Wallet : if we are working with compressed public keys
     * - Armory Wallet : if we are working with uncompressed public keys
     * allowing to sweep funds sent to a corresponding address.
     * There is no easy way to support both, I would rather expect Armory to upgrade and support compressed keys
     * By default we are working with compressed keys, supporting Electrum wallet (see constructor of this class)
     * @throws Exception
     */
    val wif: String
        @Throws(Exception::class)
        get() = this.ecKey!!.wif

    /**
     * Gets an Address
     */
    fun toAddress(netId: ByteArray) = Address(public, netId)

    /**
     * Gets public key bytes
     */
    val public: ByteArray
        get() = this.ecKey!!.public!!

    /**
     * Gets public key hexadecimal string
     */
    val publicHex: String
        get() = public.toHexString()

    override fun equals(obj: Any?): Boolean {
        return if (obj is ExtendedKey) {
            ecKey!!.equals(obj.ecKey)
                    && Arrays.areEqual(chainCode, obj.chainCode)
                    && depth == obj.depth
                    && parentFingerprint == obj.parentFingerprint
                    && sequence == obj.sequence
        } else false
    }

    private inner class ExtendedKeySerializer {

        /**
         *
         * @param version
         * @param depth
         * @param parentFingerprint
         * @param sequence - Key derivation sequence
         * @param chainCode
         * @param keyBytes - Actual key bytes coming from the Elliptic Curve
         * @return
         * @throws Exception
         */
        @Throws(Exception::class)
        fun serialize(version: ByteArray,
                      depth: Int,
                      parentFingerprint: Int,
                      sequence: Int,
                      chainCode: ByteArray?,
                      keyBytes: ByteArray): String {
            val out = ByteArrayOutputStream()
            out.write(version)
            out.write(depth and 0xff)
            out.write(parentFingerprint.ushr(24) and 0xff)
            out.write(parentFingerprint.ushr(16) and 0xff)
            out.write(parentFingerprint.ushr(8) and 0xff)
            out.write(parentFingerprint and 0xff)
            out.write(sequence.ushr(24) and 0xff)
            out.write(sequence.ushr(16) and 0xff)
            out.write(sequence.ushr(8) and 0xff)
            out.write(sequence and 0xff)
            out.write(chainCode)
            if (version == xprv) {
                out.write(0x00)
            }
            out.write(keyBytes)
            return ByteUtil.toBase58WithChecksum(out.toByteArray());
        }
    }

    object ExtendedKeyParser {

        @Throws(Exception::class)
        fun parse(serialized: String, compressed: Boolean): ExtendedKey {
            val data = ByteUtil.fromBase58WithChecksum(serialized)
            if (data.size != 78) {
                throw Exception("Invalid extended key")
            }
            val type = Arrays.copyOf(data, 4)
            val hasPrivate: Boolean
            if (Arrays.areEqual(type, xprv)) {
                hasPrivate = true
            } else if (Arrays.areEqual(type, xpub)) {
                hasPrivate = false
            } else {
                throw Exception("Invalid or unsupported key type")
            }
            val depth = data[4] and 0xff.toByte()
            var parentFingerprint = data[5] and 0xff.toByte()
            parentFingerprint = (parentFingerprint.toInt() shl 8).toByte()
            parentFingerprint = parentFingerprint or (data[6] and 0xff.toByte())
            parentFingerprint = (parentFingerprint.toInt() shl 8).toByte()
            parentFingerprint = parentFingerprint or (data[7] and 0xff.toByte())
            parentFingerprint = (parentFingerprint.toInt() shl 8).toByte()
            parentFingerprint = parentFingerprint or (data[8] and 0xff.toByte())
            var sequence = data[9] and 0xff.toByte()
            sequence = (sequence.toInt() shl 8).toByte()
            sequence = sequence or (data[10] and 0xff.toByte())
            sequence = (sequence.toInt() shl 8).toByte()
            sequence = sequence or (data[11] and 0xff.toByte())
            sequence = (sequence.toInt() shl 8).toByte()
            sequence = sequence or (data[12] and 0xff.toByte())
            val chainCode = Arrays.copyOfRange(data, 13, 13 + 32)
            val keyBytes = Arrays.copyOfRange(data, 13 + 32, data.size)
            val ecKey = ECKey(keyBytes, compressed, hasPrivate)
            return ExtendedKey(chainCode, sequence.toInt(), depth.toInt(), parentFingerprint.toInt(), ecKey)
        }
    }

    companion object {

        private val xpub = byteArrayOf(0x04, 0x88.toByte(), 0xB2.toByte(), 0x1E.toByte())
        private val xprv = byteArrayOf(0x04, 0x88.toByte(), 0xAD.toByte(), 0xE4.toByte())

        /**
         * Takes a serialized key (public or private) and constructs an instance of ExtendedKey
         *
         * @param serialized
         * @param compressed
         * @return
         * @throws Exception
         */
        @Throws(Exception::class)
        fun parse(serialized: String, compressed: Boolean): ExtendedKey {
            return ExtendedKeyParser.parse(serialized, compressed)
        }
    }
}