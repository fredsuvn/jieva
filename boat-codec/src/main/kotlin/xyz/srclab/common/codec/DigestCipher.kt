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
 * Digest cipher.
 *
 * @author sunqian
 */
interface DigestCipher : CodecCipher {
    /**
     * Digests data.
     *
     * @param data data
     * @return digested data
     */
    fun digest(data: ByteArray?): ByteArray?

    /**
     * Digests data.
     *
     * @param data data
     * @return digested data
     */
    fun digest(data: String): ByteArray? {
        return digest(CodecBytes.toBytes(data))
    }

    /**
     * Digests data.
     *
     * @param data data
     * @return digested data as string
     */
    fun digestString(data: ByteArray?): String? {
        return CodecBytes.toString(digest(data))
    }

    /**
     * Digests data.
     *
     * @param data data
     * @return digested data as string
     */
    fun digestString(data: String): String? {
        return CodecBytes.toString(digest(data))
    }

    /**
     * Digests data.
     *
     * @param data data
     * @return hex string encoded by digested data
     */
    fun digestHexString(data: ByteArray?): String? {
        return Codec.Companion.encodeHexString(digest(data))
    }

    /**
     * Digests data.
     *
     * @param data data
     * @return hex string encoded by digested data
     */
    fun digestHexString(data: String): String? {
        return Codec.Companion.encodeHexString(digest(data))
    }

    /**
     * Digests data.
     *
     * @param data data
     * @return base64 string encoded by digested data
     */
    fun digestBase64String(data: ByteArray?): String? {
        return Codec.Companion.encodeBase64String(digest(data))
    }

    /**
     * Digests data.
     *
     * @param data data
     * @return base64 string encoded by digested data
     */
    fun digestBase64String(data: String): String? {
        return Codec.Companion.encodeBase64String(digest(data))
    }
}