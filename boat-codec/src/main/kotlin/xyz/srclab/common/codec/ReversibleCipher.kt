package xyz.srclab.common.codec

import xyz.srclab.common.codec.CodecAlgorithm.Companion.toCodecAlgorithm
import xyz.srclab.common.lang.toBytes
import xyz.srclab.common.lang.toChars
import java.io.OutputStream
import javax.crypto.Mac

/**
 * Reversible cipher.
 *
 * @author sunqian
 *
 * @see SymmetricCipher
 * @see AsymmetricCipher
 * @see DigestCodec
 * @see MacDigestCipher
 */
interface ReversibleCipher : Codec {

    @JvmDefault
    fun encrypt(key: Any, data: ByteArray): ByteArray {
        return encrypt(key, data, 0)
    }

    @JvmDefault
    fun encrypt(key: Any, data: ByteArray, offset: Int): ByteArray {
        return encrypt(key, data, offset, data.size - offset)
    }

    @JvmDefault
    fun encrypt(key: Any, data: ByteArray, offset: Int, length: Int): ByteArray

    @JvmDefault
    fun encrypt(key: Any, data: ByteArray, output: OutputStream): Int {
        return encrypt(key, data, 0, output)
    }

    @JvmDefault
    fun encrypt(key: Any, data: ByteArray, offset: Int, output: OutputStream): Int {
        return encrypt(key, data, offset, data.size - offset, output)
    }

    @JvmDefault
    fun encrypt(key: Any, data: ByteArray, offset: Int, length: Int, output: OutputStream): Int {
        val bytes = encrypt(key, data, offset, length)
        output.write(bytes)
        return bytes.size
    }

    @JvmDefault
    fun encrypt(key: Any, data: CharSequence): ByteArray {
        return encrypt(key, data.toBytes())
    }

    @JvmDefault
    fun encrypt(key: Any, data: CharSequence, output: OutputStream): Int {
        return encrypt(key, data.toBytes(), output)
    }

    @JvmDefault
    fun encryptToString(key: Any, data: ByteArray): String {
        return encrypt(key, data).toChars()
    }

    @JvmDefault
    fun encryptToString(key: Any, data: ByteArray, offset: Int): String {
        return encrypt(key, data, offset).toChars()
    }

    @JvmDefault
    fun encryptToString(key: Any, data: ByteArray, offset: Int, length: Int): String {
        return encrypt(key, data, offset, length).toChars()
    }

    @JvmDefault
    fun encryptToString(key: Any, data: CharSequence): String {
        return encrypt(key, data).toChars()
    }

    @JvmDefault
    fun decrypt(key: Any, data: ByteArray): ByteArray {
        return decrypt(key, data, 0)
    }

    @JvmDefault
    fun decrypt(key: Any, data: ByteArray, offset: Int): ByteArray {
        return decrypt(key, data, offset, data.size - offset)
    }

    @JvmDefault
    fun decrypt(key: Any, data: ByteArray, offset: Int, length: Int): ByteArray

    @JvmDefault
    fun decrypt(key: Any, data: ByteArray, output: OutputStream): Int {
        return decrypt(key, data, 0, output)
    }

    @JvmDefault
    fun decrypt(key: Any, data: ByteArray, offset: Int, output: OutputStream): Int {
        return decrypt(key, data, offset, data.size - offset, output)
    }

    @JvmDefault
    fun decrypt(key: Any, data: ByteArray, offset: Int, length: Int, output: OutputStream): Int {
        val bytes = decrypt(key, data, offset, length)
        output.write(bytes)
        return bytes.size
    }

    @JvmDefault
    fun decrypt(key: Any, data: CharSequence): ByteArray {
        return decrypt(key, data.toBytes())
    }

    @JvmDefault
    fun decrypt(key: Any, data: CharSequence, output: OutputStream): Int {
        return decrypt(key, data.toBytes(), output)
    }

    @JvmDefault
    fun decryptToString(key: Any, data: ByteArray): String {
        return decrypt(key, data).toChars()
    }

    @JvmDefault
    fun decryptToString(key: Any, data: ByteArray, offset: Int): String {
        return decrypt(key, data, offset).toChars()
    }

    @JvmDefault
    fun decryptToString(key: Any, data: ByteArray, offset: Int, length: Int): String {
        return decrypt(key, data, offset, length).toChars()
    }

    @JvmDefault
    fun decryptToString(key: Any, data: CharSequence): String {
        return decrypt(key, data).toChars()
    }

    companion object {

        @JvmName("withAlgorithm")
        @JvmStatic
        fun CharSequence.toMacDigestCipher(): MacDigestCipher {
            return this.toCodecAlgorithm().toMacDigestCipher()
        }

        @JvmName("withAlgorithm")
        @JvmStatic
        fun CodecAlgorithm.toMacDigestCipher(): MacDigestCipher {
            return MacDigestCipherImpl(this.name)
        }

        private class MacDigestCipherImpl(private val algorithm: String) : MacDigestCipher {

            override val name = algorithm

            override fun digest(key: Any, data: ByteArray, offset: Int, length: Int): ByteArray {
                val mac = Mac.getInstance(algorithm)
                mac.init(key.toSecretKey(algorithm))
                mac.update(data, offset, length)
                return mac.doFinal()
            }
        }
    }
}