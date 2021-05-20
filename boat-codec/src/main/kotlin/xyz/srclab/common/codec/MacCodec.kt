package xyz.srclab.common.codec

import xyz.srclab.common.codec.CodecAlgorithm.Companion.toCodecAlgorithm
import xyz.srclab.common.lang.toBytes
import xyz.srclab.common.lang.toChars
import java.io.OutputStream
import javax.crypto.Mac

/**
 * MAC digest codec.
 *
 * @author sunqian
 */
interface MacCodec : Codec {

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
        @JvmOverloads
        @JvmStatic
        fun CharSequence.toMacCodec(
            mac: () -> Mac = { Mac.getInstance(this.toString()) }
        ): MacCodec {
            return this.toCodecAlgorithm().toMacCodec(mac)
        }

        @JvmName("withAlgorithm")
        @JvmOverloads
        @JvmStatic
        fun CodecAlgorithm.toMacCodec(
            mac: () -> Mac = { Mac.getInstance(this.name) }
        ): MacCodec {
            return MacCodecImpl(this.name, mac)
        }

        @JvmStatic
        fun hmacMd5(): MacCodec {
            return CodecAlgorithm.HMAC_MD5.toMacCodec()
        }

        @JvmStatic
        fun hmacSha1(): MacCodec {
            return CodecAlgorithm.HMAC_SHA1.toMacCodec()
        }

        @JvmStatic
        fun hmacSha256(): MacCodec {
            return CodecAlgorithm.HMAC_SHA256.toMacCodec()
        }

        @JvmStatic
        fun hmacSha384(): MacCodec {
            return CodecAlgorithm.HMAC_SHA384.toMacCodec()
        }

        @JvmStatic
        fun hmacSha512(): MacCodec {
            return CodecAlgorithm.HMAC_SHA512.toMacCodec()
        }

        private class MacCodecImpl(
            private val algorithm: String,
            private val mac: () -> Mac
        ) : MacCodec {

            override val name = algorithm

            override fun digest(key: Any, data: ByteArray, offset: Int, length: Int): ByteArray {
                val mac = this.mac()
                mac.init(key.toCodecKey(algorithm))
                mac.update(data, offset, length)
                return mac.doFinal()
            }
        }
    }
}