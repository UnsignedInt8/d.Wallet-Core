package dWallet.core.bitcoin.script

/**
 * Created by unsignedint8 on 8/22/17.
 * https://en.bitcoin.it/wiki/Script
 */

class Words {

    enum class Constants(val raw: Byte) {
        OP_0(0x00),
        OP_FALSE(OP_0.raw),
        NA_LOW(0x01),
        NA_HIGH(0x4b),
        OP_PUSHDATA1(0x4c),
        OP_PUSHDATA2(0x4d),
        OP_PUSHDATA4(0x4e),
        OP_1NEGATE(0x4f),
        OP_1(0x51),
        OP_TRUE(OP_1.raw),
        OP_2(0x52),
        OP_3(0x53),
        OP_4(0x54),
        OP_5(0x55),
        OP_6(0x56),
        OP_7(0x57),
        OP_8(0x58),
        OP_9(0x59),
        OP_10(0x5a),
        OP_11(0x5b),
        OP_12(0x5c),
        OP_13(0x5d),
        OP_14(0x5e),
        OP_15(0x5f),
        OP_16(0x60),
    }

    enum class Flow(val raw: Byte) {
        OP_NOP(0x61),
        OP_IF(0x63),
        OP_NOTIF(0x64),
        OP_ELSE(0x67),
        OP_ENDIF(0x68),
        OP_VERIFY(0x69),
        OP_RETURN(0x6a),
    }

    enum class Stack(val raw: Byte) {
        OP_TOALTSTACK(0x6b),
        OP_FROMALTSTACK(0x6c),
        OP_IFDUP(0x73),
        OP_DEPTH(0x74),
        OP_DROP(0x75),
        OP_DUP(0x76),
        OP_NIP(0x77),
        OP_OVER(0x78),
        OP_PICK(0x79),
        OP_ROLL(0x7a),
        OP_ROT(0x7b),
        OP_SWAP(0x7c),
        OP_TUCK(0x7d),
        OP_2DROP(0x6d),
        OP_2DUP(0x6e),
        OP_3DUP(0x6f),
        OP_2OVER(0x70),
        OP_2ROT(0x71),
        OP_2SWAP(0x72),
    }

    enum class Splice(val raw: Byte) {
        OP_SIZE(0x82.toByte()),
    }


    enum class Bitwise(val raw: Byte) {
        OP_EQUAL(0x87.toByte()),
        OP_EQUALVERIFY(0x88.toByte()),
    }

    enum class Arithmetic(val raw: Byte) {

        /**
         * Note: Arithmetic inputs are limited to signed 32-bit integers, but may overflow their output.
         * If any input value for any of these commands is longer than 4 bytes, the script must abort and fail. If any opcode marked as disabled is present in a script - it must also abort and fail.
         */

        OP_1ADD(0x8b.toByte()),
        OP_1SUB(0x8c.toByte()),
        OP_NEGATE(0x8f.toByte()),
        OP_ABS(0x90.toByte()),
        OP_NOT(0x91.toByte()),
        OP_0NOTEQUAL(0x92.toByte()),
        OP_ADD(0x93.toByte()),
        OP_SUB(0x94.toByte()),
        OP_BOOLAND(0x9a.toByte()),
        OP_BOOLOR(0x9b.toByte()),
        OP_NUMEQUAL(0x9c.toByte()),
        OP_NUMEQUALVERIFY(0x9d.toByte()),
        OP_NUMNOTEQUAL(0x9e.toByte()),
        OP_LESSTHAN(0x9f.toByte()),
        OP_GREATERTHAN(0xa0.toByte()),
        OP_LESSTHANOREQUAL(0xa1.toByte()),
        OP_GREATERTHANOREQUAL(0xa2.toByte()),
        OP_MIN(0xa3.toByte()),
        OP_MAX(0xa4.toByte()),
        OP_WITHIN(0xa5.toByte()),
    }

    enum class Crypto(val raw: Byte) {
        OP_RIPEMD160(0xa6.toByte()),
        OP_SHA1(0xa7.toByte()),
        OP_SHA256(0xa8.toByte()),
        OP_HASH160(0xa9.toByte()),
        OP_HASH256(0xaa.toByte()),
        OP_CODESEPARATOR(0xab.toByte()),
        OP_CHECKSIG(0xac.toByte()),
        OP_CHECKSIGVERIFY(0xad.toByte()),
        OP_CHECKMULTISIG(0xae.toByte()),
        OP_CHECKMULTISIGVERIFY(0xaf.toByte()),
    }

    enum class Locktime(val raw: Byte) {
        OP_CHECKLOCKTIMEVERIFY(0xb1.toByte()),
        OP_CHECKSEQUENCEVERIFY(0xb2.toByte()),
    }


    class Disabled {

        enum class Splice(val raw: Byte) {
            OP_CAT(0x7e),
            OP_SUBSTR(0x7f),
            OP_LEFT(0x80.toByte()),
            OP_RIGHT(0x81.toByte()),
        }

        enum class Bitwise(val raw: Byte) {
            OP_INVERT(0x83.toByte()),
            OP_AND(0x84.toByte()),
            OP_OR(0x85.toByte()),
            OP_XOR(0x86.toByte()),
        }

        enum class Arithmetic(val raw: Byte) {
            OP_2MUL(0x8d.toByte()),
            OP_2DIV(0x8e.toByte()),
            OP_MUL(0x95.toByte()),
            OP_DIV(0x96.toByte()),
            OP_MOD(0x97.toByte()),
            OP_LSHIFT(0x98.toByte()),
            OP_RSHIFT(0x99.toByte()),
        }

        enum class Pseudo(val raw: Byte) {

            /**
             * These words are used internally for assisting with transaction matching. They are invalid if used in actual scripts.
             */

            OP_PUBKEYHASH(0xfd.toByte()),
            OP_PUBKEY(0xfe.toByte()),
            OP_INVALIDOPCODE(0xff.toByte()),
        }

        enum class Reserved(val raw: Byte) {

            /**
             * Any opcode not assigned is also reserved. Using an unassigned opcode makes the transaction invalid.
             */

            OP_RESERVED(0x50.toByte()),
            OP_VER(0x62.toByte()),
            OP_VERIF(0x65.toByte()),
            OP_VERNOTIF(0x66.toByte()),
            OP_RESERVED1(0x89.toByte()),
            OP_RESERVED2(0x8a.toByte()),
            OP_NOP1(0xb0.toByte()),
            OP_NOP4(0xb3.toByte()),
            OP_NOP5(0xb4.toByte()),
            OP_NOP6(0xb5.toByte()),
            OP_NOP7(0xb6.toByte()),
            OP_NOP8(0xb7.toByte()),
            OP_NOP9(0xb8.toByte()),
            OP_NOP10(0xb9.toByte()),
        }
    }
}