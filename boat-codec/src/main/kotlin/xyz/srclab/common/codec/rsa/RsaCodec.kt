package xyz.srclab.common.codec.rsa

import xyz.srclab.common.base.needingBlock
import xyz.srclab.common.codec.CipherCodec
import xyz.srclab.common.codec.CodecAlgorithm
import xyz.srclab.common.codec.PreparedCodec
import xyz.srclab.common.io.BytesAppender
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.security.Key
import javax.crypto.Cipher
import kotlin.math.max
import kotlin.math.min

/**
 * RSA cipher codec.
 */
open class RsaCodec @JvmOverloads constructor(
    cipher: Cipher = Cipher.getInstance(CodecAlgorithm.RSA_NAME)
) : CipherCodec {

    private val cipher0 = cipher

    override val algorithm = CodecAlgorithm.RSA

    override fun getCipherOrNull(): Cipher? {
        return cipher0
    }

    override fun encrypt(key: Key, data: ByteArray, offset: Int, length: Int): PreparedCodec {
        return ByteArrayEnPreparedCodec(cipher0, key, data, offset, length)
    }

    override fun encrypt(key: Key, data: ByteBuffer): PreparedCodec {
        return ByteBufferEnPreparedCodec(cipher0, key, data)
    }

    override fun encrypt(key: Key, data: InputStream): PreparedCodec {
        return InputStreamEnPreparedCodec(cipher0, key, data)
    }

    override fun decrypt(key: Key, data: ByteArray, offset: Int, length: Int): PreparedCodec {
        return ByteArrayDePreparedCodec(cipher0, key, data, offset, length)
    }

    override fun decrypt(key: Key, data: ByteBuffer): PreparedCodec {
        return ByteBufferDePreparedCodec(cipher0, key, data)
    }

    override fun decrypt(key: Key, data: InputStream): PreparedCodec {
        return InputStreamDePreparedCodec(cipher0, key, data)
    }

    private fun encrypt0(
        data: ByteArray, offset: Int, length: Int,
        cipher: Cipher, blockSize: Int, outputSize: Int
    ): ByteArray {
        val needingSize = rsaNeedingBlock(length, blockSize, outputSize)
        val dest = ByteArray(needingSize)
        var curDataOffset = offset
        var curDestOffset = 0
        while (curDataOffset < offset + length) {
            val inLength = inLength(offset, length, curDataOffset, blockSize)
            val outLength = cipher.doFinal(data, curDataOffset, inLength, dest, curDestOffset)
            curDataOffset += inLength
            curDestOffset += outLength
        }
        return if (curDestOffset == needingSize) dest else dest.copyOfRange(0, curDestOffset)
    }

    private fun encrypt0(
        data: ByteArray, dataOffset: Int, dataLength: Int,
        dest: ByteArray, destOffset: Int,
        cipher: Cipher, blockSize: Int
    ): Int {
        var curDataOffset = dataOffset
        var curDestOffset = destOffset
        while (curDataOffset < dataOffset + dataLength) {
            val inLength = inLength(dataOffset, dataLength, curDataOffset, blockSize)
            val outLength = cipher.doFinal(data, curDataOffset, inLength, dest, curDestOffset)
            curDataOffset += inLength
            curDestOffset += outLength
        }
        return curDestOffset - destOffset
    }

    private fun encrypt0(
        data: ByteArray, dataOffset: Int, dataLength: Int,
        dest: ByteBuffer,
        cipher: Cipher, blockSize: Int, outputSize: Int
    ): Int {
        if (dest.hasArray()) {
            val startPos = dest.position()
            val cryptLength = encrypt0(data, dataOffset, dataLength, dest.array(), startPos, cipher, blockSize)
            dest.position(startPos + cryptLength)
            return cryptLength
        }
        val outBuffer = ByteArray(max(blockSize, outputSize))
        var curDataOffset = dataOffset
        var cryptLength = 0
        while (curDataOffset < dataOffset + dataLength) {
            val inLength = inLength(dataOffset, dataLength, curDataOffset, blockSize)
            val outLength = cipher.doFinal(data, curDataOffset, inLength, outBuffer)
            dest.put(outBuffer, 0, outLength)
            curDataOffset += inLength
            cryptLength += outLength
        }
        return cryptLength
    }

    private fun encrypt0(
        data: ByteArray, offset: Int, length: Int,
        dest: OutputStream,
        cipher: Cipher, blockSize: Int, outputSize: Int
    ): Long {
        val outBuffer = ByteArray(max(blockSize, outputSize))
        var curDataOffset = offset
        var cryptLength = 0L
        while (curDataOffset < offset + length) {
            val inLength = inLength(offset, length, curDataOffset, blockSize)
            val outLength = cipher.doFinal(data, curDataOffset, inLength, outBuffer)
            dest.write(outBuffer, 0, outLength)
            curDataOffset += inLength
            cryptLength += outLength
        }
        return cryptLength
    }

    private fun encrypt0(
        data: ByteBuffer,
        cipher: Cipher, blockSize: Int, outputSize: Int
    ): ByteArray {
        if (data.hasArray()) {
            val startPos = data.position()
            val array = data.array()
            val arrayOffset = data.arrayOffset() + startPos
            val result = encrypt0(array, arrayOffset, data.remaining(), cipher, blockSize, outputSize)
            data.position(data.limit())
            return result
        }
        val length = data.remaining()
        val needingSize = rsaNeedingBlock(length, blockSize, outputSize)
        val dest = ByteArray(needingSize)
        val inBuffer = ByteArray(blockSize)
        var curDataOffset = 0
        var curDestOffset = 0
        while (curDataOffset < length) {
            val inLength = inLength(data, blockSize)
            data.get(inBuffer)
            val outLength = cipher.doFinal(inBuffer, 0, inLength, dest, curDestOffset)
            curDataOffset += inLength
            curDestOffset += outLength
        }
        return if (curDestOffset == needingSize) dest else dest.copyOfRange(0, curDestOffset)
    }

    private fun encrypt0(
        data: ByteBuffer,
        dest: ByteArray, destOffset: Int,
        cipher: Cipher, blockSize: Int, outputSize: Int
    ): Int {
        if (data.hasArray()) {
            val startPos = data.position()
            val array = data.array()
            val arrayOffset = data.arrayOffset() + startPos
            val cryptLength = encrypt0(array, arrayOffset, data.remaining(), dest, destOffset, cipher, blockSize)
            data.position(data.limit())
            return cryptLength
        }
        val length = data.remaining()
        val inBuffer = ByteArray(blockSize)
        var curDataOffset = 0
        var curDestOffset = destOffset
        while (curDataOffset < length) {
            val inLength = inLength(data, blockSize)
            data.get(inBuffer)
            val outLength = cipher.doFinal(inBuffer, 0, inLength, dest, curDestOffset)
            curDataOffset += inLength
            curDestOffset += outLength
        }
        return curDestOffset - destOffset
    }

    private fun encrypt0(
        data: ByteBuffer,
        dest: ByteBuffer,
        cipher: Cipher, blockSize: Int, outputSize: Int
    ): Int {
        if (data.hasArray() && dest.hasArray()) {
            val dataStartPos = data.position()
            val dataArray = data.array()
            val dataArrayOffset = data.arrayOffset() + dataStartPos
            val destStartPos = data.position()
            val destArray = dest.array()
            val destArrayOffset = dest.arrayOffset() + destStartPos
            val cryptLength = encrypt0(
                dataArray, dataArrayOffset, data.remaining(), destArray, destArrayOffset, cipher, blockSize)
            data.position(data.limit())
            dest.position(destStartPos + cryptLength)
            return cryptLength
        } else if (data.hasArray() && !dest.hasArray()) {
            val dataStartPos = data.position()
            val dataArray = data.array()
            val dataArrayOffset = data.arrayOffset() + dataStartPos
            val cryptLength = encrypt0(
                dataArray, dataArrayOffset, data.remaining(), dest, cipher, blockSize, outputSize)
            data.position(data.limit())
            return cryptLength
        } else if (!data.hasArray() && dest.hasArray()) {
            val destStartPos = data.position()
            val destArray = dest.array()
            val destArrayOffset = dest.arrayOffset() + destStartPos
            val cryptLength = encrypt0(data, destArray, destArrayOffset, cipher, blockSize, outputSize)
            dest.position(destStartPos + cryptLength)
            return cryptLength
        } else {
            val length = data.remaining()
            val inBuffer = ByteArray(blockSize)
            val outBuffer = ByteArray(max(blockSize, outputSize))
            var curDataOffset = 0
            var cryptLength = 0
            while (curDataOffset < length) {
                val inLength = inLength(data, blockSize)
                data.get(inBuffer)
                val outLength = cipher.doFinal(inBuffer, 0, inLength, outBuffer, 0)
                dest.put(outBuffer, 0, outLength)
                curDataOffset += inLength
                cryptLength += outLength
            }
            return cryptLength
        }
    }

    private fun encrypt0(
        data: ByteBuffer,
        dest: OutputStream,
        cipher: Cipher, blockSize: Int, outputSize: Int
    ): Long {
        if (data.hasArray()) {
            val startPos = data.position()
            val array = data.array()
            val arrayOffset = data.arrayOffset() + startPos
            val cryptLength = encrypt0(array, arrayOffset, data.remaining(), dest, cipher, blockSize, outputSize)
            data.position(data.limit())
            return cryptLength
        }
        val length = data.remaining()
        val inBuffer = ByteArray(blockSize)
        val outBuffer = ByteArray(max(blockSize, outputSize))
        var curDataOffset = 0
        var cryptLength = 0L
        while (curDataOffset < length) {
            val inLength = inLength(data, blockSize)
            data.get(inBuffer)
            val outLength = cipher.doFinal(inBuffer, 0, inLength, outBuffer, 0)
            dest.write(outBuffer, 0, outLength)
            curDataOffset += inLength
            cryptLength += outLength
        }
        return cryptLength
    }

    private fun encrypt0(
        data: InputStream,
        cipher: Cipher, blockSize: Int, outputSize: Int
    ): ByteArray {
        val length = data.available()
        val needingSize = rsaNeedingBlock(length, blockSize, outputSize)
        val dest = BytesAppender(needingSize)
        encrypt0(data, dest, cipher, blockSize, outputSize)
        return dest.toBytes()
    }

    private fun encrypt0(
        data: InputStream,
        dest: ByteArray, destOffset: Int,
        cipher: Cipher, blockSize: Int, outputSize: Int
    ): Int {
        val inBuffer = ByteArray(blockSize)
        var curDestOffset = destOffset
        while (true) {
            val inLength = data.read(inBuffer)
            if (inLength < 0) {
                break
            }
            if (inLength > 0) {
                val outLength = cipher.doFinal(inBuffer, 0, inLength, dest, curDestOffset)
                curDestOffset += outLength
            }
        }
        return curDestOffset - destOffset
    }

    private fun encrypt0(
        data: InputStream,
        dest: ByteBuffer,
        cipher: Cipher, blockSize: Int, outputSize: Int
    ): Int {
        if (dest.hasArray()) {
            val startPos = dest.position()
            val array = dest.array()
            val arrayOffset = dest.arrayOffset() + startPos
            val cryptLength = encrypt0(data, array, arrayOffset, cipher, blockSize, outputSize)
            dest.position(startPos + cryptLength)
            return cryptLength
        }
        val inBuffer = ByteArray(blockSize)
        val outBuffer = ByteArray(max(blockSize, outputSize))
        var cryptLength = 0
        while (true) {
            val inLength = data.read(inBuffer)
            if (inLength < 0) {
                break
            }
            if (inLength > 0) {
                val outLength = cipher.doFinal(inBuffer, 0, inLength, outBuffer, 0)
                dest.put(outBuffer, 0, outLength)
                cryptLength += outLength
            }
        }
        return cryptLength
    }

    private fun encrypt0(
        data: InputStream,
        dest: OutputStream,
        cipher: Cipher, blockSize: Int, outputSize: Int
    ): Long {
        val inBuffer = ByteArray(blockSize)
        val outBuffer = ByteArray(max(blockSize, outputSize))
        var cryptLength = 0L
        while (true) {
            val inLength = data.read(inBuffer)
            if (inLength < 0) {
                break
            }
            if (inLength > 0) {
                val outLength = cipher.doFinal(inBuffer, 0, inLength, outBuffer, 0)
                dest.write(outBuffer, 0, outLength)
                cryptLength += outLength
            }
        }
        return cryptLength
    }

    private fun inLength(offset: Int, length: Int, dataOffset: Int, blockSize: Int): Int {
        return min(offset + length - dataOffset, blockSize)
    }

    private fun inLength(buffer: ByteBuffer, blockSize: Int): Int {
        return min(buffer.remaining(), blockSize)
    }

    private fun rsaNeedingBlock(length: Int, blockSize: Int, outputSize: Int): Int {
        val actualSize = needingBlock(length, blockSize) * outputSize
        if (blockSize <= outputSize) {
            return actualSize
        }
        return actualSize - outputSize + blockSize
    }

    open inner class ByteArrayEnPreparedCodec(
        private val cipher: Cipher,
        private val key: Key,
        private val data: ByteArray,
        private val dataOffset: Int,
        private val dataLength: Int
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            val blockSize = outputSize - 11
            return encrypt0(data, dataOffset, dataLength, cipher, blockSize, outputSize)
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            val blockSize = outputSize - 11
            return encrypt0(data, dataOffset, dataLength, dest, offset, cipher, blockSize)
        }

        override fun doFinal(dest: ByteBuffer): Int {
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            val blockSize = outputSize - 11
            return encrypt0(data, dataOffset, dataLength, dest, cipher, blockSize, outputSize)
        }

        override fun doFinal(dest: OutputStream): Long {
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            val blockSize = outputSize - 11
            return encrypt0(data, dataOffset, dataLength, dest, cipher, blockSize, outputSize)
        }
    }

    open inner class ByteBufferEnPreparedCodec(
        private val cipher: Cipher,
        private val key: Key,
        private val data: ByteBuffer
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            val blockSize = outputSize - 11
            return encrypt0(data, cipher, blockSize, outputSize)
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            val blockSize = outputSize - 11
            return encrypt0(data, dest, offset, cipher, blockSize, outputSize)
        }

        override fun doFinal(dest: ByteBuffer): Int {
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            val blockSize = outputSize - 11
            return encrypt0(data, dest, cipher, blockSize, outputSize)
        }

        override fun doFinal(dest: OutputStream): Long {
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            val blockSize = outputSize - 11
            return encrypt0(data, dest, cipher, blockSize, outputSize)
        }
    }

    open inner class InputStreamEnPreparedCodec(
        private val cipher: Cipher,
        private val key: Key,
        private val data: InputStream
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            val blockSize = outputSize - 11
            return encrypt0(data, cipher, blockSize, outputSize)
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            val blockSize = outputSize - 11
            return encrypt0(data, dest, offset, cipher, blockSize, outputSize)
        }

        override fun doFinal(dest: ByteBuffer): Int {
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            val blockSize = outputSize - 11
            return encrypt0(data, dest, cipher, blockSize, outputSize)
        }

        override fun doFinal(dest: OutputStream): Long {
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            val blockSize = outputSize - 11
            return encrypt0(data, dest, cipher, blockSize, outputSize)
        }
    }

    open inner class ByteArrayDePreparedCodec(
        private val cipher: Cipher,
        private val key: Key,
        private val data: ByteArray,
        private val dataOffset: Int,
        private val dataLength: Int
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            cipher.init(Cipher.DECRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            val blockSize = outputSize - 11
            return encrypt0(data, dataOffset, dataLength, cipher, outputSize, blockSize)
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            cipher.init(Cipher.DECRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            return encrypt0(data, dataOffset, dataLength, dest, offset, cipher, outputSize)
        }

        override fun doFinal(dest: ByteBuffer): Int {
            cipher.init(Cipher.DECRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            val blockSize = outputSize - 11
            return encrypt0(data, dataOffset, dataLength, dest, cipher, outputSize, blockSize)
        }

        override fun doFinal(dest: OutputStream): Long {
            cipher.init(Cipher.DECRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            val blockSize = outputSize - 11
            return encrypt0(data, dataOffset, dataLength, dest, cipher, outputSize, blockSize)
        }
    }

    open inner class ByteBufferDePreparedCodec(
        private val cipher: Cipher,
        private val key: Key,
        private val data: ByteBuffer
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            cipher.init(Cipher.DECRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            val blockSize = outputSize - 11
            return encrypt0(data, cipher, outputSize, blockSize)
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            cipher.init(Cipher.DECRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            return encrypt0(data, dest, offset, cipher, outputSize, outputSize)
        }

        override fun doFinal(dest: ByteBuffer): Int {
            cipher.init(Cipher.DECRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            val blockSize = outputSize - 11
            return encrypt0(data, dest, cipher, outputSize, blockSize)
        }

        override fun doFinal(dest: OutputStream): Long {
            cipher.init(Cipher.DECRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            val blockSize = outputSize - 11
            return encrypt0(data, dest, cipher, outputSize, blockSize)
        }
    }

    open inner class InputStreamDePreparedCodec(
        private val cipher: Cipher,
        private val key: Key,
        private val data: InputStream
    ) : PreparedCodec {

        override fun doFinal(): ByteArray {
            cipher.init(Cipher.DECRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            val blockSize = outputSize - 11
            return encrypt0(data, cipher, outputSize, blockSize)
        }

        override fun doFinal(dest: ByteArray, offset: Int, length: Int): Int {
            cipher.init(Cipher.DECRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            return encrypt0(data, dest, offset, cipher, outputSize, outputSize)
        }

        override fun doFinal(dest: ByteBuffer): Int {
            cipher.init(Cipher.DECRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            val blockSize = outputSize - 11
            return encrypt0(data, dest, cipher, outputSize, blockSize)
        }

        override fun doFinal(dest: OutputStream): Long {
            cipher.init(Cipher.DECRYPT_MODE, key)
            val outputSize = cipher.getOutputSize(0)
            val blockSize = outputSize - 11
            return encrypt0(data, dest, cipher, outputSize, blockSize)
        }
    }
}