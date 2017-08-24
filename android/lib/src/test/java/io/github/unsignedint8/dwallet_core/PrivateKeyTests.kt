package io.github.unsignedint8.dwallet_core

import io.github.unsignedint8.dwallet_core.bitcoin.application.Address
import io.github.unsignedint8.dwallet_core.bitcoin.application.PrivateKey
import io.github.unsignedint8.dwallet_core.bitcoin.application.bip32.Derivation
import io.github.unsignedint8.dwallet_core.bitcoin.application.bip32.ExtendedKey
import io.github.unsignedint8.dwallet_core.bitcoin.application.bip32.Hash
import io.github.unsignedint8.dwallet_core.bitcoin.application.bip32.Seed
import io.github.unsignedint8.dwallet_core.bitcoin.script.Interpreter
import io.github.unsignedint8.dwallet_core.crypto.Crypto
import io.github.unsignedint8.dwallet_core.crypto.hash256
import io.github.unsignedint8.dwallet_core.extensions.*
import io.github.unsignedint8.dwallet_core.utils.BaseX
import org.junit.Test
import java.math.BigInteger
import org.junit.Assert.*


/**
 * Created by unsignedint8 on 8/24/17.
 */

class PrivateKeyTests {

    init {
        Crypto.initSecurityEnvironment()
    }

    @Test
    fun testRandomPrivKey() {
        val scriptSign = "483045022100d68415c728150daa0abf58d1c530eb4e82ab50be73bcd60745eba2f856bcae02022011732cbdfbe0b811dae554c239e42227ee367d72faf707d4af6e321d0e69c4960121034bf9a0bd44d38531e1d714c0d9ccf77c1a1a92cda24b44e403e274ac8d424076"
        val ops = Interpreter.scriptToOps(scriptSign.hexToByteArray())
//        PrivateKey

        val wif = "cTf3uEpv9UuKLLmvvR3Zr2riiJ53FjjsUJZ35CWfT5ehiyc78uoW"
        var priv = BaseX.base58.decode(wif)
        val checksum = hash256(priv.take(priv.size - 4).toByteArray()).take(4).toByteArray().toHexString()
        val org = priv.skip(priv.size - 4).toHexString()
        priv = priv.take(priv.size - 4).toByteArray().skip(1)

//        var obj2 = PrivateKey(BigInteger("a8b0be69c813c976db90f4c074d7207b12a57a71e3fb04b0186871073f8ab618".hexToByteArray()))
//        val obj = PrivateKey(BigInteger(priv))

        val pp = PrivateKey(BigInteger("f554cfb1b5833c314b893275eddf53343bd7e4a82c5666c4d08077dff17b105f", 16))
        assertEquals(true, pp.priv == BigInteger("f554cfb1b5833c314b893275eddf53343bd7e4a82c5666c4d08077dff17b105f", 16))
        assertEquals("0271e80b9425e4116cb9bea13792f690d956788aefb4f39632f6cecf8a59508972", pp.pubKey.toHexString())
    }

    @Test
    fun testSignature() {
        // regtest tx

        val wifPriv = "cTf3uEpv9UuKLLmvvR3Zr2riiJ53FjjsUJZ35CWfT5ehiyc78uoW"

        var scriptSign = "483045022100d68415c728150daa0abf58d1c530eb4e82ab50be73bcd60745eba2f856bcae02022011732cbdfbe0b811dae554c239e42227ee367d72faf707d4af6e321d0e69c4960121034bf9a0bd44d38531e1d714c0d9ccf77c1a1a92cda24b44e403e274ac8d424076"
        val ops = Interpreter.scriptToOps(scriptSign.hexToByteArray())
        val signature = ops.first().second!!
        val pubkey = ops.last().second!!

        assertEquals("mnpbqSLQ3r293VHSjN82Ht63zf3PD8gBmm", Address(pubkey, Address.Network.BTC.Testnet.pubkeyHash).toString())

        scriptSign = "0411db93e1dcdb8a016b49840f8c53bc1eb68a382e97b1482ecad7b148a6909a5cb2e0eaddfb84ccf9744464f82e160bfa9b8b64f9d4c03f999b8643f656b412a3" // "47304402204e45e16932b8af514961a1d3a1a25fdf3f4f7732e9d624c6c61548ab5fb8cd410220181522ec8eca07de4860a4acdd12909d831cc56cbbac4622082221a8768d1d0901"
        val ops2 = Interpreter.scriptToOps(scriptSign.hexToByteArray())
    }

    @Test
    fun testUncompressedHDKey() {

        val passphraseHash = Hash("check this one *&##X87 check this 2 this passphrase 593 is tough to crack&@!!", 50000, "SHA-256").hash()!!
        assertEquals("30dde251aa487b04c7f35f2288efba6a6c89fd338aaad5cf60fc6b81fb23a476", passphraseHash.toHexString())

        val keyHash = Hash(passphraseHash).getHmacSHA512(Seed.BITCOIN_SEED)
        assertEquals("6d9e7e5fd8aa569c98f1bea704cbbb10252c76e8013bd69a7fefdc04822a09d4a24a2cae513d6c70c4076d685c56945b565810ee70bdb96198a2b60b83e1e7ba", keyHash.toHexString())

        val exKey = ExtendedKey(keyHash, false)
        assertEquals("a24a2cae513d6c70c4076d685c56945b565810ee70bdb96198a2b60b83e1e7ba", exKey.chainCode!!.toHexString())
        assertEquals("HarqLWPftSVzTwufETzTJ6jCT7jXuUxVDzzNg2t9qiUHME3687vYBVTyTLAFjH4exdNYCbFtGnzvgWHnCJSFA2jkCa7zj8hCnJSZv9yukLsmHGzFFXtYUYoXZGHS5SA8DDq6QS4D3gzi3UoDYMi7J4E2q9h", exKey.serializePublic())
        assertEquals("xprv9s21ZrQH143K3g56LSYgKiKx4LD4GJh27DUXRqShDpn2cAD4EAPbVn5T9BqqcGswHzcrztzJEJMHpS7wstai53cS6esPPkRGjp4voMhXrTP", exKey.serializePrivate())

//        val derivation = Derivation(exKey)
//        val child = derivation.derive("m/0/0/3")
//        assertEquals("HarqLYJ6Zz44AomHsPcCZrpnzJw8vvM1P6Jncf2KY4T5q3BQjQL4SygThSmN1G22WZfnDMJUR5RAKEQorypX6QYmYRc9FvqzSqpWkkT2rnmsrSrYgVUcRknZr7njDDyZxSupUSAVdGfnUgu8MY5V7QAA8Jf", child.serializePublic())
//        assertEquals("04e3f6246aa3de30bf41e2d4dae1ff89e131df957b22cadf43a38d5364e34f874a804bbfd1aca4e38fe10fc55e0d1f03641918f0a1cc64f4a3d49ea5b46bef563a", child.publicHex)
//        assertEquals("xprv9yRjeYDqF5X9XjLDomcEfK9QPZ3JGLcdaR6jaAeFnJ4a9Nem3aH4EzuZhh54pfg2XXGTYL6oCoHLHtnZa6Quqe5D1TLnhgYgbeoY6EfgeWN", child.serializePrivate())
//        assertEquals("1BBYqmkUmYrmcfK1WFw4GfLgQX7fxf7VDo", child.toAddress().toString())
    }

    @Test
    fun testDeserialization() {
        val passphraseHash = Hash("verb recall behave excuse use impulse cup derive scout teach hospital make caution anxiety differ").hash()!!
        val keyHash = Hash(passphraseHash).getHmacSHA512(Seed.BITCOIN_SEED)
//        println(keyHash.toHexString())
//        println(ExtendedKey(keyHash, true).serializePrivate())

        val key = ExtendedKey.parse("xprv9s21ZrQH143K2owk4zcoEBgttttZekAqXTwpMSs8L9dvQjAM4kEdEosP5QYUVbM1Us5kNt4TmBDVVdMffMQm9o44wEa4MLGjnHHc3siJrRB", true)
        assertEquals("0291b612168ac4bc762d3e6555de0933d02bfaef5fd47d4a69b573017442eab8bc", key.publicHex)
        assertEquals("1CmsagqC722cx5aUbm6dRWw5fcEcRpuVAG", key.toAddress().toString())

        val derivation = Derivation(key)
        val child = derivation.derive("m/0/0/3")
        assertEquals("xprv9yVEYKB5xCCVF1RBybgpwKRwjKthpVsqMioy8vdbAoxSkjG6iHYbk46KGGYv7u1S9Q7WNAexAorPmfJ2SHNpANvhHzSGnxvD7pXYYA3Seyj", child.serializePrivate())
        assertEquals("xprv9yVEYKB5xCCVF1RBybgpwKRwjKthpVsqMioy8vdbAoxSkjG6iHYbk46KGGYv7u1S9Q7WNAexAorPmfJ2SHNpANvhHzSGnxvD7pXYYA3Seyj", key.derive(0).derive(0).derive(3).serializePrivate())
    }
}