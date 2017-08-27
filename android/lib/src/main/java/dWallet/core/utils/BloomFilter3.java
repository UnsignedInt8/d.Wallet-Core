package dWallet.core.utils;

/**
 * Created by unsignedint8 on 8/27/17.
 */

import static java.lang.Math.*;

public class BloomFilter3 {

    public enum BloomUpdate {
        UPDATE_NONE, // 0
        UPDATE_ALL, // 1
        /**
         * Only adds outpoints to the filter if the output is a pay-to-pubkey/pay-to-multisig script
         */
        UPDATE_P2PUBKEY_ONLY //2
    }

    public byte[] data;
    public long nHashFuncs;
    public long nTweak;
    public byte nFlags;

    // Same value as Bitcoin Core
    // A filter of 20,000 items and a false positive rate of 0.1% or one of 10,000 items and 0.0001% is just under 36,000 bytes
    private static final long MAX_FILTER_SIZE = 36000;
    // There is little reason to ever have more hash functions than 50 given a limit of 36,000 bytes
    private static final int MAX_HASH_FUNCS = 50;

    public BloomFilter3(int elements, double falsePositiveRate, long randomNonce, BloomUpdate updateFlag) {
        // The following formulas were stolen from Wikipedia's page on Bloom Filters (with the addition of min(..., MAX_...))
        //                        Size required for a given number of elements and false-positive rate
        int size = (int) (-1 / (pow(log(2), 2)) * elements * log(falsePositiveRate));
        size = max(1, min(size, (int) MAX_FILTER_SIZE * 8) / 8);
        data = new byte[size];
        // Optimal number of hash functions for a given filter size and element count.
        nHashFuncs = (int) (data.length * 8 / (double) elements * log(2));
        nHashFuncs = max(1, min(nHashFuncs, MAX_HASH_FUNCS));
        this.nTweak = randomNonce;
        this.nFlags = (byte) (0xff & updateFlag.ordinal());
    }

    public BloomFilter3(int elements, double falsePositiveRate) {
        this(elements, falsePositiveRate, 0, BloomUpdate.UPDATE_NONE);
    }

    /**
     * Returns the theoretical false positive rate of this filter if were to contain the given number of elements.
     */
    public double getFalsePositiveRate(int elements) {
        return pow(1 - pow(E, -1.0 * (nHashFuncs * elements) / (data.length * 8)), nHashFuncs);
    }

    private static int rotateLeft32(int x, int r) {
        return (x << r) | (x >>> (32 - r));
    }

    /**
     * Applies the MurmurHash3 (x86_32) algorithm to the given data.
     * See this <a href="https://github.com/aappleby/smhasher/blob/master/src/MurmurHash3.cpp">C++ code for the original.</a>
     */
    public static int murmurHash3(byte[] data, long nTweak, int hashNum, byte[] object) {
        int h1 = (int) (hashNum * 0xFBA4C795L + nTweak);
        final int c1 = 0xcc9e2d51;
        final int c2 = 0x1b873593;

        int numBlocks = (object.length / 4) * 4;
        // body
        for (int i = 0; i < numBlocks; i += 4) {
            int k1 = (object[i] & 0xFF) |
                    ((object[i + 1] & 0xFF) << 8) |
                    ((object[i + 2] & 0xFF) << 16) |
                    ((object[i + 3] & 0xFF) << 24);

            k1 *= c1;
            k1 = rotateLeft32(k1, 15);
            k1 *= c2;

            h1 ^= k1;
            h1 = rotateLeft32(h1, 13);
            h1 = h1 * 5 + 0xe6546b64;
        }

        int k1 = 0;
        switch (object.length & 3) {
            case 3:
                k1 ^= (object[numBlocks + 2] & 0xff) << 16;
                // Fall through.
            case 2:
                k1 ^= (object[numBlocks + 1] & 0xff) << 8;
                // Fall through.
            case 1:
                k1 ^= (object[numBlocks] & 0xff);
                k1 *= c1;
                k1 = rotateLeft32(k1, 15);
                k1 *= c2;
                h1 ^= k1;
                // Fall through.
            default:
                // Do nothing.
                break;
        }

        // finalization
        h1 ^= object.length;
        h1 ^= h1 >>> 16;
        h1 *= 0x85ebca6b;
        h1 ^= h1 >>> 13;
        h1 *= 0xc2b2ae35;
        h1 ^= h1 >>> 16;

        return (int) ((h1 & 0xFFFFFFFFL) % (data.length * 8));
    }

    /**
     * Returns true if the given object matches the filter either because it was inserted, or because we have a
     * false-positive.
     */
    public synchronized boolean contains(byte[] object) {
        for (int i = 0; i < nHashFuncs; i++) {
            if (!checkBitLE(data, murmurHash3(data, nTweak, i, object)))
                return false;
        }
        return true;
    }

    /**
     * Insert the given arbitrary data into the filter
     */
    public synchronized void insert(byte[] object) {
        for (int i = 0; i < nHashFuncs; i++)
            setBitLE(data, murmurHash3(data, nTweak, i, object));
    }

    // 00000001, 00000010, 00000100, 00001000, ...
    private static final int[] bitMask = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80};

    /**
     * Checks if the given bit is set in data, using little endian (not the same as Java native big endian)
     */
    public static boolean checkBitLE(byte[] data, int index) {
        return (data[index >>> 3] & bitMask[7 & index]) != 0;
    }

    /**
     * Sets the given bit in data to one, using little endian (not the same as Java native big endian)
     */
    public static void setBitLE(byte[] data, int index) {
        data[index >>> 3] |= bitMask[7 & index];
    }
}
