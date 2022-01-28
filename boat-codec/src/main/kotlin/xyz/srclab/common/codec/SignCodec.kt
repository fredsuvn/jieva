package xyz.srclab.common.codec

import xyz.srclab.common.base.ThreadSafePolicy
import xyz.srclab.common.base.remainingLength
import xyz.srclab.common.codec.CodecAlgorithm.Companion.toCodecAlgorithm
import xyz.srclab.common.codec.bcprov.DEFAULT_BCPROV_PROVIDER
import xyz.srclab.common.io.toBytes
import java.io.InputStream
import java.nio.ByteBuffer
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.util.function.Supplier

/**
 * To sign and verify.
 */
interface SignCodec : Codec {

    val signature: Signature

    fun sign(key: PrivateKey, data: ByteArray): PreparedCodec {
        return sign(key, data, 0)
    }

    fun sign(key: PrivateKey, data: ByteArray, offset: Int): PreparedCodec {
        return sign(key, data, offset, remainingLength(data.size, offset))
    }

    fun sign(key: PrivateKey, data: ByteArray, offset: Int, length: Int): PreparedCodec {
        return ByteArrayPreparedCodec(signature, key, data, offset, length)
    }

    fun sign(key: PrivateKey, data: ByteBuffer): PreparedCodec {
        return ByteBufferPreparedCodec(signature, key, data)
    }

    fun sign(key: PrivateKey, data: InputStream): PreparedCodec {
        return InputStreamPreparedCodec(signature, key, data)
    }

    fun verify(key: PublicKey, data: ByteArray): Boolean {
        return verify(key, data, 0)
    }

    fun verify(key: PublicKey, data: ByteArray, offset: Int): Boolean {
        return verify(key, data, offset, remainingLength(data.size, offset))
    }

    fun verify(key: PublicKey, data: ByteArray, offset: Int, length: Int): Boolean {
        val signature = this.signature
        signature.initVerify(key)
        return signature.verify(data, offset, length)
    }

    fun verify(key: PublicKey, data: ByteBuffer): Boolean {
        val signature = this.signature
        signature.initVerify(key)
        if (data.hasArray()) {
            val startPos = data.position()
            val array = data.array()
            val arrayOffset = data.arrayOffset() + startPos
            data.position(data.limit())
            return signature.verify(array, arrayOffset, data.remaining())
        }
        return signature.verify(data.toBytes(false))
    }

    fun verify(key: PublicKey, data: InputStream): Boolean {
        val signature = this.signature
        signature.initVerify(key)
        return signature.verify(data.readBytes())
    }

    open class ByteArrayPreparedCodec(
        private val signature: Signature,
        private val key: PrivateKey,
        private val data: ByteArray,
        private val dataOffset: Int,
        private val dataLength: Int
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            signature.initSign(key)
            signature.update(data, dataOffset, dataLength)
            return signature.sign()
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            signature.initSign(key)
            signature.update(data, dataOffset, dataLength)
            return signature.sign(dest, offset, length)
        }
    }

    open class ByteBufferPreparedCodec(
        private val signature: Signature,
        private val key: PrivateKey,
        private val data: ByteBuffer
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            signature.initSign(key)
            signature.update(data)
            return signature.sign()
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            signature.initSign(key)
            signature.update(data)
            return signature.sign(dest, offset, length)
        }
    }

    open class InputStreamPreparedCodec(
        private val signature: Signature,
        private val key: PrivateKey,
        private val data: InputStream
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            signature.initSign(key)
            signature.update(data.readBytes())
            return signature.sign()
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            signature.initSign(key)
            signature.update(data.readBytes())
            return signature.sign(dest, offset, length)
        }
    }

    /**
     * Builder for [SignCodec].
     */
    open class Builder {

        private var algorithm: CodecAlgorithm? = null
        private var signatureSupplier: Supplier<Signature>? = null
        private var codecSupplier: Supplier<SignCodec>? = null
        private var threadSafePolicy: ThreadSafePolicy = ThreadSafePolicy.SYNCHRONIZED

        open fun algorithm(algorithm: CodecAlgorithm) = apply {
            this.algorithm = algorithm
        }

        open fun signatureSupplier(signatureSupplier: Supplier<Signature>) = apply {
            this.signatureSupplier = signatureSupplier
        }

        open fun codecSupplier(codecSupplier: Supplier<SignCodec>) = apply {
            this.codecSupplier = codecSupplier
        }

        /**
         * Default is [ThreadSafePolicy.SYNCHRONIZED].
         */
        open fun threadSafePolicy(threadSafePolicy: ThreadSafePolicy) = apply {
            this.threadSafePolicy = threadSafePolicy
        }

        open fun build(): SignCodec {
            val codecSupplier = run {
                val c = this.codecSupplier
                if (c === null) {
                    val supplier = this.signatureSupplier
                    if (supplier === null) {
                        throw IllegalStateException("signatureSupplier was not specified.")
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
                return ThreadLocalSignCodec {
                    codecSupplier.get()
                }
            }
            if (threadSafePolicy == ThreadSafePolicy.SYNCHRONIZED) {
                return SynchronizedSignCodec(codecSupplier.get())
            }
            return codecSupplier.get()
        }

        private class SynchronizedSignCodec(
            private val signCodec: SignCodec
        ) : SignCodec {

            override val algorithm: CodecAlgorithm = signCodec.algorithm
            override val signature: Signature = signCodec.signature

            @Synchronized
            override fun sign(key: PrivateKey, data: ByteArray): PreparedCodec {
                return signCodec.sign(key, data)
            }

            @Synchronized
            override fun sign(key: PrivateKey, data: ByteArray, offset: Int): PreparedCodec {
                return signCodec.sign(key, data, offset)
            }

            @Synchronized
            override fun sign(key: PrivateKey, data: ByteArray, offset: Int, length: Int): PreparedCodec {
                return signCodec.sign(key, data, offset, length)
            }

            @Synchronized
            override fun sign(key: PrivateKey, data: ByteBuffer): PreparedCodec {
                return signCodec.sign(key, data)
            }

            @Synchronized
            override fun sign(key: PrivateKey, data: InputStream): PreparedCodec {
                return signCodec.sign(key, data)
            }

            @Synchronized
            override fun verify(key: PublicKey, data: ByteArray): Boolean {
                return signCodec.verify(key, data)
            }

            @Synchronized
            override fun verify(key: PublicKey, data: ByteArray, offset: Int): Boolean {
                return signCodec.verify(key, data, offset)
            }

            @Synchronized
            override fun verify(key: PublicKey, data: ByteArray, offset: Int, length: Int): Boolean {
                return signCodec.verify(key, data, offset, length)
            }

            @Synchronized
            override fun verify(key: PublicKey, data: ByteBuffer): Boolean {
                return signCodec.verify(key, data)
            }

            @Synchronized
            override fun verify(key: PublicKey, data: InputStream): Boolean {
                return signCodec.verify(key, data)
            }
        }

        private class ThreadLocalSignCodec(
            sign: () -> SignCodec
        ) : SignCodec {

            private val threadLocal: ThreadLocal<SignCodec> = ThreadLocal.withInitial(sign)

            override val algorithm: CodecAlgorithm
                get() = threadLocal.get().algorithm

            override val signature: Signature
                get() = threadLocal.get().signature

            override fun sign(key: PrivateKey, data: ByteArray): PreparedCodec {
                return threadLocal.get().sign(key, data)
            }

            override fun sign(key: PrivateKey, data: ByteArray, offset: Int): PreparedCodec {
                return threadLocal.get().sign(key, data, offset)
            }

            override fun sign(key: PrivateKey, data: ByteArray, offset: Int, length: Int): PreparedCodec {
                return threadLocal.get().sign(key, data, offset, length)
            }

            override fun sign(key: PrivateKey, data: ByteBuffer): PreparedCodec {
                return threadLocal.get().sign(key, data)
            }

            override fun sign(key: PrivateKey, data: InputStream): PreparedCodec {
                return threadLocal.get().sign(key, data)
            }

            override fun verify(key: PublicKey, data: ByteArray): Boolean {
                return threadLocal.get().verify(key, data)
            }

            override fun verify(key: PublicKey, data: ByteArray, offset: Int): Boolean {
                return threadLocal.get().verify(key, data, offset)
            }

            override fun verify(key: PublicKey, data: ByteArray, offset: Int, length: Int): Boolean {
                return threadLocal.get().verify(key, data, offset, length)
            }

            override fun verify(key: PublicKey, data: ByteBuffer): Boolean {
                return threadLocal.get().verify(key, data)
            }

            override fun verify(key: PublicKey, data: InputStream): Boolean {
                return threadLocal.get().verify(key, data)
            }
        }
    }

    companion object {

        @JvmStatic
        fun simpleImpl(algorithm: CodecAlgorithm, signature: Signature): SignCodec {
            return object : SignCodec {
                override val signature: Signature = signature
                override val algorithm: CodecAlgorithm = algorithm
            }
        }

        @JvmStatic
        fun newBuilder(): Builder {
            return Builder()
        }

        @JvmStatic
        fun sha1WithDsa(): SignCodec {
            return CodecAlgorithm.SHA1_WITH_DSA.toSignCodec()
        }

        @JvmStatic
        fun sha1WithRsa(): SignCodec {
            return CodecAlgorithm.SHA1_WITH_RSA.toSignCodec()
        }

        @JvmStatic
        fun sha256WithRsa(): SignCodec {
            return CodecAlgorithm.SHA256_WITH_RSA.toSignCodec()
        }

        @JvmStatic
        fun sm3WithSm2(): SignCodec {
            return newBuilder()
                .algorithm(CodecAlgorithm.SM3_WITH_SM2)
                .signatureSupplier { Signature.getInstance(CodecAlgorithm.SM3_WITH_SM2_NAME, DEFAULT_BCPROV_PROVIDER) }
                .build()
        }

        @JvmName("forAlgorithm")
        @JvmStatic
        fun CharSequence.toSignCodec(): SignCodec {
            return this.toCodecAlgorithm(CodecAlgorithmType.CIPHER).toSignCodec()
        }

        @JvmName("forAlgorithm")
        @JvmStatic
        fun CodecAlgorithm.toSignCodec(): SignCodec {
            return newBuilder()
                .algorithm(this)
                .signatureSupplier { Signature.getInstance(this.name) }
                .build()
        }
    }
}