package dWallet.u8

import dWallet.core.bitcoin.application.bip32.ExtendedKey
import dWallet.core.bitcoin.application.bip32.Hash
import dWallet.core.bitcoin.application.bip32.Seed
import dWallet.core.bitcoin.application.bip39.BIP39
import dWallet.core.crypto.Crypto
import dWallet.core.extensions.hexToByteArray
import dWallet.core.extensions.toHexString
import org.junit.Test
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


        cases.add(Pair("parade skill social future veteran cigar chef bleak federal benefit steel such car air embark music solid adult setup walk leader engage filter spider", "88839b846726aa832472d03699c832c45010a0da599660d8e2633560c818d770"))
        cases.add(Pair("surround field legend split clock sick talent time gospel dwarf slim dentist crunch release put vicious concert same dress bunker genuine immense voice cement", "9bb1d822e8e1f4f5b1eaba3726ba73ad0209666b91c1f52ed68880ff7c040e8d"))
        cases.add(Pair("tonight sing body seek over lucky antique struggle injury half verify winner duty mimic disease shrimp coffee tip spike model prize sudden suffer novel", "46d686db163b9f38b72fca90fabd50107317cb7f434c409654b5ca93de687622"))
        cases.add(Pair("salt north crush bunker rifle fragile collect shallow sad same lawsuit know scrap nephew label around garlic latin cram intact hope odor tomato head", "037108aeb40ba0661ad81781c5d296717c4271942ea888462fab7f7a4387d999"))

        assertEquals(true, cases.all { it.second == BIP39.decode(it.first).toHexString() })
    }

    @Test
    fun testDeserialization() {
        val cases = mutableListOf<Pair<String, String>>()
        cases.add(Pair("2952f95cefe041616f6f379ab649cf8b702ecf8e4acceaebdda4cc50e2bf1d7b", "citizen oak fire thank advice radar sad tragic one rather initial black actual guitar decrease flower turtle galaxy hard obvious athlete garbage invest have"))
        cases.add(Pair("f5e82717078a6ddc538a03e825f91bed", "vote donkey shift audit plug until evolve document trial cool eight swarm"))
        cases.add(Pair("16b59b6a426f2f302f73049a32ab8572394278982212357a", "birth proud surround luggage very object saddle gauge olive next throw tongue neither detail gauge drastic cube strategy"))
        cases.add(Pair("95b6cb48c7bc9c2a54496ae3eea790824b57e52b9637058f084555bc1b809b2f", "noble rent split month six benefit eye coil token inside tomorrow afraid rely verb purity shoulder airport joke bacon problem script scare hole trumpet"))
        cases.add(Pair("7f93397f750f70a26513de2732ed95ee", "legend oil garlic tube warfare eye nephew knock cheese number grace tackle"))
        cases.add(Pair("14c29fe840dd1c9f05d392ba13e4e1466b32ed0726a15f89", "below belt wheel like spike exhibit blanket inch ring palace debate mimic rebel isolate broken stage garbage enhance"))

        assertEquals(true, cases.all { it.second == BIP39.getMnemonic(it.first.hexToByteArray()) })
    }

    @Test
    fun testPBKDF2() {
        val code = "parade skill social future veteran cigar chef bleak federal benefit steel such car air embark music solid adult setup walk leader engage filter spider"
        val seed = BIP39.mnemonicToSeed(code)

        assertEquals("5dfff1b790f8f52389be98a72b2028259b56fb08849a025657bf4d7f8eb3ffe2a5d47a705f60efc9659181e1aac69c8a8697f4a99f88bcde2e5d20a08683215b", BIP39.pbkdf2(code.toCharArray(), ("mnemonic" + "222").toByteArray(), 2048, 64, "hmacsha256").toHexString())
    }

}