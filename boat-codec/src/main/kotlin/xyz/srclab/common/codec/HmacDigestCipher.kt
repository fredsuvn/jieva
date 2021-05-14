package xyz.srclab.common.codec

import xyz.srclab.common.lang.toBytes
import xyz.srclab.common.lang.toChars
import xyz.srclab.common.codec.Codec.Companion.encodeBase64String
import xyz.srclab.common.codec.Codec.Companion.encodeHexString
import xyz.srclab.common.codec.CodecAlgorithm.Companion.toCodecAlgorithm
import javax.crypto.Mac
import javax.crypto.SecretKey

/**
 * Hamc digest cipher.
 *
 * @param [K] secret key
 * @author sunqian
 *
 * @see SecretKeyHmacDigestCipher
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
     * Digests data by given secret key.
     *
     * @param key given secret key
     * @param data     data
     * @return digested data
     */
    fun digest(key: ByteArray, data: ByteArray): ByteArray

    /**
     * Digests data by given secret key.
     *
     * @param key given secret key
     * @param data     data
     * @return digested data
     */
    @JvmDefault
    fun digest(key: CharSequence, data: ByteArray): ByteArray {
        return digest(key.toBytes(), data)
    }

    /**
     * Digests data by given secret key.
     *
     * @param key  given secret key
     * @param data data
     * @return digested data
     */
    fun digestWithAny(key: Any, data: ByteArray): ByteArray

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
     * Digests data by given secret key.
     *
     * @param key given secret key
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
     * @param key given secret key
     * @param data     data
     * @return digested data
     */
    @JvmDefault
    fun digest(key: CharSequence, data: CharSequence): ByteArray {
        return digest(key, data.toBytes())
    }

    /**
     * Digests data by given secret key.
     *
     * @param key  given secret key
     * @param data data
     * @return digested data
     */
    @JvmDefault
    fun digestWithAny(key: Any, data: CharSequence): ByteArray {
        return digestWithAny(key, data.toBytes())
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
     * Digests data by given secret key.
     *
     * @param key given secret key
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
     * @param key given secret key
     * @param data     data
     * @return digested data as string
     */
    @JvmDefault
    fun digestToString(key: CharSequence, data: ByteArray): String {
        return digest(key, data).toChars()
    }

    /**
     * Digests data by given secret key.
     *
     * @param key given secret key
     * @param data     data
     * @return digested data as string
     */
    @JvmDefault
    fun digestToStringWithAny(key: Any, data: ByteArray): String {
        return digestWithAny(key, data).toChars()
    }

    /**
     * Digests data by given secret key.
     *
     * @param key given secret key
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
     * Digests data by given secret key.
     *
     * @param key  given secret key
     * @param data data
     * @return digested data as string
     */
    @JvmDefault
    fun digestToString(key: CharSequence, data: CharSequence): String {
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
    fun digestToStringWithAny(key: Any, data: CharSequence): String {
        return digestWithAny(key, data).toChars()
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
     * Digests data by given secret key.
     *
     * @param key given secret key
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
     * @param key given secret key
     * @param data     data
     * @return hex string encoded by digested data
     */
    @JvmDefault
    fun digestToHexString(key: CharSequence, data: ByteArray): String {
        return digest(key, data).encodeHexString()
    }

    /**
     * Digests data by given secret key.
     *
     * @param key given secret key
     * @param data     data
     * @return hex string encoded by digested data
     */
    @JvmDefault
    fun digestToHexStringWithAny(key: Any, data: ByteArray): String {
        return digestWithAny(key, data).encodeHexString()
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
     * Digests data by given secret key.
     *
     * @param key given secret key
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
     * @param key given secret key
     * @param data     data
     * @return hex string encoded by digested data
     */
    @JvmDefault
    fun digestToHexString(key: CharSequence, data: CharSequence): String {
        return digest(key, data).encodeHexString()
    }

    /**
     * Digests data by given secret key.
     *
     * @param key given secret key
     * @param data     data
     * @return hex string encoded by digested data
     */
    @JvmDefault
    fun digestToHexStringWithAny(key: Any, data: CharSequence): String {
        return digestWithAny(key, data).encodeHexString()
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
     * Digests data by given secret key.
     *
     * @param key given secret key
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
     * @param key given secret key
     * @param data     data
     * @return base64 string encoded by digested data
     */
    @JvmDefault
    fun digestToBase64String(key: CharSequence, data: ByteArray): String {
        return digest(key, data).encodeBase64String()
    }

    /**
     * Digests data by given secret key.
     *
     * @param key given secret key
     * @param data     data
     * @return base64 string encoded by digested data
     */
    @JvmDefault
    fun digestToBase64StringWithAny(key: Any, data: ByteArray): String {
        return digestWithAny(key, data).encodeBase64String()
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
     * Digests data by given secret key.
     *
     * @param key given secret key
     * @param data     data
     * @return base64 string encoded by digested data
     */
    @JvmDefault
    fun digestToBase64String(key: ByteArray, data: CharSequence): String {
        return digest(key, data).encodeBase64String()
    }

    /**
     * Digests data by given secret key.
     *
     * @param key given secret key
     * @param data     data
     * @return base64 string encoded by digested data
     */
    @JvmDefault
    fun digestToBase64String(key: CharSequence, data: CharSequence): String {
        return digest(key, data).encodeBase64String()
    }

    /**
     * Digests data by given secret key.
     *
     * @param key given secret key
     * @param data     data
     * @return base64 string encoded by digested data
     */
    @JvmDefault
    fun digestToBase64StringWithAny(key: Any, data: CharSequence): String {
        return digestWithAny(key, data).encodeBase64String()
    }

    companion object {

        @JvmName("withAlgorithm")
        @JvmStatic
        fun CharSequence.toSecretKeyHmacDigestCipher(): SecretKeyHmacDigestCipher {
            return this.toCodecAlgorithm().toSecretKeyHmacDigestCipher()
        }

        @JvmName("withAlgorithm")
        @JvmStatic
        fun CodecAlgorithm.toSecretKeyHmacDigestCipher(): SecretKeyHmacDigestCipher {
            return SecretKeyHmacDigestCipher(this.name)
        }
    }
}

open class SecretKeyHmacDigestCipher(private val algorithm: String) : HmacDigestCipher<SecretKey> {

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
        return digest(key.toSecretKey(algorithm), data)
    }

    override fun digestWithAny(key: Any, data: ByteArray): ByteArray {
        return when (key) {
            is SecretKey -> digest(key, data)
            is ByteArray -> digest(key, data)
            else -> throw IllegalArgumentException("Unsupported key type: ${key::javaClass}")
        }
    }
}