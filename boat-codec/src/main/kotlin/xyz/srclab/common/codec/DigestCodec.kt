package xyz.srclab.common.codec

import xyz.srclab.common.base.ThreadSafePolicy
import xyz.srclab.common.base.remainingLength
import xyz.srclab.common.codec.CodecAlgorithm.Companion.toCodecAlgorithm
import xyz.srclab.common.codec.bcprov.DEFAULT_BCPROV_PROVIDER
import java.io.InputStream
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.function.Supplier

/**
 * Digest codec such as `MD5`.
 */
interface DigestCodec : Codec {

    val digest: MessageDigest

    val digestLength: Int
        get() = digest.digestLength

    fun digest(data: ByteArray): PreparedCodec {
        return digest(data, 0)
    }

    fun digest(data: ByteArray, offset: Int): PreparedCodec {
        return digest(data, offset, remainingLength(data.size, offset))
    }

    fun digest(data: ByteArray, offset: Int, length: Int): PreparedCodec {
        return ByteArrayPreparedCodec(digest, data, offset, length)
    }

    fun digest(data: ByteBuffer): PreparedCodec {
        return ByteBufferPreparedCodec(digest, data)
    }

    fun digest(data: InputStream): PreparedCodec {
        return InputStreamPreparedCodec(digest, data)
    }

    open class ByteArrayPreparedCodec(
        private val digest: MessageDigest,
        private val data: ByteArray,
        private val dataOffset: Int,
        private val dataLength: Int
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            digest.reset()
            digest.update(data, dataOffset, dataLength)
            return digest.digest()
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            digest.reset()
            digest.update(data, dataOffset, dataLength)
            return digest.digest(dest, offset, length)
        }
    }

    open class ByteBufferPreparedCodec(
        private val digest: MessageDigest,
        private val data: ByteBuffer
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            digest.reset()
            digest.update(data)
            return digest.digest()
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            digest.reset()
            digest.update(data)
            return digest.digest(dest, offset, length)
        }
    }

    open class InputStreamPreparedCodec(
        private val digest: MessageDigest,
        private val data: InputStream
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            digest.reset()
            digest.update(data.readBytes())
            return digest.digest()
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            digest.reset()
            digest.update(data.readBytes())
            return digest.digest(dest, offset, length)
        }
    }

    /**
     * Builder for [DigestCodec].
     */
    open class Builder {

        private var algorithm: CodecAlgorithm? = null
        private var digestSupplier: Supplier<MessageDigest>? = null
        private var codecSupplier: Supplier<DigestCodec>? = null
        private var threadSafePolicy: ThreadSafePolicy = ThreadSafePolicy.SYNCHRONIZED

        open fun algorithm(algorithm: CodecAlgorithm) = apply {
            this.algorithm = algorithm
        }

        open fun digestSupplier(digestSupplier: Supplier<MessageDigest>) = apply {
            this.digestSupplier = digestSupplier
            return this
        }

        open fun codecSupplier(codecSupplier: Supplier<DigestCodec>) = apply {
            this.codecSupplier = codecSupplier
        }

        /**
         * Default is [ThreadSafePolicy.SYNCHRONIZED].
         */
        open fun threadSafePolicy(threadSafePolicy: ThreadSafePolicy) = apply {
            this.threadSafePolicy = threadSafePolicy
            return this
        }

        open fun build(): DigestCodec {
            val codecSupplier = run {
                val c = this.codecSupplier
                if (c === null) {
                    val supplier = this.digestSupplier
                    if (supplier === null) {
                        throw IllegalStateException("digestSupplier was not specified.")
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
                return ThreadLocalDigestCodec {
                    codecSupplier.get()
                }
            }
            if (threadSafePolicy == ThreadSafePolicy.SYNCHRONIZED) {
                return SynchronizedDigestCodec(codecSupplier.get())
            }
            return codecSupplier.get()
        }

        private class SynchronizedDigestCodec(
            private val digestCodec: DigestCodec
        ) : DigestCodec {

            override val algorithm: CodecAlgorithm = digestCodec.algorithm
            override val digest: MessageDigest = digestCodec.digest

            @get:Synchronized
            override val digestLength: Int
                get() = digestCodec.digestLength

            @Synchronized
            override fun digest(data: ByteArray): PreparedCodec {
                return digestCodec.digest(data)
            }

            @Synchronized
            override fun digest(data: ByteArray, offset: Int): PreparedCodec {
                return digestCodec.digest(data, offset)
            }

            @Synchronized
            override fun digest(data: ByteArray, offset: Int, length: Int): PreparedCodec {
                return digestCodec.digest(data, offset, length)
            }

            @Synchronized
            override fun digest(data: ByteBuffer): PreparedCodec {
                return digestCodec.digest(data)
            }

            @Synchronized
            override fun digest(data: InputStream): PreparedCodec {
                return digestCodec.digest(data)
            }
        }

        private class ThreadLocalDigestCodec(
            digest: () -> DigestCodec
        ) : DigestCodec {

            private val threadLocal: ThreadLocal<DigestCodec> = ThreadLocal.withInitial(digest)

            override val algorithm: CodecAlgorithm
                get() = threadLocal.get().algorithm

            override val digest: MessageDigest
                get() = threadLocal.get().digest

            override val digestLength: Int
                get() = threadLocal.get().digestLength

            override fun digest(data: ByteArray): PreparedCodec {
                return threadLocal.get().digest(data)
            }

            override fun digest(data: ByteArray, offset: Int): PreparedCodec {
                return threadLocal.get().digest(data, offset)
            }

            override fun digest(data: ByteArray, offset: Int, length: Int): PreparedCodec {
                return threadLocal.get().digest(data, offset, length)
            }

            override fun digest(data: ByteBuffer): PreparedCodec {
                return threadLocal.get().digest(data)
            }

            override fun digest(data: InputStream): PreparedCodec {
                return threadLocal.get().digest(data)
            }
        }
    }

    companion object {

        @JvmStatic
        fun simpleImpl(algorithm: CodecAlgorithm, digest: MessageDigest): DigestCodec {
            return object : DigestCodec {
                override val digest: MessageDigest = digest
                override val algorithm: CodecAlgorithm = algorithm
            }
        }

        @JvmStatic
        fun newBuilder(): Builder {
            return Builder()
        }

        @JvmStatic
        fun md2(): DigestCodec {
            return CodecAlgorithm.MD2.toDigestCodec()
        }

        @JvmStatic
        fun md5(): DigestCodec {
            return CodecAlgorithm.MD5.toDigestCodec()
        }

        @JvmStatic
        fun sha1(): DigestCodec {
            return CodecAlgorithm.SHA1.toDigestCodec()
        }

        @JvmStatic
        fun sha256(): DigestCodec {
            return CodecAlgorithm.SHA256.toDigestCodec()
        }

        @JvmStatic
        fun sha384(): DigestCodec {
            return CodecAlgorithm.SHA384.toDigestCodec()
        }

        @JvmStatic
        fun sha512(): DigestCodec {
            return CodecAlgorithm.SHA512.toDigestCodec()
        }

        @JvmStatic
        fun sm3(): DigestCodec {
            return newBuilder()
                .algorithm(CodecAlgorithm.SM3)
                .digestSupplier { MessageDigest.getInstance(CodecAlgorithm.SM3_NAME, DEFAULT_BCPROV_PROVIDER) }
                .build()
        }

        @JvmName("forAlgorithm")
        @JvmStatic
        fun CharSequence.toDigestCodec(): DigestCodec {
            return this.toCodecAlgorithm(CodecAlgorithmType.DIGEST).toDigestCodec()
        }

        @JvmName("forAlgorithm")
        @JvmStatic
        fun CodecAlgorithm.toDigestCodec(): DigestCodec {
            return newBuilder()
                .algorithm(this)
                .digestSupplier { MessageDigest.getInstance(this.name) }
                .build()
        }
    }
}