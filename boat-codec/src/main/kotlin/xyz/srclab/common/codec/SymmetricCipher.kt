package xyz.srclab.common.codec

import xyz.srclab.common.codec.CodecAlgorithm.Companion.toCodecAlgorithm
import javax.crypto.Cipher
import javax.crypto.SecretKey

/**
 * Symmetric cipher.
 *
 * @param [K] secret key
 * @author sunqian
 *
 * @see SecretKeySymmetricCipher
 */
interface SymmetricCipher<K> : ReversibleCipher<K, K> {

    companion object {

        @JvmName("withAlgorithm")
        @JvmStatic
        fun CharSequence.toSecretKeySymmetricCipher(): SecretKeySymmetricCipher {
            return this.toCodecAlgorithm().toSecretKeySymmetricCipher()
        }

        @JvmName("withAlgorithm")
        @JvmStatic
        fun CodecAlgorithm.toSecretKeySymmetricCipher(): SecretKeySymmetricCipher {
            return SecretKeySymmetricCipher(this.name)
        }
    }
}

open class SecretKeySymmetricCipher(private val algorithm: String) : SymmetricCipher<SecretKey> {

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
        return encrypt(encryptKey.toSecretKey(algorithm), data)
    }

    override fun encryptWithAny(encryptKey: Any, data: ByteArray): ByteArray {
        return when (encryptKey) {
            is SecretKey -> encrypt(encryptKey, data)
            is ByteArray -> encrypt(encryptKey, data)
            else -> throw IllegalArgumentException("Unsupported encrypt key type: ${encryptKey::javaClass}")
        }
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
        return decrypt(decryptKey.toSecretKey(algorithm), encrypted)
    }

    override fun decryptWithAny(decryptKey: Any, encrypted: ByteArray): ByteArray {
        return when (decryptKey) {
            is SecretKey -> decrypt(decryptKey, encrypted)
            is ByteArray -> decrypt(decryptKey, encrypted)
            else -> throw IllegalArgumentException("Unsupported key decrypt type: ${decryptKey::javaClass}")
        }
    }
}