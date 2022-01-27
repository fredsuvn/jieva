//package xyz.srclab.common.codec.sm2
//
//import java.math.BigInteger
//
///**
// * SM2 cipher params, it needs:
// *
// * * n:
// * * p:
// * * a:
// * * b:
// * * gx:
// * * gy:
// *
// * @author sunqian
// */
//class Sm2Params(
//    @get:JvmName("n") val n: BigInteger,
//    @get:JvmName("p") val p: BigInteger,
//    @get:JvmName("a") val a: BigInteger,
//    @get:JvmName("b") val b: BigInteger,
//    @get:JvmName("gx") val gx: BigInteger,
//    @get:JvmName("gy") val gy: BigInteger
//) {
//
//    companion object {
//
//        private val N = BigInteger(
//            "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "7203DF6B" + "21C6052B" + "53BBF409" + "39D54123", 16
//        )
//        private val P = BigInteger(
//            "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "00000000" + "FFFFFFFF" + "FFFFFFFF", 16
//        )
//        private val A = BigInteger(
//            "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "00000000" + "FFFFFFFF" + "FFFFFFFC", 16
//        )
//        private val B = BigInteger(
//            "28E9FA9E" + "9D9F5E34" + "4D5A9E4B" + "CF6509A7" + "F39789F5" + "15AB8F92" + "DDBCBD41" + "4D940E93", 16
//        )
//        private val GX = BigInteger(
//            "32C4AE2C" + "1F198119" + "5F990446" + "6A39C994" + "8FE30BBF" + "F2660BE1" + "715A4589" + "334C74C7", 16
//        )
//        private val GY = BigInteger(
//            "BC3736A2" + "F4F6779C" + "59BDCEE3" + "6B692153" + "D0A9877C" + "C62A4740" + "02DF32E5" + "2139F0A0", 16
//        )
//
//        @JvmField
//        val DEFAULT: Sm2Params = Sm2Params(N, P, A, B, GX, GY)
//    }
//}