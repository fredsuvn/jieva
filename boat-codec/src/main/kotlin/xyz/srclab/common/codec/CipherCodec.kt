package xyz.srclab.common.codec

import xyz.srclab.common.base.ThreadSafePolicy
import xyz.srclab.common.base.remainingLength
import xyz.srclab.common.codec.Codec.Companion.toCodecAlgorithm
import xyz.srclab.common.codec.rsa.RsaCodec
import xyz.srclab.common.codec.rsa.newRsaCodec
import xyz.srclab.common.codec.sm2.Sm2Codec
import xyz.srclab.common.codec.sm2.Sm2Params
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.security.Key
import java.util.function.Supplier
import javax.crypto.Cipher

/**
 * Cipher codec which support `encrypt` and `decrypt` such as `AES`.
 *
 * @see RsaCodec
 * @see Sm2Codec
 */
interface CipherCodec : Codec {

    val cipher: Cipher

    val blockSize: Int
        get() = cipher.blockSize

    fun getOutputSize(inputSize: Int): Int {
        val cipher = this.cipher
        return cipher.getOutputSize(inputSize)
    }

    fun encrypt(key: Key, data: ByteArray): ByteArray {
        return encrypt(key, data, 0)
    }

    fun encrypt(key: Key, data: ByteArray, offset: Int): ByteArray {
        return encrypt(key, data, offset, remainingLength(data.size, offset))
    }

    fun encrypt(key: Key, data: ByteArray, offset: Int, length: Int): ByteArray {
        val cipher = this.cipher
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(data, offset, length)
    }

    fun encrypt(key: Key, data: ByteArray, output: OutputStream): Long {
        return encrypt(key, data, 0, output)
    }

    fun encrypt(key: Key, data: ByteArray, offset: Int, output: OutputStream): Long {
        return encrypt(key, data, offset, remainingLength(data.size, offset), output)
    }

    fun encrypt(key: Key, data: ByteArray, offset: Int, length: Int, output: OutputStream): Long {
        val encrypt = encrypt(key, data, offset, length)
        output.write(encrypt)
        return encrypt.size.toLong()
    }

    fun encrypt(key: Key, data: ByteBuffer): ByteBuffer {
        val cipher = this.cipher
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val dest = ByteBuffer.allocate(cipher.getOutputSize(data.remaining()))
        cipher.doFinal(data, dest)
        return dest
    }

    fun encrypt(key: Key, data: ByteBuffer, dest: ByteBuffer): Int {
        val cipher = this.cipher
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(data, dest)
    }

    fun encrypt(key: Key, data: InputStream): ByteArray {
        return encrypt(key, data.readBytes())
    }

    fun encrypt(key: Key, data: InputStream, output: OutputStream): Long {
        return encrypt(key, data.readBytes(), output)
    }

    fun decrypt(key: Key, data: ByteArray): ByteArray {
        return decrypt(key, data, 0)
    }

    fun decrypt(key: Key, data: ByteArray, offset: Int): ByteArray {
        return decrypt(key, data, offset, remainingLength(data.size, offset))
    }

    fun decrypt(key: Key, data: ByteArray, offset: Int, length: Int): ByteArray {
        val cipher = this.cipher
        cipher.init(Cipher.DECRYPT_MODE, key)
        return cipher.doFinal(data, offset, length)
    }

    fun decrypt(key: Key, data: ByteArray, output: OutputStream): Long {
        return decrypt(key, data, 0, output)
    }

    fun decrypt(key: Key, data: ByteArray, offset: Int, output: OutputStream): Long {
        return decrypt(key, data, offset, remainingLength(data.size, offset), output)
    }

    fun decrypt(key: Key, data: ByteArray, offset: Int, length: Int, output: OutputStream): Long {
        val decrypt = decrypt(key, data, offset, length)
        output.write(decrypt)
        return decrypt.size.toLong()
    }

    fun decrypt(key: Key, data: ByteBuffer): ByteBuffer {
        val cipher = this.cipher
        cipher.init(Cipher.DECRYPT_MODE, key)
        val dest = ByteBuffer.allocate(cipher.getOutputSize(data.remaining()))
        cipher.doFinal(data, dest)
        return dest
    }

    fun decrypt(key: Key, data: ByteBuffer, dest: ByteBuffer): Int {
        val cipher = this.cipher
        cipher.init(Cipher.DECRYPT_MODE, key)
        return cipher.doFinal(data, dest)
    }

    fun decrypt(key: Key, data: InputStream): ByteArray {
        return decrypt(key, data.readBytes())
    }

    fun decrypt(key: Key, data: InputStream, output: OutputStream): Long {
        return decrypt(key, data.readBytes(), output)
    }

    /**
     * Builder for [HmacCodec].
     */
    open class Builder {

        private var algorithm: CodecAlgorithm? = null
        private var cipherSupplier: Supplier<Cipher>? = null
        private var codecSupplier: Supplier<CipherCodec>? = null
        private var threadSafePolicy: ThreadSafePolicy = ThreadSafePolicy.SYNCHRONIZED

        open fun algorithm(algorithm: CodecAlgorithm) = apply {
            this.algorithm = algorithm
        }

        open fun cipherSupplier(cipherSupplier: Supplier<Cipher>) = apply {
            this.cipherSupplier = cipherSupplier
        }

        open fun codecSupplier(codecSupplier: Supplier<CipherCodec>) = apply {
            this.codecSupplier = codecSupplier
        }

        /**
         * Default is [ThreadSafePolicy.SYNCHRONIZED].
         */
        open fun threadSafePolicy(threadSafePolicy: ThreadSafePolicy) = apply {
            this.threadSafePolicy = threadSafePolicy
        }

        open fun build(): CipherCodec {
            val algorithm = this.algorithm
            if (algorithm === null) {
                throw IllegalStateException("algorithm was not specified.")
            }
            val codecSupplier = run {
                val c = this.codecSupplier
                if (c === null) {
                    val supplier = this.cipherSupplier
                    if (supplier === null) {
                        throw IllegalStateException("digesterSupplier was not specified.")
                    }
                    return@run Supplier { simpleImpl(algorithm, supplier.get()) }
                }
                c
            }
            if (threadSafePolicy == ThreadSafePolicy.THREAD_LOCAL) {
                return ThreadLocalCipherCodec(algorithm) {
                    codecSupplier.get()
                }
            }
            if (threadSafePolicy == ThreadSafePolicy.SYNCHRONIZED) {
                return SynchronizedCipherCodec(codecSupplier.get())
            }
            return codecSupplier.get()
        }

        private class SynchronizedCipherCodec(
            private val cipherCodec: CipherCodec
        ) : CipherCodec {

            override val algorithm: CodecAlgorithm = cipherCodec.algorithm
            override val cipher: Cipher = cipherCodec.cipher

            @get:Synchronized
            override val blockSize: Int
                get() = cipherCodec.blockSize

            @Synchronized
            override fun getOutputSize(inputSize: Int): Int {
                return cipherCodec.getOutputSize(inputSize)
            }

            @Synchronized
            override fun encrypt(key: Key, data: ByteArray): ByteArray {
                return cipherCodec.encrypt(key, data)
            }

            @Synchronized
            override fun encrypt(key: Key, data: ByteArray, offset: Int): ByteArray {
                return cipherCodec.encrypt(key, data, offset)
            }

            @Synchronized
            override fun encrypt(key: Key, data: ByteArray, offset: Int, length: Int): ByteArray {
                return cipherCodec.encrypt(key, data, offset, length)
            }

            @Synchronized
            override fun encrypt(key: Key, data: ByteArray, output: OutputStream): Long {
                return cipherCodec.encrypt(key, data, output)
            }

            @Synchronized
            override fun encrypt(key: Key, data: ByteArray, offset: Int, output: OutputStream): Long {
                return cipherCodec.encrypt(key, data, offset, output)
            }

            @Synchronized
            override fun encrypt(key: Key, data: ByteArray, offset: Int, length: Int, output: OutputStream): Long {
                return cipherCodec.encrypt(key, data, offset, length, output)
            }

            @Synchronized
            override fun encrypt(key: Key, data: ByteBuffer): ByteBuffer {
                return cipherCodec.encrypt(key, data)
            }

            @Synchronized
            override fun encrypt(key: Key, data: ByteBuffer, dest: ByteBuffer): Int {
                return cipherCodec.encrypt(key, data, dest)
            }

            @Synchronized
            override fun encrypt(key: Key, data: InputStream): ByteArray {
                return cipherCodec.encrypt(key, data)
            }

            @Synchronized
            override fun encrypt(key: Key, data: InputStream, output: OutputStream): Long {
                return cipherCodec.encrypt(key, data, output)
            }

            @Synchronized
            override fun decrypt(key: Key, data: ByteArray): ByteArray {
                return cipherCodec.decrypt(key, data)
            }

            @Synchronized
            override fun decrypt(key: Key, data: ByteArray, offset: Int): ByteArray {
                return cipherCodec.decrypt(key, data, offset)
            }

            @Synchronized
            override fun decrypt(key: Key, data: ByteArray, offset: Int, length: Int): ByteArray {
                return cipherCodec.decrypt(key, data, offset, length)
            }

            @Synchronized
            override fun decrypt(key: Key, data: ByteArray, output: OutputStream): Long {
                return cipherCodec.decrypt(key, data, output)
            }

            @Synchronized
            override fun decrypt(key: Key, data: ByteArray, offset: Int, output: OutputStream): Long {
                return cipherCodec.decrypt(key, data, offset, output)
            }

            @Synchronized
            override fun decrypt(key: Key, data: ByteArray, offset: Int, length: Int, output: OutputStream): Long {
                return cipherCodec.decrypt(key, data, offset, length, output)
            }

            @Synchronized
            override fun decrypt(key: Key, data: ByteBuffer): ByteBuffer {
                return cipherCodec.decrypt(key, data)
            }

            @Synchronized
            override fun decrypt(key: Key, data: ByteBuffer, dest: ByteBuffer): Int {
                return cipherCodec.decrypt(key, data, dest)
            }

            @Synchronized
            override fun decrypt(key: Key, data: InputStream): ByteArray {
                return cipherCodec.decrypt(key, data)
            }

            @Synchronized
            override fun decrypt(key: Key, data: InputStream, output: OutputStream): Long {
                return cipherCodec.decrypt(key, data, output)
            }
        }

        private class ThreadLocalCipherCodec(
            override val algorithm: CodecAlgorithm,
            cipher: () -> CipherCodec
        ) : CipherCodec {

            private val threadLocal: ThreadLocal<CipherCodec> = ThreadLocal.withInitial(cipher)
            override val cipher: Cipher
                get() = threadLocal.get().cipher

            override val blockSize: Int
                get() = threadLocal.get().blockSize

            override fun getOutputSize(inputSize: Int): Int {
                return threadLocal.get().getOutputSize(inputSize)
            }

            override fun encrypt(key: Key, data: ByteArray): ByteArray {
                return threadLocal.get().encrypt(key, data)
            }

            override fun encrypt(key: Key, data: ByteArray, offset: Int): ByteArray {
                return threadLocal.get().encrypt(key, data, offset)
            }

            override fun encrypt(key: Key, data: ByteArray, offset: Int, length: Int): ByteArray {
                return threadLocal.get().encrypt(key, data, offset, length)
            }

            override fun encrypt(key: Key, data: ByteArray, output: OutputStream): Long {
                return threadLocal.get().encrypt(key, data, output)
            }

            override fun encrypt(key: Key, data: ByteArray, offset: Int, output: OutputStream): Long {
                return threadLocal.get().encrypt(key, data, offset, output)
            }

            override fun encrypt(key: Key, data: ByteArray, offset: Int, length: Int, output: OutputStream): Long {
                return threadLocal.get().encrypt(key, data, offset, length, output)
            }

            override fun encrypt(key: Key, data: ByteBuffer): ByteBuffer {
                return threadLocal.get().encrypt(key, data)
            }

            override fun encrypt(key: Key, data: ByteBuffer, dest: ByteBuffer): Int {
                return threadLocal.get().encrypt(key, data, dest)
            }

            override fun encrypt(key: Key, data: InputStream): ByteArray {
                return threadLocal.get().encrypt(key, data)
            }

            override fun encrypt(key: Key, data: InputStream, output: OutputStream): Long {
                return threadLocal.get().encrypt(key, data, output)
            }

            override fun decrypt(key: Key, data: ByteArray): ByteArray {
                return threadLocal.get().decrypt(key, data)
            }

            override fun decrypt(key: Key, data: ByteArray, offset: Int): ByteArray {
                return threadLocal.get().decrypt(key, data, offset)
            }

            override fun decrypt(key: Key, data: ByteArray, offset: Int, length: Int): ByteArray {
                return threadLocal.get().decrypt(key, data, offset, length)
            }

            override fun decrypt(key: Key, data: ByteArray, output: OutputStream): Long {
                return threadLocal.get().decrypt(key, data, output)
            }

            override fun decrypt(key: Key, data: ByteArray, offset: Int, output: OutputStream): Long {
                return threadLocal.get().decrypt(key, data, offset, output)
            }

            override fun decrypt(key: Key, data: ByteArray, offset: Int, length: Int, output: OutputStream): Long {
                return threadLocal.get().decrypt(key, data, offset, length, output)
            }

            override fun decrypt(key: Key, data: ByteBuffer): ByteBuffer {
                return threadLocal.get().decrypt(key, data)
            }

            override fun decrypt(key: Key, data: ByteBuffer, dest: ByteBuffer): Int {
                return threadLocal.get().decrypt(key, data, dest)
            }

            override fun decrypt(key: Key, data: InputStream): ByteArray {
                return threadLocal.get().decrypt(key, data)
            }

            override fun decrypt(key: Key, data: InputStream, output: OutputStream): Long {
                return threadLocal.get().decrypt(key, data, output)
            }
        }
    }

    companion object {

        @JvmStatic
        fun simpleImpl(algorithm: CodecAlgorithm, cipher: Cipher): CipherCodec {
            return object : CipherCodec {
                override val cipher: Cipher = cipher
                override val algorithm: CodecAlgorithm = algorithm
            }
        }

        @JvmStatic
        fun newBuilder(): Builder {
            return Builder()
        }

        @JvmStatic
        fun aes(): CipherCodec {
            return CodecAlgorithm.AES_NAME.toCipherCodec()
        }

        @JvmStatic
        fun rsa(): RsaCodec {
            return newRsaCodec()
        }

        @JvmStatic
        fun sm2(sm2Params: Sm2Params): Sm2Codec {
            return Sm2Codec(sm2Params)
        }

        @JvmName("forAlgorithm")
        @JvmStatic
        fun CharSequence.toCipherCodec(): CipherCodec {
            return this.toCodecAlgorithm().toCipherCodec()
        }

        @JvmName("forAlgorithm")
        @JvmStatic
        fun CodecAlgorithm.toCipherCodec(): CipherCodec {
            return newBuilder()
                .algorithm(this)
                .cipherSupplier { Cipher.getInstance(this.name) }
                .build()
        }
    }
}