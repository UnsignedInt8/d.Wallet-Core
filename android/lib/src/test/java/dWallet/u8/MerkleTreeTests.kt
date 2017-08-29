package dwallet.u8

import dwallet.core.utils.MerkleTree
import org.junit.Test
import org.junit.Assert.*

/**
 * Created by unsignedint8 on 8/27/17.
 */

class MerkleTreeTests {

    @Test
    fun test80000() {
        val hashes = listOf("c06fbab289f723c6261d3030ddb6be121f7d2508d77862bb1e484f5cd7f92b25", "5a4ebf66822b0b2d56bd9dc64ece0bc38ee7844a23ff1d7320a88c5fdb2ad3e2")
        assertEquals("8fb300e3fdb6f30a4c67233b997f99fdd518b968b9a3fd65857bfe78b2600719", MerkleTree.generateRootHash(hashes))
    }

    @Test
    fun test481955() {
        val hashes = listOf("5c9bed0716c7920081101eea92530cff33cf1bacf1b3036a2342157869cb4ece")
        assertEquals("5c9bed0716c7920081101eea92530cff33cf1bacf1b3036a2342157869cb4ece", MerkleTree.generateRootHash(hashes))
    }

    @Test
    fun test50001() {
        val hashes = listOf("e1882d41800d96d0fddc196cd8d3f0b45d65b030c652d97eaba79a1174e64d58", "7940cdde4d713e171849efc6bd89939185be270266c94e92369e3877ad89455a", "f84761459a00c6df3176ae5d94c99e69f25100d09548e5686bd0c354bb8cc60a")
        assertEquals("ee3a2d2b895cafacff526d06a55b55e049cf84a9735e4a63f7fd08f96d0f4649", MerkleTree.generateRootHash(hashes))
    }

    @Test
    fun testItems(){
        val hashes = listOf("aa151b40af4bbd61085cbb115046f7d23a9dc8659ada5465c30649236ce998e9", "6fd7b22beed1d2f30b1e116c58ce14a0780b3611a23c74e30fc7257a9c120e8c", "829186075dcb208a3830fa55dd4f5ec8828c5347b48472ef46f03a97fdcddb1f", "116ff28f61f36a1293032eaf0a869c5e5979283b2e6aa9eb0602e5cd002bdcbc", "85bf5ba778d24616d2ec7ee91bcf1e2df53f8566f116e8667ae778798305bf7c", "226c67a7e937cb2bd663f6ae0d4abc645ee8621d44a51ddbd95a53b6754b60d0", "2a7dcf153b5435817519fea9bab0e84981eb4cf6aab357b00aec840f902b8a5b", "cb3231549a135ab81ea867ec4ec96ad29782563efe7d12497942006135ab0a7e", "48f80c5f900f8c4e536330623a9e86526490e22a72a73829fd50afb41ac21195")
        println("cfb0adb00f5710a22bf91aca207bb704c757b60209fa7a9768a47d010a6a4636")
        println(MerkleTree.generateRootHash(hashes))

        val h2 = listOf("aa151b40af4bbd61085cbb115046f7d23a9dc8659ada5465c30649236ce998e9",
                "6fd7b22beed1d2f30b1e116c58ce14a0780b3611a23c74e30fc7257a9c120e8c",
                "1eecb66b8e870ab2d0d9dd11ecc224f43cc904247186bcafca0ed15f1264cf2d",
                "ecb25cf6e2db5c88070fb0e40fd16c196fc3ecd3a66271d2dbf4ff383a69b111",
                "d1d227648c092ee627354c9c94132189bf9dc8eac34baf829b3958afa29150ff",
                "d5a389e98a7a2dd7ab7896d309efed991550253a2926d6dbf578e2756ad6367e",
                "1e8ff3c8ab67d6bbe468cb03e35fad4fd41f87f616809b3ce522f3613cc938a2",
                "ee88dc730996c6e758ad822cf71407383e2958daec7762f51802fb563478df21",
                "fac0a43c329eca74878274ee3f5678a745886de17cc2c2840576a748d9e42143",
                "e72f2fddb66616ec044c146d3d7505d4d81571abbe4ab4e3ab2b355b42ce3ac3",
                "f6dca0a22e872e30603e46490ae2159012f823940778203970d4ab7f384689c7",
                "843ec59bff1d83b64c0234d96c9eadd67c6918f83202e072cc06d9ad1a55fb67",
                "226c67a7e937cb2bd663f6ae0d4abc645ee8621d44a51ddbd95a53b6754b60d0",
                "2a7dcf153b5435817519fea9bab0e84981eb4cf6aab357b00aec840f902b8a5b",
                "79d97a9a212460acb45be9173c35dfbc6f28df68747de4bbf4a02577d361de2b",
                "321908e3ea7a20857040c89b973370137029a89ab3a2b52437ca68d571669959",
                "4d37a521703ee0eebaba90d1ed7e9d0617703f75d4c9f29973bfc0435c8377b1",
                "7c8c646ba097174b2f949489ba9cca2d0aa9887c1aa7a037f103af760fd52784",
                "dfe6d75ca99dfb45847f39935311f930cbcef7ebd9d7fdfaf46e0c779fd66954",
                "b3f5bafeab5103f5fd0c973c1256c52950a2c460d506e1b5a6bb8716bffa4eb8",
                "986ddcf7842d125bc0b054d154418d3a1c518411c902a13e2e08567b19e3052a")
        println(MerkleTree.generateRootHash(h2))
    }
}