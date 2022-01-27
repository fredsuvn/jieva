package xyz.srclab.common.codec.rsa

import xyz.srclab.common.base.needingBlock
import xyz.srclab.common.base.remainingLength
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
open class RsaCodec : CipherCodec {

    override val algorithm = CodecAlgorithm.RSA
    override val cipher: Cipher = Cipher.getInstance(CodecAlgorithm.RSA_NAME)

    private fun blockSize(data: ByteArray, offset: Int, blockSize: Int): Int {
        return min(remainingLength(data.size, offset), blockSize)
    }

    override fun encrypt(key: Key, data: ByteArray, offset: Int, length: Int): ByteArray {
        val cipher = this.cipher
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        val needingSize = needingBlock(length, blockSize) * outputBlockSize
        val dest = ByteArray(needingSize)
        var curOffset = offset
        var blockCount = 0
        while (curOffset < data.size) {
            cipher.doFinal(data, curOffset, blockSize(data, curOffset, blockSize), dest, blockCount * outputBlockSize)
            curOffset += blockSize
            blockCount++
        }
        return dest
    }

    override fun encrypt(key: Key, data: ByteArray, offset: Int, length: Int, output: OutputStream): Long {
        val cipher = this.cipher
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        val buffer = ByteArray(outputBlockSize)
        var curOffset = offset
        var resultLength = 0L
        while (curOffset < data.size) {
            val outLength = cipher.doFinal(data, curOffset, blockSize(data, curOffset, blockSize), buffer)
            output.write(buffer, 0, outLength)
            curOffset += blockSize
            resultLength += outLength
        }
        return resultLength
    }

    override fun encrypt(key: Key, data: ByteBuffer): ByteBuffer {
        val cipher = this.cipher
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        val length = data.remaining()
        val needingSize = needingBlock(length, blockSize) * outputBlockSize
        val dest = ByteBuffer.allocate(needingSize)
        encrypt0(data, dest, cipher, blockSize, outputBlockSize, length)
        return dest
    }

    override fun encrypt(key: Key, data: ByteBuffer, dest: ByteBuffer): Int {
        val cipher = this.cipher
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        val length = data.remaining()
        return encrypt0(data, dest, cipher, blockSize, outputBlockSize, length)
    }

    private fun encrypt0(
        data: ByteBuffer, dest: ByteBuffer, cipher: Cipher, blockSize: Int, outputBlockSize: Int, length: Int): Int {
        if (data.hasArray()) {
            val buffer = ByteArray(outputBlockSize)
            val array = data.array()
            var arrayOffset = data.arrayOffset()
            var resultLength = 0
            while (arrayOffset < length) {
                val outLength = cipher.doFinal(array, arrayOffset, blockSize(array, arrayOffset, blockSize), buffer)
                arrayOffset += blockSize
                dest.put(buffer, 0, outLength)
                resultLength += outLength
            }
            data.position(data.position() + resultLength)
            return resultLength
        } else {
            val inBuffer = ByteArray(blockSize)
            val outBuffer = ByteArray(outputBlockSize)
            var resultLength = 0
            var curOffset = 0
            while (curOffset < length) {
                val inLength = if (data.remaining() >= inBuffer.size) inBuffer.size else data.remaining()
                data.get(inBuffer)
                val outLength = cipher.doFinal(inBuffer, 0, inLength, outBuffer)
                dest.put(outBuffer, 0, outLength)
                curOffset += inBuffer.size
                resultLength += outLength
            }
            return resultLength
        }
    }

    override fun encrypt(key: Key, data: InputStream): ByteArray {
        val cipher = this.cipher
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        val needingSize = needingBlock(data.available(), blockSize) * outputBlockSize
        val output = BytesAppender(needingSize)
        encrypt0(data, output, cipher, blockSize)
        return output.toBytes()
    }

    override fun encrypt(key: Key, data: InputStream, output: OutputStream): Long {
        val cipher = this.cipher
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        return encrypt0(data, output, cipher, blockSize)
    }

    private fun encrypt0(data: InputStream, output: OutputStream, cipher: Cipher, blockSize: Int): Long {
        val inBuffer = ByteArray(blockSize)
        var resultLength = 0L
        while (true) {
            val count = data.read(inBuffer)
            if (count < 0) {
                break
            }
            if (count > 0) {
                val outBuffer = cipher.doFinal(inBuffer, 0, count)
                output.write(outBuffer)
                resultLength += outBuffer.size
            }
        }
        return resultLength
    }


    override fun decrypt(key: Key, data: ByteArray, offset: Int, length: Int): ByteArray {
        val cipher = this.cipher
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        val needingSize = needingBlock(length, blockSize) * outputBlockSize
        val dest = ByteArray(needingSize)
        var curOffset = offset
        var blockCount = 0
        while (curOffset < data.size) {
            cipher.doFinal(data, curOffset, blockSize(data, curOffset, blockSize), dest, blockCount * outputBlockSize)
            curOffset += blockSize
            blockCount++
        }
        return dest
    }

    override fun decrypt(key: Key, data: ByteArray, offset: Int, length: Int, output: OutputStream): Long {
        val cipher = this.cipher
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        val buffer = ByteArray(outputBlockSize)
        var curOffset = offset
        var resultLength = 0L
        while (curOffset < data.size) {
            val outLength = cipher.doFinal(data, curOffset, blockSize(data, curOffset, blockSize), buffer)
            output.write(buffer, 0, outLength)
            curOffset += blockSize
            resultLength += outLength
        }
        return resultLength
    }

    override fun decrypt(key: Key, data: ByteBuffer): ByteBuffer {
        val cipher = this.cipher
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        val length = data.remaining()
        val needingSize = needingBlock(length, blockSize) * outputBlockSize
        val dest = ByteBuffer.allocate(needingSize)
        decrypt0(data, dest, cipher, blockSize, outputBlockSize, length)
        return dest
    }

    override fun decrypt(key: Key, data: ByteBuffer, dest: ByteBuffer): Int {
        val cipher = this.cipher
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        val length = data.remaining()
        return decrypt0(data, dest, cipher, blockSize, outputBlockSize, length)
    }

    private fun decrypt0(
        data: ByteBuffer, dest: ByteBuffer, cipher: Cipher, blockSize: Int, outputBlockSize: Int, length: Int): Int {
        if (data.hasArray()) {
            val buffer = ByteArray(outputBlockSize)
            val array = data.array()
            var arrayOffset = data.arrayOffset()
            var resultLength = 0
            while (arrayOffset < length) {
                val outLength = cipher.doFinal(array, arrayOffset, blockSize(array, arrayOffset, blockSize), buffer)
                arrayOffset += blockSize
                dest.put(buffer, 0, outLength)
                resultLength += outLength
            }
            data.position(data.position() + resultLength)
            return resultLength
        } else {
            val inBuffer = ByteArray(blockSize)
            val outBuffer = ByteArray(outputBlockSize)
            var resultLength = 0
            var curOffset = 0
            while (curOffset < length) {
                val inLength = if (data.remaining() >= inBuffer.size) inBuffer.size else data.remaining()
                data.get(inBuffer)
                val outLength = cipher.doFinal(inBuffer, 0, inLength, outBuffer)
                dest.put(outBuffer, 0, outLength)
                curOffset += inBuffer.size
                resultLength += outLength
            }
            return resultLength
        }
    }

    override fun decrypt(key: Key, data: InputStream): ByteArray {
        val cipher = this.cipher
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        val needingSize = needingBlock(data.available(), blockSize) * outputBlockSize
        val output = BytesAppender(needingSize)
        decrypt0(data, output, cipher, blockSize)
        return output.toBytes()
    }

    override fun decrypt(key: Key, data: InputStream, output: OutputStream): Long {
        val cipher = this.cipher
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        return decrypt0(data, output, cipher, blockSize)
    }

    private fun decrypt0(data: InputStream, output: OutputStream, cipher: Cipher, blockSize: Int): Long {
        val inBuffer = ByteArray(blockSize)
        var resultLength = 0L
        while (true) {
            val count = data.read(inBuffer)
            if (count < 0) {
                break
            }
            if (count > 0) {
                val outBuffer = cipher.doFinal(inBuffer, 0, count)
                output.write(outBuffer)
                resultLength += outBuffer.size
            }
        }
        return resultLength
    }
}