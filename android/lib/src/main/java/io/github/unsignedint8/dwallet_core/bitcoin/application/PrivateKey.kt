package io.github.unsignedint8.dwallet_core.bitcoin.application

import io.github.unsignedint8.dwallet_core.extensions.toHexString
import org.spongycastle.asn1.*
import org.spongycastle.asn1.sec.SECNamedCurves
import org.spongycastle.crypto.generators.ECKeyPairGenerator
import org.spongycastle.crypto.params.ECDomainParameters
import org.spongycastle.crypto.params.ECKeyGenerationParameters
import org.spongycastle.crypto.params.ECPrivateKeyParameters
import org.spongycastle.crypto.params.ECPublicKeyParameters
import org.spongycastle.crypto.signers.ECDSASigner
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.math.BigInteger
import java.security.SecureRandom

/**
 * Created by unsignedint8 on 8/24/17.
 */


/**
 * Represents an elliptic curve keypair that we own and can use for signing transactions. Currently,
 * Bouncy Castle is used. In future this may become an interface with multiple implementations using different crypto
 * libraries. The class also provides a static method that can verify a signature with just the public key.
 *
 * source from https://github.com/bitcoin-labs/bitcoinj-minimal/blob/master/core/ECKey.java
 *
 */
class PrivateKey {

    val priv: BigInteger
    val pubKey: ByteArray

    @Transient private var pubKeyHash: ByteArray? = null

    /** Generates an entirely new keypair.  */
    constructor() {
        val generator = ECKeyPairGenerator()
        val keygenParams = ECKeyGenerationParameters(ecParams, secureRandom)
        generator.init(keygenParams)
        val keypair = generator.generateKeyPair()
        val privParams = keypair.private as ECPrivateKeyParameters
        val pubParams = keypair.public as ECPublicKeyParameters
        priv = privParams.d
        // The public key is an encoded point on the elliptic curve. It has no meaning independent of the curve.
        pubKey = pubParams.q.getEncoded(true)
    }

    /**
     * Creates an ECKey given only the private key. This works because EC public keys are derivable from their
     * private keys by doing a multiply with the generator value.
     */
    constructor(privKey: BigInteger) {
        this.priv = privKey
        this.pubKey = publicKeyFromPrivate(privKey)
    }

    /**
     * Output this ECKey as an ASN.1 encoded private key, as understood by OpenSSL or used by the BitCoin reference
     * implementation in its wallet storage format.
     */
    //    fun toASN1(): ByteArray {
//        try {
//            val baos = ByteArrayOutputStream(400)
//            val encoder = ASN1OutputStream(baos)
//
//            // ASN1_SEQUENCE(EC_PRIVATEKEY) = {
//            //   ASN1_SIMPLE(EC_PRIVATEKEY, version, LONG),
//            //   ASN1_SIMPLE(EC_PRIVATEKEY, privateKey, ASN1_OCTET_STRING),
//            //   ASN1_EXP_OPT(EC_PRIVATEKEY, parameters, ECPKPARAMETERS, 0),
//            //   ASN1_EXP_OPT(EC_PRIVATEKEY, publicKey, ASN1_BIT_STRING, 1)
//            // } ASN1_SEQUENCE_END(EC_PRIVATEKEY)
//            val seq = DERSequenceGenerator(encoder)
//            seq.addObject(DERInteger(1)) // version
//            seq.addObject(DEROctetString(priv.toByteArray()))
//            seq.addObject(DERTaggedObject(0, SECNamedCurves.getByName("secp256k1").getDERObject()))
//            seq.addObject(DERTaggedObject(1, DERBitString(pubKey)))
//            seq.close()
//            encoder.close()
//            return baos.toByteArray()
//        } catch (e: IOException) {
//            throw RuntimeException(e)  // Cannot happen, writing to memory stream.
//        }
//
//    }


    override fun toString(): String {
        val b = StringBuffer()
        b.append("pub:").append(pubKey.toHexString())
        b.append(" priv:").append(priv.toByteArray().toHexString())
        return b.toString()
    }

    /**
     * Returns the address that corresponds to the public part of this ECKey. Note that an address is derived from
     * the RIPEMD-160 hash of the public key and is not the public key itself (which is too large to be convenient).
     */
    fun toAddress(netId: ByteArray = Address.Network.BTC.Main.pubkeyHash) = Address(pubKey, netId)

    /**
     * Calcuates an ECDSA signature in DER format for the given input hash. Note that the input is expected to be
     * 32 bytes long.
     */
    fun sign(input: ByteArray): ByteArray {
        val signer = ECDSASigner()
        val privKey = ECPrivateKeyParameters(priv, ecParams)
        signer.init(true, privKey)
        val sigs = signer.generateSignature(input)

        // What we get back from the signer are the two components of a signature, r and s. To get a flat byte stream
        // of the type used by BitCoin we have to encode them using DER encoding, which is just a way to pack the two
        // components into a structure.
        try {
            val bos = ByteArrayOutputStream()
            val seq = DERSequenceGenerator(bos)
            seq.addObject(ASN1Integer(sigs[0]))
            seq.addObject(ASN1Integer(sigs[1]))
            seq.close()
            return bos.toByteArray()
        } catch (e: IOException) {
            throw RuntimeException(e)  // Cannot happen.
        }

    }

    /**
     * Verifies the given ASN.1 encoded ECDSA signature against a hash using the public key.
     * @param data Hash of the data to verify.
     * @param signature ASN.1 encoded signature.
     */
    fun verify(data: ByteArray, signature: ByteArray): Boolean {
        return PrivateKey.verify(data, signature, pubKey)
    }

    companion object {
        private val ecParams: ECDomainParameters

        private val secureRandom: SecureRandom
        private val serialVersionUID = -728224901792295832L

        init {
            // All clients must agree on the curve to use by agreement. BitCoin uses secp256k1.
            val params = SECNamedCurves.getByName("secp256k1")
            ecParams = ECDomainParameters(params.curve, params.g, params.n, params.h)
            secureRandom = SecureRandom()
        }

        /**
         * Construct an ECKey from an ASN.1 encoded private key. These are produced by OpenSSL and stored by the BitCoin
         * reference implementation in its wallet.
         */
        fun fromASN1(asn1privkey: ByteArray): PrivateKey {
            return PrivateKey(extractPrivateKeyFromASN1(asn1privkey))
        }

        /** Derive the public key by doing a point multiply of G * priv.  */
        private fun publicKeyFromPrivate(privKey: BigInteger): ByteArray {
            return ecParams.g.multiply(privKey).getEncoded(true)
        }

        /**
         * Verifies the given ASN.1 encoded ECDSA signature against a hash using the public key.
         * @param data Hash of the data to verify.
         * @param signature ASN.1 encoded signature.
         * @param pub The public key bytes to use.
         */
        fun verify(data: ByteArray, signature: ByteArray, pub: ByteArray): Boolean {
            val signer = ECDSASigner()
            val params = ECPublicKeyParameters(ecParams.curve.decodePoint(pub), ecParams)
            signer.init(false, params)
            try {
                val decoder = ASN1InputStream(signature)
                val seq = decoder.readObject() as DERSequence
                val r = seq.getObjectAt(0) as ASN1Integer
                val s = seq.getObjectAt(1) as ASN1Integer
                decoder.close()
                return signer.verifySignature(data, r.value, s.value)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        private fun extractPrivateKeyFromASN1(asn1privkey: ByteArray): BigInteger {
            // To understand this code, see the definition of the ASN.1 format for EC private keys in the OpenSSL source
            // code in ec_asn1.c:
            //
            // ASN1_SEQUENCE(EC_PRIVATEKEY) = {
            //   ASN1_SIMPLE(EC_PRIVATEKEY, version, LONG),
            //   ASN1_SIMPLE(EC_PRIVATEKEY, privateKey, ASN1_OCTET_STRING),
            //   ASN1_EXP_OPT(EC_PRIVATEKEY, parameters, ECPKPARAMETERS, 0),
            //   ASN1_EXP_OPT(EC_PRIVATEKEY, publicKey, ASN1_BIT_STRING, 1)
            // } ASN1_SEQUENCE_END(EC_PRIVATEKEY)
            //
            try {
                val decoder = ASN1InputStream(asn1privkey)
                val seq = decoder.readObject() as DERSequence
                assert(seq.size() == 4) { "Input does not appear to be an ASN.1 OpenSSL EC private key" }
                assert((seq.getObjectAt(0) as ASN1Integer).value == BigInteger.ONE) { "Input is of wrong version" }
                val key = seq.getObjectAt(1) as DEROctetString
                decoder.close()
                return BigInteger(key.octets)
            } catch (e: IOException) {
                throw RuntimeException(e)  // Cannot happen, reading from memory stream.
            }

        }
    }
}