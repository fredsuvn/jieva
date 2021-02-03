package xyz.srclab.common.codec.rsa

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
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

/**
 * RSA key pair.
 *
 * @author sunqian
 */
class RsaKeyPair(rawPublicKey: RSAPublicKey, rawPrivateKey: RSAPrivateKey) :
    AbstractCodecKeyPair<RSAPublicKey, RSAPrivateKey>(rawPublicKey, rawPrivateKey) {
    override fun publicKeyToBytes(publicKey: RSAPublicKey): ByteArray? {
        return publicKey.encoded
    }

    override fun privateKeyToBytes(privateKey: RSAPrivateKey): ByteArray? {
        return privateKey.encoded
    }
}