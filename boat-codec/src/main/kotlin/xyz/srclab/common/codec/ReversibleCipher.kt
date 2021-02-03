package xyz.srclab.common.codec

import xyz.srclab.common.base.toBytes
import xyz.srclab.common.base.toChars
import xyz.srclab.common.codec.Codec.Companion.encodeBase64String
import xyz.srclab.common.codec.Codec.Companion.encodeHexString

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
    @JvmDefault
    fun encrypt(encryptKey: EK, data: String): ByteArray {
        return encrypt(encryptKey, data.toBytes())
    }

    /**
     * Encrypts data by given encrypt key bytes.
     *
     * @param encryptKeyBytes encrypt key bytes
     * @param data           data
     * @return encrypted data
     */
    @JvmDefault
    fun encrypt(encryptKeyBytes: ByteArray, data: String): ByteArray {
        return encrypt(encryptKeyBytes, data.toBytes())
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data      data
     * @return encrypted data as string
     */
    @JvmDefault
    fun encryptString(encryptKey: EK, data: ByteArray): String {
        return encrypt(encryptKey, data).toChars()
    }

    /**
     * Encrypts data by given encrypt key bytes.
     *
     * @param encryptKeyBytes encrypt key bytes
     * @param data           data
     * @return encrypted data as string
     */
    @JvmDefault
    fun encryptString(encryptKeyBytes: ByteArray, data: ByteArray): String {
        return encrypt(encryptKeyBytes, data).toChars()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data      data
     * @return encrypted data as string
     */
    @JvmDefault
    fun encryptString(encryptKey: EK, data: String): String {
        return encrypt(encryptKey, data).toChars()
    }

    /**
     * Encrypts data by given encrypt key bytes.
     *
     * @param encryptKeyBytes encrypt key bytes
     * @param data           data
     * @return encrypted data as string
     */
    @JvmDefault
    fun encryptString(encryptKeyBytes: ByteArray, data: String): String {
        return encrypt(encryptKeyBytes, data).toChars()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data      data
     * @return hex string encoded by encrypted data
     */
    @JvmDefault
    fun encryptHexString(encryptKey: EK, data: ByteArray): String {
        return encrypt(encryptKey, data).toChars().encodeHexString()
    }

    /**
     * Encrypts data by given encrypt key bytes.
     *
     * @param encryptKeyBytes encrypt key bytes
     * @param data           data
     * @return hex string encoded by encrypted data
     */
    @JvmDefault
    fun encryptHexString(encryptKeyBytes: ByteArray, data: ByteArray): String {
        return encrypt(encryptKeyBytes, data).toChars().encodeHexString()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data      data
     * @return hex string encoded by encrypted data
     */
    @JvmDefault
    fun encryptHexString(encryptKey: EK, data: String): String {
        return encrypt(encryptKey, data).toChars().encodeHexString()
    }

    /**
     * Encrypts data by given encrypt key bytes.
     *
     * @param encryptKeyBytes encrypt key bytes
     * @param data           data
     * @return hex string encoded by encrypted data
     */
    @JvmDefault
    fun encryptHexString(encryptKeyBytes: ByteArray, data: String): String {
        return encrypt(encryptKeyBytes, data).toChars().encodeHexString()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data      data
     * @return base64 string encoded by encrypted data
     */
    @JvmDefault
    fun encryptBase64String(encryptKey: EK, data: ByteArray): String {
        return encrypt(encryptKey, data).toChars().encodeBase64String()
    }

    /**
     * Encrypts data by given encrypt key bytes.
     *
     * @param encryptKeyBytes encrypt key bytes
     * @param data           data
     * @return base64 string encoded by encrypted data
     */
    @JvmDefault
    fun encryptBase64String(encryptKeyBytes: ByteArray, data: ByteArray): String {
        return encrypt(encryptKeyBytes, data).toChars().encodeBase64String()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data      data
     * @return base64 string encoded by encrypted data
     */
    @JvmDefault
    fun encryptBase64String(encryptKey: EK, data: String): String {
        return encrypt(encryptKey, data).toChars().encodeBase64String()
    }

    /**
     * Encrypts data by given encrypt key bytes.
     *
     * @param encryptKeyBytes encrypt key bytes
     * @param data           data
     * @return base64 string encoded by encrypted data
     */
    @JvmDefault
    fun encryptBase64String(encryptKeyBytes: ByteArray, data: String): String {
        return encrypt(encryptKeyBytes, data).toChars().encodeBase64String()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return decrypted data
     */
    @JvmDefault
    fun decrypt(decryptKey: DK, encrypted: ByteArray): ByteArray

    /**
     * Decrypts data by given decrypt key bytes.
     *
     * @param decryptKey decrypt key bytes
     * @param encrypted       encrypted data
     * @return decrypted data
     */
    @JvmDefault
    fun decrypt(decryptKey: ByteArray, encrypted: ByteArray): ByteArray

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return decrypted data
     */
    @JvmDefault
    fun decrypt(decryptKey: DK, encrypted: String): ByteArray {
        return decrypt(decryptKey, encrypted.toBytes())
    }

    /**
     * Decrypts data by given decrypt key bytes.
     *
     * @param decryptKey decrypt key bytes
     * @param encrypted       encrypted data
     * @return decrypted data
     */
    @JvmDefault
    fun decrypt(decryptKey: ByteArray, encrypted: String): ByteArray {
        return decrypt(decryptKey, encrypted.toBytes())
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return decrypted data as string
     */
    @JvmDefault
    fun decryptString(decryptKey: DK, encrypted: ByteArray): String {
        return decrypt(decryptKey, encrypted).toChars()
    }

    /**
     * Decrypts data by given decrypt key bytes.
     *
     * @param decryptKey decrypt key bytes
     * @param encrypted       encrypted data
     * @return decrypted data as string
     */
    @JvmDefault
    fun decryptString(decryptKey: ByteArray, encrypted: ByteArray): String {
        return decrypt(decryptKey, encrypted).toChars()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return decrypted data as string
     */
    @JvmDefault
    fun decryptString(decryptKey: DK, encrypted: String): String {
        return decrypt(decryptKey, encrypted).toChars()
    }

    /**
     * Decrypts data by given decrypt key bytes.
     *
     * @param decryptKey decrypt key bytes
     * @param encrypted       encrypted data
     * @return decrypted data as string
     */
    @JvmDefault
    fun decryptString(decryptKey: ByteArray, encrypted: String): String {
        return decrypt(decryptKey, encrypted).toChars()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return hex string encoded by decrypted data
     */
    @JvmDefault
    fun decryptHexString(decryptKey: DK, encrypted: ByteArray): String {
        return decrypt(decryptKey, encrypted).toChars().encodeHexString()
    }

    /**
     * Decrypts data by given decrypt key bytes.
     *
     * @param decryptKey decrypt key bytes
     * @param encrypted       encrypted data
     * @return hex string encoded by decrypted data
     */
    @JvmDefault
    fun decryptHexString(decryptKey: ByteArray, encrypted: ByteArray): String {
        return decrypt(decryptKey, encrypted).toChars().encodeHexString()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return hex string encoded by decrypted data
     */
    @JvmDefault
    fun decryptHexString(decryptKey: DK, encrypted: String): String {
        return decrypt(decryptKey, encrypted).toChars().encodeHexString()
    }

    /**
     * Decrypts data by given decrypt key bytes.
     *
     * @param decryptKey decrypt key bytes
     * @param encrypted       encrypted data
     * @return hex string encoded by decrypted data
     */
    @JvmDefault
    fun decryptHexString(decryptKey: ByteArray, encrypted: String): String {
        return decrypt(decryptKey, encrypted).toChars().encodeHexString()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return base64 string encoded by decrypted data
     */
    @JvmDefault
    fun decryptBase64String(decryptKey: DK, encrypted: ByteArray): String {
        return decrypt(decryptKey, encrypted).toChars().encodeBase64String()
    }

    /**
     * Decrypts data by given decrypt key bytes.
     *
     * @param decryptKey decrypt key bytes
     * @param encrypted       encrypted data
     * @return base64 string encoded by decrypted data
     */
    @JvmDefault
    fun decryptBase64String(decryptKey: ByteArray, encrypted: ByteArray): String {
        return decrypt(decryptKey, encrypted).toChars().encodeBase64String()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return base64 string encoded by decrypted data
     */
    @JvmDefault
    fun decryptBase64String(decryptKey: DK, encrypted: String): String {
        return decrypt(decryptKey, encrypted).toChars().encodeBase64String()
    }

    /**
     * Decrypts data by given decrypt key bytes.
     *
     * @param decryptKey decrypt key bytes
     * @param encrypted       encrypted data
     * @return base64 string encoded by decrypted data
     */
    @JvmDefault
    fun decryptBase64String(decryptKey: ByteArray, encrypted: String): String {
        return decrypt(decryptKey, encrypted).toChars().encodeBase64String()
    }
}