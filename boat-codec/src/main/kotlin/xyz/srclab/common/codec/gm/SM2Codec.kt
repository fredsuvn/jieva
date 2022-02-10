package xyz.srclab.common.codec.gm

import org.bouncycastle.crypto.engines.SM2Engine
import org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi
import xyz.srclab.common.base.DEFAULT_IO_BUFFER_SIZE
import xyz.srclab.common.codec.CipherCodec
import xyz.srclab.common.codec.CodecAlgorithm
import xyz.srclab.common.codec.PreparedCodec
import xyz.srclab.common.io.getBytes
import xyz.srclab.common.io.readBytes
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.security.Key
import java.security.SecureRandom
import javax.crypto.Cipher

/**
 * SM2 cipher codec.
 */
open class SM2Codec @JvmOverloads constructor(
    mode: Int = MODE_C1C3C2
) : CipherCodec {

    override val algorithm: CodecAlgorithm = CodecAlgorithm.SM2

    private val cipher = run {
        val engine = SM2Engine(
            if (mode == MODE_C1C2C3) {
                SM2Engine.Mode.C1C2C3
            } else {
                SM2Engine.Mode.C1C3C2
            }
        )
        GMCipherSpi(engine)
    }

    override fun getCipherOrNull(): Cipher? {
        return null
    }

    override fun getBlockSize(): Int {
        return 0
    }

    override fun getOutputSize(inputSize: Int): Int {
        return cipher.engineGetOutputSize(inputSize)
    }

    override fun encrypt(key: Key, data: ByteArray, offset: Int, length: Int): PreparedCodec {
        return ByteArrayPreparedCodec(cipher, Cipher.ENCRYPT_MODE, key, data, offset, length)
    }

    override fun encrypt(key: Key, data: ByteBuffer): PreparedCodec {
        if (data.hasArray()) {
            val array = data.array()
            val arrayOffset = data.arrayOffset()
            val length = data.remaining()
            data.position(data.limit())
            return encrypt(key, array, arrayOffset, length)
        }
        return encrypt(key, data.getBytes())
    }

    override fun encrypt(key: Key, data: InputStream): PreparedCodec {
        return InputStreamPreparedCodec(cipher, Cipher.ENCRYPT_MODE, key, data)
    }

    override fun decrypt(key: Key, data: ByteArray, offset: Int, length: Int): PreparedCodec {
        return ByteArrayPreparedCodec(cipher, Cipher.DECRYPT_MODE, key, data, offset, length)
    }

    override fun decrypt(key: Key, data: ByteBuffer): PreparedCodec {
        if (data.hasArray()) {
            val array = data.array()
            val arrayOffset = data.arrayOffset()
            val length = data.remaining()
            data.position(data.limit())
            return decrypt(key, array, arrayOffset, length)
        }
        return decrypt(key, data.getBytes())
    }

    override fun decrypt(key: Key, data: InputStream): PreparedCodec {
        return InputStreamPreparedCodec(cipher, Cipher.DECRYPT_MODE, key, data)
    }

    open class ByteArrayPreparedCodec(
        private val cipher: GMCipherSpi,
        private val mode: Int,
        private val key: Key,
        private val data: ByteArray,
        private val dataOffset: Int,
        private val dataLength: Int
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            cipher.engineInit(mode, key, SecureRandom())
            return cipher.engineDoFinal(data, dataOffset, dataLength)
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            cipher.engineInit(mode, key, SecureRandom())
            return cipher.engineDoFinal(data, dataOffset, dataLength, dest, offset)
        }

        override fun doFinal(dest: OutputStream): Long {
            val result = doFinal()
            dest.write(result)
            return result.size.toLong()
        }
    }

    open class InputStreamPreparedCodec(
        private val cipher: GMCipherSpi,
        private val mode: Int,
        private val key: Key,
        private val data: InputStream
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            cipher.engineInit(mode, key, SecureRandom())
            val bytes = data.readBytes()
            return cipher.engineDoFinal(bytes, 0, bytes.size)
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            cipher.engineInit(mode, key, SecureRandom())
            val bytes = data.readBytes()
            return cipher.engineDoFinal(bytes, 0, bytes.size, dest, offset)
        }

        override fun doFinal(dest: OutputStream): Long {
            cipher.engineInit(mode, key, SecureRandom())
            val buffer = ByteArray(DEFAULT_IO_BUFFER_SIZE)
            var c = data.read(buffer)
            while (c >= 0) {
                cipher.engineUpdate(buffer, 0, c)
                c = data.read(buffer)
            }
            val result = cipher.engineDoFinal(buffer, 0, 0)
            dest.write(result)
            return result.size.toLong()
        }
    }

    companion object {
        const val MODE_C1C2C3 = 1
        const val MODE_C1C3C2 = 2
    }
}