package xyz.srclab.common.codec

import xyz.srclab.common.base.ThreadSafePolicy
import xyz.srclab.common.base.remainingLength
import xyz.srclab.common.codec.CodecAlgorithm.Companion.toCodecAlgorithm
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.function.Supplier
import kotlin.math.min

/**
 * Digest codec such as `MD5`.
 */
interface DigestCodec : Codec {

    val digest: MessageDigest

    val digestLength: Int
        get() = digest.digestLength

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

    fun digest(data: ByteBuffer): ByteArray {
        val digest = this.digest
        digest.reset()
        digest.update(data)
        return digest.digest()
    }

    fun digest(data: ByteBuffer, dest: ByteBuffer): Int {
        val digest = this.digest
        digest.reset()
        digest.update(data)
        return if (dest.hasArray()) {
            val startPos = dest.position()
            val array = dest.array()
            val arrayOffset = dest.arrayOffset()
            val length = min(remainingLength(array.size, arrayOffset), dest.remaining())
            val result = digest.digest(array, arrayOffset, length)
            dest.position(startPos + result)
            result
        } else {
            val startPos = dest.position()
            dest.put(digest.digest())
            dest.position() - startPos
        }
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
            val algorithm = this.algorithm
            if (algorithm === null) {
                throw IllegalStateException("algorithm was not specified.")
            }
            val codecSupplier = run {
                val c = this.codecSupplier
                if (c === null) {
                    val supplier = this.digestSupplier
                    if (supplier === null) {
                        throw IllegalStateException("digesterSupplier was not specified.")
                    }
                    return@run Supplier { simpleImpl(algorithm, supplier.get()) }
                }
                c
            }
            if (threadSafePolicy == ThreadSafePolicy.THREAD_LOCAL) {
                return ThreadLocalDigestCodec(algorithm) {
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
            override fun digest(data: ByteArray): ByteArray {
                return digestCodec.digest(data)
            }

            @Synchronized
            override fun digest(data: ByteArray, offset: Int): ByteArray {
                return digestCodec.digest(data, offset)
            }

            @Synchronized
            override fun digest(data: ByteArray, offset: Int, length: Int): ByteArray {
                return digestCodec.digest(data, offset, length)
            }

            @Synchronized
            override fun digest(data: ByteArray, output: OutputStream): Long {
                return digestCodec.digest(data, output)
            }

            @Synchronized
            override fun digest(data: ByteArray, offset: Int, output: OutputStream): Long {
                return digestCodec.digest(data, offset, output)
            }

            @Synchronized
            override fun digest(data: ByteArray, offset: Int, length: Int, output: OutputStream): Long {
                return digestCodec.digest(data, offset, length, output)
            }

            @Synchronized
            override fun digest(data: ByteBuffer): ByteArray {
                return digestCodec.digest(data)
            }

            @Synchronized
            override fun digest(data: ByteBuffer, dest: ByteBuffer): Int {
                return digestCodec.digest(data, dest)
            }

            @Synchronized
            override fun digest(data: InputStream): ByteArray {
                return digestCodec.digest(data)
            }

            @Synchronized
            override fun digest(data: InputStream, output: OutputStream): Long {
                return digestCodec.digest(data, output)
            }
        }

        private class ThreadLocalDigestCodec(
            override val algorithm: CodecAlgorithm,
            digest: () -> DigestCodec
        ) : DigestCodec {

            private val threadLocal: ThreadLocal<DigestCodec> = ThreadLocal.withInitial(digest)
            override val digest: MessageDigest
                get() = threadLocal.get().digest

            override val digestLength: Int
                get() = threadLocal.get().digestLength

            override fun digest(data: ByteArray): ByteArray {
                return threadLocal.get().digest(data)
            }

            override fun digest(data: ByteArray, offset: Int): ByteArray {
                return threadLocal.get().digest(data, offset)
            }

            override fun digest(data: ByteArray, offset: Int, length: Int): ByteArray {
                return threadLocal.get().digest(data, offset, length)
            }

            override fun digest(data: ByteArray, output: OutputStream): Long {
                return threadLocal.get().digest(data, output)
            }

            override fun digest(data: ByteArray, offset: Int, output: OutputStream): Long {
                return threadLocal.get().digest(data, offset, output)
            }

            override fun digest(data: ByteArray, offset: Int, length: Int, output: OutputStream): Long {
                return threadLocal.get().digest(data, offset, length, output)
            }

            override fun digest(data: ByteBuffer): ByteArray {
                return threadLocal.get().digest(data)
            }

            override fun digest(data: ByteBuffer, dest: ByteBuffer): Int {
                return threadLocal.get().digest(data, dest)
            }

            override fun digest(data: InputStream): ByteArray {
                return threadLocal.get().digest(data)
            }

            override fun digest(data: InputStream, output: OutputStream): Long {
                return threadLocal.get().digest(data, output)
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
            return CodecAlgorithm.MD2_NAME.toDigestCodec()
        }

        @JvmStatic
        fun md5(): DigestCodec {
            return CodecAlgorithm.MD5_NAME.toDigestCodec()
        }

        @JvmStatic
        fun sha1(): DigestCodec {
            return CodecAlgorithm.SHA1_NAME.toDigestCodec()
        }

        @JvmStatic
        fun sha256(): DigestCodec {
            return CodecAlgorithm.SHA256_NAME.toDigestCodec()
        }

        @JvmStatic
        fun sha384(): DigestCodec {
            return CodecAlgorithm.SHA384_NAME.toDigestCodec()
        }

        @JvmStatic
        fun sha512(): DigestCodec {
            return CodecAlgorithm.SHA512_NAME.toDigestCodec()
        }

        @JvmName("forAlgorithm")
        @JvmStatic
        fun CharSequence.toDigestCodec(): DigestCodec {
            return this.toCodecAlgorithm().toDigestCodec()
        }

        @JvmName("forAlgorithm")
        @JvmStatic
        fun CodecAlgorithm.toDigestCodec(): DigestCodec {
            return newBuilder()
                .algorithm(this)
                .digestSupplier() { MessageDigest.getInstance(this.name) }
                .build()
        }
    }
}