package xyz.srclab.common.codec.rsa

import xyz.srclab.common.codec.AsymmetricCryptCodec
import xyz.srclab.common.codec.CodecAlgorithm
import xyz.srclab.common.lang.toBytes
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
 * RSA cipher codec.
 *
 * @author sunqian
 */
class RsaCodec(
    private val encryptBlockSize: Int = DEFAULT_ENCRYPT_BLOCK,
    private val decryptBlockSize: Int = DEFAULT_DECRYPT_BLOCK
) : AsymmetricCryptCodec<RSAPublicKey, RSAPrivateKey> {

    override val algorithm = CodecAlgorithm.RSA_NAME

    private fun getCipher(): Cipher {
        return Cipher.getInstance(algorithm)
    }

    override fun newKeyPair(): RsaKeyPair {
        return newKeyPair(DEFAULT_KEY_SIZE)
    }

    override fun newKeyPair(size: Int): RsaKeyPair {
        val random = SecureRandom.getInstance(CodecAlgorithm.SHA1PRNG_NAME)
        // size: 96-1024
        keyPairGen.initialize(size, random)
        val keyPair = keyPairGen.generateKeyPair()
        val privateKey = keyPair.private as RSAPrivateKey
        val publicKey = keyPair.public as RSAPublicKey
        return RsaKeyPair(publicKey, privateKey)
    }

    override fun encrypt(key: Any, data: ByteArray, offset: Int, length: Int): ByteArray {
        val cipher = getCipher()
        cipher.init(Cipher.ENCRYPT_MODE, key.toPublicKey())
        return getBytes(data, cipher, encryptBlockSize)
    }

    override fun decrypt(key: Any, data: ByteArray, offset: Int, length: Int): ByteArray {
        val cipher = getCipher()
        cipher.init(Cipher.DECRYPT_MODE, key.toPrivateKey())
        return getBytes(data, cipher, decryptBlockSize)
    }

    fun withBlockSize(
        encryptBlockSize: Int,
        decryptBlockSize: Int
    ): RsaCodec {
        return RsaCodec(encryptBlockSize, decryptBlockSize)
    }

    private fun Any.toPublicKey(): RSAPublicKey {
        return when (this) {
            is RSAPublicKey -> this
            is ByteArray -> keyFactory.generatePublic(X509EncodedKeySpec(this)) as RSAPublicKey
            is CharSequence -> keyFactory.generatePublic(X509EncodedKeySpec(this.toBytes())) as RSAPublicKey
            else -> throw UnsupportedOperationException("Unsupported public key: $this")
        }
    }

    private fun Any.toPrivateKey(): RSAPrivateKey {
        return when (this) {
            is RSAPrivateKey -> this
            is ByteArray -> keyFactory.generatePrivate(PKCS8EncodedKeySpec(this)) as RSAPrivateKey
            is CharSequence -> keyFactory.generatePrivate(PKCS8EncodedKeySpec(this.toBytes())) as RSAPrivateKey
            else -> throw UnsupportedOperationException("Unsupported private key: $this")
        }
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
        const val DEFAULT_DECRYPT_BLOCK = 256
        const val DEFAULT_ENCRYPT_BLOCK = 245

        private val keyPairGen: KeyPairGenerator = KeyPairGenerator.getInstance(CodecAlgorithm.RSA.name)
        private val keyFactory: KeyFactory = KeyFactory.getInstance(CodecAlgorithm.RSA.name)
    }
}