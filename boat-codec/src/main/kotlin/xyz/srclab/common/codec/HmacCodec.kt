package xyz.srclab.common.codec

import xyz.srclab.common.base.ThreadSafePolicy
import xyz.srclab.common.base.remainingLength
import xyz.srclab.common.codec.CodecAlgorithm.Companion.toCodecAlgorithm
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.security.Key
import java.util.function.Supplier
import javax.crypto.Mac

/**
 * HMAC codec such as `HmacMD5`.
 */
interface HmacCodec : Codec {

    val hmac: Mac

    val hmacLength: Int
        get() = hmac.macLength

    fun digest(key: Key, data: ByteArray): ByteArray {
        return digest(key, data, 0)
    }

    fun digest(key: Key, data: ByteArray, offset: Int): ByteArray {
        return digest(key, data, offset, remainingLength(data.size, offset))
    }

    fun digest(key: Key, data: ByteArray, offset: Int, length: Int): ByteArray {
        val hmac = this.hmac
        hmac.reset()
        hmac.init(key)
        hmac.update(data, offset, length)
        return hmac.doFinal()
    }

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

    fun digest(key: Key, data: ByteBuffer): ByteArray {
        val hmac = this.hmac
        hmac.reset()
        hmac.init(key)
        hmac.update(data)
        return hmac.doFinal()
    }

    fun digest(key: Key, data: ByteBuffer, dest: ByteBuffer): Int {
        val hmac = this.hmac
        hmac.reset()
        hmac.init(key)
        hmac.update(data)
        return if (dest.hasArray()) {
            val startPos = dest.position()
            val array = dest.array()
            val arrayOffset = dest.arrayOffset()
            hmac.doFinal(array, arrayOffset)
            dest.position(startPos + hmacLength)
            hmacLength
        } else {
            val startPos = dest.position()
            dest.put(hmac.doFinal())
            dest.position() - startPos
        }
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
                        throw IllegalStateException("digesterSupplier was not specified.")
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
            override fun digest(key: Key, data: ByteArray): ByteArray {
                return hmacCodec.digest(key, data)
            }

            @Synchronized
            override fun digest(key: Key, data: ByteArray, offset: Int): ByteArray {
                return hmacCodec.digest(key, data, offset)
            }

            @Synchronized
            override fun digest(key: Key, data: ByteArray, offset: Int, length: Int): ByteArray {
                return hmacCodec.digest(key, data, offset, length)
            }

            @Synchronized
            override fun digest(key: Key, data: ByteArray, output: OutputStream): Long {
                return hmacCodec.digest(key, data, output)
            }

            @Synchronized
            override fun digest(key: Key, data: ByteArray, offset: Int, output: OutputStream): Long {
                return hmacCodec.digest(key, data, offset, output)
            }

            @Synchronized
            override fun digest(key: Key, data: ByteArray, offset: Int, length: Int, output: OutputStream): Long {
                return hmacCodec.digest(key, data, offset, length, output)
            }

            @Synchronized
            override fun digest(key: Key, data: ByteBuffer): ByteArray {
                return hmacCodec.digest(key, data)
            }

            @Synchronized
            override fun digest(key: Key, data: ByteBuffer, dest: ByteBuffer): Int {
                return hmacCodec.digest(key, data, dest)
            }

            @Synchronized
            override fun digest(key: Key, data: InputStream): ByteArray {
                return hmacCodec.digest(key, data)
            }

            @Synchronized
            override fun digest(key: Key, data: InputStream, output: OutputStream): Long {
                return hmacCodec.digest(key, data, output)
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

            override fun digest(key: Key, data: ByteArray): ByteArray {
                return threadLocal.get().digest(key, data)
            }

            override fun digest(key: Key, data: ByteArray, offset: Int): ByteArray {
                return threadLocal.get().digest(key, data, offset)
            }

            override fun digest(key: Key, data: ByteArray, offset: Int, length: Int): ByteArray {
                return threadLocal.get().digest(key, data, offset, length)
            }

            override fun digest(key: Key, data: ByteArray, output: OutputStream): Long {
                return threadLocal.get().digest(key, data, output)
            }

            override fun digest(key: Key, data: ByteArray, offset: Int, output: OutputStream): Long {
                return threadLocal.get().digest(key, data, offset, output)
            }

            override fun digest(key: Key, data: ByteArray, offset: Int, length: Int, output: OutputStream): Long {
                return threadLocal.get().digest(key, data, offset, length, output)
            }

            override fun digest(key: Key, data: ByteBuffer): ByteArray {
                return threadLocal.get().digest(key, data)
            }

            override fun digest(key: Key, data: ByteBuffer, dest: ByteBuffer): Int {
                return threadLocal.get().digest(key, data, dest)
            }

            override fun digest(key: Key, data: InputStream): ByteArray {
                return threadLocal.get().digest(key, data)
            }

            override fun digest(key: Key, data: InputStream, output: OutputStream): Long {
                return threadLocal.get().digest(key, data, output)
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
        fun CharSequence.toHmacCodec(): HmacCodec {
            return this.toCodecAlgorithm(CodecAlgorithmType.HMAC).toHmacCodec()
        }

        @JvmName("forAlgorithm")
        @JvmStatic
        fun CodecAlgorithm.toHmacCodec(): HmacCodec {
            return newBuilder()
                .algorithm(this)
                .hmacSupplier { Mac.getInstance(this.name) }
                .build()
        }
    }
}