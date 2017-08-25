package io.github.unsignedint8.dwallet_core.bitcoin.application

import io.github.unsignedint8.dwallet_core.bitcoin.application.bip32.*
import io.github.unsignedint8.dwallet_core.bitcoin.application.bip39.*
import java.security.SecureRandom

/**
 * Created by unsignedint8 on 8/24/17.
 */

class Wallet private constructor(private val masterXprvKey: ExtendedKey) {

    init {

    }

    companion object {

        fun fromMnemonic(mnemonic: String, passphrase: String = ""): Wallet {
            val seed = BIP39.mnemonicToSeed(mnemonic, passphrase)
            val seedHash = Hash(seed).getHmacSHA512()
            val masterPrivateKey = ExtendedKey(seedHash)

            return fromMasterXprvKey(masterPrivateKey)
        }

        fun fromMasterXprvKey(masterPrivateKey: ExtendedKey, childPrivateKeys: List<ExtendedKey> = listOf()): Wallet {
            val wallet = Wallet(masterPrivateKey)

            return wallet
        }

        fun fromMasterXprvKey(masterPrivateKey: String, childPrivateKeys: List<String> = listOf()): Wallet? {
            return try {
                val master = ExtendedKey.parse(masterPrivateKey, true)
                val children = childPrivateKeys.map { ExtendedKey.parse(it, true) }
                fromMasterXprvKey(master, children)
            } catch (e: Exception) {
                null
            }
        }

        fun create(passphrase: String = ""): Wallet {
            val mnemonic = BIP39.getMnemonic(SecureRandom.getSeed(256))
            return fromMnemonic(mnemonic, passphrase)
        }
    }


}