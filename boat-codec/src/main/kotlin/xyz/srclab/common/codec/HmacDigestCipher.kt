package xyz.srclab.common.codec

import javax.crypto.SecretKey
import xyz.srclab.common.codec.CodecBytes
import javax.crypto.spec.SecretKeySpec
import xyz.srclab.common.codec.CodecAlgorithmConstants
import java.lang.IllegalStateException
import xyz.srclab.common.codec.AsymmetricCipher
import java.security.KeyPairGenerator
import java.security.spec.X509EncodedKeySpec
import java.security.spec.PKCS8EncodedKeySpec
import javax.crypto.NoSuchPaddingException
import java.security.NoSuchAlgorithmException
import xyz.srclab.common.codec.CodecAlgorithm
import java.io.IOException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.BadPaddingException
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
import java.security.MessageDigest

/**
 * Hamc digest cipher.
 *
 * @param <K> secret key
 * @author sunqian
</K> */
interface HmacDigestCipher<K> : CodecCipher {
    /**
     * Digests data by given secret key.
     *
     * @param key  given secret key
     * @param data data
     * @return digested data
     */
    fun digest(key: K, data: ByteArray?): ByteArray?

    /**
     * Digests data by given secret key bytes.
     *
     * @param keyBytes given secret key bytes
     * @param data     data
     * @return digested data
     */
    fun digest(keyBytes: ByteArray?, data: ByteArray?): ByteArray?

    /**
     * Digests data by given secret key.
     *
     * @param key  given secret key
     * @param data data
     * @return digested data
     */
    fun digest(key: K, data: String): ByteArray? {
        return digest(key, CodecBytes.toBytes(data))
    }

    /**
     * Digests data by given secret key bytes.
     *
     * @param keyBytes given secret key bytes
     * @param data     data
     * @return digested data
     */
    fun digest(keyBytes: ByteArray?, data: String): ByteArray? {
        return digest(keyBytes, CodecBytes.toBytes(data))
    }

    /**
     * Digests data by given secret key.
     *
     * @param key  given secret key
     * @param data data
     * @return digested data as string
     */
    fun digestString(key: K, data: ByteArray?): String? {
        return CodecBytes.toString(digest(key, data))
    }

    /**
     * Digests data by given secret key bytes.
     *
     * @param keyBytes given secret key bytes
     * @param data     data
     * @return digested data as string
     */
    fun digestString(keyBytes: ByteArray?, data: ByteArray?): String? {
        return CodecBytes.toString(digest(keyBytes, data))
    }

    /**
     * Digests data by given secret key.
     *
     * @param key  given secret key
     * @param data data
     * @return hex string encoded by digested data
     */
    fun digestHexString(key: K, data: ByteArray?): String? {
        return Codec.Companion.encodeHexString(digest(key, data))
    }

    /**
     * Digests data by given secret key bytes.
     *
     * @param keyBytes given secret key bytes
     * @param data     data
     * @return hex string encoded by digested data
     */
    fun digestHexString(keyBytes: ByteArray?, data: ByteArray?): String? {
        return Codec.Companion.encodeHexString(digest(keyBytes, data))
    }

    /**
     * Digests data by given secret key.
     *
     * @param key  given secret key
     * @param data data
     * @return hex string encoded by digested data
     */
    fun digestHexString(key: K, data: String): String? {
        return Codec.Companion.encodeHexString(digest(key, data))
    }

    /**
     * Digests data by given secret key bytes.
     *
     * @param keyBytes given secret key bytes
     * @param data     data
     * @return hex string encoded by digested data
     */
    fun digestHexString(keyBytes: ByteArray?, data: String): String? {
        return Codec.Companion.encodeHexString(digest(keyBytes, data))
    }

    /**
     * Digests data by given secret key.
     *
     * @param key  given secret key
     * @param data data
     * @return base64 string encoded by digested data
     */
    fun digestBase64String(key: K, data: ByteArray?): String? {
        return Codec.Companion.encodeBase64String(digest(key, data))
    }

    /**
     * Digests data by given secret key bytes.
     *
     * @param keyBytes given secret key bytes
     * @param data     data
     * @return base64 string encoded by digested data
     */
    fun digestBase64String(keyBytes: ByteArray?, data: ByteArray?): String? {
        return Codec.Companion.encodeBase64String(digest(keyBytes, data))
    }

    /**
     * Digests data by given secret key.
     *
     * @param key  given secret key
     * @param data data
     * @return base64 string encoded by digested data
     */
    fun digestBase64String(key: K, data: String): String? {
        return Codec.Companion.encodeBase64String(digest(key, data))
    }

    /**
     * Digests data by given secret key bytes.
     *
     * @param keyBytes given secret key bytes
     * @param data     data
     * @return base64 string encoded by digested data
     */
    fun digestBase64String(keyBytes: ByteArray?, data: String): String? {
        return Codec.Companion.encodeBase64String(digest(keyBytes, data))
    }
}