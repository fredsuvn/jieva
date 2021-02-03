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
 * Reversible cipher.
 *
 * @param [EK] encrypt key
 * @param [DK] decrypt key
 * @author sunqian
*/
interface ReversibleCipher<EK, DK> : CodecCipher {

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data      data
     * @return encrypted data
     */
    fun encrypt(encryptKey: EK, data: ByteArray): ByteArray

    /**
     * Encrypts data by given encrypt key bytes.
     *
     * @param encryptKeyBytes encrypt key bytes
     * @param data           data
     * @return encrypted data
     */
    fun encrypt(encryptKeyBytes: ByteArray, data: ByteArray): ByteArray

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data      data
     * @return encrypted data
     */
    fun encrypt(encryptKey: EK, data: String): ByteArray {
        return encrypt(encryptKey, CodecBytes.toBytes(data))
    }

    /**
     * Encrypts data by given encrypt key bytes.
     *
     * @param encryptKeyBytes encrypt key bytes
     * @param data           data
     * @return encrypted data
     */
    fun encrypt(encryptKeyBytes: ByteArray, data: String): ByteArray {
        return encrypt(encryptKeyBytes, CodecBytes.toBytes(data))
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data      data
     * @return encrypted data as string
     */
    fun encryptString(encryptKey: EK, data: ByteArray): String {
        return CodecBytes.toString(encrypt(encryptKey, data))
    }

    /**
     * Encrypts data by given encrypt key bytes.
     *
     * @param encryptKeyBytes encrypt key bytes
     * @param data           data
     * @return encrypted data as string
     */
    fun encryptString(encryptKeyBytes: ByteArray, data: ByteArray): String {
        return CodecBytes.toString(encrypt(encryptKeyBytes, data))
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data      data
     * @return encrypted data as string
     */
    fun encryptString(encryptKey: EK, data: String): String {
        return CodecBytes.toString(encrypt(encryptKey, data))
    }

    /**
     * Encrypts data by given encrypt key bytes.
     *
     * @param encryptKeyBytes encrypt key bytes
     * @param data           data
     * @return encrypted data as string
     */
    fun encryptString(encryptKeyBytes: ByteArray, data: String): String {
        return CodecBytes.toString(encrypt(encryptKeyBytes, data))
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data      data
     * @return hex string encoded by encrypted data
     */
    fun encryptHexString(encryptKey: EK, data: ByteArray): String {
        return Codec.Companion.encodeHexString(encrypt(encryptKey, data))
    }

    /**
     * Encrypts data by given encrypt key bytes.
     *
     * @param encryptKeyBytes encrypt key bytes
     * @param data           data
     * @return hex string encoded by encrypted data
     */
    fun encryptHexString(encryptKeyBytes: ByteArray, data: ByteArray): String {
        return Codec.Companion.encodeHexString(encrypt(encryptKeyBytes, data))
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data      data
     * @return hex string encoded by encrypted data
     */
    fun encryptHexString(encryptKey: EK, data: String): String {
        return Codec.Companion.encodeHexString(encrypt(encryptKey, data))
    }

    /**
     * Encrypts data by given encrypt key bytes.
     *
     * @param encryptKeyBytes encrypt key bytes
     * @param data           data
     * @return hex string encoded by encrypted data
     */
    fun encryptHexString(encryptKeyBytes: ByteArray, data: String): String {
        return Codec.Companion.encodeHexString(encrypt(encryptKeyBytes, data))
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data      data
     * @return base64 string encoded by encrypted data
     */
    fun encryptBase64String(encryptKey: EK, data: ByteArray): String {
        return Codec.Companion.encodeBase64String(encrypt(encryptKey, data))
    }

    /**
     * Encrypts data by given encrypt key bytes.
     *
     * @param encryptKeyBytes encrypt key bytes
     * @param data           data
     * @return base64 string encoded by encrypted data
     */
    fun encryptBase64String(encryptKeyBytes: ByteArray, data: ByteArray): String {
        return Codec.Companion.encodeBase64String(encrypt(encryptKeyBytes, data))
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data      data
     * @return base64 string encoded by encrypted data
     */
    fun encryptBase64String(encryptKey: EK, data: String): String {
        return Codec.Companion.encodeBase64String(encrypt(encryptKey, data))
    }

    /**
     * Encrypts data by given encrypt key bytes.
     *
     * @param encryptKeyBytes encrypt key bytes
     * @param data           data
     * @return base64 string encoded by encrypted data
     */
    fun encryptBase64String(encryptKeyBytes: ByteArray, data: String): String {
        return Codec.Companion.encodeBase64String(encrypt(encryptKeyBytes, data))
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return decrypted data
     */
    fun decrypt(decryptKey: DK, encrypted: ByteArray): ByteArray

    /**
     * Decrypts data by given decrypt key bytes.
     *
     * @param decryptKeyBytes decrypt key bytes
     * @param encrypted       encrypted data
     * @return decrypted data
     */
    fun decrypt(decryptKeyBytes: ByteArray, encrypted: ByteArray): ByteArray

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return decrypted data
     */
    fun decrypt(decryptKey: DK, encrypted: String): ByteArray {
        return decrypt(decryptKey, CodecBytes.toBytes(encrypted))
    }

    /**
     * Decrypts data by given decrypt key bytes.
     *
     * @param decryptKeyBytes decrypt key bytes
     * @param encrypted       encrypted data
     * @return decrypted data
     */
    fun decrypt(decryptKeyBytes: ByteArray, encrypted: String): ByteArray {
        return decrypt(decryptKeyBytes, CodecBytes.toBytes(encrypted))
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return decrypted data as string
     */
    fun decryptString(decryptKey: DK, encrypted: ByteArray): String {
        return CodecBytes.toString(decrypt(decryptKey, encrypted))
    }

    /**
     * Decrypts data by given decrypt key bytes.
     *
     * @param decryptKeyBytes decrypt key bytes
     * @param encrypted       encrypted data
     * @return decrypted data as string
     */
    fun decryptString(decryptKeyBytes: ByteArray, encrypted: ByteArray): String {
        return CodecBytes.toString(decrypt(decryptKeyBytes, encrypted))
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return decrypted data as string
     */
    fun decryptString(decryptKey: DK, encrypted: String): String {
        return CodecBytes.toString(decrypt(decryptKey, encrypted))
    }

    /**
     * Decrypts data by given decrypt key bytes.
     *
     * @param decryptKeyBytes decrypt key bytes
     * @param encrypted       encrypted data
     * @return decrypted data as string
     */
    fun decryptString(decryptKeyBytes: ByteArray, encrypted: String): String {
        return CodecBytes.toString(decrypt(decryptKeyBytes, encrypted))
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return hex string encoded by decrypted data
     */
    fun decryptHexString(decryptKey: DK, encrypted: ByteArray): String {
        return Codec.Companion.encodeHexString(decrypt(decryptKey, encrypted))
    }

    /**
     * Decrypts data by given decrypt key bytes.
     *
     * @param decryptKeyBytes decrypt key bytes
     * @param encrypted       encrypted data
     * @return hex string encoded by decrypted data
     */
    fun decryptHexString(decryptKeyBytes: ByteArray, encrypted: ByteArray): String {
        return Codec.Companion.encodeHexString(decrypt(decryptKeyBytes, encrypted))
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return hex string encoded by decrypted data
     */
    fun decryptHexString(decryptKey: DK, encrypted: String): String {
        return Codec.Companion.encodeHexString(decrypt(decryptKey, encrypted))
    }

    /**
     * Decrypts data by given decrypt key bytes.
     *
     * @param decryptKeyBytes decrypt key bytes
     * @param encrypted       encrypted data
     * @return hex string encoded by decrypted data
     */
    fun decryptHexString(decryptKeyBytes: ByteArray, encrypted: String): String {
        return Codec.Companion.encodeHexString(decrypt(decryptKeyBytes, encrypted))
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return base64 string encoded by decrypted data
     */
    fun decryptBase64String(decryptKey: DK, encrypted: ByteArray): String {
        return Codec.Companion.encodeBase64String(decrypt(decryptKey, encrypted))
    }

    /**
     * Decrypts data by given decrypt key bytes.
     *
     * @param decryptKeyBytes decrypt key bytes
     * @param encrypted       encrypted data
     * @return base64 string encoded by decrypted data
     */
    fun decryptBase64String(decryptKeyBytes: ByteArray, encrypted: ByteArray): String {
        return Codec.Companion.encodeBase64String(decrypt(decryptKeyBytes, encrypted))
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return base64 string encoded by decrypted data
     */
    fun decryptBase64String(decryptKey: DK, encrypted: String): String {
        return Codec.Companion.encodeBase64String(decrypt(decryptKey, encrypted))
    }

    /**
     * Decrypts data by given decrypt key bytes.
     *
     * @param decryptKeyBytes decrypt key bytes
     * @param encrypted       encrypted data
     * @return base64 string encoded by decrypted data
     */
    fun decryptBase64String(decryptKeyBytes: ByteArray, encrypted: String): String {
        return Codec.Companion.encodeBase64String(decrypt(decryptKeyBytes, encrypted))
    }
}