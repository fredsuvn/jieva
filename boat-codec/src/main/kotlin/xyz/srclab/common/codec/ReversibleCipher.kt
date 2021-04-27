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
 *
 * @see SymmetricCipher
 * @see AsymmetricCipher
 * @see DigestCipher
 * @see HmacDigestCipher
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
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return encrypted data
     */
    fun encrypt(encryptKey: ByteArray, data: ByteArray): ByteArray

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return encrypted data
     */
    @JvmDefault
    fun encrypt(encryptKey: CharSequence, data: ByteArray): ByteArray {
        return encrypt(encryptKey.toBytes(), data)
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return encrypted data
     */
    @JvmDefault
    fun encryptWithAny(encryptKey: Any, data: ByteArray): ByteArray

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data      data
     * @return encrypted data
     */
    @JvmDefault
    fun encrypt(encryptKey: EK, data: CharSequence): ByteArray {
        return encrypt(encryptKey, data.toBytes())
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return encrypted data
     */
    @JvmDefault
    fun encrypt(encryptKey: ByteArray, data: CharSequence): ByteArray {
        return encrypt(encryptKey, data.toBytes())
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return encrypted data
     */
    @JvmDefault
    fun encrypt(encryptKey: CharSequence, data: CharSequence): ByteArray {
        return encrypt(encryptKey, data.toBytes())
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return encrypted data
     */
    @JvmDefault
    fun encryptWithAny(encryptKey: Any, data: CharSequence): ByteArray {
        return encryptWithAny(encryptKey, data.toBytes())
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data      data
     * @return encrypted data as string
     */
    @JvmDefault
    fun encryptToString(encryptKey: EK, data: ByteArray): String {
        return encrypt(encryptKey, data).toChars()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return encrypted data as string
     */
    @JvmDefault
    fun encryptToString(encryptKey: ByteArray, data: ByteArray): String {
        return encrypt(encryptKey, data).toChars()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return encrypted data as string
     */
    @JvmDefault
    fun encryptToString(encryptKey: CharSequence, data: ByteArray): String {
        return encrypt(encryptKey, data).toChars()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return encrypted data as string
     */
    @JvmDefault
    fun encryptToStringWithAny(encryptKey: Any, data: ByteArray): String {
        return encryptWithAny(encryptKey, data).toChars()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data      data
     * @return encrypted data as string
     */
    @JvmDefault
    fun encryptToString(encryptKey: EK, data: CharSequence): String {
        return encrypt(encryptKey, data).toChars()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return encrypted data as string
     */
    @JvmDefault
    fun encryptToString(encryptKey: ByteArray, data: CharSequence): String {
        return encrypt(encryptKey, data).toChars()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return encrypted data as string
     */
    @JvmDefault
    fun encryptToString(encryptKey: CharSequence, data: CharSequence): String {
        return encrypt(encryptKey, data).toChars()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return encrypted data as string
     */
    @JvmDefault
    fun encryptToStringWithAny(encryptKey: Any, data: CharSequence): String {
        return encryptWithAny(encryptKey, data).toChars()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data      data
     * @return hex string encoded by encrypted data
     */
    @JvmDefault
    fun encryptToHexString(encryptKey: EK, data: ByteArray): String {
        return encrypt(encryptKey, data).encodeHexString()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return hex string encoded by encrypted data
     */
    @JvmDefault
    fun encryptToHexString(encryptKey: ByteArray, data: ByteArray): String {
        return encrypt(encryptKey, data).encodeHexString()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return hex string encoded by encrypted data
     */
    @JvmDefault
    fun encryptToHexString(encryptKey: CharSequence, data: ByteArray): String {
        return encrypt(encryptKey, data).encodeHexString()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return hex string encoded by encrypted data
     */
    @JvmDefault
    fun encryptToHexStringWithAny(encryptKey: Any, data: ByteArray): String {
        return encryptWithAny(encryptKey, data).encodeHexString()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data      data
     * @return hex string encoded by encrypted data
     */
    @JvmDefault
    fun encryptToHexString(encryptKey: EK, data: CharSequence): String {
        return encrypt(encryptKey, data).encodeHexString()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return hex string encoded by encrypted data
     */
    @JvmDefault
    fun encryptToHexString(encryptKey: ByteArray, data: CharSequence): String {
        return encrypt(encryptKey, data).encodeHexString()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return hex string encoded by encrypted data
     */
    @JvmDefault
    fun encryptToHexString(encryptKey: CharSequence, data: CharSequence): String {
        return encrypt(encryptKey, data).encodeHexString()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return hex string encoded by encrypted data
     */
    @JvmDefault
    fun encryptToHexStringWithAny(encryptKey: Any, data: CharSequence): String {
        return encryptWithAny(encryptKey, data).encodeHexString()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data      data
     * @return base64 string encoded by encrypted data
     */
    @JvmDefault
    fun encryptToBase64String(encryptKey: EK, data: ByteArray): String {
        return encrypt(encryptKey, data).encodeBase64String()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return base64 string encoded by encrypted data
     */
    @JvmDefault
    fun encryptToBase64String(encryptKey: ByteArray, data: ByteArray): String {
        return encrypt(encryptKey, data).encodeBase64String()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return base64 string encoded by encrypted data
     */
    @JvmDefault
    fun encryptToBase64String(encryptKey: CharSequence, data: ByteArray): String {
        return encrypt(encryptKey, data).encodeBase64String()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return base64 string encoded by encrypted data
     */
    @JvmDefault
    fun encryptToBase64StringWithAny(encryptKey: Any, data: ByteArray): String {
        return encryptWithAny(encryptKey, data).encodeBase64String()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data      data
     * @return base64 string encoded by encrypted data
     */
    @JvmDefault
    fun encryptToBase64String(encryptKey: EK, data: CharSequence): String {
        return encrypt(encryptKey, data).encodeBase64String()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return base64 string encoded by encrypted data
     */
    @JvmDefault
    fun encryptToBase64String(encryptKey: ByteArray, data: CharSequence): String {
        return encrypt(encryptKey, data).encodeBase64String()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return base64 string encoded by encrypted data
     */
    @JvmDefault
    fun encryptToBase64String(encryptKey: CharSequence, data: CharSequence): String {
        return encrypt(encryptKey, data).encodeBase64String()
    }

    /**
     * Encrypts data by given encrypt key.
     *
     * @param encryptKey encrypt key
     * @param data           data
     * @return base64 string encoded by encrypted data
     */
    @JvmDefault
    fun encryptToBase64StringWithAny(encryptKey: Any, data: CharSequence): String {
        return encryptWithAny(encryptKey, data).encodeBase64String()
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
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return decrypted data
     */
    fun decrypt(decryptKey: ByteArray, encrypted: ByteArray): ByteArray

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return decrypted data
     */
    @JvmDefault
    fun decrypt(decryptKey: CharSequence, encrypted: ByteArray): ByteArray {
        return decrypt(decryptKey.toBytes(), encrypted)
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return decrypted data
     */
    @JvmDefault
    fun decryptWithAny(decryptKey: Any, encrypted: ByteArray): ByteArray

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return decrypted data
     */
    @JvmDefault
    fun decrypt(decryptKey: DK, encrypted: CharSequence): ByteArray {
        return decrypt(decryptKey, encrypted.toBytes())
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return decrypted data
     */
    @JvmDefault
    fun decrypt(decryptKey: ByteArray, encrypted: CharSequence): ByteArray {
        return decrypt(decryptKey, encrypted.toBytes())
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return decrypted data
     */
    @JvmDefault
    fun decrypt(decryptKey: CharSequence, encrypted: CharSequence): ByteArray {
        return decrypt(decryptKey, encrypted.toBytes())
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return decrypted data
     */
    @JvmDefault
    fun decryptWithAny(decryptKey: Any, encrypted: CharSequence): ByteArray {
        return decryptWithAny(decryptKey, encrypted.toBytes())
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return decrypted data as string
     */
    @JvmDefault
    fun decryptToString(decryptKey: DK, encrypted: ByteArray): String {
        return decrypt(decryptKey, encrypted).toChars()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return decrypted data as string
     */
    @JvmDefault
    fun decryptToString(decryptKey: ByteArray, encrypted: ByteArray): String {
        return decrypt(decryptKey, encrypted).toChars()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return decrypted data as string
     */
    @JvmDefault
    fun decryptToString(decryptKey: CharSequence, encrypted: ByteArray): String {
        return decrypt(decryptKey, encrypted).toChars()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return decrypted data as string
     */
    @JvmDefault
    fun decryptToStringWithAny(decryptKey: Any, encrypted: ByteArray): String {
        return decryptWithAny(decryptKey, encrypted).toChars()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return decrypted data as string
     */
    @JvmDefault
    fun decryptToString(decryptKey: DK, encrypted: CharSequence): String {
        return decrypt(decryptKey, encrypted).toChars()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return decrypted data as string
     */
    @JvmDefault
    fun decryptToString(decryptKey: ByteArray, encrypted: CharSequence): String {
        return decrypt(decryptKey, encrypted).toChars()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return decrypted data as string
     */
    @JvmDefault
    fun decryptToString(decryptKey: CharSequence, encrypted: CharSequence): String {
        return decrypt(decryptKey, encrypted).toChars()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return decrypted data as string
     */
    @JvmDefault
    fun decryptToStringWithAny(decryptKey: Any, encrypted: CharSequence): String {
        return decryptWithAny(decryptKey, encrypted).toChars()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return hex string encoded by decrypted data
     */
    @JvmDefault
    fun decryptToHexString(decryptKey: DK, encrypted: ByteArray): String {
        return decrypt(decryptKey, encrypted).encodeHexString()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return hex string encoded by decrypted data
     */
    @JvmDefault
    fun decryptToHexString(decryptKey: ByteArray, encrypted: ByteArray): String {
        return decrypt(decryptKey, encrypted).encodeHexString()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return hex string encoded by decrypted data
     */
    @JvmDefault
    fun decryptToHexString(decryptKey: CharSequence, encrypted: ByteArray): String {
        return decrypt(decryptKey, encrypted).encodeHexString()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return hex string encoded by decrypted data
     */
    @JvmDefault
    fun decryptToHexStringWithAny(decryptKey: Any, encrypted: ByteArray): String {
        return decryptWithAny(decryptKey, encrypted).encodeHexString()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return hex string encoded by decrypted data
     */
    @JvmDefault
    fun decryptToHexString(decryptKey: DK, encrypted: CharSequence): String {
        return decrypt(decryptKey, encrypted).encodeHexString()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return hex string encoded by decrypted data
     */
    @JvmDefault
    fun decryptToHexString(decryptKey: ByteArray, encrypted: CharSequence): String {
        return decrypt(decryptKey, encrypted).encodeHexString()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return hex string encoded by decrypted data
     */
    @JvmDefault
    fun decryptToHexString(decryptKey: CharSequence, encrypted: CharSequence): String {
        return decrypt(decryptKey, encrypted).encodeHexString()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return hex string encoded by decrypted data
     */
    @JvmDefault
    fun decryptToHexStringWithAny(decryptKey: Any, encrypted: CharSequence): String {
        return decryptWithAny(decryptKey, encrypted).encodeHexString()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return base64 string encoded by decrypted data
     */
    @JvmDefault
    fun decryptToBase64String(decryptKey: DK, encrypted: ByteArray): String {
        return decrypt(decryptKey, encrypted).encodeBase64String()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return base64 string encoded by decrypted data
     */
    @JvmDefault
    fun decryptToBase64String(decryptKey: ByteArray, encrypted: ByteArray): String {
        return decrypt(decryptKey, encrypted).encodeBase64String()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return base64 string encoded by decrypted data
     */
    @JvmDefault
    fun decryptToBase64String(decryptKey: CharSequence, encrypted: ByteArray): String {
        return decrypt(decryptKey, encrypted).encodeBase64String()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return base64 string encoded by decrypted data
     */
    @JvmDefault
    fun decryptToBase64StringWithAny(decryptKey: Any, encrypted: ByteArray): String {
        return decryptWithAny(decryptKey, encrypted).encodeBase64String()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted  encrypted data
     * @return base64 string encoded by decrypted data
     */
    @JvmDefault
    fun decryptToBase64String(decryptKey: DK, encrypted: CharSequence): String {
        return decrypt(decryptKey, encrypted).encodeBase64String()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return base64 string encoded by decrypted data
     */
    @JvmDefault
    fun decryptToBase64String(decryptKey: ByteArray, encrypted: CharSequence): String {
        return decrypt(decryptKey, encrypted).encodeBase64String()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return base64 string encoded by decrypted data
     */
    @JvmDefault
    fun decryptToBase64String(decryptKey: CharSequence, encrypted: CharSequence): String {
        return decrypt(decryptKey, encrypted).encodeBase64String()
    }

    /**
     * Decrypts data by given decrypt key.
     *
     * @param decryptKey decrypt key
     * @param encrypted       encrypted data
     * @return base64 string encoded by decrypted data
     */
    @JvmDefault
    fun decryptToBase64StringWithAny(decryptKey: Any, encrypted: CharSequence): String {
        return decryptWithAny(decryptKey, encrypted).encodeBase64String()
    }
}