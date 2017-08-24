package io.github.unsignedint8.dwallet_core.crypto

import java.security.*

/**
 * Created by unsignedint8 on 8/23/17.
 */

object Crypto {
    fun initSecurityEnvironment() = Security.insertProviderAt(org.spongycastle.jce.provider.BouncyCastleProvider(), 1)
}