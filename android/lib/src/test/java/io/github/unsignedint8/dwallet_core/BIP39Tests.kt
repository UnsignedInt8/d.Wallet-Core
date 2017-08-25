package io.github.unsignedint8.dwallet_core

import io.github.unsignedint8.dwallet_core.bitcoin.application.bip32.ExtendedKey
import io.github.unsignedint8.dwallet_core.bitcoin.application.bip32.Hash
import io.github.unsignedint8.dwallet_core.bitcoin.application.bip32.Seed
import io.github.unsignedint8.dwallet_core.bitcoin.application.bip39.BIP39
import io.github.unsignedint8.dwallet_core.crypto.Crypto
import io.github.unsignedint8.dwallet_core.extensions.reduce
import io.github.unsignedint8.dwallet_core.extensions.toBigInteger
import io.github.unsignedint8.dwallet_core.extensions.toHexString
import org.junit.Test
import java.security.SecureRandom
import org.junit.Assert.*

/**
 * Created by unsignedint8 on 8/25/17.
 */

class BIP39Tests {

    init {
        Crypto.setupCryptoProvider()
    }

    @Test
    fun testRandomMnemonic() {
        val cases = mutableListOf<Pair<String, String>>()

        val bitcore = "select scout crash enforce riot rival spring whale hollow radar rule sentence"
        var seed = BIP39.decode(bitcore, "")
        val privkey = Hash(seed).getHmacSHA512(Seed.BITCOIN_SEED)
        assertEquals("xprv9s21ZrQH143K3pRb4RjJqxjtXm4VwVpdADMhg1DZaQEHy2GLGv6M2o1xxM6vYBFjdpXjz6bpPnPk6hbsQfJaAGFCe6NJMbXLvCsPeCxKsaF", ExtendedKey(privkey).serializePrivate())

        kotlin.repeat(500) {
            val mnemonic = BIP39.getMnemonic(SecureRandom.getSeed(32))
            println(mnemonic)
            println(BIP39.decode(mnemonic, "").toHexString())
        }

        println("verification")

        cases.add(Pair("parade skill social future veteran cigar chef bleak federal benefit steel such car air embark music solid adult setup walk leader engage filter spider", "88839b846726aa832472d03699c832c45010a0da599660d8e2633560c818d770"))
        cases.add(Pair("surround field legend split clock sick talent time gospel dwarf slim dentist crunch release put vicious concert same dress bunker genuine immense voice cement", "9bb1d822e8e1f4f5b1eaba3726ba73ad0209666b91c1f52ed68880ff7c040e8d"))
        cases.add(Pair("tonight sing body seek over lucky antique struggle injury half verify winner duty mimic disease shrimp coffee tip spike model prize sudden suffer novel", "46d686db163b9f38b72fca90fabd50107317cb7f434c409654b5ca93de687622"))
        cases.add(Pair("salt north crush bunker rifle fragile collect shallow sad same lawsuit know scrap nephew label around garlic latin cram intact hope odor tomato head", "037108aeb40ba0661ad81781c5d296717c4271942ea888462fab7f7a4387d999"))
        cases.forEach {
            assertEquals(it.second, BIP39.decode(it.first, "").toHexString())
        }

        seed = BIP39.decode("chapter fiscal equip tooth add walk jeans noble arrest theory code hour couch grab juice style blood rug cute monkey believe spirit spend boss")
        val seedHash = Hash(seed).getHmacSHA512(Seed.BITCOIN_SEED)
        println(ExtendedKey(seedHash).serializePrivate() + " " + seedHash.size)
        println(seedHash.reduce("", { item, acc -> acc + item.toBigInteger().toInt() + " " }))
    }
}