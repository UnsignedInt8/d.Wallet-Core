package io.github.unsignedint8.dwallet_core.bitcoin.application.wallet

/**
 * Created by unsignedint8 on 8/25/17.
 */

enum class CoinType(val raw: Int) {
    Bitcoin(0),
    BitcoinTestnet(1),
    Litecoin(1),
}