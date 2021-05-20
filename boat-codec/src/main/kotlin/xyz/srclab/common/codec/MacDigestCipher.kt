package xyz.srclab.common.codec

import xyz.srclab.common.codec.CodecAlgorithm.Companion.toCodecAlgorithm
import xyz.srclab.common.lang.toBytes
import xyz.srclab.common.lang.toChars
import java.io.OutputStream
import javax.crypto.Mac

/**
 * MAC digest cipher.
 *
 * @author sunqian
 */
interface MacDigestCipher : Codec {

    @JvmDefault
    fun digest(key: Any, data: ByteArray): ByteArray {
        return digest(key, data, 0)
    }

    @JvmDefault
    fun digest(key: Any, data: ByteArray, offset: Int): ByteArray {
        return digest(key, data, offset, data.size - offset)
    }

    @JvmDefault
    fun digest(key: Any, data: ByteArray, offset: Int, length: Int): ByteArray

    @JvmDefault
    fun digest(key: Any, data: ByteArray, output: OutputStream): Int {
        return digest(key, data, 0, output)
    }

    @JvmDefault
    fun digest(key: Any, data: ByteArray, offset: Int, output: OutputStream): Int {
        return digest(key, data, offset, data.size - offset, output)
    }

    @JvmDefault
    fun digest(key: Any, data: ByteArray, offset: Int, length: Int, output: OutputStream): Int {
        val bytes = digest(key, data, offset, length)
        output.write(bytes)
        return bytes.size
    }

    @JvmDefault
    fun digest(key: Any, data: CharSequence): ByteArray {
        return digest(key, data.toBytes())
    }

    @JvmDefault
    fun digest(key: Any, data: CharSequence, output: OutputStream): Int {
        return digest(key, data.toBytes(), output)
    }

    @JvmDefault
    fun digestToString(key: Any, data: ByteArray): String {
        return digest(key, data).toChars()
    }

    @JvmDefault
    fun digestToString(key: Any, data: ByteArray, offset: Int): String {
        return digest(key, data, offset).toChars()
    }

    @JvmDefault
    fun digestToString(key: Any, data: ByteArray, offset: Int, length: Int): String {
        return digest(key, data, offset, length).toChars()
    }

    @JvmDefault
    fun digestToString(key: Any, data: CharSequence): String {
        return digest(key, data).toChars()
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