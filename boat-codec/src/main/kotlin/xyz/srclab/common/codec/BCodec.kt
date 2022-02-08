@file:JvmName("BCodec")

package xyz.srclab.common.codec

import xyz.srclab.common.base.DEFAULT_IO_BUFFER_SIZE
import xyz.srclab.common.codec.gm.SM2Codec
import xyz.srclab.common.io.unclose
import java.io.InputStream
import java.io.OutputStream
import java.security.MessageDigest
import java.security.Signature
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.Mac

fun simpleDigest(algorithm: CodecAlgorithm, digest: MessageDigest): DigestCodec {
    return DigestCodec.simpleImpl(algorithm, digest)
}

fun simpleHmac(algorithm: CodecAlgorithm, hmac: Mac): HmacCodec {
    return HmacCodec.simpleImpl(algorithm, hmac)
}

fun simpleCipher(algorithm: CodecAlgorithm, cipher: Cipher): CipherCodec {
    return CipherCodec.simpleImpl(algorithm, cipher)
}

fun simpleSign(algorithm: CodecAlgorithm, signature: Signature, digestCodec: DigestCodec): SignCodec {
    return SignCodec.simpleImpl(algorithm, signature, digestCodec)
}

fun newDigestBuilder(): DigestCodec.Builder {
    return DigestCodec.newBuilder()
}

fun newHmacBuilder(): HmacCodec.Builder {
    return HmacCodec.newBuilder()
}

fun newCipherBuilder(): CipherCodec.Builder {
    return CipherCodec.newBuilder()
}

fun newSignBuilder(): SignCodec.Builder {
    return SignCodec.newBuilder()
}

fun md2(): DigestCodec {
    return DigestCodec.md2()
}

fun md5(): DigestCodec {
    return DigestCodec.md5()
}

fun sha1(): DigestCodec {
    return DigestCodec.sha1()
}

fun sha256(): DigestCodec {
    return DigestCodec.sha256()
}

fun sha384(): DigestCodec {
    return DigestCodec.sha384()
}

fun sha512(): DigestCodec {
    return DigestCodec.sha512()
}

fun sm3(): DigestCodec {
    return DigestCodec.sm3()
}

fun hmacMd5(): HmacCodec {
    return HmacCodec.hmacMd5()
}

fun hmacSha1(): HmacCodec {
    return HmacCodec.hmacSha1()
}

fun hmacSha256(): HmacCodec {
    return HmacCodec.hmacSha256()
}

fun hmacSha384(): HmacCodec {
    return HmacCodec.hmacSha384()
}

fun hmacSha512(): HmacCodec {
    return HmacCodec.hmacSha512()
}

fun aes(): CipherCodec {
    return CipherCodec.aes()
}

fun rsa(): CipherCodec {
    return CipherCodec.rsa()
}

@JvmOverloads
fun sm2(mode: Int = SM2Codec.MODE_C1C3C2): CipherCodec {
    return CipherCodec.sm2(mode)
}

fun sm4(): CipherCodec {
    return CipherCodec.sm4()
}

fun sha1WithRsa(): SignCodec {
    return SignCodec.sha1WithRsa()
}

fun sha256WithRsa(): SignCodec {
    return SignCodec.sha256WithRsa()
}

fun sha256WithSm2(): SignCodec {
    return SignCodec.sha256WithSm2()
}

fun sm3WithSm2(): SignCodec {
    return SignCodec.sm3WithSm2()
}

fun Int.isEncryptMode(): Boolean {
    return this == Cipher.ENCRYPT_MODE || this == Cipher.WRAP_MODE
}

/**
 * Encrypts/Decrypts from [from] to [to], return length of output (not input).
 */
fun Cipher.encryptAfterInit(from: InputStream, to: OutputStream, mode: Int): Long {
    val wrapDest = to.unclose()
    if (mode.isEncryptMode()) {
        val cop = CipherOutputStream(wrapDest, this)
        from.copyTo(cop)
        cop.close()
    } else {
        val cip = CipherInputStream(from, this)
        cip.copyTo(wrapDest)
        cip.close()
    }
    return wrapDest.count
}

/**
 * Updates from given [InputStream].
 */
@JvmOverloads
fun MessageDigest.updateFromStream(from: InputStream, bufferSize: Int = DEFAULT_IO_BUFFER_SIZE) {
    val buffer = ByteArray(bufferSize)
    var c = from.read(buffer)
    while (c >= 0) {
        this.update(buffer, 0, c)
        c = from.read(buffer)
    }
}

/**
 * Updates from given [InputStream].
 */
@JvmOverloads
fun Mac.updateFromStream(from: InputStream, bufferSize: Int = DEFAULT_IO_BUFFER_SIZE) {
    val buffer = ByteArray(bufferSize)
    var c = from.read(buffer)
    while (c >= 0) {
        this.update(buffer, 0, c)
        c = from.read(buffer)
    }
}

/**
 * Updates from given [InputStream].
 */
@JvmOverloads
fun Signature.updateFromStream(from: InputStream, bufferSize: Int = DEFAULT_IO_BUFFER_SIZE) {
    val buffer = ByteArray(bufferSize)
    var c = from.read(buffer)
    while (c >= 0) {
        this.update(buffer, 0, c)
        c = from.read(buffer)
    }
}