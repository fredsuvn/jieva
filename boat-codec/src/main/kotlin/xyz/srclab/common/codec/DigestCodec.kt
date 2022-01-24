package xyz.srclab.common.codec

import xyz.srclab.common.base.ThreadSafePolicy
import xyz.srclab.common.base.remainingLength
import xyz.srclab.common.base.toKotlinFun
import xyz.srclab.common.codec.CodecAlgorithm.Companion.toCodecAlgorithm
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.function.Supplier

/**
 * Digest codec such as `MD5`.
 */
interface DigestCodec : Codec {

    val digest: MessageDigest

    fun digest(data: ByteArray): ByteArray {
        return digest(data, 0)
    }

    fun digest(data: ByteArray, offset: Int): ByteArray {
        return digest(data, offset, remainingLength(data.size, offset))
    }

    fun digest(data: ByteArray, offset: Int, length: Int): ByteArray {
        val digest = this.digest
        digest.reset()
        digest.update(data, offset, length)
        return digest.digest()
    }

    fun digest(data: ByteArray, output: OutputStream): Long {
        return digest(data, 0, output)
    }

    fun digest(data: ByteArray, offset: Int, output: OutputStream): Long {
        return digest(data, offset, remainingLength(data.size, offset), output)
    }

    fun digest(data: ByteArray, offset: Int, length: Int, output: OutputStream): Long {
        val digest = digest(data, offset, length)
        output.write(digest)
        return digest.size.toLong()
    }

    fun digest(data: ByteBuffer): ByteBuffer {
        val digest = this.digest
        digest.reset()
        digest.update(data)
        return ByteBuffer.wrap(digest.digest())
    }

    fun digest(data: InputStream): ByteArray {
        return digest(data.readBytes())
    }

    fun digest(data: InputStream, output: OutputStream): Long {
        return digest(data.readBytes(), output)
    }

    /**
     * Builder for [DigestCodec].
     */
    open class Builder {

        private var algorithm: CodecAlgorithm? = null
        private var digestSupplier: Supplier<MessageDigest>? = null
        private var threadSafePolicy: ThreadSafePolicy = ThreadSafePolicy.SYNCHRONIZED

        open fun algorithm(algorithm: CodecAlgorithm) = apply {
            this.algorithm = algorithm
        }

        open fun digestSupplier(digestSupplier: Supplier<MessageDigest>) = apply {
            this.digestSupplier = digestSupplier
            return this
        }

        /**
         * Default is [ThreadSafePolicy.SYNCHRONIZED].
         */
        open fun threadSafePolicy(threadSafePolicy: ThreadSafePolicy) = apply {
            this.threadSafePolicy = threadSafePolicy
            return this
        }

        open fun build(): DigestCodec {
            val algorithm = this.algorithm
            if (algorithm === null) {
                throw IllegalStateException("algorithm was not specified.")
            }
            val digestSupplier = this.digestSupplier
            if (digestSupplier === null) {
                throw IllegalStateException("digesterSupplier was not specified.")
            }
            if (threadSafePolicy == ThreadSafePolicy.THREAD_LOCAL) {
                return ThreadLocalDigestCodec(algorithm, digestSupplier.toKotlinFun())
            }
            val digestCodec = DigestCodecImpl(algorithm, digestSupplier.get())
            if (threadSafePolicy == ThreadSafePolicy.SYNCHRONIZED) {
                return SynchronizedDigestCodec(digestCodec)
            }
            return digestCodec
        }

        private class DigestCodecImpl(
            override val algorithm: CodecAlgorithm,
            override val digest: MessageDigest
        ) : DigestCodec

        private class SynchronizedDigestCodec(
            private val digestCodec: DigestCodec
        ) : DigestCodec {

            override val algorithm: CodecAlgorithm = digestCodec.algorithm
            override val digest: MessageDigest = digestCodec.digest

            @Synchronized
            override fun digest(data: ByteArray, offset: Int, length: Int): ByteArray {
                return digestCodec.digest(data, offset, length)
            }

            @Synchronized
            override fun digest(data: ByteBuffer): ByteBuffer {
                return digestCodec.digest(data)
            }
        }

        private class ThreadLocalDigestCodec(
            override val algorithm: CodecAlgorithm,
            digest: () -> MessageDigest
        ) : DigestCodec {
            private val threadLocal: ThreadLocal<MessageDigest> = ThreadLocal.withInitial(digest)
            override val digest: MessageDigest
                get() = threadLocal.get()
        }
    }

    companion object {

        @JvmStatic
        fun newBuilder(): Builder {
            return Builder()
        }

        @JvmStatic
        fun md2(): DigestCodec {
            return withAlgorithm(CodecAlgorithm.MD2_NAME)
        }

        @JvmStatic
        fun md5(): DigestCodec {
            return withAlgorithm(CodecAlgorithm.MD5_NAME)
        }

        @JvmStatic
        fun sha1(): DigestCodec {
            return withAlgorithm(CodecAlgorithm.SHA1_NAME)
        }

        @JvmStatic
        fun sha256(): DigestCodec {
            return withAlgorithm(CodecAlgorithm.SHA256_NAME)
        }

        @JvmStatic
        fun sha384(): DigestCodec {
            return withAlgorithm(CodecAlgorithm.SHA384_NAME)
        }

        @JvmStatic
        fun sha512(): DigestCodec {
            return withAlgorithm(CodecAlgorithm.SHA512_NAME)
        }

        @JvmStatic
        fun withAlgorithm(
            algorithm: CharSequence
        ): DigestCodec {
            return forAlgorithm(algorithm.toCodecAlgorithm())
        }

        @JvmStatic
        fun forAlgorithm(
            algorithm: CodecAlgorithm
        ): DigestCodec {
            return newBuilder()
                .algorithm(algorithm)
                .digestSupplier { MessageDigest.getInstance(algorithm.toString()) }
                .build()
        }
    }
}