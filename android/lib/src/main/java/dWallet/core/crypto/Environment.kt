package dWallet.core.crypto

import java.security.*

/**
 * Created by unsignedint8 on 8/23/17.
 */

object Crypto {
    fun setupCryptoProvider() = Security.insertProviderAt(org.spongycastle.jce.provider.BouncyCastleProvider(), 1)
}