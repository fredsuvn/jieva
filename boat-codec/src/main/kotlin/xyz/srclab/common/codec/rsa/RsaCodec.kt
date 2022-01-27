package xyz.srclab.common.codec.rsa

import xyz.srclab.common.base.needingBlock
import xyz.srclab.common.codec.CipherCodec
import xyz.srclab.common.codec.CodecAlgorithm
import xyz.srclab.common.io.BytesAppender
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.security.Key
import javax.crypto.Cipher
import kotlin.math.min

/**
 * RSA cipher codec.
 */
open class RsaCodec @JvmOverloads constructor(
    override val cipher: Cipher = Cipher.getInstance(CodecAlgorithm.RSA_NAME)
) : CipherCodec {

    override val algorithm = CodecAlgorithm.RSA

    override fun encrypt(key: Key, data: ByteArray, offset: Int, length: Int): ByteArray {
        val cipher = this.cipher
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val outputSize = cipher.getOutputSize(0)
        val blockSize = outputSize - 11
        return encrypt0(data, offset, length, cipher, blockSize, outputSize)
    }

    override fun encrypt(key: Key, data: ByteArray, offset: Int, length: Int, output: OutputStream): Long {
        val cipher = this.cipher
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val outputSize = cipher.getOutputSize(0)
        val blockSize = outputSize - 11
        return encrypt0(data, offset, length, output, cipher, blockSize, outputSize)
    }

    override fun encrypt(key: Key, data: ByteBuffer): ByteBuffer {
        val cipher = this.cipher
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val outputSize = cipher.getOutputSize(0)
        val blockSize = outputSize - 11
        val length = data.remaining()
        val needingSize = needingBlock(length, blockSize) * outputSize
        val dest = ByteBuffer.allocate(needingSize)
        encrypt0(data, dest, cipher, blockSize, outputSize, length)
        return dest
    }

    override fun encrypt(key: Key, data: ByteBuffer, dest: ByteBuffer): Int {
        val cipher = this.cipher
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val outputSize = cipher.getOutputSize(0)
        val blockSize = outputSize - 11
        val length = data.remaining()
        return encrypt0(data, dest, cipher, blockSize, outputSize, length)
    }

    override fun encrypt(key: Key, data: InputStream): ByteArray {
        val cipher = this.cipher
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val outputSize = cipher.getOutputSize(0)
        val blockSize = outputSize - 11
        val needingSize = needingBlock(data.available(), blockSize) * outputSize
        val output = BytesAppender(needingSize)
        encrypt0(data, output, cipher, blockSize)
        return output.toBytes()
    }

    override fun encrypt(key: Key, data: InputStream, output: OutputStream): Long {
        val cipher = this.cipher
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val outputSize = cipher.getOutputSize(0)
        val blockSize = outputSize - 11
        return encrypt0(data, output, cipher, blockSize)
    }

    override fun decrypt(key: Key, data: ByteArray, offset: Int, length: Int): ByteArray {
        val cipher = this.cipher
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputSize = cipher.getOutputSize(0)
        val blockSize = outputSize - 11
        return encrypt0(data, offset, length, cipher, outputSize, blockSize)
    }

    override fun decrypt(key: Key, data: ByteArray, offset: Int, length: Int, output: OutputStream): Long {
        val cipher = this.cipher
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputSize = cipher.getOutputSize(0)
        val blockSize = outputSize - 11
        return encrypt0(data, offset, length, output, cipher, outputSize, blockSize)
    }

    override fun decrypt(key: Key, data: ByteBuffer): ByteBuffer {
        val cipher = this.cipher
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputSize = cipher.getOutputSize(0)
        val blockSize = outputSize - 11
        val length = data.remaining()
        val needingSize = needingBlock(length, outputSize) * blockSize
        val dest = ByteBuffer.allocate(needingSize)
        encrypt0(data, dest, cipher, outputSize, blockSize, length)
        return dest
    }

    override fun decrypt(key: Key, data: ByteBuffer, dest: ByteBuffer): Int {
        val cipher = this.cipher
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputSize = cipher.getOutputSize(0)
        val blockSize = outputSize - 11
        val length = data.remaining()
        return encrypt0(data, dest, cipher, outputSize, blockSize, length)
    }

    override fun decrypt(key: Key, data: InputStream): ByteArray {
        val cipher = this.cipher
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputSize = cipher.getOutputSize(0)
        val blockSize = outputSize - 11
        val needingSize = needingBlock(data.available(), outputSize) * blockSize
        val output = BytesAppender(needingSize)
        encrypt0(data, output, cipher, outputSize)
        return output.toBytes()
    }

    override fun decrypt(key: Key, data: InputStream, output: OutputStream): Long {
        val cipher = this.cipher
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputSize = cipher.getOutputSize(0)
        return encrypt0(data, output, cipher, outputSize)
    }

    private fun encrypt0(
        data: ByteArray, offset: Int, length: Int, cipher: Cipher, blockSize: Int, outputSize: Int
    ): ByteArray {
        val needingSize = needingBlock(length, blockSize) * outputSize
        val dest = ByteArray(needingSize)
        var dataOffset = offset
        var destOffset = 0
        while (dataOffset < data.size) {
            val inLength = inLength(offset, length, dataOffset, blockSize)
            val outLength = cipher.doFinal(data, dataOffset, inLength, dest, destOffset)
            dataOffset += inLength
            destOffset += outLength
        }
        return if (destOffset == needingSize) dest else dest.copyOfRange(0, destOffset)
    }

    private fun encrypt0(
        data: ByteArray, offset: Int, length: Int, output: OutputStream,
        cipher: Cipher, blockSize: Int, outputSize: Int
    ): Long {
        val outBuffer = ByteArray(outputSize)
        var dataOffset = offset
        var destLength = 0L
        while (dataOffset < data.size) {
            val inLength = inLength(offset, length, dataOffset, blockSize)
            val outLength = cipher.doFinal(data, dataOffset, inLength, outBuffer)
            output.write(outBuffer, 0, outLength)
            dataOffset += inLength
            destLength += outLength
        }
        return destLength
    }

    private fun encrypt0(
        data: ByteBuffer, dest: ByteBuffer, cipher: Cipher, blockSize: Int, outputSize: Int, remaining: Int
    ): Int {
        if (data.hasArray()) {
            val buffer = ByteArray(outputSize)
            val array = data.array()
            val arrayOffset = data.arrayOffset() + data.position()
            var dataOffset = arrayOffset
            var destLength = 0
            while (arrayOffset < remaining) {
                val inLength = inLength(arrayOffset, remaining, dataOffset, blockSize)
                val outLength = cipher.doFinal(array, arrayOffset, inLength, buffer)
                dest.put(buffer, 0, outLength)
                dataOffset += inLength
                destLength += outLength
            }
            data.position(data.position() + destLength)
            return destLength
        } else {
            val inBuffer = ByteArray(blockSize)
            val outBuffer = ByteArray(outputSize)
            var dataOffset = 0
            var destLength = 0
            while (dataOffset < remaining) {
                val inLength = if (data.remaining() >= inBuffer.size) inBuffer.size else data.remaining()
                data.get(inBuffer)
                val outLength = cipher.doFinal(inBuffer, 0, inLength, outBuffer)
                dest.put(outBuffer, 0, outLength)
                dataOffset += inLength
                destLength += outLength
            }
            return destLength
        }
    }

    private fun encrypt0(data: InputStream, output: OutputStream, cipher: Cipher, blockSize: Int): Long {
        val inBuffer = ByteArray(blockSize)
        var destLength = 0L
        while (true) {
            val inLength = data.read(inBuffer)
            if (inLength < 0) {
                break
            }
            if (inLength > 0) {
                val outBuffer = cipher.doFinal(inBuffer, 0, inLength)
                output.write(outBuffer)
                destLength += outBuffer.size
            }
        }
        return destLength
    }

    private fun inLength(offset: Int, length: Int, dataOffset: Int, blockSize: Int): Int {
        return min(offset + length - dataOffset, blockSize)
    }
}