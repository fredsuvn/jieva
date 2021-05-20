package xyz.srclab.common.codec

import xyz.srclab.common.codec.CodecAlgorithm.Companion.toCodecAlgorithm
import xyz.srclab.common.lang.toBytes
import xyz.srclab.common.lang.toChars
import java.io.OutputStream
import java.security.MessageDigest

/**
 * Digest codec.
 *
 * @author sunqian
 */
interface DigestCodec : Codec {

    @JvmDefault
    fun digest(data: ByteArray): ByteArray {
        return digest(data, 0)
    }

    @JvmDefault
    fun digest(data: ByteArray, offset: Int): ByteArray {
        return digest(data, offset, data.size - offset)
    }

    @JvmDefault
    fun digest(data: ByteArray, offset: Int, length: Int): ByteArray

    @JvmDefault
    fun digest(data: ByteArray, output: OutputStream): Int {
        return digest(data, 0, output)
    }

    @JvmDefault
    fun digest(data: ByteArray, offset: Int, output: OutputStream): Int {
        return digest(data, offset, data.size - offset, output)
    }

    @JvmDefault
    fun digest(data: ByteArray, offset: Int, length: Int, output: OutputStream): Int {
        val bytes = digest(data, offset, length)
        output.write(bytes)
        return bytes.size
    }

    @JvmDefault
    fun digest(data: CharSequence): ByteArray {
        return digest(data.toBytes())
    }

    @JvmDefault
    fun digest(data: CharSequence, output: OutputStream): Int {
        return digest(data.toBytes(), output)
    }

    @JvmDefault
    fun digestToString(data: ByteArray): String {
        return digest(data).toChars()
    }

    @JvmDefault
    fun digestToString(data: ByteArray, offset: Int): String {
        return digest(data, offset).toChars()
    }

    @JvmDefault
    fun digestToString(data: ByteArray, offset: Int, length: Int): String {
        return digest(data, offset, length).toChars()
    }

    @JvmDefault
    fun digestToString(data: CharSequence): String {
        return digest(data).toChars()
    }

    companion object {

        @JvmName("withAlgorithm")
        @JvmStatic
        fun CharSequence.toDigestCipher(): DigestCodec {
            return this.toCodecAlgorithm().toDigestCipher()
        }

        @JvmName("withAlgorithm")
        @JvmStatic
        fun CodecAlgorithm.toDigestCipher(): DigestCodec {
            return DigestCipherImpl(this.name)
        }

        private class DigestCipherImpl(private val algorithm: String) : DigestCodec {

            override val name = algorithm

            override fun digest(data: ByteArray, offset: Int, length: Int): ByteArray {
                val digester = MessageDigest.getInstance(algorithm)
                digester.update(data, offset, length)
                return digester.digest()
            }
        }
    }
}