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

    override fun encrypt(encryptKey: RSAPublicKey, data: ByteArray): ByteArray {
        return encrypt(encryptKey, data, DEFAULT_MAX_ENCRYPT_BLOCK)
    }

    override fun encrypt(encryptKey: ByteArray, data: ByteArray): ByteArray {
        val publicKey = keyFactory.generatePublic(X509EncodedKeySpec(encryptKey)) as RSAPublicKey
        return encrypt(publicKey, data)
    }

    fun encrypt(encryptKey: RSAPublicKey, data: ByteArray, maxBlock: Int): ByteArray {
        val cipher = Cipher.getInstance(CodecAlgorithm.RSA.name)
        cipher.init(Cipher.ENCRYPT_MODE, encryptKey)
        return getBytes(data, cipher, maxBlock)
    }

    fun encrypt(encryptKey: ByteArray, data: ByteArray, maxBlock: Int): ByteArray {
        val publicKey = keyFactory.generatePublic(X509EncodedKeySpec(encryptKey)) as RSAPublicKey
        return encrypt(publicKey, data, maxBlock)
    }

    override fun decrypt(decryptKey: RSAPrivateKey, encrypted: ByteArray): ByteArray {
        return decrypt(decryptKey, encrypted, DEFAULT_MAX_DECRYPT_BLOCK)
    }

    override fun decrypt(decryptKey: ByteArray, encrypted: ByteArray): ByteArray {
        val privateKey = keyFactory.generatePrivate(PKCS8EncodedKeySpec(decryptKey)) as RSAPrivateKey
        return decrypt(privateKey, encrypted)
    }

    fun decrypt(decryptKey: RSAPrivateKey, encrypted: ByteArray, maxBlock: Int): ByteArray {
        val cipher = Cipher.getInstance(CodecAlgorithm.RSA.name)
        cipher.init(Cipher.DECRYPT_MODE, decryptKey)
        return getBytes(encrypted, cipher, maxBlock)
    }

    fun decrypt(decryptKey: ByteArray, encrypted: ByteArray, maxBlock: Int): ByteArray {
        val privateKey = keyFactory.generatePrivate(PKCS8EncodedKeySpec(decryptKey)) as RSAPrivateKey
        return decrypt(privateKey, encrypted, maxBlock)
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