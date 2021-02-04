package xyz.srclab.common.codec

import javax.crypto.Cipher
import javax.crypto.SecretKey

/**
 * Symmetric cipher.
 *
 * @param [K] secret key
 * @author sunqian
 */
interface SymmetricCipher<K> : ReversibleCipher<K, K> {

    companion object {

        @JvmStatic
        fun forAlgorithm(algorithm: String): SecretKeySymmetricCipher {
            return SecretKeySymmetricCipher(algorithm)
        }

        @JvmStatic
        fun forAlgorithm(algorithm: CodecAlgorithm): SecretKeySymmetricCipher {
            return SecretKeySymmetricCipher(algorithm.name)
        }
    }
}

class SecretKeySymmetricCipher(private val algorithm: String) : SymmetricCipher<SecretKey> {

    override val name = algorithm

    override fun encrypt(encryptKey: SecretKey, data: ByteArray): ByteArray {
        return try {
            val cipher = Cipher.getInstance(algorithm)
            cipher.init(Cipher.ENCRYPT_MODE, encryptKey)
            cipher.doFinal(data)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    override fun encrypt(encryptKey: ByteArray, data: ByteArray): ByteArray {
        return encrypt(Codec.secretKey(encryptKey, algorithm), data)
    }

    override fun decrypt(decryptKey: SecretKey, encrypted: ByteArray): ByteArray {
        return try {
            val cipher = Cipher.getInstance(algorithm)
            cipher.init(Cipher.DECRYPT_MODE, decryptKey)
            cipher.doFinal(encrypted)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    override fun decrypt(decryptKey: ByteArray, encrypted: ByteArray): ByteArray {
        return decrypt(Codec.secretKey(decryptKey, algorithm), encrypted)
    }
}