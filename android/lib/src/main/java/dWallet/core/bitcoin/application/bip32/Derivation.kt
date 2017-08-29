package dwallet.core.bitcoin.application.bip32


/**
 * Created by Jesion on 2015-01-19.
 */

class Derivation(private val root: ExtendedKey) {

    @Throws(Exception::class)
    fun derive(path: String): ExtendedKey {
        val p = path.split("/")
        if (p.size == 2) {
            val sequence = Integer.parseInt(p[1])
            return basic(sequence)
        } else if (p.size == 3) {
            val accountType = Integer.parseInt(p[2])
            val account = Integer.parseInt(p[1])
            return accountMaster(accountType, account)
        } else if (p.size == 4) {
            val accountType = Integer.parseInt(p[2])
            val account = Integer.parseInt(p[1])
            val key = Integer.parseInt(p[3])
            return accountKey(accountType, account, key)
        }
        throw Exception("Invalid derivation path")
    }


    /**
     * Level 1
     *
     * Path m/i
     *
     * @param sequence
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun basic(sequence: Int): ExtendedKey {
        return root.derive(sequence)
    }

    /**
     * Account Master
     *
     * Path m/k/0 or m/k/1
     *
     * @param account
     * @param type
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun accountMaster(type: Int, account: Int): ExtendedKey {
        if (type == 0) {
            return externalAccountMaster(account)
        } else if (type == 1) {
            return internalAccountMaster(account)
        }
        throw Exception("Account type not recognized")
    }

    /**
     * External Account Master
     *
     * Path m/k/0
     *
     * @param account
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun externalAccountMaster(account: Int): ExtendedKey {
        val base = root.derive(account)
        return base.derive(0)
    }

    /**
     * Internal Account Master
     *
     * Path m/k/1
     *
     * @param account
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun internalAccountMaster(account: Int): ExtendedKey {
        val base = root.derive(account)
        return base.derive(1)
    }

    /**
     * Account Key
     *
     * Path m/k/0/i or m/k/1/i
     *
     * @param accountType
     * @param account
     * @param key
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun accountKey(accountType: Int, account: Int, key: Int): ExtendedKey {
        val base = accountMaster(accountType, account)
        return base.derive(key)
    }
}
