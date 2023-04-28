package xyz.srclab.common.codec

import xyz.srclab.common.base.ThreadSafePolicy
import xyz.srclab.common.base.remLength
import xyz.srclab.common.codec.CodecAlgorithm.Companion.toCodecAlgorithm
import xyz.srclab.common.codec.PreparedCodec.Companion.toSync
import xyz.srclab.common.codec.bc.DEFAULT_BCPROV_PROVIDER
import xyz.srclab.common.codec.gm.SM2Codec
import xyz.srclab.common.codec.rsa.RsaCodec
import xyz.srclab.common.io.asInputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.security.Key
import java.security.Provider
import java.util.function.Supplier
import javax.crypto.Cipher

/**
 * Cipher codec which support `encrypt` and `decrypt` such as `AES`.
 *
 * @see RsaCodec
 * @see SM2Codec
 */
interface CipherCodec : Codec {

    fun getCipher(): Cipher {
        return getCipherOrNull() ?: throw CodecException("${this.algorithm.name} codec doesn't have a Cipher!")
    }

    fun getCipherOrNull(): Cipher?

    fun getBlockSize(): Int {
        return getCipher().blockSize
    }

    fun getOutputSize(inputSize: Int): Int {
        val cipher = getCipher()
        return cipher.getOutputSize(inputSize)
    }

    fun encrypt(key: Key, data: ByteArray): PreparedCodec {
        return encrypt(key, data, 0)
    }

    fun encrypt(key: Key, data: ByteArray, offset: Int): PreparedCodec {
        return encrypt(key, data, offset, remLength(data.size, offset))
    }

    fun encrypt(key: Key, data: ByteArray, offset: Int, length: Int): PreparedCodec {
        return ByteArrayPreparedCodec(getCipher(), Cipher.ENCRYPT_MODE, key, data, offset, length)
    }

    fun encrypt(key: Key, data: ByteBuffer): PreparedCodec {
        return ByteBufferPreparedCodec(getCipher(), Cipher.ENCRYPT_MODE, key, data)
    }

    fun encrypt(key: Key, data: InputStream): PreparedCodec {
        return InputStreamPreparedCodec(getCipher(), Cipher.ENCRYPT_MODE, key, data)
    }

    fun decrypt(key: Key, data: ByteArray): PreparedCodec {
        return decrypt(key, data, 0)
    }

    fun decrypt(key: Key, data: ByteArray, offset: Int): PreparedCodec {
        return decrypt(key, data, offset, remLength(data.size, offset))
    }

    fun decrypt(key: Key, data: ByteArray, offset: Int, length: Int): PreparedCodec {
        return ByteArrayPreparedCodec(getCipher(), Cipher.DECRYPT_MODE, key, data, offset, length)
    }

    fun decrypt(key: Key, data: ByteBuffer): PreparedCodec {
        return ByteBufferPreparedCodec(getCipher(), Cipher.DECRYPT_MODE, key, data)
    }

    fun decrypt(key: Key, data: InputStream): PreparedCodec {
        return InputStreamPreparedCodec(getCipher(), Cipher.DECRYPT_MODE, key, data)
    }

    open class ByteArrayPreparedCodec(
        private val cipher: Cipher,
        private val mode: Int,
        private val key: Key,
        private val data: ByteArray,
        private val dataOffset: Int,
        private val dataLength: Int
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            cipher.init(mode, key)
            return cipher.doFinal(data, dataOffset, dataLength)
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            cipher.init(mode, key)
            return cipher.doFinal(data, dataOffset, dataLength, dest, offset)
        }

        override fun doFinal(dest: OutputStream): Long {
            cipher.init(mode, key)
            return cipher.encryptAfterInit(data.asInputStream(dataOffset, dataLength), dest, mode)
        }
    }

    open class ByteBufferPreparedCodec(
        private val cipher: Cipher,
        private val mode: Int,
        private val key: Key,
        private val data: ByteBuffer
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            cipher.init(mode, key)
            return if (data.hasArray()) {
                val startPos = data.position()
                val array = data.array()
                val arrayOffset = data.arrayOffset() + startPos
                val length = data.remaining()
                val outputSize = cipher.getOutputSize(length)
                val dest = ByteArray(outputSize)
                cipher.doFinal(array, arrayOffset, length, dest)
                data.position(data.limit())
                dest
            } else {
                val outputSize = cipher.getOutputSize(data.remaining())
                val dest = ByteArray(outputSize)
                val buffer = ByteBuffer.wrap(dest)
                cipher.doFinal(data, buffer)
                dest
            }
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            cipher.init(mode, key)
            return if (data.hasArray()) {
                val startPos = data.position()
                val array = data.array()
                val arrayOffset = data.arrayOffset() + startPos
                val cryptLength = cipher.doFinal(array, arrayOffset, data.remaining(), dest, offset)
                data.position(data.limit())
                cryptLength
            } else {
                val buffer = ByteBuffer.wrap(dest, offset, length)
                cipher.doFinal(data, buffer)
            }
        }

        override fun doFinal(dest: ByteBuffer): Int {
            cipher.init(mode, key)
            return cipher.doFinal(data, dest)
        }

        override fun doFinal(dest: OutputStream): Long {
            cipher.init(mode, key)
            return cipher.encryptAfterInit(data.asInputStream(), dest, mode)
        }
    }

    open class InputStreamPreparedCodec(
        private val cipher: Cipher,
        private val mode: Int,
        private val key: Key,
        private val data: InputStream
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            cipher.init(mode, key)
            return cipher.doFinal(data.readBytes())
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            cipher.init(mode, key)
            val src = data.readBytes()
            return cipher.doFinal(src, 0, src.size, dest, offset)
        }

        override fun doFinal(dest: OutputStream): Long {
            cipher.init(mode, key)
            return cipher.encryptAfterInit(data, dest, mode)
        }
    }

    /**
     * Builder for [CipherCodec].
     */
    open class Builder {

        private var algorithm: CodecAlgorithm? = null
        private var cipherSupplier: Supplier<Cipher>? = null
        private var codecSupplier: Supplier<CipherCodec>? = null
        private var threadSafePolicy: ThreadSafePolicy = ThreadSafePolicy.THREAD_LOCAL

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
         * Default is [ThreadSafePolicy.THREAD_LOCAL].
         */
        open fun threadSafePolicy(threadSafePolicy: ThreadSafePolicy) = apply {
            this.threadSafePolicy = threadSafePolicy
        }

        open fun build(): CipherCodec {
            val codecSupplier = run {
                val c = this.codecSupplier
                if (c === null) {
                    val supplier = this.cipherSupplier
                    if (supplier === null) {
                        throw IllegalStateException("cipherSupplier was not specified.")
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
                return ThreadLocalCipherCodec {
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

            override fun getCipher(): Cipher {
                return cipherCodec.getCipher()
            }

            override fun getCipherOrNull(): Cipher? {
                return cipherCodec.getCipherOrNull()
            }

            @Synchronized
            override fun getBlockSize(): Int {
                return cipherCodec.getBlockSize()
            }

            @Synchronized
            override fun getOutputSize(inputSize: Int): Int {
                return cipherCodec.getOutputSize(inputSize)
            }

            override fun encrypt(key: Key, data: ByteArray): PreparedCodec {
                return cipherCodec.encrypt(key, data).toSync(this)
            }

            override fun encrypt(key: Key, data: ByteArray, offset: Int): PreparedCodec {
                return cipherCodec.encrypt(key, data, offset).toSync(this)
            }

            override fun encrypt(key: Key, data: ByteArray, offset: Int, length: Int): PreparedCodec {
                return cipherCodec.encrypt(key, data, offset, length).toSync(this)
            }

            override fun encrypt(key: Key, data: ByteBuffer): PreparedCodec {
                return cipherCodec.encrypt(key, data).toSync(this)
            }

            override fun encrypt(key: Key, data: InputStream): PreparedCodec {
                return cipherCodec.encrypt(key, data).toSync(this)
            }

            override fun decrypt(key: Key, data: ByteArray): PreparedCodec {
                return cipherCodec.decrypt(key, data).toSync(this)
            }

            override fun decrypt(key: Key, data: ByteArray, offset: Int): PreparedCodec {
                return cipherCodec.decrypt(key, data, offset).toSync(this)
            }

            override fun decrypt(key: Key, data: ByteArray, offset: Int, length: Int): PreparedCodec {
                return cipherCodec.decrypt(key, data, offset, length).toSync(this)
            }

            override fun decrypt(key: Key, data: ByteBuffer): PreparedCodec {
                return cipherCodec.decrypt(key, data).toSync(this)
            }

            override fun decrypt(key: Key, data: InputStream): PreparedCodec {
                return cipherCodec.decrypt(key, data).toSync(this)
            }
        }

        private class ThreadLocalCipherCodec(
            cipher: () -> CipherCodec
        ) : CipherCodec {

            private val threadLocal: ThreadLocal<CipherCodec> = ThreadLocal.withInitial(cipher)

            override val algorithm: CodecAlgorithm
                get() = threadLocal.get().algorithm

            override fun getCipher(): Cipher {
                return threadLocal.get().getCipher()
            }

            override fun getCipherOrNull(): Cipher? {
                return threadLocal.get().getCipherOrNull()
            }

            override fun getBlockSize(): Int {
                return threadLocal.get().getBlockSize()
            }

            override fun getOutputSize(inputSize: Int): Int {
                return threadLocal.get().getOutputSize(inputSize)
            }

            override fun encrypt(key: Key, data: ByteArray): PreparedCodec {
                return threadLocal.get().encrypt(key, data)
            }

            override fun encrypt(key: Key, data: ByteArray, offset: Int): PreparedCodec {
                return threadLocal.get().encrypt(key, data, offset)
            }

            override fun encrypt(key: Key, data: ByteArray, offset: Int, length: Int): PreparedCodec {
                return threadLocal.get().encrypt(key, data, offset, length)
            }

            override fun encrypt(key: Key, data: ByteBuffer): PreparedCodec {
                return threadLocal.get().encrypt(key, data)
            }

            override fun encrypt(key: Key, data: InputStream): PreparedCodec {
                return threadLocal.get().encrypt(key, data)
            }

            override fun decrypt(key: Key, data: ByteArray): PreparedCodec {
                return threadLocal.get().decrypt(key, data)
            }

            override fun decrypt(key: Key, data: ByteArray, offset: Int): PreparedCodec {
                return threadLocal.get().decrypt(key, data, offset)
            }

            override fun decrypt(key: Key, data: ByteArray, offset: Int, length: Int): PreparedCodec {
                return threadLocal.get().decrypt(key, data, offset, length)
            }

            override fun decrypt(key: Key, data: ByteBuffer): PreparedCodec {
                return threadLocal.get().decrypt(key, data)
            }

            override fun decrypt(key: Key, data: InputStream): PreparedCodec {
                return threadLocal.get().decrypt(key, data)
            }
        }
    }

    companion object {

        @JvmStatic
        fun simpleImpl(algorithm: CodecAlgorithm, cipher: Cipher): CipherCodec {
            return object : CipherCodec {
                override val algorithm: CodecAlgorithm = algorithm
                override fun getCipherOrNull(): Cipher {
                    return cipher
                }
            }
        }

        @JvmStatic
        fun newBuilder(): Builder {
            return Builder()
        }

        @JvmStatic
        fun aes(): CipherCodec {
            return CodecAlgorithm.AES.toCipherCodec()
        }

        @JvmStatic
        fun rsa(): CipherCodec {
            return newBuilder()
                .codecSupplier { RsaCodec() }
                .build()
        }

        @JvmOverloads
        @JvmStatic
        fun sm2(mode: Int = SM2Codec.MODE_C1C3C2): CipherCodec {
            return newBuilder()
                .codecSupplier { SM2Codec(mode) }
                .build()
        }

        @JvmStatic
        fun sm4(): CipherCodec {
            return CodecAlgorithm.SM4.toCipherCodec(DEFAULT_BCPROV_PROVIDER)
        }

        @JvmName("forAlgorithm")
        @JvmStatic
        @JvmOverloads
        fun CharSequence.toCipherCodec(provider: Provider? = null): CipherCodec {
            return this.toCodecAlgorithm(CodecAlgorithmType.CIPHER).toCipherCodec(provider)
        }

        @JvmName("forAlgorithm")
        @JvmStatic
        @JvmOverloads
        fun CodecAlgorithm.toCipherCodec(provider: Provider? = null): CipherCodec {
            return newBuilder()
                .algorithm(this)
                .cipherSupplier {
                    if (provider === null)
                        Cipher.getInstance(this.name)
                    else
                        Cipher.getInstance(this.name, provider)
                }
                .build()
        }
    }
}