package xyz.srclab.common.codec

import xyz.srclab.common.base.ThreadSafePolicy
import xyz.srclab.common.base.remainingLength
import xyz.srclab.common.base.toKotlinFun
import xyz.srclab.common.io.toBytes
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.function.Supplier

/**
 * Digest codec such as `MD5`.
 */
interface Digester : Codec {

    fun digest(data: ByteArray): ByteArray {
        return digest(data, 0)
    }

    fun digest(data: ByteArray, offset: Int): ByteArray {
        return digest(data, offset, remainingLength(data.size, offset))
    }

    fun digest(data: ByteArray, offset: Int, length: Int): ByteArray


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
        return ByteBuffer.wrap(data.toBytes())
    }

    fun digest(data: InputStream): ByteArray {
        return digest(data.readBytes())
    }

    fun digest(data: InputStream, output: OutputStream): Long {
        return digest(data.readBytes(), output)
    }

    /**
     * Builder for [Digester].
     */
    open class Builder {

        private var algorithm: CodecAlgorithm? = null
        private var digesterSupplier: Supplier<MessageDigest>? = null
        private var threadSafePolicy: ThreadSafePolicy = ThreadSafePolicy.SYNCHRONIZED

        open fun algorithm(algorithm: CodecAlgorithm): Builder {
            this.algorithm = algorithm
            return this
        }

        open fun digesterSupplier(digestSupplier: Supplier<MessageDigest>): Builder {
            this.digesterSupplier = digestSupplier
            return this
        }

        /**
         * Default is [ThreadSafePolicy.SYNCHRONIZED].
         */
        open fun threadSafePolicy(threadSafePolicy: ThreadSafePolicy): Builder {
            this.threadSafePolicy = threadSafePolicy
            return this
        }

        open fun build(): Digester {
            val algorithm = this.algorithm
            if (algorithm === null) {
                throw IllegalStateException("algorithm was not specified.")
            }
            val digesterSupplier = this.digesterSupplier
            if (digesterSupplier === null) {
                throw IllegalStateException("digesterSupplier was not specified.")
            }
            if (threadSafePolicy == ThreadSafePolicy.THREAD_LOCAL) {
                return ThreadLocalDigester(algorithm, digesterSupplier.toKotlinFun())
            }
            val digester = DigesterImpl(algorithm, digesterSupplier.get())
            if (threadSafePolicy == ThreadSafePolicy.SYNCHRONIZED) {
                return SynchronizedDigester(digester)
            }
            return digester
        }

        private class DigesterImpl(
            override val algorithm: CodecAlgorithm,
            private val digest: MessageDigest
        ) : Digester {

            override fun digest(data: ByteArray, offset: Int, length: Int): ByteArray {
                digest.reset()
                digest.update(data, offset, length)
                return digest.digest()
            }

            override fun digest(data: ByteBuffer): ByteBuffer {
                digest.reset()
                digest.update(data)
                return ByteBuffer.wrap(digest.digest())
            }
        }

        private class SynchronizedDigester(
            private val digester: Digester
        ) : Digester {

            override val algorithm: CodecAlgorithm = digester.algorithm

            @Synchronized
            override fun digest(data: ByteArray, offset: Int, length: Int): ByteArray {
                return digester.digest(data, offset, length)
            }

            @Synchronized
            override fun digest(data: ByteBuffer): ByteBuffer {
                return digester.digest(data)
            }
        }

        private class ThreadLocalDigester(
            override val algorithm: CodecAlgorithm,
            digester: () -> MessageDigest
        ) : Digester {

            private val threadLocal: ThreadLocal<MessageDigest> = ThreadLocal.withInitial(digester)

            override fun digest(data: ByteArray, offset: Int, length: Int): ByteArray {
                val digest = threadLocal.get()
                digest.reset()
                digest.update(data, offset, length)
                return digest.digest()
            }

            override fun digest(data: ByteBuffer): ByteBuffer {
                val digest = threadLocal.get()
                digest.reset()
                digest.update(data)
                return ByteBuffer.wrap(digest.digest())
            }
        }
    }

    companion object {

        @JvmStatic
        fun newBuilder(): Builder {
            return Builder()
        }

        @JvmStatic
        fun md2(): Digester {
            return withAlgorithm(CodecAlgorithm.MD2_NAME)
        }

        @JvmStatic
        fun md5(): Digester {
            return withAlgorithm(CodecAlgorithm.MD5_NAME)
        }

        @JvmStatic
        fun sha1(): Digester {
            return withAlgorithm(CodecAlgorithm.SHA1_NAME)
        }

        @JvmStatic
        fun sha256(): Digester {
            return withAlgorithm(CodecAlgorithm.SHA256_NAME)
        }

        @JvmStatic
        fun sha384(): Digester {
            return withAlgorithm(CodecAlgorithm.SHA384_NAME)
        }

        @JvmStatic
        fun sha512(): Digester {
            return withAlgorithm(CodecAlgorithm.SHA512_NAME)
        }

        @JvmStatic
        fun withAlgorithm(
            algorithm: CharSequence
        ): Digester {
            return newBuilder()
                .algorithm(CodecAlgorithm.forName(algorithm))
                .digesterSupplier { MessageDigest.getInstance(algorithm.toString()) }
                .build()
        }
    }
}