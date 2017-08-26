package io.github.unsignedint8.dwallet_core.bitcoin.application.bip32

/**
 * Created by unsignedint8 on 8/24/17.
 */


import io.github.unsignedint8.dwallet_core.extensions.*

import org.spongycastle.asn1.ASN1InputStream
import org.spongycastle.asn1.ASN1Integer
import org.spongycastle.asn1.DERSequenceGenerator
import org.spongycastle.asn1.DLSequence
import org.spongycastle.asn1.sec.SECNamedCurves
import org.spongycastle.crypto.digests.SHA256Digest
import org.spongycastle.crypto.params.ECDomainParameters
import org.spongycastle.crypto.params.ECPrivateKeyParameters
import org.spongycastle.crypto.params.ECPublicKeyParameters
import org.spongycastle.crypto.signers.ECDSASigner
import org.spongycastle.crypto.signers.HMacDSAKCalculator
import org.spongycastle.util.Arrays

import java.io.ByteArrayOutputStream
import java.math.BigInteger

/**
 * Created by Jesion on 2015-01-14.
 *
 * Elliptic Curve Key represents a pair of keys actually,
 * - Private which is a BigInteger
 * - Public which is an Elliptic Curve multiplication of private
 */

class ECKey {

    var priv: BigInteger? = null
        private set
    var public: ByteArray? = null
        private set
    var publicKeyHash: ByteArray? = null
        private set
    var isCompressed: Boolean = false
        private set

    //l is unsigned byte[] - coming from left part of bitcoin key hash
    //it can be converted to BigInt and saved as private master key
    constructor(l: ByteArray, compressed: Boolean) : this(l, compressed, true) {}

    //ECKey constructor with parent argument for key derivation
    constructor(l: ByteArray, parent: ECKey) {

        if (parent.hasPrivate()) {
            this.priv = BigInteger(1, l).add(parent.priv).mod(curve.n)
            setPub(parent.isCompressed, true, null)
        } else {
            throw Error("Support derived ECKey with public key only")
        }
    }

    //bytes is either coming from left part of bitcoin key hash
    //it can be converted to BigInt and saved as private master key
    //isPrivate is set to true in this case,
    //otherwise its a public key only
    constructor(bytes: ByteArray, compressed: Boolean, isPrivate: Boolean) {
        if (isPrivate) {
            this.priv = BigInteger(1, bytes)
            setPub(compressed, true, null)
        } else {
            setPub(compressed, false, bytes)
        }
    }

    @Throws(Exception::class)
    fun sign(message: ByteArray): ByteArray {
        if (priv == null) {
            throw Exception("Unable to sign")
        }
        val signer = ECDSASigner(HMacDSAKCalculator(SHA256Digest()))
        signer.init(true, ECPrivateKeyParameters(priv, params))
        val signature = signer.generateSignature(message)
        val outputStream = ByteArrayOutputStream()
        val seqGen = DERSequenceGenerator(outputStream)
        seqGen.addObject(ASN1Integer(signature[0]))
        seqGen.addObject(ASN1Integer(signature[1]))
        seqGen.close()
        return outputStream.toByteArray()
    }

    @Throws(Exception::class)
    fun verify(message: ByteArray, signature: ByteArray): Boolean {
        val asn1 = ASN1InputStream(signature)
        val signer = ECDSASigner()
        //not for signing...
        signer.init(false, ECPublicKeyParameters(curve.curve.decodePoint(public!!), params))
        val seq = asn1.readObject() as DLSequence
        val r = (seq.getObjectAt(0) as ASN1Integer).positiveValue
        val s = (seq.getObjectAt(1) as ASN1Integer).positiveValue
        return signer.verifySignature(message, r, s)
    }

    val private: ByteArray?
        get() {
            if (hasPrivate()) {
                var p = priv!!.toByteArray()
                if (p.size != 32) {
                    val tmp = ByteArray(32)
                    System.arraycopy(p, Math.max(0, p.size - 32), tmp, Math.max(0, 32 - p.size), Math.min(32, p.size))
                    p = tmp
                }
                return p
            }
            return null
        }

    val wif: String
        @Throws(Exception::class)
        get() = ByteUtil.toBase58(wifBytes)

    fun hasPrivate(): Boolean {
        return priv != null
    }

    val publicHex: String?
        get() = public?.toHexString()

    override fun equals(obj: Any?): Boolean = if (obj is ECKey) {
        Arrays.areEqual(obj.private, this.private)
                && Arrays.areEqual(obj.public, this.public)
                && Arrays.areEqual(obj.publicKeyHash, this.publicKeyHash)
                && obj.isCompressed == this.isCompressed

    } else false

    private fun setPub(compressed: Boolean, fromPrivate: Boolean, bytes: ByteArray?) {
        this.isCompressed = compressed
        if (fromPrivate) {
            public = curve.g.multiply(priv).getEncoded(compressed)
        } else {
            public = bytes
        }
        publicKeyHash = Hash(public!!).keyHash()
    }

    private val wifBytes: ByteArray
        @Throws(Exception::class)
        get() {
            if (hasPrivate()) {
                val k = private
                if (isCompressed == true) {
                    val encoded = ByteArray(k!!.size + 6)
                    val ek = ByteArray(k.size + 2)
                    ek[0] = 0x80.toByte()
                    System.arraycopy(k, 0, ek, 1, k.size)
                    ek[k.size + 1] = 0x01
                    val hash = Hash.hash(ek)
                    System.arraycopy(ek, 0, encoded, 0, ek.size)
                    System.arraycopy(hash, 0, encoded, ek.size, 4)
                    return encoded
                } else {
                    val encoded = ByteArray(k!!.size + 5)
                    val ek = ByteArray(k.size + 1)
                    ek[0] = 0x80.toByte()
                    System.arraycopy(k, 0, ek, 1, k.size)
                    val hash = Hash.hash(ek)
                    System.arraycopy(ek, 0, encoded, 0, ek.size)
                    System.arraycopy(hash, 0, encoded, ek.size, 4)
                    return encoded
                }
            } else {
                throw Exception("Won't provide WIF if no private key is present")
            }
        }

    object ECKeyParser {

        fun parse(wif: String): ECKey? {
            return try {
                parseBytes(ByteUtil.fromBase58(wif))
            } catch (e: Exception) {
                null
            }
        }

        @Throws(Exception::class)
        fun parseBytes(keyBytes: ByteArray): ECKey {
            checkChecksum(keyBytes)
            //decode uncompressed
            if (keyBytes.size == 37) {
                val key = ByteArray(keyBytes.size - 5)
                System.arraycopy(keyBytes, 1, key, 0, keyBytes.size - 5)
                return ECKey(key, false)
            } else if (keyBytes.size == 38) {
                val key = ByteArray(keyBytes.size - 6)
                System.arraycopy(keyBytes, 1, key, 0, keyBytes.size - 6)
                return ECKey(key, true)
            }//decode compressed
            throw Exception("Invalid key length")
        }

        @Throws(Exception::class)
        private fun checkChecksum(keyBytes: ByteArray) {
            val checksum = ByteArray(4)
            //last 4 bytes of key are checksum, copy it to checksum byte[]
            System.arraycopy(keyBytes, keyBytes.size - 4, checksum, 0, 4)
            val eckey = ByteArray(keyBytes.size - 4)
            //anything else is the EC key base, copy it to eckey
            System.arraycopy(keyBytes, 0, eckey, 0, keyBytes.size - 4)
            //now hash the eckey
            val hash = Hash.hash(eckey)
            for (i in 0..3) {
                //compare first 4 bytes of the key hash with corresponding positions in checksum bytes
                if (hash[i] != checksum[i]) {
                    throw Exception("checksum mismatch")
                }
            }
        }
    }

    companion object {
        val curve = SECNamedCurves.getByName("secp256k1")
        val params = ECDomainParameters(curve.curve, curve.g, curve.n, curve.h)
    }

}

