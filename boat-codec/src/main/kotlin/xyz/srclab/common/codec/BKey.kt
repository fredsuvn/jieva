@file:JvmName("BKey")

package xyz.srclab.common.codec

import xyz.srclab.common.base.DEFAULT_CHARSET
import xyz.srclab.common.base.byteArray
import xyz.srclab.common.base.deBase64
import xyz.srclab.common.base.to8BitBytes
import xyz.srclab.common.cache.Cache
import java.nio.charset.Charset
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Provider
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

private val cache = Cache.ofWeak<String, Any>()

@JvmOverloads
fun CharSequence.base64PassphraseToKey(
    algorithm: CharSequence, size: Int, provider: Provider? = null
): SecretKey {
    return this.to8BitBytes().deBase64().passphraseToKey(algorithm, size, provider)
}

@JvmOverloads
fun CharSequence.passphraseToKey(
    algorithm: CharSequence, size: Int, charset: Charset = DEFAULT_CHARSET, provider: Provider? = null
): SecretKey {
    return this.byteArray(charset).passphraseToKey(algorithm, size, provider)
}

@JvmOverloads
fun ByteArray.passphraseToKey(algorithm: CharSequence, size: Int, provider: Provider? = null): SecretKey {
    //val random = SecureRandom.getInstance(CodecAlgorithm.SHA1PRNG_NAME)
    //random.setSeed(this)
    val algorithmString = algorithm.toString()
    val keyGenerator = cache.getOrLoad("passphraseToKey[$algorithmString]") {
        if (provider === null)
            KeyGenerator.getInstance(algorithmString)
        else
            KeyGenerator.getInstance(algorithmString, provider)
    } as KeyGenerator
    val secureRandom = SecureRandom.getInstance(CodecAlgorithm.SHA1PRNG_NAME)
    synchronized(keyGenerator) {
        keyGenerator.init(size, secureRandom)
        return keyGenerator.generateKey()
    }
    //return SecretKeySpec(secretKey.encoded, algorithmString)
}

@JvmOverloads
fun generateKeyPair(algorithm: CharSequence, size: Int, provider: Provider? = null): KeyPair {
    val algorithmString = algorithm.toString()
    val keyPairGenerator = cache.getOrLoad("generateKeyPair[$algorithmString]") {
        if (provider === null)
            KeyPairGenerator.getInstance(algorithmString)
        else
            KeyPairGenerator.getInstance(algorithmString, provider)
    } as KeyPairGenerator
    val secureRandom = SecureRandom.getInstance(CodecAlgorithm.SHA1PRNG_NAME)
    synchronized(keyPairGenerator) {
        keyPairGenerator.initialize(size, secureRandom)
        return keyPairGenerator.generateKeyPair()
    }
}