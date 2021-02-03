package xyz.srclab.common.codec

import javax.crypto.SecretKey
import xyz.srclab.common.codec.CodecBytes
import javax.crypto.spec.SecretKeySpec
import xyz.srclab.common.codec.CodecAlgorithmConstants
import java.lang.IllegalStateException
import xyz.srclab.common.codec.AsymmetricCipher
import java.security.KeyPairGenerator
import java.security.spec.X509EncodedKeySpec
import java.security.spec.PKCS8EncodedKeySpec
import javax.crypto.NoSuchPaddingException
import java.security.NoSuchAlgorithmException
import xyz.srclab.common.codec.CodecAlgorithm
import java.io.IOException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.BadPaddingException
import xyz.srclab.common.codec.AbstractCodecKeyPair
import xyz.srclab.common.codec.CodecKeyPair
import xyz.srclab.common.codec.ReversibleCipher
import xyz.srclab.common.codec.CodecImpl
import xyz.srclab.common.codec.CodecKeySupport
import xyz.srclab.common.codec.SymmetricCipherImpl
import xyz.srclab.common.codec.DigestCipher
import xyz.srclab.common.codec.DigestCipherImpl
import xyz.srclab.common.codec.HmacDigestCipher
import xyz.srclab.common.codec.HmacDigestCipherImpl
import xyz.srclab.common.codec.CodecAlgorithmSupport
import java.util.NoSuchElementException
import xyz.srclab.common.codec.CodecAlgorithmSupport.CodecAlgorithmImpl
import xyz.srclab.common.codec.CodecCipher
import java.security.MessageDigest

/**
 * @author sunqian
 */
object CodecAlgorithmConstants {
    const val PLAIN = "PLAIN"
    const val HEX = "HEX"
    const val BASE64 = "BASE64"
    const val AES = "AES"
    const val MD2 = "MD2"
    const val MD5 = "MD5"
    const val SHA1 = "SHA-1"
    const val SHA256 = "SHA-256"
    const val SHA384 = "SHA-384"
    const val SHA512 = "SHA-512"
    const val HMAC_MD5 = "HmacMD5"
    const val HMAC_SHA1 = "HmacSHA1"
    const val HMAC_SHA256 = "HmacSHA256"
    const val HMAC_SHA384 = "HmacSHA384"
    const val HMAC_SHA512 = "HmacSHA512"
    const val RSA = "RSA"
    const val SM2 = "SM2"
    const val SHA1PRNG = "SHA1PRNG"
}