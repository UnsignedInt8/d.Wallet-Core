package io.github.unsignedint8.dwallet_core.bitcoin.script

import android.util.Range

/**
 * Created by unsignedint8 on 8/22/17.
 * https://en.bitcoin.it/wiki/Script
 */

class Words {

    class Constants {

        companion object {
            const val OP_0 = 0x00.toByte()
            const val OP_FALSE = OP_0
            const val NA_LOW = 0x01.toByte()
            const val NA_HIGH = 0x4b.toByte()
            const val OP_PUSHDATA1 = 0x4c.toByte()
            const val OP_PUSHDATA2 = 0x4d.toByte()
            const val OP_PUSHDATA4 = 0x4e.toByte()
            const val OP_1NEGATE = 0x4f.toByte()
            const val OP_1 = 0x51.toByte()
            const val OP_TRUE = OP_1
            const val OP_2 = 0x52.toByte()
            const val OP_3 = 0x53.toByte()
            const val OP_4 = 0x54.toByte()
            const val OP_5 = 0x55.toByte()
            const val OP_6 = 0x56.toByte()
            const val OP_7 = 0x57.toByte()
            const val OP_8 = 0x58.toByte()
            const val OP_9 = 0x59.toByte()
            const val OP_10 = 0x5a.toByte()
            const val OP_11 = 0x5b.toByte()
            const val OP_12 = 0x5c.toByte()
            const val OP_13 = 0x5d.toByte()
            const val OP_14 = 0x5e.toByte()
            const val OP_15 = 0x5f.toByte()
            const val OP_16 = 0x60.toByte()
        }
    }

    class Flow {

        companion object {
            const val OP_NOP = 0x61.toByte()
            const val OP_IF = 0x63.toByte()
            const val OP_NOTIF = 0x64.toByte()
            const val OP_ELSE = 0x67.toByte()
            const val OP_ENDIF = 0x68.toByte()
            const val OP_VERIFY = 0x69.toByte()
            const val OP_RETURN = 0x6a.toByte()
        }
    }

    class Stack {

        companion object {
            const val OP_TOALTSTACK = 0x6b.toByte()
            const val OP_FROMALTSTACK = 0x6c.toByte()
            const val OP_IFDUP = 0x73.toByte()
            const val OP_DEPTH = 0x74.toByte()
            const val OP_DROP = 0x75.toByte()
            const val OP_DUP = 0x76.toByte()
            const val OP_NIP = 0x77.toByte()
            const val OP_OVER = 0x78.toByte()
            const val OP_PICK = 0x79.toByte()
            const val OP_ROLL = 0x7a.toByte()
            const val OP_ROT = 0x7b.toByte()
            const val OP_SWAP = 0x7c.toByte()
            const val OP_TUCK = 0x7d.toByte()
            const val OP_2DROP = 0x6d.toByte()
            const val OP_2DUP = 0x6e.toByte()
            const val OP_3DUP = 0x6f.toByte()
            const val OP_2OVER = 0x70.toByte()
            const val OP_2ROT = 0x71.toByte()
            const val OP_2SWAP = 0x72.toByte()
        }
    }

    class Splice {

        class Disabled {

            companion object {
                const val OP_CAT = 0x7e.toByte()
                const val OP_SUBSTR = 0x7f.toByte()
                const val OP_LEFT = 0x80.toByte()
                const val OP_RIGHT = 0x81.toByte()
            }
        }

        companion object {

            const val OP_SIZE = 0x82.toByte()
        }
    }

    class Bitwise {

        class Disabled {

            companion object {
                const val OP_INVERT = 0x83.toByte()
                const val OP_AND = 0x84.toByte()
                const val OP_OR = 0x85.toByte()
                const val OP_XOR = 0x86.toByte()
            }
        }

        companion object {
            const val OP_EQUAL = 0x87.toByte()
            const val OP_EQUALVERIFY = 0x88.toByte()
        }
    }

    class Arithmetic {

        /**
         * Note: Arithmetic inputs are limited to signed 32-bit integers, but may overflow their output.
         * If any input value for any of these commands is longer than 4 bytes, the script must abort and fail. If any opcode marked as disabled is present in a script - it must also abort and fail.
         */

        class Disabled {

            companion object {
                const val OP_2MUL = 0x8d.toByte()
                const val OP_2DIV = 0x8e.toByte()
                const val OP_MUL = 0x95.toByte()
                const val OP_DIV = 0x96.toByte()
                const val OP_MOD = 0x97.toByte()
                const val OP_LSHIFT = 0x98.toByte()
                const val OP_RSHIFT = 0x99.toByte()
            }
        }

        companion object {
            const val OP_1ADD = 0x8b.toByte()
            const val OP_1SUB = 0x8c.toByte()
            const val OP_NEGATE = 0x8f.toByte()
            const val OP_ABS = 0x90.toByte()
            const val OP_NOT = 0x91.toByte()
            const val OP_0NOTEQUAL = 0x92.toByte()
            const val OP_ADD = 0x93.toByte()
            const val OP_SUB = 0x94.toByte()
            const val OP_BOOLAND = 0x9a.toByte()
            const val OP_BOOLOR = 0x9b.toByte()
            const val OP_NUMEQUAL = 0x9c.toByte()
            const val OP_NUMEQUALVERIFY = 0x9d.toByte()
            const val OP_NUMNOTEQUAL = 0x9e.toByte()
            const val OP_LESSTHAN = 0x9f.toByte()
            const val OP_GREATERTHAN = 0xa0.toByte()
            const val OP_LESSTHANOREQUAL = 0xa1.toByte()
            const val OP_GREATERTHANOREQUAL = 0xa2.toByte()
            const val OP_MIN = 0xa3.toByte()
            const val OP_MAX = 0xa4.toByte()
            const val OP_WITHIN = 0xa5.toByte()
        }
    }

    class Crypto {

        companion object {
            const val OP_RIPEMD160 = 0xa6.toByte()
            const val OP_SHA1 = 0xa7.toByte()
            const val OP_SHA256 = 0xa8.toByte()
            const val OP_HASH160 = 0xa9.toByte()
            const val OP_HASH256 = 0xaa.toByte()
            const val OP_CODESEPARATOR = 0xab.toByte()
            const val OP_CHECKSIG = 0xac.toByte()
            const val OP_CHECKSIGVERIFY = 0xad.toByte()
            const val OP_CHECKMULTISIG = 0xae.toByte()
            const val OP_CHECKMULTISIGVERIFY = 0xaf.toByte()
        }
    }

    class Locktime {

        companion object {
            const val OP_CHECKLOCKTIMEVERIFY = 0xb1.toByte()
            const val OP_CHECKSEQUENCEVERIFY = 0xb2.toByte()
        }
    }

    class Pseudo {

        /**
         * These words are used internally for assisting with transaction matching. They are invalid if used in actual scripts.
         */
        companion object {
            const val OP_PUBKEYHASH = 0xfd.toByte()
            const val OP_PUBKEY = 0xfe.toByte()
            const val OP_INVALIDOPCODE = 0xff.toByte()
        }
    }

    class Reserved {

        /**
         * Any opcode not assigned is also reserved. Using an unassigned opcode makes the transaction invalid.
         */
        companion object {
            const val OP_RESERVED = 0x50.toByte()
            const val OP_VER = 0x62.toByte()
            const val OP_VERIF = 0x65.toByte()
            const val OP_VERNOTIF = 0x66.toByte()
            const val OP_RESERVED1 = 0x89.toByte()
            const val OP_RESERVED2 = 0x8a.toByte()
            const val OP_NOP1 = 0xb0.toByte()
            const val OP_NOP4 = 0xb3.toByte()
            const val OP_NOP10 = 0xb9.toByte()
        }
    }
}