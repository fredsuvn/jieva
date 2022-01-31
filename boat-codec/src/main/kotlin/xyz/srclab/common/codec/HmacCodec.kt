package xyz.srclab.common.codec

import xyz.srclab.common.base.ThreadSafePolicy
import xyz.srclab.common.base.remainingLength
import xyz.srclab.common.codec.CodecAlgorithm.Companion.toCodecAlgorithm
import java.io.InputStream
import java.nio.ByteBuffer
import java.security.Key
import java.security.Provider
import java.util.function.Supplier
import javax.crypto.Mac

/**
 * HMAC codec such as `HmacMD5`.
 */
interface HmacCodec : Codec {

    val hmac: Mac

    val hmacLength: Int
        get() = hmac.macLength

    fun hmac(key: Key, data: ByteArray): PreparedCodec {
        return hmac(key, data, 0)
    }

    fun hmac(key: Key, data: ByteArray, offset: Int): PreparedCodec {
        return hmac(key, data, offset, remainingLength(data.size, offset))
    }

    fun hmac(key: Key, data: ByteArray, offset: Int, length: Int): PreparedCodec {
        return ByteArrayPreparedCodec(hmac, key, data, offset, length)
    }

    fun hmac(key: Key, data: ByteBuffer): PreparedCodec {
        return ByteBufferPreparedCodec(hmac, key, data)
    }

    fun hmac(key: Key, data: InputStream): PreparedCodec {
        return InputStreamPreparedCodec(hmac, key, data)
    }

    open class ByteArrayPreparedCodec(
        private val hmac: Mac,
        private val key: Key,
        private val data: ByteArray,
        private val dataOffset: Int,
        private val dataLength: Int
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            hmac.reset()
            hmac.init(key)
            hmac.update(data, dataOffset, dataLength)
            return hmac.doFinal()
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            hmac.reset()
            hmac.init(key)
            hmac.update(data, dataOffset, dataLength)
            hmac.doFinal(dest, offset)
            return hmac.macLength
        }
    }

    open class ByteBufferPreparedCodec(
        private val hmac: Mac,
        private val key: Key,
        private val data: ByteBuffer
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            hmac.reset()
            hmac.init(key)
            hmac.update(data)
            return hmac.doFinal()
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            hmac.reset()
            hmac.init(key)
            hmac.update(data)
            hmac.doFinal(dest, offset)
            return hmac.macLength
        }
    }

    open class InputStreamPreparedCodec(
        private val hmac: Mac,
        private val key: Key,
        private val data: InputStream
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            hmac.reset()
            hmac.init(key)
            hmac.update(data.readBytes())
            return hmac.doFinal()
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            hmac.reset()
            hmac.init(key)
            hmac.update(data.readBytes())
            hmac.doFinal(dest, offset)
            return hmac.macLength
        }
    }

    /**
     * Builder for [HmacCodec].
     */
    open class Builder {

        private var algorithm: CodecAlgorithm? = null
        private var hmacSupplier: Supplier<Mac>? = null
        private var codecSupplier: Supplier<HmacCodec>? = null
        private var threadSafePolicy: ThreadSafePolicy = ThreadSafePolicy.SYNCHRONIZED

        open fun algorithm(algorithm: CodecAlgorithm) = apply {
            this.algorithm = algorithm
        }

        open fun hmacSupplier(hmacSupplier: Supplier<Mac>) = apply {
            this.hmacSupplier = hmacSupplier
        }

        open fun codecSupplier(codecSupplier: Supplier<HmacCodec>) = apply {
            this.codecSupplier = codecSupplier
        }

        /**
         * Default is [ThreadSafePolicy.SYNCHRONIZED].
         */
        open fun threadSafePolicy(threadSafePolicy: ThreadSafePolicy) = apply {
            this.threadSafePolicy = threadSafePolicy
        }

        open fun build(): HmacCodec {
            val codecSupplier = run {
                val c = this.codecSupplier
                if (c === null) {
                    val supplier = this.hmacSupplier
                    if (supplier === null) {
                        throw IllegalStateException("hmacSupplier was not specified.")
                    }
                    val algorithm = this.algorithm
                    if (algorithm === null) {
                        throw IllegalStateException("algorithm was not specified.")
                    }
                    return@run Supplier { simpleImpl(algorithm, supplier.get()) }
                }
                c
            }
            if (threadSafePolicy == ThreadSafePolicy.THREAD_LOCAL) {
                return ThreadLocalHmacCodec {
                    codecSupplier.get()
                }
            }
            if (threadSafePolicy == ThreadSafePolicy.SYNCHRONIZED) {
                return SynchronizedHmacCodec(codecSupplier.get())
            }
            return codecSupplier.get()
        }

        private class SynchronizedHmacCodec(
            private val hmacCodec: HmacCodec
        ) : HmacCodec {

            override val algorithm: CodecAlgorithm = hmacCodec.algorithm
            override val hmac: Mac = hmacCodec.hmac

            @get:Synchronized
            override val hmacLength: Int
                get() = hmacCodec.hmacLength

            @Synchronized
            override fun hmac(key: Key, data: ByteArray): PreparedCodec {
                return hmacCodec.hmac(key, data)
            }

            @Synchronized
            override fun hmac(key: Key, data: ByteArray, offset: Int): PreparedCodec {
                return hmacCodec.hmac(key, data, offset)
            }

            @Synchronized
            override fun hmac(key: Key, data: ByteArray, offset: Int, length: Int): PreparedCodec {
                return hmacCodec.hmac(key, data, offset, length)
            }

            @Synchronized
            override fun hmac(key: Key, data: ByteBuffer): PreparedCodec {
                return hmacCodec.hmac(key, data)
            }

            @Synchronized
            override fun hmac(key: Key, data: InputStream): PreparedCodec {
                return hmacCodec.hmac(key, data)
            }
        }

        private class ThreadLocalHmacCodec(
            hmac: () -> HmacCodec
        ) : HmacCodec {

            private val threadLocal: ThreadLocal<HmacCodec> = ThreadLocal.withInitial(hmac)

            override val algorithm: CodecAlgorithm
                get() = threadLocal.get().algorithm

            override val hmac: Mac
                get() = threadLocal.get().hmac

            override val hmacLength: Int
                get() = threadLocal.get().hmacLength

            override fun hmac(key: Key, data: ByteArray): PreparedCodec {
                return threadLocal.get().hmac(key, data)
            }

            override fun hmac(key: Key, data: ByteArray, offset: Int): PreparedCodec {
                return threadLocal.get().hmac(key, data, offset)
            }

            override fun hmac(key: Key, data: ByteArray, offset: Int, length: Int): PreparedCodec {
                return threadLocal.get().hmac(key, data, offset, length)
            }

            override fun hmac(key: Key, data: ByteBuffer): PreparedCodec {
                return threadLocal.get().hmac(key, data)
            }

            override fun hmac(key: Key, data: InputStream): PreparedCodec {
                return threadLocal.get().hmac(key, data)
            }
        }
    }

    companion object {

        @JvmStatic
        fun simpleImpl(algorithm: CodecAlgorithm, hmac: Mac): HmacCodec {
            return object : HmacCodec {
                override val hmac: Mac = hmac
                override val algorithm: CodecAlgorithm = algorithm
            }
        }

        @JvmStatic
        fun newBuilder(): Builder {
            return Builder()
        }

        @JvmStatic
        fun hmacMd5(): HmacCodec {
            return CodecAlgorithm.HMAC_MD5.toHmacCodec()
        }

        @JvmStatic
        fun hmacSha1(): HmacCodec {
            return CodecAlgorithm.HMAC_SHA1.toHmacCodec()
        }

        @JvmStatic
        fun hmacSha256(): HmacCodec {
            return CodecAlgorithm.HMAC_SHA256.toHmacCodec()
        }

        @JvmStatic
        fun hmacSha384(): HmacCodec {
            return CodecAlgorithm.HMAC_SHA384.toHmacCodec()
        }

        @JvmStatic
        fun hmacSha512(): HmacCodec {
            return CodecAlgorithm.HMAC_SHA512.toHmacCodec()
        }

        @JvmName("forAlgorithm")
        @JvmStatic
        fun CharSequence.toHmacCodec(provider: Provider? = null): HmacCodec {
            return this.toCodecAlgorithm(CodecAlgorithmType.HMAC).toHmacCodec(provider)
        }

        @JvmName("forAlgorithm")
        @JvmStatic
        fun CodecAlgorithm.toHmacCodec(provider: Provider? = null): HmacCodec {
            return newBuilder()
                .algorithm(this)
                .hmacSupplier {
                    if (provider === null)
                        Mac.getInstance(this.name)
                    else
                        Mac.getInstance(this.name, provider)
                }
                .build()
        }
    }
}