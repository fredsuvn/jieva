package xyz.srclab.common.codec

import xyz.srclab.common.base.toBytes
import xyz.srclab.common.base.toChars
import xyz.srclab.common.codec.Codec.Companion.encodeBase64String
import xyz.srclab.common.codec.Codec.Companion.encodeHexString
import xyz.srclab.common.codec.CodecAlgorithm.Companion.toCodecAlgorithm
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Digest cipher.
 *
 * @author sunqian
 *
 * @see CommonDigestCipher
 */
interface DigestCipher : CodecCipher {

    /**
     * Digests data.
     *
     * @param data data
     * @return digested data
     */
    fun digest(data: ByteArray): ByteArray

    /**
     * Digests data.
     *
     * @param data data
     * @return digested data
     */
    @JvmDefault
    fun digest(data: CharSequence): ByteArray {
        return digest(data.toBytes())
    }

    /**
     * Digests data.
     *
     * @param data data
     * @return digested data as string
     */
    @JvmDefault
    fun digestToString(data: ByteArray): String {
        return digest(data).toChars()
    }

    /**
     * Digests data.
     *
     * @param data data
     * @return digested data as string
     */
    @JvmDefault
    fun digestToString(data: CharSequence): String {
        return digest(data).toChars()
    }

    /**
     * Digests data.
     *
     * @param data data
     * @return hex string encoded by digested data
     */
    @JvmDefault
    fun digestToHexString(data: ByteArray): String {
        return digest(data).encodeHexString()
    }

    /**
     * Digests data.
     *
     * @param data data
     * @return hex string encoded by digested data
     */
    @JvmDefault
    fun digestToHexString(data: CharSequence): String {
        return digest(data).encodeHexString()
    }

    /**
     * Digests data.
     *
     * @param data data
     * @return base64 string encoded by digested data
     */
    @JvmDefault
    fun digestToBase64String(data: ByteArray): String {
        return digest(data).encodeBase64String()
    }

    /**
     * Digests data.
     *
     * @param data data
     * @return base64 string encoded by digested data
     */
    @JvmDefault
    fun digestToBase64String(data: CharSequence): String {
        return digest(data).encodeBase64String()
    }

    companion object {

        @JvmName("withAlgorithm")
        @JvmStatic
        fun CharSequence.toDigestCipher(): DigestCipher {
            return this.toCodecAlgorithm().toDigestCipher()
        }

        @JvmName("withAlgorithm")
        @JvmStatic
        fun CodecAlgorithm.toDigestCipher(): DigestCipher {
            return CommonDigestCipher(this.name)
        }
    }
}

open class CommonDigestCipher(private val algorithm: String) : DigestCipher {

    override val name = algorithm

    override fun digest(data: ByteArray): ByteArray {
        return try {
            MessageDigest.getInstance(algorithm).digest(data)
        } catch (e: NoSuchAlgorithmException) {
            throw IllegalStateException(e)
        }
    }
}