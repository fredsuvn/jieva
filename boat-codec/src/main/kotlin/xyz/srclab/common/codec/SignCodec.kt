package xyz.srclab.common.codec

import xyz.srclab.common.base.ThreadSafePolicy
import xyz.srclab.common.base.remLength
import xyz.srclab.common.codec.CodecAlgorithm.Companion.toCodecAlgorithm
import xyz.srclab.common.codec.PreparedCodec.Companion.toSync
import xyz.srclab.common.codec.PreparedVerify.Companion.toSync
import xyz.srclab.common.codec.bc.DEFAULT_BCPROV_PROVIDER
import xyz.srclab.common.io.getBytes
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.security.PrivateKey
import java.security.Provider
import java.security.PublicKey
import java.security.Signature
import java.util.function.Supplier

/**
 * To sign and verify.
 */
interface SignCodec : Codec {

    val digestCodec: DigestCodec

    fun getSignature(): Signature {
        return getSignatureOrNull() ?: throw CodecException("${this.algorithm.name} codec doesn't have a Signature!")
    }

    fun getSignatureOrNull(): Signature?

    fun sign(key: PrivateKey, data: ByteArray): PreparedCodec {
        return sign(key, data, 0)
    }

    fun sign(key: PrivateKey, data: ByteArray, offset: Int): PreparedCodec {
        return sign(key, data, offset, remLength(data.size, offset))
    }

    fun sign(key: PrivateKey, data: ByteArray, offset: Int, length: Int): PreparedCodec {
        return ByteArrayPreparedCodec(getSignature(), digestCodec, key, data, offset, length)
    }

    fun sign(key: PrivateKey, data: ByteBuffer): PreparedCodec {
        return ByteBufferPreparedCodec(getSignature(), digestCodec, key, data)
    }

    fun sign(key: PrivateKey, data: InputStream): PreparedCodec {
        return InputStreamPreparedCodec(getSignature(), digestCodec, key, data)
    }

    fun verify(key: PublicKey, data: ByteArray): PreparedVerify {
        return verify(key, data, 0)
    }

    fun verify(key: PublicKey, data: ByteArray, offset: Int): PreparedVerify {
        return verify(key, data, offset, remLength(data.size, offset))
    }

    fun verify(key: PublicKey, data: ByteArray, offset: Int, length: Int): PreparedVerify {
        return ByteArrayPreparedVerify(getSignature(), digestCodec, key, data, offset, length)
    }

    fun verify(key: PublicKey, data: ByteBuffer): PreparedVerify {
        return ByteBufferPreparedVerify(getSignature(), digestCodec, key, data)
    }

    fun verify(key: PublicKey, data: InputStream): PreparedVerify {
        return InputStreamPreparedVerify(getSignature(), digestCodec, key, data)
    }

    open class ByteArrayPreparedCodec(
        private val signature: Signature,
        private val digestCodec: DigestCodec,
        private val key: PrivateKey,
        private val data: ByteArray,
        private val dataOffset: Int,
        private val dataLength: Int
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            signature.initSign(key)
            signature.update(digestCodec.digest(data, dataOffset, dataLength).doFinal())
            return signature.sign()
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            signature.initSign(key)
            signature.update(digestCodec.digest(data, dataOffset, dataLength).doFinal())
            return signature.sign(dest, offset, length)
        }

        override fun doFinal(dest: OutputStream): Long {
            signature.initSign(key)
            signature.update(data, dataOffset, dataLength)
            val d = signature.sign()
            dest.write(d)
            return d.size.toLong()
        }
    }

    open class ByteBufferPreparedCodec(
        private val signature: Signature,
        private val digestCodec: DigestCodec,
        private val key: PrivateKey,
        private val data: ByteBuffer
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            signature.initSign(key)
            signature.update(digestCodec.digest(data).doFinal())
            return signature.sign()
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            signature.initSign(key)
            signature.update(digestCodec.digest(data).doFinal())
            return signature.sign(dest, offset, length)
        }

        override fun doFinal(dest: OutputStream): Long {
            signature.initSign(key)
            if (data.hasArray()) {
                val startPos = data.position()
                val array = data.array()
                val arrayOffset = data.arrayOffset() + startPos
                signature.update(array, arrayOffset, data.remaining())
                data.position(data.limit())
            } else {
                signature.update(data.getBytes())
            }
            val d = signature.sign()
            dest.write(d)
            return d.size.toLong()
        }
    }

    open class InputStreamPreparedCodec(
        private val signature: Signature,
        private val digestCodec: DigestCodec,
        private val key: PrivateKey,
        private val data: InputStream
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            signature.initSign(key)
            signature.update(digestCodec.digest(data).doFinal())
            return signature.sign()
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            signature.initSign(key)
            signature.update(digestCodec.digest(data).doFinal())
            return signature.sign(dest, offset, length)
        }

        override fun doFinal(dest: OutputStream): Long {
            signature.initSign(key)
            signature.updateFromStream(data)
            val result = signature.sign()
            dest.write(result)
            return result.size.toLong()
        }
    }

    open class ByteArrayPreparedVerify(
        private val signature: Signature,
        private val digestCodec: DigestCodec,
        private val key: PublicKey,
        private val data: ByteArray,
        private val dataOffset: Int,
        private val dataLength: Int
    ) : PreparedVerify {
        override fun verify(sign: ByteArray, offset: Int, length: Int): Boolean {
            signature.initVerify(key)
            signature.update(digestCodec.digest(data, dataOffset, dataLength).doFinal())
            return signature.verify(sign, offset, length)
        }
    }

    open class ByteBufferPreparedVerify(
        private val signature: Signature,
        private val digestCodec: DigestCodec,
        private val key: PublicKey,
        private val data: ByteBuffer
    ) : PreparedVerify {
        override fun verify(sign: ByteArray, offset: Int, length: Int): Boolean {
            signature.initVerify(key)
            signature.update(digestCodec.digest(data).doFinal())
            return signature.verify(sign, offset, length)
        }
    }

    open class InputStreamPreparedVerify(
        private val signature: Signature,
        private val digestCodec: DigestCodec,
        private val key: PublicKey,
        private val data: InputStream
    ) : PreparedVerify {
        override fun verify(sign: ByteArray, offset: Int, length: Int): Boolean {
            signature.initVerify(key)
            signature.update(digestCodec.digest(data).doFinal())
            return signature.verify(sign, offset, length)
        }
    }

    /**
     * Builder for [SignCodec].
     */
    open class Builder {

        private var algorithm: CodecAlgorithm? = null
        private var digestCodec: DigestCodec? = null
        private var signatureSupplier: Supplier<Signature>? = null
        private var codecSupplier: Supplier<SignCodec>? = null
        private var threadSafePolicy: ThreadSafePolicy = ThreadSafePolicy.SYNCHRONIZED

        open fun algorithm(algorithm: CodecAlgorithm) = apply {
            this.algorithm = algorithm
        }

        open fun digestCodec(digestCodec: DigestCodec) = apply {
            this.digestCodec = digestCodec
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
            val digestCodec = this.digestCodec
            if (digestCodec === null) {
                throw IllegalStateException("digestCodec was not specified.")
            }
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
                    return@run Supplier { simpleImpl(algorithm, supplier.get(), digestCodec) }
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
            override val digestCodec: DigestCodec = signCodec.digestCodec

            override fun getSignature(): Signature {
                return signCodec.getSignature()
            }

            override fun getSignatureOrNull(): Signature? {
                return signCodec.getSignatureOrNull()
            }

            override fun sign(key: PrivateKey, data: ByteArray): PreparedCodec {
                return signCodec.sign(key, data).toSync(this)
            }

            override fun sign(key: PrivateKey, data: ByteArray, offset: Int): PreparedCodec {
                return signCodec.sign(key, data, offset).toSync(this)
            }

            override fun sign(key: PrivateKey, data: ByteArray, offset: Int, length: Int): PreparedCodec {
                return signCodec.sign(key, data, offset, length).toSync(this)
            }

            override fun sign(key: PrivateKey, data: ByteBuffer): PreparedCodec {
                return signCodec.sign(key, data).toSync(this)
            }

            override fun sign(key: PrivateKey, data: InputStream): PreparedCodec {
                return signCodec.sign(key, data).toSync(this)
            }

            override fun verify(key: PublicKey, data: ByteArray): PreparedVerify {
                return signCodec.verify(key, data).toSync(this)
            }

            override fun verify(key: PublicKey, data: ByteArray, offset: Int): PreparedVerify {
                return signCodec.verify(key, data, offset).toSync(this)
            }

            override fun verify(key: PublicKey, data: ByteArray, offset: Int, length: Int): PreparedVerify {
                return signCodec.verify(key, data, offset, length).toSync(this)
            }

            override fun verify(key: PublicKey, data: ByteBuffer): PreparedVerify {
                return signCodec.verify(key, data).toSync(this)
            }

            override fun verify(key: PublicKey, data: InputStream): PreparedVerify {
                return signCodec.verify(key, data).toSync(this)
            }
        }

        private class ThreadLocalSignCodec(
            sign: () -> SignCodec
        ) : SignCodec {

            private val threadLocal: ThreadLocal<SignCodec> = ThreadLocal.withInitial(sign)

            override val algorithm: CodecAlgorithm
                get() = threadLocal.get().algorithm

            override val digestCodec: DigestCodec
                get() = threadLocal.get().digestCodec

            override fun getSignature(): Signature {
                return threadLocal.get().getSignature()
            }

            override fun getSignatureOrNull(): Signature? {
                return threadLocal.get().getSignatureOrNull()
            }

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

            override fun verify(key: PublicKey, data: ByteArray): PreparedVerify {
                return threadLocal.get().verify(key, data)
            }

            override fun verify(key: PublicKey, data: ByteArray, offset: Int): PreparedVerify {
                return threadLocal.get().verify(key, data, offset)
            }

            override fun verify(key: PublicKey, data: ByteArray, offset: Int, length: Int): PreparedVerify {
                return threadLocal.get().verify(key, data, offset, length)
            }

            override fun verify(key: PublicKey, data: ByteBuffer): PreparedVerify {
                return threadLocal.get().verify(key, data)
            }

            override fun verify(key: PublicKey, data: InputStream): PreparedVerify {
                return threadLocal.get().verify(key, data)
            }
        }
    }

    companion object {

        @JvmStatic
        fun simpleImpl(algorithm: CodecAlgorithm, signature: Signature, digestCodec: DigestCodec): SignCodec {
            return object : SignCodec {
                override val algorithm: CodecAlgorithm = algorithm
                override val digestCodec: DigestCodec = digestCodec
                override fun getSignatureOrNull(): Signature {
                    return signature
                }
            }
        }

        @JvmStatic
        fun newBuilder(): Builder {
            return Builder()
        }

        @JvmStatic
        fun sha1WithRsa(): SignCodec {
            return newBuilder()
                .algorithm(CodecAlgorithm.SHA1_WITH_RSA)
                .digestCodec(sha1())
                .signatureSupplier { Signature.getInstance(CodecAlgorithm.SHA1_WITH_RSA_NAME) }
                .build()
        }

        @JvmStatic
        fun sha256WithRsa(): SignCodec {
            return newBuilder()
                .algorithm(CodecAlgorithm.SHA256_WITH_RSA)
                .digestCodec(sha256())
                .signatureSupplier { Signature.getInstance(CodecAlgorithm.SHA256_WITH_RSA_NAME) }
                .build()
        }

        @JvmStatic
        fun sha256WithSm2(): SignCodec {
            return newBuilder()
                .algorithm(CodecAlgorithm.SHA256_WITH_SM2)
                .digestCodec(sm3())
                .signatureSupplier {
                    Signature.getInstance(
                        CodecAlgorithm.SHA256_WITH_SM2_NAME,
                        DEFAULT_BCPROV_PROVIDER
                    )
                }
                .build()
        }

        @JvmStatic
        fun sm3WithSm2(): SignCodec {
            return newBuilder()
                .algorithm(CodecAlgorithm.SM3_WITH_SM2)
                .digestCodec(sm3())
                .signatureSupplier { Signature.getInstance(CodecAlgorithm.SM3_WITH_SM2_NAME, DEFAULT_BCPROV_PROVIDER) }
                .build()
        }

        @JvmName("forAlgorithm")
        @JvmStatic
        @JvmOverloads
        fun CharSequence.toSignCodec(provider: Provider? = null): SignCodec {
            return this.toCodecAlgorithm(CodecAlgorithmType.CIPHER).toSignCodec(provider)
        }

        @JvmName("forAlgorithm")
        @JvmStatic
        @JvmOverloads
        fun CodecAlgorithm.toSignCodec(provider: Provider? = null): SignCodec {
            return newBuilder()
                .algorithm(this)
                .signatureSupplier {
                    if (provider === null)
                        Signature.getInstance(this.name)
                    else
                        Signature.getInstance(this.name, provider)
                }
                .build()
        }
    }
}