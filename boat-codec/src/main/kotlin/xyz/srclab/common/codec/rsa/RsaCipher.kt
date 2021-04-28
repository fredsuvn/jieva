package xyz.srclab.common.codec.rsa

import xyz.srclab.common.codec.AsymmetricCipher
import xyz.srclab.common.codec.CodecAlgorithm
import java.io.ByteArrayOutputStream
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

/**
 * RSA cipher.
 *
 * @author sunqian
 */
class RsaCipher : AsymmetricCipher<RSAPublicKey, RSAPrivateKey> {

    override val name = CodecAlgorithm.RSA_NAME

    override fun newKeyPair(): RsaKeyPair {
        return newKeyPair(DEFAULT_KEY_SIZE)
    }

    override fun newKeyPair(size: Int): RsaKeyPair {
        // 初始化密钥对生成器，密钥大小为96-1024位
        val random = SecureRandom.getInstance(CodecAlgorithm.SHA1PRNG_NAME)
        keyPairGen.initialize(size, random)
        // 生成一个密钥对，保存在keyPair中
        val keyPair = keyPairGen.generateKeyPair()
        val privateKey = keyPair.private as RSAPrivateKey // 得到私钥
        val publicKey = keyPair.public as RSAPublicKey // 得到公钥
        return RsaKeyPair(publicKey, privateKey)
    }

    override fun encrypt(publicKey: RSAPublicKey, data: ByteArray): ByteArray {
        return encrypt(publicKey, data, DEFAULT_MAX_ENCRYPT_BLOCK)
    }

    override fun encrypt(publicKey: ByteArray, data: ByteArray): ByteArray {
        val rsaPublicKey = keyFactory.generatePublic(X509EncodedKeySpec(publicKey)) as RSAPublicKey
        return encrypt(rsaPublicKey, data)
    }

    override fun encryptWithAny(publicKey: Any, data: ByteArray): ByteArray {
        return when (publicKey) {
            is RSAPublicKey -> encrypt(publicKey, data)
            is ByteArray -> encrypt(publicKey, data)
            else -> throw IllegalArgumentException("Unsupported RSA public key type: ${publicKey::javaClass}")
        }
    }

    fun encrypt(publicKey: RSAPublicKey, data: ByteArray, maxBlock: Int): ByteArray {
        val cipher = Cipher.getInstance(CodecAlgorithm.RSA.name)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return getBytes(data, cipher, maxBlock)
    }

    fun encrypt(publicKey: ByteArray, data: ByteArray, maxBlock: Int): ByteArray {
        val rsaPublicKey = keyFactory.generatePublic(X509EncodedKeySpec(publicKey)) as RSAPublicKey
        return encrypt(rsaPublicKey, data, maxBlock)
    }

    override fun decrypt(privateKey: RSAPrivateKey, encrypted: ByteArray): ByteArray {
        return decrypt(privateKey, encrypted, DEFAULT_MAX_DECRYPT_BLOCK)
    }

    override fun decrypt(privateKey: ByteArray, encrypted: ByteArray): ByteArray {
        val rsaPrivateKey = keyFactory.generatePrivate(PKCS8EncodedKeySpec(privateKey)) as RSAPrivateKey
        return decrypt(rsaPrivateKey, encrypted)
    }

    override fun decryptWithAny(privateKey: Any, encrypted: ByteArray): ByteArray {
        return when (privateKey) {
            is RSAPrivateKey -> decrypt(privateKey, encrypted)
            is ByteArray -> decrypt(privateKey, encrypted)
            else -> throw IllegalArgumentException("Unsupported key RSA private type: ${privateKey::javaClass}")
        }
    }

    fun decrypt(privateKey: RSAPrivateKey, encrypted: ByteArray, maxBlock: Int): ByteArray {
        val cipher = Cipher.getInstance(CodecAlgorithm.RSA.name)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return getBytes(encrypted, cipher, maxBlock)
    }

    fun decrypt(privateKey: ByteArray, encrypted: ByteArray, maxBlock: Int): ByteArray {
        val rsaPrivateKey = keyFactory.generatePrivate(PKCS8EncodedKeySpec(privateKey)) as RSAPrivateKey
        return decrypt(rsaPrivateKey, encrypted, maxBlock)
    }

    private fun getBytes(encryptedData: ByteArray, cipher: Cipher, maxDecryptBlock: Int): ByteArray {
        val outputStream = ByteArrayOutputStream()
        var offset = 0
        do {
            if (encryptedData.size - offset >= maxDecryptBlock) {
                outputStream.write(cipher.doFinal(encryptedData, offset, maxDecryptBlock))
                offset += maxDecryptBlock
            } else {
                outputStream.write(cipher.doFinal(encryptedData, offset, encryptedData.size - offset))
                offset = encryptedData.size
            }
        } while (offset < encryptedData.size)
        return outputStream.toByteArray()
    }

    companion object {

        const val DEFAULT_KEY_SIZE = 2048
        const val DEFAULT_MAX_DECRYPT_BLOCK = 256
        const val DEFAULT_MAX_ENCRYPT_BLOCK = 245

        private val keyPairGen: KeyPairGenerator = KeyPairGenerator.getInstance(CodecAlgorithm.RSA.name)
        private val keyFactory: KeyFactory = KeyFactory.getInstance(CodecAlgorithm.RSA.name)
    }
}