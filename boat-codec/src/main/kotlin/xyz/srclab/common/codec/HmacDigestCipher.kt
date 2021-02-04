package xyz.srclab.common.codec

import xyz.srclab.common.base.toBytes
import xyz.srclab.common.base.toChars
import xyz.srclab.common.codec.Codec.Companion.encodeBase64String
import xyz.srclab.common.codec.Codec.Companion.encodeHexString
import javax.crypto.Mac
import javax.crypto.SecretKey

/**
 * Hamc digest cipher.
 *
 * @param [K] secret key
 * @author sunqian
 */
interface HmacDigestCipher<K> : CodecCipher {

    /**
     * Digests data by given secret key.
     *
     * @param key  given secret key
     * @param data data
     * @return digested data
     */
    fun digest(key: K, data: ByteArray): ByteArray

    /**
     * Digests data by given secret key bytes.
     *
     * @param key given secret key bytes
     * @param data     data
     * @return digested data
     */
    fun digest(key: ByteArray, data: ByteArray): ByteArray

    /**
     * Digests data by given secret key.
     *
     * @param key  given secret key
     * @param data data
     * @return digested data
     */
    @JvmDefault
    fun digest(key: K, data: CharSequence): ByteArray {
        return digest(key, data.toBytes())
    }

    /**
     * Digests data by given secret key bytes.
     *
     * @param key given secret key bytes
     * @param data     data
     * @return digested data
     */
    @JvmDefault
    fun digest(key: ByteArray, data: CharSequence): ByteArray {
        return digest(key, data.toBytes())
    }

    /**
     * Digests data by given secret key.
     *
     * @param key  given secret key
     * @param data data
     * @return digested data as string
     */
    @JvmDefault
    fun digestToString(key: K, data: ByteArray): String {
        return digest(key, data).toChars()
    }

    /**
     * Digests data by given secret key bytes.
     *
     * @param key given secret key bytes
     * @param data     data
     * @return digested data as string
     */
    @JvmDefault
    fun digestToString(key: K, data: CharSequence): String {
        return digest(key, data).toChars()
    }

    /**
     * Digests data by given secret key.
     *
     * @param key  given secret key
     * @param data data
     * @return digested data as string
     */
    @JvmDefault
    fun digestToString(key: ByteArray, data: CharSequence): String {
        return digest(key, data).toChars()
    }

    /**
     * Digests data by given secret key bytes.
     *
     * @param key given secret key bytes
     * @param data     data
     * @return digested data as string
     */
    @JvmDefault
    fun digestToString(key: ByteArray, data: ByteArray): String {
        return digest(key, data).toChars()
    }

    /**
     * Digests data by given secret key.
     *
     * @param key  given secret key
     * @param data data
     * @return hex string encoded by digested data
     */
    @JvmDefault
    fun digestToHexString(key: K, data: ByteArray): String {
        return digest(key, data).encodeHexString()
    }

    /**
     * Digests data by given secret key bytes.
     *
     * @param key given secret key bytes
     * @param data     data
     * @return hex string encoded by digested data
     */
    @JvmDefault
    fun digestToHexString(key: ByteArray, data: ByteArray): String {
        return digest(key, data).encodeHexString()
    }

    /**
     * Digests data by given secret key.
     *
     * @param key  given secret key
     * @param data data
     * @return hex string encoded by digested data
     */
    @JvmDefault
    fun digestToHexString(key: K, data: CharSequence): String {
        return digest(key, data).encodeHexString()
    }

    /**
     * Digests data by given secret key bytes.
     *
     * @param key given secret key bytes
     * @param data     data
     * @return hex string encoded by digested data
     */
    @JvmDefault
    fun digestToHexString(key: ByteArray, data: CharSequence): String {
        return digest(key, data).encodeHexString()
    }

    /**
     * Digests data by given secret key.
     *
     * @param key  given secret key
     * @param data data
     * @return base64 string encoded by digested data
     */
    @JvmDefault
    fun digestToBase64String(key: K, data: ByteArray): String {
        return digest(key, data).encodeBase64String()
    }

    /**
     * Digests data by given secret key bytes.
     *
     * @param key given secret key bytes
     * @param data     data
     * @return base64 string encoded by digested data
     */
    @JvmDefault
    fun digestToBase64String(key: ByteArray, data: ByteArray): String {
        return digest(key, data).encodeBase64String()
    }

    /**
     * Digests data by given secret key.
     *
     * @param key  given secret key
     * @param data data
     * @return base64 string encoded by digested data
     */
    @JvmDefault
    fun digestToBase64String(key: K, data: CharSequence): String {
        return digest(key, data).encodeBase64String()
    }

    /**
     * Digests data by given secret key bytes.
     *
     * @param key given secret key bytes
     * @param data     data
     * @return base64 string encoded by digested data
     */
    @JvmDefault
    fun digestToBase64String(key: ByteArray, data: CharSequence): String {
        return digest(key, data).encodeBase64String()
    }

    companion object {

        @JvmStatic
        fun forAlgorithm(algorithm: String): SecretKeyHmacDigestCipher {
            return SecretKeyHmacDigestCipher(algorithm)
        }

        @JvmStatic
        fun forAlgorithm(algorithm: CodecAlgorithm): SecretKeyHmacDigestCipher {
            return SecretKeyHmacDigestCipher(algorithm.name)
        }
    }
}

class SecretKeyHmacDigestCipher(private val algorithm: String) : HmacDigestCipher<SecretKey> {

    override val name = algorithm

    override fun digest(key: SecretKey, data: ByteArray): ByteArray {
        return try {
            val mac = Mac.getInstance(algorithm)
            mac.init(key)
            mac.doFinal(data)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    override fun digest(key: ByteArray, data: ByteArray): ByteArray {
        return digest(Codec.secretKey(key, algorithm), data)
    }
}