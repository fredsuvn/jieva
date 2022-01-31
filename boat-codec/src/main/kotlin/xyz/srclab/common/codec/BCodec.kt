@file:JvmName("BCodec")

package xyz.srclab.common.codec

import java.security.MessageDigest
import java.security.Signature
import javax.crypto.Cipher
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

fun sm2(): CipherCodec {
    return CipherCodec.sm2()
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

fun sm3WithSm2(): SignCodec {
    return SignCodec.sm3WithSm2()
}