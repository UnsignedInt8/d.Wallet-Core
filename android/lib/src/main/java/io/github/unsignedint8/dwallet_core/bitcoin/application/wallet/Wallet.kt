package io.github.unsignedint8.dwallet_core.bitcoin.application.wallet

import io.github.unsignedint8.dwallet_core.bitcoin.application.bip32.*
import io.github.unsignedint8.dwallet_core.bitcoin.application.bip39.*
import io.github.unsignedint8.dwallet_core.bitcoin.protocol.structures.*
import io.github.unsignedint8.dwallet_core.bitcoin.script.*
import io.github.unsignedint8.dwallet_core.infrastructure.*
import java.security.SecureRandom


/**
 * Created by unsignedint8 on 8/24/17.
 */

open class Wallet private constructor(val masterXprvKey: ExtendedKey, externalKeys: List<ExtendedKey>, changeKeys: List<ExtendedKey>, val coin: CoinType = CoinType.Bitcoin) : Event() {

    val externalPrivKeys = mutableListOf<ExtendedKey>()
    val changePrivKeys = mutableListOf<ExtendedKey>()
    private val cachedTxs = mutableMapOf<String, Transaction>()
    private val utxos = mutableMapOf<String, Transaction>()

    /**
     * As the ExtendedKey can't support hardened derivation keys, so we use m/44/0/0/0/0 to navigate private keys
     * m / purpose' / coin_type' / account' / change / address_index
     */
    init {
        externalKeys.forEach { externalPrivKeys.add(it) }

        if (externalKeys.size < externalKeysAmount) {
            val begin = externalKeys.size
            (begin..externalKeysAmount).mapTo(externalPrivKeys) { masterXprvKey.derive(44).derive(coin.raw).derive(0).derive(0).derive(it) }
        }

        if (changeKeys.size < changeKeysAmount) {
            val begin = changeKeys.size
            (begin..changeKeysAmount).mapTo(changePrivKeys) { masterXprvKey.derive(44).derive(coin.raw).derive(0).derive(1).derive(it) }
        }
    }

    companion object {

        fun fromMnemonic(mnemonic: String, passphrase: String = "", coin: CoinType = CoinType.Bitcoin): Wallet {
            val seed = BIP39.mnemonicToSeed(mnemonic, passphrase)
            val seedHash = Hash(seed).getHmacSHA512()
            val masterPrivateKey = ExtendedKey(seedHash)

            return fromMasterXprvKey(masterPrivateKey, coin = coin)
        }

        fun fromMasterXprvKey(masterPrivateKey: ExtendedKey, externalKeys: List<ExtendedKey> = listOf(), changeKeys: List<ExtendedKey> = listOf(), coin: CoinType = CoinType.Bitcoin) = Wallet(masterPrivateKey, externalKeys, changeKeys, coin)

        fun fromMasterXprvKey(masterPrivateKey: String, externalKeys: List<String> = listOf(), changeKeys: List<String> = listOf(), coin: CoinType = CoinType.Bitcoin): Wallet? {
            return try {
                val master = ExtendedKey.parse(masterPrivateKey, true)
                val external = externalKeys.map { ExtendedKey.parse(it, true) }
                val change = changeKeys.map { ExtendedKey.parse(it, true) }

                fromMasterXprvKey(master, external, change, coin)
            } catch (e: Exception) {
                null
            }
        }

        fun create(passphrase: String = "", coin: CoinType = CoinType.Bitcoin): Wallet {
            val mnemonic = BIP39.getMnemonic(SecureRandom.getSeed(256))
            return fromMnemonic(mnemonic, passphrase, coin)
        }

        var externalKeysAmount = 50
        var changeKeysAmount = 50
    }

    fun insertTransaction(tx: Transaction) {
        val usedUtxos = utxos.values.filter { utxo -> tx.txIns.any { it.txId == utxo.id } }
        usedUtxos.forEach { utxos.remove(it.id) }

    }

    fun isValidTx(tx: Transaction) = tx.txOuts.any { out ->
        val ops = Interpreter.scriptToOps(out.pubkeyScript)

        if (!Interpreter.isP2PKHOutScript(ops.map { it.first })) return@any false
        if (ops[2].second == null) return@any false

        return@any externalPrivKeys.any { it.ecKey!!.publicKeyHash!!.contentEquals(ops[2].second!!) } || changePrivKeys.any { it.ecKey!!.publicKeyHash!!.contentEquals(ops[2].second!!) }
    }
}