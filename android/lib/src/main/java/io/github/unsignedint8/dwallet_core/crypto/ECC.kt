package io.github.unsignedint8.dwallet_core.crypto

import org.spongycastle.asn1.*
import org.spongycastle.jce.provider.*
import java.security.*
import java.security.spec.*


/**
 * Created by unsignedint8 on 8/23/17.
 */

class ECC private constructor() {

    companion object {
        val instance = ECC()
    }

    init {
        Security.insertProviderAt(BouncyCastleProvider(), 1)
    }

    fun generateKeyPair(seed: ByteArray = SecureRandom.getSeed(256)): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("ECDsA", "SC")
        val ecSpec = ECGenParameterSpec("secp256k1")
        keyGen.initialize(ecSpec, SecureRandom(seed))
        return keyGen.generateKeyPair()
    }

    fun signature(data: ByteArray) {
        Signature.getInstance("ECDSA", "SC")
    }
}