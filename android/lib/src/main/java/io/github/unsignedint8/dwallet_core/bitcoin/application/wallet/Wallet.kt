package io.github.unsignedint8.dwallet_core.bitcoin.application.wallet

import io.github.unsignedint8.dwallet_core.bitcoin.application.bip32.*
import io.github.unsignedint8.dwallet_core.bitcoin.application.bip39.*
import io.github.unsignedint8.dwallet_core.bitcoin.protocol.structures.*
import io.github.unsignedint8.dwallet_core.bitcoin.script.*
import io.github.unsignedint8.dwallet_core.infrastructure.*
import io.github.unsignedint8.dwallet_core.extensions.*
import java.security.SecureRandom


/**
 * Created by unsignedint8 on 8/24/17.
 */

open class Wallet private constructor(val masterXprvKey: ExtendedKey, externalKeys: List<ExtendedKey>, changeKeys: List<ExtendedKey>, val coin: Coins = Coins.Bitcoin) : Event() {

    private val externalPrivKeys = mutableListOf<ExtendedKey>()
    private val changePrivKeys = mutableListOf<ExtendedKey>()
    private val allPrivKeys = mutableListOf<ECKey>()
    private val cachedTxs = mutableMapOf<String, Transaction>()
    private val utxos = mutableMapOf<String, Transaction>()

    /**
     * As the ExtendedKey doesn't support hardened derivation keys, so we use m/44/0/0/0/0 to navigate private keys
     * m / purpose' / coin_type' / account' / change / address_index
     */
    init {
        externalKeys.forEach { externalPrivKeys.add(it) }

        if (externalKeys.size < externalKeysAmount) {
            val begin = externalKeys.size
            (begin..externalKeysAmount).mapTo(externalPrivKeys) { masterXprvKey.derive(44).derive(coin.hdCoinId).derive(0).derive(0).derive(it) }
        }

        if (changeKeys.size < changeKeysAmount) {
            val begin = changeKeys.size
            (begin..changeKeysAmount).mapTo(changePrivKeys) { masterXprvKey.derive(44).derive(coin.hdCoinId).derive(0).derive(1).derive(it) }
        }

        (externalPrivKeys + changePrivKeys).forEach { allPrivKeys.add(it.ecKey!!) }
        allPrivKeys.add(masterXprvKey.ecKey!!)
    }

    companion object {

        fun fromMnemonic(mnemonic: String, passphrase: String = "", coin: Coins = Coins.Bitcoin): Wallet {
            val seed = BIP39.mnemonicToSeed(mnemonic, passphrase)
            val seedHash = Hash(seed).getHmacSHA512()
            val masterPrivateKey = ExtendedKey(seedHash)

            return fromMasterXprvKey(masterPrivateKey, coin = coin)
        }

        fun fromMasterXprvKey(masterPrivateKey: ExtendedKey, externalKeys: List<ExtendedKey> = listOf(), changeKeys: List<ExtendedKey> = listOf(), coin: Coins = Coins.Bitcoin) = Wallet(masterPrivateKey, externalKeys, changeKeys, coin)

        fun fromMasterXprvKey(masterPrivateKey: String, externalKeys: List<String> = listOf(), changeKeys: List<String> = listOf(), coin: Coins = Coins.Bitcoin): Wallet? {
            return try {
                val master = ExtendedKey.parse(masterPrivateKey, true)
                val external = externalKeys.map { ExtendedKey.parse(it, true) }
                val change = changeKeys.map { ExtendedKey.parse(it, true) }

                fromMasterXprvKey(master, external, change, coin)
            } catch (e: Exception) {
                null
            }
        }

        fun create(passphrase: String = "", coin: Coins = Coins.Bitcoin): Wallet {
            val mnemonic = BIP39.getMnemonic(SecureRandom.getSeed(256))
            return fromMnemonic(mnemonic, passphrase, coin)
        }

        var externalKeysAmount = 50
        var changeKeysAmount = 50

        private object Events {
            const val balanceChanged = "BalanceChanged"
        }
    }

    var balance: Long = 0
        private set(value) {
            if (field == value) return
            field = value
            super.trigger(Events.balanceChanged, this, field)
        }

    val externalAddresses by lazy { externalPrivKeys.map { it.toAddress(coin.pubkeyHashId) } }

    val changeAddresses by lazy {}

    fun insertTx(tx: Transaction) {
        if (utxos.contains(tx.id)) return

        val usedUtxos = utxos.values.filter { utxo -> tx.txIns.any { it.txId == utxo.id } }
        usedUtxos.forEach { utxos.remove(it.id) }

        utxos[tx.id] = tx

        balance = utxos.values.sum { tx ->
            tx.txOuts.filter { txOut ->
                val ops = Interpreter.scriptToOps(txOut.pubkeyScript)
                if (!Interpreter.isP2PKHOutScript(ops.map { it.first })) return@filter false
                return@filter allPrivKeys.any { key -> key.publicKeyHash!!.contentEquals(ops[2].second!!) }
            }.sum { txOut -> txOut.value }
        }
    }

    fun isUtxo(tx: Transaction): Boolean {
        insertTx(tx)
        return utxos.contains(tx.id)
    }

    fun isIncomeTx(tx: Transaction) = tx.txOuts.any { out ->
        val ops = Interpreter.scriptToOps(out.pubkeyScript)

        if (!Interpreter.isP2PKHOutScript(ops.map { it.first })) return@any false
        if (ops[2].second == null) return@any false

        return@any allPrivKeys.any { it.publicKeyHash!!.contentEquals(ops[2].second!!) }
    }

    fun isOutgoTx(tx: Transaction) = tx.txIns.any { input ->
        val ops = Interpreter.scriptToOps(input.signatureScript)
        return@any allPrivKeys.any { ecKey -> ops.any { it.second != null && it.second!!.contentEquals(ecKey.public!!) } }
    }

    fun onBalanceChanged(callback: (sender: Wallet, balance: Long) -> Unit) = super.register(Events.balanceChanged, callback as Callback)

}