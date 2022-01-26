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

/**
 * RSA cipher codec.
 */
open class RsaCodec : CipherCodec {

    override val algorithm = CodecAlgorithm.RSA
    override val cipher: Cipher = Cipher.getInstance(CodecAlgorithm.RSA_NAME)

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
            cipher.doFinal(data, curOffset, blockSize, dest, blockCount * outputBlockSize)
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
        val needingSize = needingBlock(length, blockSize) * outputBlockSize
        val buffer = ByteArray(outputBlockSize)
        var curOffset = offset
        while (curOffset < data.size) {
            cipher.doFinal(data, curOffset, blockSize, buffer)
            output.write(buffer)
            curOffset += blockSize
        }
        return needingSize.toLong()
    }

    override fun encrypt(key: Key, data: ByteBuffer): ByteBuffer {
        val cipher = this.cipher
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        val length = data.remaining()
        val needingSize = needingBlock(length, blockSize) * outputBlockSize
        val dest = ByteBuffer.allocate(needingSize)
        var curOffset = 0
        while (curOffset < length) {
            cipher.doFinal(data,dest)
            curOffset += blockSize
        }
        return dest
    }

    override fun encrypt(key: Key, data: ByteBuffer, dest: ByteBuffer): Int {
        val cipher = this.cipher
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        val length = data.remaining()
        val startPos = dest.position()
        var curOffset = 0
        while (curOffset < length) {
            cipher.doFinal(data,dest)
            curOffset += blockSize
        }
        return dest.position() - startPos
    }

    override fun encrypt(key: Key, data: InputStream): ByteArray {
        val cipher = this.cipher
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        val length = data.available()
        val needingSize = needingBlock(length, blockSize) * outputBlockSize
        val output = BytesAppender(needingSize)
        val buffer = ByteArray(blockSize)
        while (true){
            val count = data.read(buffer)
            if (count < 0) {
                break
            }
            output.write(cipher.doFinal(buffer, 0, count))
        }
        return output.toBytes()
    }

    override fun encrypt(key: Key, data: InputStream, output: OutputStream): Long {
        val cipher = this.cipher
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        val buffer = ByteArray(blockSize)
        var blockCount = 0
        while (true){
            val count = data.read(buffer)
            if (count < 0) {
                break
            }
            output.write(cipher.doFinal(buffer, 0, count))
            blockCount++
        }
        return (blockCount * outputBlockSize).toLong()
    }

    override fun decrypt(key: Key, data: ByteArray, offset: Int, length: Int): ByteArray {
        val cipher = this.cipher
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        val needingSize = needingBlock(length, outputBlockSize) * blockSize
        val dest = ByteArray(needingSize)
        var curOffset = offset
        var blockCount = 0
        while (curOffset < data.size) {
            cipher.doFinal(data, curOffset, blockSize, dest, blockCount * blockSize)
            curOffset += outputBlockSize
            blockCount++
        }
        return dest
    }

    override fun decrypt(key: Key, data: ByteArray, offset: Int, length: Int, output: OutputStream): Long {
        val cipher = this.cipher
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        val needingSize = needingBlock(length, outputBlockSize) * blockSize
        val buffer = ByteArray(blockSize)
        var curOffset = offset
        while (curOffset < data.size) {
            cipher.doFinal(data, curOffset, blockSize, buffer)
            output.write(buffer)
            curOffset += outputBlockSize
        }
        return needingSize.toLong()
    }
////////////////////////////
    override fun decrypt(key: Key, data: ByteBuffer): ByteBuffer {
        val cipher = this.cipher
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        val length = data.remaining()
        val needingSize = needingBlock(length, outputBlockSize) * blockSize
        val dest = ByteBuffer.allocate(needingSize)
        var curOffset = 0
        while (curOffset < length) {
            cipher.doFinal(data,dest)
            curOffset += outputBlockSize
        }
        return dest
    }

    override fun decrypt(key: Key, data: ByteBuffer, dest: ByteBuffer): Int {
        val cipher = this.cipher
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        val length = data.remaining()
        val startPos = dest.position()
        var curOffset = 0
        while (curOffset < length) {
            cipher.doFinal(data,dest)
            curOffset += blockSize
        }
        return dest.position() - startPos
    }

    override fun decrypt(key: Key, data: InputStream): ByteArray {
        val cipher = this.cipher
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        val length = data.available()
        val needingSize = needingBlock(length, blockSize) * outputBlockSize
        val output = BytesAppender(needingSize)
        val buffer = ByteArray(blockSize)
        while (true){
            val count = data.read(buffer)
            if (count < 0) {
                break
            }
            output.write(cipher.doFinal(buffer, 0, count))
        }
        return output.toBytes()
    }

    override fun decrypt(key: Key, data: InputStream, output: OutputStream): Long {
        val cipher = this.cipher
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        val buffer = ByteArray(blockSize)
        var blockCount = 0
        while (true){
            val count = data.read(buffer)
            if (count < 0) {
                break
            }
            output.write(cipher.doFinal(buffer, 0, count))
            blockCount++
        }
        return (blockCount * outputBlockSize).toLong()
    }
}