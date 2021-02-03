package xyz.srclab.common.codec.aes

import xyz.srclab.common.codec.CodecBytes
import javax.crypto.spec.SecretKeySpec
import xyz.srclab.common.codec.CodecAlgorithmConstants
import java.lang.IllegalStateException
import xyz.srclab.common.codec.AsymmetricCipher
import java.security.KeyPairGenerator
import java.security.spec.X509EncodedKeySpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.NoSuchAlgorithmException
import xyz.srclab.common.codec.CodecAlgorithm
import java.io.IOException
import xyz.srclab.common.codec.AbstractCodecKeyPair
import xyz.srclab.common.codec.CodecKeyPair
import xyz.srclab.common.codec.ReversibleCipher
import xyz.srclab.common.codec.CodecImpl
import xyz.srclab.common.codec.CodecKeySupport
import xyz.srclab.common.codec.SymmetricCipherImpl
import xyz.srclab.common.codec.DigestCipher
import xyz.srclab.common.codec.DigestCipherImpl
import xyz.srclab.common.codec.HmacDigestCipher
import xyz.srclab.common.codec.HmacDigestCipherImpl
import xyz.srclab.common.codec.CodecAlgorithmSupport
import java.util.NoSuchElementException
import xyz.srclab.common.codec.CodecAlgorithmSupport.CodecAlgorithmImpl
import xyz.srclab.common.codec.CodecCipher
import java.lang.Exception
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.*

/**
 * @author sunqian
 */
object AesKeys {
    private const val AES_KEY_INIT_LENGTH = 128
    fun generateKey(key: String): SecretKey {
        return generateKey(CodecBytes.toBytes(key))
    }

    fun generateKey(key: String, keySize: Int): SecretKey {
        return generateKey(CodecBytes.toBytes(key), keySize)
    }

    @JvmOverloads
    fun generateKey(key: ByteArray?, keySize: Int = AES_KEY_INIT_LENGTH): SecretKey {
        return try {
            val result: SecretKeySpec
            val random = SecureRandom.getInstance(CodecAlgorithmConstants.SHA1PRNG)
            random.setSeed(key)
            val kgen = KeyGenerator.getInstance(CodecAlgorithmConstants.AES)
            kgen.init(keySize, random)
            val secretKey = kgen.generateKey()
            val enCodeFormat = secretKey.encoded
            result = SecretKeySpec(enCodeFormat, CodecAlgorithmConstants.AES)
            result
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }
}