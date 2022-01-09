package xyz.srclab.common.codec

import org.bouncycastle.util.encoders.HexEncoder
import xyz.srclab.common.codec.Codec.Companion.toCodecKey
import xyz.srclab.common.lang.toBytes
import xyz.srclab.common.lang.toChars
import java.io.OutputStream
import java.util.*
import javax.crypto.Mac

/**
 * MAC digest codec such as `HmacMD5`.
 *
 * @author sunqian
 */
interface MacCodec : Codec {

    @JvmDefault
    fun digest(key: Any, data: ByteArray): ByteArray {
        HexEncoder.
        return digest(key, data, 0)
    }

    @JvmDefault
    fun digest(key: Any, data: ByteArray, offset: Int): ByteArray {
        return digest(key, data, offset, data.size - offset)
    }

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

        @JvmStatic
        fun hmacMd5(): MacCodec {
            return withAlgorithm(CodecAlgorithm.HMAC_MD5_NAME)
        }

        @JvmStatic
        fun hmacSha1(): MacCodec {
            return withAlgorithm(CodecAlgorithm.HMAC_SHA1_NAME)
        }

        @JvmStatic
        fun hmacSha256(): MacCodec {
            return withAlgorithm(CodecAlgorithm.HMAC_SHA256_NAME)
        }

        @JvmStatic
        fun hmacSha384(): MacCodec {
            return withAlgorithm(CodecAlgorithm.HMAC_SHA384_NAME)
        }

        @JvmStatic
        fun hmacSha512(): MacCodec {
            return withAlgorithm(CodecAlgorithm.HMAC_SHA512_NAME)
        }

        @JvmStatic
        fun withAlgorithm(
            algorithm: CharSequence
        ): MacCodec {
            return MacCodecImpl(algorithm.toString())
        }

        private class MacCodecImpl(
            override val algorithm: String
        ) : MacCodec {

            private val mac: Mac by lazy {
                Mac.getInstance(algorithm)
            }

            override fun digest(key: Any, data: ByteArray, offset: Int, length: Int): ByteArray {
                mac.reset()
                mac.init(key.toCodecKey(algorithm))
                mac.update(data, offset, length)
                return mac.doFinal()
            }
        }
    }
}