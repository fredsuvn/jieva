package xyz.srclab.common.codec

import xyz.srclab.common.base.CacheableBuilder
import xyz.srclab.common.base.ThreadSafePolicy
import xyz.srclab.common.base.toBytes
import xyz.srclab.common.base.toChars
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.Reader
import java.security.MessageDigest

/**
 * Digest codec such as `MD5`.
 *
 * @author sunqian
 */
interface Digester : Codec {

    fun digest(data: ByteArray): ByteArray {
        return digest(data, 0)
    }

    fun digest(data: ByteArray, offset: Int): ByteArray {
        return digest(data, offset, data.size - offset)
    }

    fun digest(data: ByteArray, offset: Int, length: Int): ByteArray {
        val outputStream = ByteArrayOutputStream()
        digest(data, offset, length, outputStream)
        return outputStream.toByteArray()
    }

    fun digest(data: ByteArray, output: OutputStream): Int {
        return digest(data, 0, output)
    }

    fun digest(data: ByteArray, offset: Int, output: OutputStream): Int {
        return digest(data, offset, data.size - offset, output)
    }

    fun digest(data: ByteArray, offset: Int, length: Int, output: OutputStream): Int

    fun digest(data: InputStream, output: OutputStream): Int

    fun digest(data: CharSequence): ByteArray {
        return digest(data.toBytes())
    }

    fun digest(data: CharSequence, output: OutputStream): Int {
        return digest(data.toBytes(), output)
    }

    fun digest(data: Reader, output: OutputStream): Int {
        return digest(data.readText(), output)
    }

    fun digestToString(data: ByteArray): String {
        return digest(data).toChars()
    }

    fun digestToString(data: ByteArray, offset: Int): String {
        return digest(data, offset).toChars()
    }

    fun digestToString(data: ByteArray, offset: Int, length: Int): String {
        return digest(data, offset, length).toChars()
    }

    fun digestToString(data: CharSequence): String {
        return digest(data).toChars()
    }

    /**
     * Builder for [Digester].
     */
    open class Builder : CacheableBuilder<Digester>() {

        private var algorithm: String? = null
        private var digestSupplier: (() -> MessageDigest)? = null
        private var threadSafePolicy: ThreadSafePolicy = ThreadSafePolicy.NONE

        fun algorithm(algorithm: String): Builder {
            this.algorithm = algorithm
            this.commit()
            return this
        }

        fun digestSupplier(digestSupplier: () -> MessageDigest): Builder {
            this.digestSupplier = digestSupplier
            this.commit()
            return this
        }

        fun threadSafePolicy(threadSafePolicy: ThreadSafePolicy): Builder {
            this.threadSafePolicy = threadSafePolicy
            this.commit()
            return this
        }

        override fun buildNew(): Digester {

        }

        private class DigesterImpl(
            override val algorithm: String,
            private val digest:MessageDigest
        ) : Digester {

            override fun digest(data: ByteArray, offset: Int, length: Int, output: OutputStream): Int {
                digest.reset()
                digest.update(data, offset, length)
                 digest.digest()
            }

            override fun digest(data: InputStream, output: OutputStream): Int {
                TODO("Not yet implemented")
            }
        }

        private class SynchronizedDigester {

        }
    }

    companion object {

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
            return DigesterImpl(algorithm.toString())
        }

        private class DigesterImpl(
            override val algorithm: String
        ) : Digester {

            private val digest: MessageDigest by lazy {
                MessageDigest.getInstance(algorithm)
            }

            override fun digest(data: ByteArray, offset: Int, length: Int): ByteArray {
                digest.reset()
                digest.update(data, offset, length)
                return digest.digest()
            }
        }
    }
}