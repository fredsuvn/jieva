package xyz.srclab.common.codec

import xyz.srclab.common.codec.rsa.RsaCodec
import xyz.srclab.common.codec.sm2.Sm2Codec
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.Mac

/**
 * Codec interface, represents a type of codec way.
 *
 * @see DigestCodec
 * @see HmacCodec
 * @see CipherCodec
 * @see CodecAlgorithm
 * @see RsaCodec
 * @see Sm2Codec
 */
interface Codec {

    val algorithm: CodecAlgorithm

    companion object {

        @JvmStatic
        fun simpleDigest(algorithm: CodecAlgorithm, digest: MessageDigest): DigestCodec {
            return DigestCodec.simpleImpl(algorithm, digest)
        }

        @JvmStatic
        fun simpleHmac(algorithm: CodecAlgorithm, hmac: Mac): HmacCodec {
            return HmacCodec.simpleImpl(algorithm, hmac)
        }

        @JvmStatic
        fun simpleCipher(algorithm: CodecAlgorithm, cipher: Cipher): CipherCodec {
            return CipherCodec.simpleImpl(algorithm, cipher)
        }

        @JvmStatic
        fun newDigestBuilder(): DigestCodec.Builder {
            return DigestCodec.newBuilder()
        }

        @JvmStatic
        fun newHmacBuilder(): HmacCodec.Builder {
            return HmacCodec.newBuilder()
        }

        @JvmStatic
        fun newCipherBuilder(): CipherCodec.Builder {
            return CipherCodec.newBuilder()
        }

        @JvmStatic
        fun md2(): DigestCodec {
            return DigestCodec.md2()
        }

        @JvmStatic
        fun md5(): DigestCodec {
            return DigestCodec.md5()
        }

        @JvmStatic
        fun sha1(): DigestCodec {
            return DigestCodec.sha1()
        }

        @JvmStatic
        fun sha256(): DigestCodec {
            return DigestCodec.sha256()
        }

        @JvmStatic
        fun sha384(): DigestCodec {
            return DigestCodec.sha384()
        }

        @JvmStatic
        fun sha512(): DigestCodec {
            return DigestCodec.sha512()
        }

        @JvmStatic
        fun hmacMd5(): HmacCodec {
            return HmacCodec.hmacMd5()
        }

        @JvmStatic
        fun hmacSha1(): HmacCodec {
            return HmacCodec.hmacSha1()
        }

        @JvmStatic
        fun hmacSha256(): HmacCodec {
            return HmacCodec.hmacSha256()
        }

        @JvmStatic
        fun hmacSha384(): HmacCodec {
            return HmacCodec.hmacSha384()
        }

        @JvmStatic
        fun hmacSha512(): HmacCodec {
            return HmacCodec.hmacSha512()
        }

        @JvmStatic
        fun aes(): CipherCodec {
            return CipherCodec.aes()
        }

        @JvmStatic
        fun rsa(): CipherCodec {
            return CipherCodec.rsa()
        }

        @JvmStatic
        fun sm2(): CipherCodec {
            return CipherCodec.sm2()
        }
    }
}