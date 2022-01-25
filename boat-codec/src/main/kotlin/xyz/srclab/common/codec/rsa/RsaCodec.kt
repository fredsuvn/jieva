package xyz.srclab.common.codec.rsa

import xyz.srclab.common.base.needingBlock
import xyz.srclab.common.codec.CipherCodec
import xyz.srclab.common.codec.CodecAlgorithm
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.security.Key
import javax.crypto.Cipher

/**
 * RSA cipher codec.
 *
 * @author sunqian
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
        val result = ByteArray(needingSize)
        var curOffset = offset
        var blockCount = 0
        while (curOffset < data.size) {
            cipher.doFinal(data, curOffset, blockSize, result, blockCount * outputBlockSize)
            curOffset += blockSize
            blockCount++
        }
        return result
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

    //override fun encrypt(key: Key, data: ByteBuffer): ByteBuffer {
    //    val cipher = this.cipher
    //    cipher.init(Cipher.ENCRYPT_MODE, key)
    //    val outputBlockSize = cipher.getOutputSize(0)
    //    val blockSize = outputBlockSize - 11
    //    val length = data.remaining()
    //    val needingSize = needingBlock(length, blockSize) * outputBlockSize
    //    val result = ByteBuffer.allocate(needingSize)
    //    var curOffset = 0
    //    var blockCount = 0
    //    while (curOffset < length) {
    //        cipher.doFinal(data, curOffset, blockSize, result, blockCount * outputBlockSize)
    //        curOffset += blockSize
    //        blockCount++
    //    }
    //    return result
    //}

    override fun encrypt(key: Key, data: InputStream): ByteArray {
        return super.encrypt(key, data)
    }

    override fun encrypt(key: Key, data: InputStream, output: OutputStream): Long {
        val cipher = this.cipher
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val outputBlockSize = cipher.getOutputSize(0)
        val blockSize = outputBlockSize - 11
        val buffer = ByteArray(outputBlockSize)
        while (data.available() > 0) {
            cipher.doFinal(data, curOffset, blockSize, buffer)
            output.write(buffer)
            curOffset += blockSize
        }
        return needingSize.toLong()
    }

    override fun decrypt(key: Key, data: ByteArray, offset: Int, length: Int): ByteArray {
        return super.decrypt(key, data, offset, length)
    }

    override fun decrypt(key: Key, data: ByteArray, offset: Int, length: Int, output: OutputStream): Long {
        return super.decrypt(key, data, offset, length, output)
    }

    override fun decrypt(key: Key, data: ByteBuffer): ByteBuffer {
        return super.decrypt(key, data)
    }

    override fun decrypt(key: Key, data: InputStream): ByteArray {
        return super.decrypt(key, data)
    }

    override fun decrypt(key: Key, data: InputStream, output: OutputStream): Long {
        return super.decrypt(key, data, output)
    }

    //override fun encrypt(key: Any, data: ByteArray, offset: Int, length: Int): ByteArray {
    //    val cipher = getCipher()
    //    cipher.init(Cipher.ENCRYPT_MODE, key.toPublicKey())
    //    return getBytes(data, cipher, encryptBlockSize)
    //}
    //
    //override fun decrypt(key: Any, data: ByteArray, offset: Int, length: Int): ByteArray {
    //    val cipher = getCipher()
    //    cipher.init(Cipher.DECRYPT_MODE, key.toPrivateKey())
    //    return getBytes(data, cipher, decryptBlockSize)
    //}
    //
    //fun withBlockSize(
    //    encryptBlockSize: Int,
    //    decryptBlockSize: Int
    //): RsaCodec {
    //    return RsaCodec(encryptBlockSize, decryptBlockSize)
    //}
    //
    //private fun Any.toPublicKey(): RSAPublicKey {
    //    return when (this) {
    //        is RSAPublicKey -> this
    //        is ByteArray -> keyFactory.generatePublic(X509EncodedKeySpec(this)) as RSAPublicKey
    //        is CharSequence -> keyFactory.generatePublic(X509EncodedKeySpec(this.toBytes())) as RSAPublicKey
    //        else -> throw UnsupportedOperationException("Unsupported public key: $this")
    //    }
    //}
    //
    //private fun Any.toPrivateKey(): RSAPrivateKey {
    //    return when (this) {
    //        is RSAPrivateKey -> this
    //        is ByteArray -> keyFactory.generatePrivate(PKCS8EncodedKeySpec(this)) as RSAPrivateKey
    //        is CharSequence -> keyFactory.generatePrivate(PKCS8EncodedKeySpec(this.toBytes())) as RSAPrivateKey
    //        else -> throw UnsupportedOperationException("Unsupported private key: $this")
    //    }
    //}
    //
    //private fun getBytes(encryptedData: ByteArray, cipher: Cipher, maxDecryptBlock: Int): ByteArray {
    //    val outputStream = ByteArrayOutputStream()
    //    var offset = 0
    //    do {
    //        if (encryptedData.size - offset >= maxDecryptBlock) {
    //            outputStream.write(cipher.doFinal(encryptedData, offset, maxDecryptBlock))
    //            offset += maxDecryptBlock
    //        } else {
    //            outputStream.write(cipher.doFinal(encryptedData, offset, encryptedData.size - offset))
    //            offset = encryptedData.size
    //        }
    //    } while (offset < encryptedData.size)
    //    return outputStream.toByteArray()
    //}
    //
    //companion object {
    //
    //    const val DEFAULT_KEY_SIZE = 2048
    //    const val DEFAULT_DECRYPT_BLOCK = 256
    //    const val DEFAULT_ENCRYPT_BLOCK = 245
    //
    //    private val keyPairGen: KeyPairGenerator = KeyPairGenerator.getInstance(CodecAlgorithm.RSA.name)
    //    private val keyFactory: KeyFactory = KeyFactory.getInstance(CodecAlgorithm.RSA.name)
    //}
}