package dWallet.core.bitcoin.application.wallet

import dWallet.core.bitcoin.application.*
import dWallet.core.bitcoin.application.bip32.*
import dWallet.core.bitcoin.application.bip39.*
import dWallet.core.bitcoin.protocol.structures.*
import dWallet.core.bitcoin.script.*
import dWallet.core.infrastructure.*
import dWallet.core.extensions.*
import java.security.SecureRandom


/**
 * Created by unsignedint8 on 8/24/17.
 */

open class Wallet private constructor(val masterXprvKey: ExtendedKey, externalKeys: List<ECKey>, changeKeys: List<ECKey>, importedKeys: List<ECKey>, val coin: Coins = Coins.Bitcoin) : Event() {

    val externalPrivKeys = mutableListOf<ECKey>()
    val changePrivKeys = mutableListOf<ECKey>()
    val importedPrivKeys = mutableListOf<ECKey>()
    val allPrivKeys get() = externalPrivKeys + changePrivKeys + importedPrivKeys

    private val utxos = mutableMapOf<String, Transaction>()

    /**
     * As the ExtendedKey doesn't support hardened derivation keys, so we use m/44/0/0/0/0 to navigate private keys
     * m / purpose' / coin_type' / account' / change / address_index
     */
    init {
        externalKeys.forEach { externalPrivKeys.add(it) }
        changeKeys.forEach { changePrivKeys.add(it) }
        importedKeys.forEach { importedPrivKeys.add(it) }

        if (externalKeys.size < externalKeysAmount) {
            val begin = externalKeys.size
            (begin until externalKeysAmount).mapTo(externalPrivKeys) { masterXprvKey.derive(44).derive(coin.hdCoinId).derive(0).derive(0).derive(it).ecKey!! }
        }

        if (changeKeys.size < changeKeysAmount) {
            val begin = changeKeys.size
            (begin until changeKeysAmount).mapTo(changePrivKeys) { masterXprvKey.derive(44).derive(coin.hdCoinId).derive(0).derive(1).derive(it).ecKey!! }
        }
    }

    companion object {

        fun fromMnemonic(mnemonic: String, passphrase: String = "", coin: Coins = Coins.Bitcoin): Wallet {
            val seed = BIP39.mnemonicToSeed(mnemonic, passphrase)
            val seedHash = Hash(seed).getHmacSHA512()
            val masterPrivateKey = ExtendedKey(seedHash)

            return fromMasterXprvKey(masterPrivateKey, coin = coin)
        }

        fun fromMasterXprvKey(masterPrivateKey: ExtendedKey, externalKeys: List<ECKey> = listOf(), changeKeys: List<ECKey> = listOf(), importKeys: List<ECKey> = listOf(), coin: Coins = Coins.Bitcoin) = Wallet(masterPrivateKey, externalKeys, changeKeys, importKeys, coin)

        fun fromMasterXprvKey(masterPrivateKey: String, externalWIFKeys: List<String> = listOf(), changeWIFKeys: List<String> = listOf(), importedWIFKeys: List<String> = listOf(), coin: Coins = Coins.Bitcoin): Wallet? {
            return try {
                val master = ExtendedKey.parse(masterPrivateKey, true)
                val external = externalWIFKeys.map { ECKey.ECKeyParser.parse(it) }.filter { it != null } as List<ECKey>
                val change = changeWIFKeys.map { ECKey.ECKeyParser.parse(it) }.filter { it != null } as List<ECKey>
                val imported = importedWIFKeys.map { ECKey.ECKeyParser.parse(it) }.filter { it != null } as List<ECKey>

                fromMasterXprvKey(master, external, change, imported, coin)
            } catch (e: Exception) {
                null
            }
        }

        fun create(passphrase: String = "", coin: Coins = Coins.Bitcoin): Pair<Wallet, String> {
            val mnemonic = BIP39.getMnemonic(SecureRandom.getSeed(256))
            return Pair(fromMnemonic(mnemonic, passphrase, coin), mnemonic)
        }

        var externalKeysAmount = 50
        var changeKeysAmount = 50

        private object Events {
            const val balanceChanged = "BalanceChanged"
            const val utxoRemvoed = "UtxoRemoved"
            const val utxoAdded = "UtxoAdded"
        }
    }


    /**
     * Properties
     */

    var balance: Long = 0
        private set(value) {
            if (field == value) return
            field = value
            super.trigger(Events.balanceChanged, this, field)
        }

    val externalAddresses
        get() = externalPrivKeys.map { Address(it.public!!, coin.pubkeyHashId) }

    val changeAddresses
        get() = changePrivKeys.map { Address(it.public!!, coin.pubkeyHashId) }

    val importAddresses
        get() = importedPrivKeys.map { Address(it.public!!, coin.pubkeyHashId) }

    /**
     * Handling Private keys
     */

    fun insertWIF(wif: String): Boolean {
        if (importedPrivKeys.any { it.wif == wif }) return false
        val key = ECKey.ECKeyParser.parse(wif) ?: return false
        importedPrivKeys.add(key)
        return true
    }

    fun dumpExternalWIFKeys() = externalPrivKeys.map { it.wif }

    fun dumpChangeWIFKeys() = changePrivKeys.map { it.wif }

    fun dumpImportedWIFKeys() = importedPrivKeys.map { it.wif }

    fun dumpKeysToFilterItems() = allPrivKeys.map { it.publicKeyHash!! } + allPrivKeys.map { it.public!! }

    /**
     * Handling Txs
     */

    fun insertUtxo(tx: Transaction): Boolean {
        if (utxos.contains(tx.id)) return false

        val isOutgoTx = isOutgoTx(tx)
        val isIncomeTx = isIncomeTx(tx)
        if (!isOutgoTx && !isIncomeTx) return false

        if (isOutgoTx) {
            val usedUtxos = utxos.values.filter { utxo -> tx.txIns.any { it.txId == utxo.id } }
            usedUtxos.forEach { utxos.remove(it.id) }
            if (usedUtxos.isNotEmpty()) super.trigger(Events.utxoRemvoed, this, usedUtxos)
        }

        if (isIncomeTx) {
            utxos[tx.id] = tx

            balance = utxos.values.sum { tx ->
                tx.txOuts.filter { txOut ->
                    val ops = Interpreter.scriptToOps(txOut.pubkeyScript)
                    if (!Interpreter.isP2PKHOutScript(ops.map { it.first })) return@filter false
                    return@filter allPrivKeys.any { key -> key.publicKeyHash!!.contentEquals(ops[2].second!!) }
                }.sum { txOut -> txOut.value }
            }

            super.trigger(Events.utxoAdded, this, tx)
        }

        return true
    }

    fun insertUtxos(txs: Iterable<Transaction>) = txs.forEach { insertUtxo(it) }

    fun isUtxo(tx: Transaction) = insertUtxo(tx)

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

    fun isUserTx(tx: Transaction) = isOutgoTx(tx) || isIncomeTx(tx)

    fun onBalanceChanged(callback: (sender: Wallet, balance: Long) -> Unit) = super.register(Events.balanceChanged, callback as EventCallback)

    fun onUtxosRemoved(callback: (sender: Wallet, utxos: List<Transaction>) -> Unit) = super.register(Events.utxoRemvoed, callback as EventCallback)

    fun onUtxoAdded(callback: (sender: Wallet, utxo: Transaction) -> Unit) = super.register(Events.utxoAdded, callback as EventCallback)

    /**
     * Sending Tx
     */

    fun createTx(toAddress: String, amount: Long, fee: Long) {
        throw NotImplementedError()
    }
}