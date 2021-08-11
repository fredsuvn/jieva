//package xyz.srclab.common.codec
//
//import xyz.srclab.common.lang.toBytes
//import xyz.srclab.common.lang.toChars
//import java.io.OutputStream
//import java.security.PrivateKey
//
///**
// * To sign and verify.
// */
//interface Signer {
//
//    @JvmDefault
//    fun sign(key: PrivateKey, data: ByteArray): ByteArray {
//        return sign(key, data, 0)
//    }
//
//    @JvmDefault
//    fun sign(key: PrivateKey, data: ByteArray, offset: Int): ByteArray {
//        return sign(key, data, offset, data.size - offset)
//    }
//
//    fun sign(key: PrivateKey, data: ByteArray, offset: Int, length: Int): ByteArray
//
//    @JvmDefault
//    fun sign(key: PrivateKey, data: ByteArray, output: OutputStream): Int {
//        return sign(key, data, 0, output)
//    }
//
//    @JvmDefault
//    fun sign(key: PrivateKey, data: ByteArray, offset: Int, output: OutputStream): Int {
//        return sign(key, data, offset, data.size - offset, output)
//    }
//
//    @JvmDefault
//    fun sign(key: PrivateKey, data: ByteArray, offset: Int, length: Int, output: OutputStream): Int
//
//    @JvmDefault
//    fun sign(key: Any, data: CharSequence): ByteArray {
//        return sign(key, data.toBytes())
//    }
//
//    @JvmDefault
//    fun sign(key: Any, data: CharSequence, output: OutputStream): Int {
//        return sign(key, data.toBytes(), output)
//    }
//
//    @JvmDefault
//    fun signToString(key: Any, data: ByteArray): String {
//        return sign(key, data).toChars()
//    }
//
//    @JvmDefault
//    fun signToString(key: Any, data: ByteArray, offset: Int): String {
//        return sign(key, data, offset).toChars()
//    }
//
//    @JvmDefault
//    fun signToString(key: Any, data: ByteArray, offset: Int, length: Int): String {
//        return sign(key, data, offset, length).toChars()
//    }
//
//    @JvmDefault
//    fun signToString(key: Any, data: CharSequence): String {
//        return sign(key, data).toChars()
//    }
//}