# d.Wallet-Core
An API Platform for building Bitcoin wallet

## 1. Architecture

```
+-------------------------------------------------+--------+
|                                                 |        |
|   Application Layer Implementations             |        |
|                                                 |  Top   |
|   +---------+---------+----------+--------+     |  Level |
|   |  BIP32  |  BIP39  | SPV Node | Wallet |     |  APIs  |
|   +---------+---------+----------+--------+     |        |
|                                                 |        |
+-------------------------------------------------+--------+
|                                                 |        |
|   Bitcoin Protocol Implementations              |        |
|                                                 | Medium |
|   +----------+-----+------------------+         | Level  |
|   | Protocol | P2P | Scripting System |         | APIs   |
|   +----------+-----+------------------+         |        |
|                                                 |        |
+-------------------------------------------------+--------+
|                                                 |        |
|   Infrastructure                                |        |
|                                                 | Low    |
|   +--------+----------+------------+-------+    | Level  |
|   | Crypto | SocketEx | Extensions | Utils |    | APIs   |
|   +--------+----------+------------+-------+    |        |
|                                                 |        |
+-------------------------------------------------+--------+
```

## 2. APIs