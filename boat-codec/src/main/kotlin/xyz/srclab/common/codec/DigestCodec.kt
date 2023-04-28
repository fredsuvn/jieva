package xyz.srclab.common.codec

import xyz.srclab.common.base.ThreadSafePolicy
import xyz.srclab.common.base.remLength
import xyz.srclab.common.codec.CodecAlgorithm.Companion.toCodecAlgorithm
import xyz.srclab.common.codec.PreparedCodec.Companion.toSync
import xyz.srclab.common.codec.bc.DEFAULT_BCPROV_PROVIDER
import xyz.srclab.common.io.getBytes
import xyz.srclab.common.io.readBytes
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.security.Provider
import java.util.function.Supplier

/**
 * Digest codec such as `MD5`, `SHA256`.
 */
interface DigestCodec : Codec {

    fun getDigest(): MessageDigest {
        return getDigestOrNull() ?: throw CodecException("${this.algorithm.name} codec doesn't have a MessageDigest!")
    }

    fun getDigestOrNull(): MessageDigest?

    fun getDigestLength(): Int {
        return getDigest().digestLength
    }

    fun digest(data: ByteArray): PreparedCodec {
        return digest(data, 0)
    }

    fun digest(data: ByteArray, offset: Int): PreparedCodec {
        return digest(data, offset, remLength(data.size, offset))
    }

    fun digest(data: ByteArray, offset: Int, length: Int): PreparedCodec {
        return ByteArrayPreparedCodec(getDigest(), data, offset, length)
    }

    fun digest(data: ByteBuffer): PreparedCodec {
        return ByteBufferPreparedCodec(getDigest(), data)
    }

    fun digest(data: InputStream): PreparedCodec {
        return InputStreamPreparedCodec(getDigest(), data)
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

        override fun doFinal(dest: OutputStream): Long {
            digest.reset()
            digest.update(data, dataOffset, dataLength)
            val d = digest.digest()
            dest.write(d)
            return d.size.toLong()
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

        override fun doFinal(dest: OutputStream): Long {
            digest.reset()
            if (data.hasArray()) {
                val startPos = data.position()
                val array = data.array()
                val arrayOffset = data.arrayOffset() + startPos
                digest.update(array, arrayOffset, data.remaining())
                data.position(data.limit())
            } else {
                digest.update(data.getBytes())
            }
            val d = digest.digest()
            dest.write(d)
            return d.size.toLong()
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

        override fun doFinal(dest: OutputStream): Long {
            digest.reset()
            digest.updateFromStream(data)
            val result = digest.digest()
            dest.write(result)
            return result.size.toLong()
        }
    }

    /**
     * Builder for [DigestCodec].
     */
    open class Builder {

        private var algorithm: CodecAlgorithm? = null
        private var digestSupplier: Supplier<MessageDigest>? = null
        private var codecSupplier: Supplier<DigestCodec>? = null
        private var threadSafePolicy: ThreadSafePolicy = ThreadSafePolicy.THREAD_LOCAL

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
         * Default is [ThreadSafePolicy.THREAD_LOCAL].
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

            override fun getDigest(): MessageDigest {
                return digestCodec.getDigest()
            }

            override fun getDigestOrNull(): MessageDigest? {
                return digestCodec.getDigestOrNull()
            }

            @Synchronized
            override fun getDigestLength(): Int {
                return digestCodec.getDigestLength()
            }

            override fun digest(data: ByteArray): PreparedCodec {
                return digestCodec.digest(data).toSync(this)
            }

            override fun digest(data: ByteArray, offset: Int): PreparedCodec {
                return digestCodec.digest(data, offset).toSync(this)
            }

            override fun digest(data: ByteArray, offset: Int, length: Int): PreparedCodec {
                return digestCodec.digest(data, offset, length).toSync(this)
            }

            override fun digest(data: ByteBuffer): PreparedCodec {
                return digestCodec.digest(data).toSync(this)
            }

            override fun digest(data: InputStream): PreparedCodec {
                return digestCodec.digest(data).toSync(this)
            }
        }

        private class ThreadLocalDigestCodec(
            digest: () -> DigestCodec
        ) : DigestCodec {

            private val threadLocal: ThreadLocal<DigestCodec> = ThreadLocal.withInitial(digest)

            override val algorithm: CodecAlgorithm
                get() = threadLocal.get().algorithm

            override fun getDigest(): MessageDigest {
                return threadLocal.get().getDigest()
            }

            override fun getDigestOrNull(): MessageDigest? {
                return threadLocal.get().getDigestOrNull()
            }

            override fun getDigestLength(): Int {
                return threadLocal.get().getDigestLength()
            }

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
                override val algorithm: CodecAlgorithm = algorithm
                override fun getDigestOrNull(): MessageDigest {
                    return digest
                }
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
            return CodecAlgorithm.SM3.toDigestCodec(DEFAULT_BCPROV_PROVIDER)
        }

        @JvmName("forAlgorithm")
        @JvmStatic
        @JvmOverloads
        fun CharSequence.toDigestCodec(provider: Provider? = null): DigestCodec {
            return this.toCodecAlgorithm(CodecAlgorithmType.DIGEST).toDigestCodec(provider)
        }

        @JvmName("forAlgorithm")
        @JvmStatic
        @JvmOverloads
        fun CodecAlgorithm.toDigestCodec(provider: Provider? = null): DigestCodec {
            return newBuilder()
                .algorithm(this)
                .digestSupplier {
                    if (provider === null)
                        MessageDigest.getInstance(this.name)
                    else
                        MessageDigest.getInstance(this.name, provider)
                }
                .build()
        }
    }
}