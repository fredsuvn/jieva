@file:JvmName("Codecs")

package xyz.srclab.common.codec

import xyz.srclab.common.codec.rsa.RsaCodec
import xyz.srclab.common.codec.sm2.Sm2Codec
import xyz.srclab.common.codec.sm2.Sm2Params
import xyz.srclab.common.lang.toBytes
import java.security.Key
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@JvmName("secretKey")
fun ByteArray.toSecretKey(algorithm: String): SecretKey {
    return SecretKeySpec(this, algorithm)
}

@JvmName("secretKey")
fun CharSequence.toSecretKey(algorithm: String): SecretKey {
    return this.toBytes().toSecretKey(algorithm)
}

@JvmName("secretKey")
fun Any.toSecretKey(algorithm: String): SecretKey {
    return when (this) {
        is SecretKey -> this
        is ByteArray -> toSecretKey(algorithm)
        is CharSequence -> toSecretKey(algorithm)
        else -> throw UnsupportedOperationException("Unsupported key: $this")
    }
}

@JvmName("codecKey")
fun Any.toCodecKey(algorithm: String): Key {
    if (this is Key) {
        return this
    }
    return this.toSecretKey(algorithm)
}

/**
 * Create a new [SecretKey] with [algorithm] and min key size ([minKeySize]).
 * If [minKeySize] <= 0, return simply like:
 *
 * ```
 * return new SecretKeySpec(bytes, algorithm)
 * ```
 */
@JvmName("secretKey")
fun ByteArray.toSecretKey(algorithm: String, minKeySize: Int): SecretKey {
    if (minKeySize <= 0) {
        return SecretKeySpec(this, algorithm)
    }
    val random = SecureRandom.getInstance(CodecAlgorithm.SHA1PRNG_NAME)
    random.setSeed(this)
    val keyGenerator = KeyGenerator.getInstance(algorithm)
    keyGenerator.init(minKeySize, random)
    val secretKey = keyGenerator.generateKey()
    return SecretKeySpec(secretKey.encoded, algorithm)
}

@JvmName("codecAlgorithm")
fun CharSequence.toCodecAlgorithm(): CodecAlgorithm {
    return CodecAlgorithm.forName(this)
}

@JvmName("codecAlgorithm")
fun CharSequence.toCodecAlgorithm(type: CodecAlgorithmType): CodecAlgorithm {
    return CodecAlgorithm.forName(this, type)
}

@JvmName("encodeCodec")
fun CharSequence.toEncodeCodec(): EncodeCodec {
    return EncodeCodec.withAlgorithm(this)
}

@JvmName("encodeCodec")
fun CodecAlgorithm.toEncodeCodec(): EncodeCodec {
    return EncodeCodec.withAlgorithm(this)
}

@JvmOverloads
@JvmName("digestCodec")
fun CharSequence.toDigestCodec(
    digest: () -> MessageDigest = { MessageDigest.getInstance(this.toString()) }
): DigestCodec {
    return DigestCodec.withAlgorithm(this, digest)
}

@JvmOverloads
@JvmName("digestCodec")
fun CodecAlgorithm.toDigestCodec(
    digest: () -> MessageDigest = { MessageDigest.getInstance(this.name) }
): DigestCodec {
    return DigestCodec.withAlgorithm(this, digest)
}

@JvmOverloads
@JvmName("macCodec")
fun CharSequence.toMacCodec(
    mac: () -> Mac = { Mac.getInstance(this.toString()) }
): MacCodec {
    return MacCodec.withAlgorithm(this, mac)
}

@JvmOverloads
@JvmName("macCodec")
fun CodecAlgorithm.toMacCodec(
    mac: () -> Mac = { Mac.getInstance(this.name) }
): MacCodec {
    return MacCodec.withAlgorithm(this, mac)
}

@JvmOverloads
@JvmName("cipherCodec")
fun CharSequence.toCipherCodec(
    cipher: () -> Cipher = { Cipher.getInstance(this.toString()) }
): CipherCodec {
    return CipherCodec.withAlgorithm(this, cipher)
}

@JvmOverloads
@JvmName("cipherCodec")
fun CodecAlgorithm.toCipherCodec(
    cipher: () -> Cipher = { Cipher.getInstance(this.name) }
): CipherCodec {
    return CipherCodec.withAlgorithm(this, cipher)
}

fun plainCodec(): EncodeCodec {
    return EncodeCodec.plain()
}

fun hexCodec(): EncodeCodec {
    return EncodeCodec.hex()
}

fun base64Codec(): EncodeCodec {
    return EncodeCodec.base64()
}

fun md2Codec(): DigestCodec {
    return DigestCodec.md2()
}

fun md5Codec(): DigestCodec {
    return DigestCodec.md5()
}

fun sha1Codec(): DigestCodec {
    return DigestCodec.sha1()
}

fun sha256Codec(): DigestCodec {
    return DigestCodec.sha256()
}

fun sha384Codec(): DigestCodec {
    return DigestCodec.sha384()
}

fun sha512Codec(): DigestCodec {
    return DigestCodec.sha512()
}

fun hmacMd5Codec(): MacCodec {
    return MacCodec.hmacMd5()
}

fun hmacSha1Codec(): MacCodec {
    return MacCodec.hmacSha1()
}

fun hmacSha256Codec(): MacCodec {
    return MacCodec.hmacSha256()
}

fun hmacSha384Codec(): MacCodec {
    return MacCodec.hmacSha384()
}

fun hmacSha512Codec(): MacCodec {
    return MacCodec.hmacSha512()
}

@JvmOverloads
fun aesCodec(
    cipher: () -> Cipher = { Cipher.getInstance(CodecAlgorithm.AES_NAME) }
): CipherCodec {
    return CipherCodec.aes(cipher)
}

@JvmOverloads
fun rsaCodec(
    cipher: () -> Cipher = { Cipher.getInstance(CodecAlgorithm.RSA_NAME) }
): RsaCodec {
    return CipherCodec.rsa(cipher)
}

@JvmOverloads
fun rsaCodec(
    encryptBlockSize: Int,
    decryptBlockSize: Int,
    cipher: () -> Cipher = { Cipher.getInstance(CodecAlgorithm.RSA_NAME) }
): RsaCodec {
    return CipherCodec.rsa(encryptBlockSize, decryptBlockSize, cipher)
}

@JvmOverloads
fun sm2Codec(sm2Params: Sm2Params = Sm2Params.DEFAULT): Sm2Codec {
    return CipherCodec.sm2(sm2Params)
}

@JvmName("hexString")
fun ByteArray.toHexString(): String {
    return hexCodec().encodeToString(this)
}

@JvmName("hexString")
fun CharSequence.toHexString(): String {
    return hexCodec().encodeToString(this)
}

@JvmName("base64String")
fun ByteArray.toBase64String(): String {
    return base64Codec().encodeToString(this)
}

@JvmName("base64String")
fun CharSequence.toBase64String(): String {
    return base64Codec().encodeToString(this)
}