package xyz.srclab.common.codec.rsa

import xyz.srclab.common.codec.CodecBytes
import javax.crypto.spec.SecretKeySpec
import xyz.srclab.common.codec.CodecAlgorithmConstants
import java.lang.IllegalStateException
import xyz.srclab.common.codec.AsymmetricCipher
import java.security.spec.X509EncodedKeySpec
import java.security.spec.PKCS8EncodedKeySpec
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
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.security.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import javax.crypto.*

/**
 * RSA cipher.
 *
 * @author sunqian
 */
class RsaCipher : AsymmetricCipher<RSAPublicKey?, RSAPrivateKey?> {
    private var keyPairGen: KeyPairGenerator? = null
    private var keyFactory: KeyFactory? = null
    override fun generateKeyPair(): CodecKeyPair<RSAPublicKey?, RSAPrivateKey?>? {
        return try {
            // 初始化密钥对生成器，密钥大小为96-1024位
            val random = SecureRandom.getInstance(CodecAlgorithmConstants.SHA1PRNG)
            keyPairGen!!.initialize(KEY_SIZE, random)
            // 生成一个密钥对，保存在keyPair中
            val keyPair = keyPairGen!!.generateKeyPair()
            val privateKey = keyPair.private as RSAPrivateKey // 得到私钥
            val publicKey = keyPair.public as RSAPublicKey // 得到公钥
            RsaKeyPair(publicKey, privateKey)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    override fun encrypt(publicKey: RSAPublicKey, data: ByteArray?): ByteArray {
        return try {
            //RSA加密
            val cipher = cipher
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            getBytes(data, cipher, MAX_ENCRYPT_BLOCK)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    override fun encrypt(publicKeyBytes: ByteArray?, data: ByteArray?): ByteArray {
        return try {
            val publicKey = keyFactory!!.generatePublic(X509EncodedKeySpec(publicKeyBytes)) as RSAPublicKey
            encrypt(publicKey, data)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    override fun decrypt(privateKey: RSAPrivateKey, encrypted: ByteArray?): ByteArray {
        return try {
            //RSA解密
            val cipher = cipher
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            getBytes(encrypted, cipher, MAX_DECRYPT_BLOCK)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    override fun decrypt(privateKeyBytes: ByteArray?, encrypted: ByteArray?): ByteArray {
        return try {
            val privateKey = keyFactory!!.generatePrivate(PKCS8EncodedKeySpec(privateKeyBytes)) as RSAPrivateKey
            decrypt(privateKey, encrypted)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    @get:Throws(NoSuchPaddingException::class, NoSuchAlgorithmException::class)
    private val cipher: Cipher
        private get() = Cipher.getInstance(CodecAlgorithm.Companion.RSA!!.name())

    @Throws(IOException::class, IllegalBlockSizeException::class, BadPaddingException::class)
    private fun getBytes(encryptedData: ByteArray?, cipher: Cipher, maxDecryptBlock: Int): ByteArray {
        val outputStream = ByteArrayOutputStream()
        var offset = 0
        do {
            if (encryptedData!!.size - offset >= maxDecryptBlock) {
                outputStream.write(cipher.doFinal(encryptedData, offset, maxDecryptBlock))
                offset += maxDecryptBlock
            } else {
                outputStream.write(cipher.doFinal(encryptedData, offset, encryptedData.size - offset))
                offset = encryptedData.size
            }
        } while (offset < encryptedData!!.size)
        return outputStream.toByteArray()
    }

    override fun name(): String? {
        return CodecAlgorithm.Companion.RSA!!.name()
    }

    companion object {
        private const val KEY_SIZE = 2048
        private const val MAX_DECRYPT_BLOCK = 256
        private const val MAX_ENCRYPT_BLOCK = 245
    }

    init {
        try {
            keyPairGen = KeyPairGenerator.getInstance(CodecAlgorithm.Companion.RSA!!.name())
            keyFactory = KeyFactory.getInstance(CodecAlgorithm.Companion.RSA!!.name())
        } catch (e: NoSuchAlgorithmException) {
            throw IllegalStateException(e)
        }
    }
}