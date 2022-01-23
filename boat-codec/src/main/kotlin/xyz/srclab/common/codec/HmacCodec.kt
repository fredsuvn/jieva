package xyz.srclab.common.codec

import xyz.srclab.common.base.ThreadSafePolicy
import xyz.srclab.common.base.remainingLength
import xyz.srclab.common.base.toKotlinFun
import xyz.srclab.common.io.toBytes
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.security.Key
import java.util.function.Supplier
import javax.crypto.Mac

/**
 * MAC digest codec such as `HmacMD5`.
 *
 * @author sunqian
 */
interface HmacCodec : Codec {

    fun digest(key: Key, data: ByteArray): ByteArray {
        return digest(key, data, 0)
    }

    fun digest(key: Key, data: ByteArray, offset: Int): ByteArray {
        return digest(key, data, offset, remainingLength(data.size, offset))
    }

    fun digest(key: Key, data: ByteArray, offset: Int, length: Int): ByteArray


    fun digest(key: Key, data: ByteArray, output: OutputStream): Long {
        return digest(key, data, 0, output)
    }

    fun digest(key: Key, data: ByteArray, offset: Int, output: OutputStream): Long {
        return digest(key, data, offset, remainingLength(data.size, offset), output)
    }

    fun digest(key: Key, data: ByteArray, offset: Int, length: Int, output: OutputStream): Long {
        val digest = digest(key, data, offset, length)
        output.write(digest)
        return digest.size.toLong()
    }

    fun digest(key: Key, data: ByteBuffer): ByteBuffer {
        return ByteBuffer.wrap(digest(key, data.toBytes(true)))
    }

    fun digest(key: Key, data: InputStream): ByteArray {
        return digest(key, data.readBytes())
    }

    fun digest(key: Key, data: InputStream, output: OutputStream): Long {
        return digest(key, data.readBytes(), output)
    }

    /**
     * Builder for [HmacCodec].
     */
    open class Builder {

        private var algorithm: CodecAlgorithm? = null
        private var hmacSupplier: Supplier<Mac>? = null
        private var threadSafePolicy: ThreadSafePolicy = ThreadSafePolicy.SYNCHRONIZED

        open fun algorithm(algorithm: CodecAlgorithm) = apply {
            this.algorithm = algorithm
        }

        open fun hmacSupplier(hmacSupplier: Supplier<Mac>) = apply {
            this.hmacSupplier = hmacSupplier
        }

        /**
         * Default is [ThreadSafePolicy.SYNCHRONIZED].
         */
        open fun threadSafePolicy(threadSafePolicy: ThreadSafePolicy) = apply {
            this.threadSafePolicy = threadSafePolicy
        }

        open fun build(): HmacCodec {
            val algorithm = this.algorithm
            if (algorithm === null) {
                throw IllegalStateException("algorithm was not specified.")
            }
            val hmacSupplier = this.hmacSupplier
            if (hmacSupplier === null) {
                throw IllegalStateException("digesterSupplier was not specified.")
            }
            if (threadSafePolicy == ThreadSafePolicy.THREAD_LOCAL) {
                return ThreadLocalHmacCodec(algorithm, hmacSupplier.toKotlinFun())
            }
            val digester = HmacCodecImpl(algorithm, hmacSupplier.get())
            if (threadSafePolicy == ThreadSafePolicy.SYNCHRONIZED) {
                return SynchronizedHmacCodec(digester)
            }
            return digester
        }

        private class HmacCodecImpl(
            override val algorithm: CodecAlgorithm,
            private val hmac: Mac
        ) : HmacCodec {

            override fun digest(key: Key, data: ByteArray, offset: Int, length: Int): ByteArray {
                hmac.reset()
                hmac.init(key)
                hmac.update(data, offset, length)
                return hmac.doFinal()
            }

            override fun digest(key: Key, data: ByteBuffer): ByteBuffer {
                hmac.reset()
                hmac.init(key)
                hmac.update(data)
                return ByteBuffer.wrap(hmac.doFinal())
            }
        }

        private class SynchronizedHmacCodec(
            private val hmacCodec: HmacCodec
        ) : HmacCodec {

            override val algorithm: CodecAlgorithm = hmacCodec.algorithm

            @Synchronized
            override fun digest(key: Key, data: ByteArray, offset: Int, length: Int): ByteArray {
                return hmacCodec.digest(key, data, offset, length)
            }

            @Synchronized
            override fun digest(key: Key, data: ByteBuffer): ByteBuffer {
                return hmacCodec.digest(key, data)
            }
        }

        private class ThreadLocalHmacCodec(
            override val algorithm: CodecAlgorithm,
            hmac: () -> Mac
        ) : HmacCodec {

            private val threadLocal: ThreadLocal<Mac> = ThreadLocal.withInitial(hmac)

            override fun digest(key: Key, data: ByteArray, offset: Int, length: Int): ByteArray {
                val hmac = threadLocal.get()
                hmac.reset()
                hmac.init(key)
                hmac.update(data, offset, length)
                return hmac.doFinal()
            }

            override fun digest(key: Key, data: ByteBuffer): ByteBuffer {
                val hmac = threadLocal.get()
                hmac.reset()
                hmac.init(key)
                hmac.update(data)
                return ByteBuffer.wrap(hmac.doFinal())
            }
        }
    }

    companion object {

        @JvmStatic
        fun newBuilder(): Builder {
            return Builder()
        }

        @JvmStatic
        fun hmacMd5(): HmacCodec {
            return withAlgorithm(CodecAlgorithm.HMAC_MD5_NAME)
        }

        @JvmStatic
        fun hmacSha1(): HmacCodec {
            return withAlgorithm(CodecAlgorithm.HMAC_SHA1_NAME)
        }

        @JvmStatic
        fun hmacSha256(): HmacCodec {
            return withAlgorithm(CodecAlgorithm.HMAC_SHA256_NAME)
        }

        @JvmStatic
        fun hmacSha384(): HmacCodec {
            return withAlgorithm(CodecAlgorithm.HMAC_SHA384_NAME)
        }

        @JvmStatic
        fun hmacSha512(): HmacCodec {
            return withAlgorithm(CodecAlgorithm.HMAC_SHA512_NAME)
        }

        @JvmStatic
        fun withAlgorithm(
            algorithm: CharSequence
        ): HmacCodec {
            return newBuilder()
                .algorithm(CodecAlgorithm.forName(algorithm))
                .hmacSupplier { Mac.getInstance(algorithm.toString()) }
                .build()
        }
    }
}