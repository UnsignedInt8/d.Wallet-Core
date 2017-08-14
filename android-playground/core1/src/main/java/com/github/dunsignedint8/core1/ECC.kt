package com.github.dunsignedint8.core1

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.Security
import java.security.spec.ECGenParameterSpec
import java.security.Security.insertProviderAt


/**
 * Created by unsignedint8 on 8/14/17.
 */

object abo {
    init {
        Security.insertProviderAt(org.spongycastle.jce.provider.BouncyCastleProvider(), 1)
        println("abo init")
    }
}


fun generateKeyPair(): KeyPair? {
    abo
    abo
    abo
    val keyGen = KeyPairGenerator.getInstance("ECDsA", "SC")
    val ecSpec = ECGenParameterSpec("secp256k1")
    keyGen.initialize(ecSpec, SecureRandom())
    return keyGen.generateKeyPair()
}