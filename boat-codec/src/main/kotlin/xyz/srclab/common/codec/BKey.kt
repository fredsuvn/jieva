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
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

private val cache = Cache.ofWeak<String, Any>()

fun CharSequence.base64PassphraseToKey(algorithm: CharSequence, size: Int): SecretKey {
    return this.to8BitBytes().deBase64().passphraseToKey(algorithm, size)
}

@JvmOverloads
fun CharSequence.passphraseToKey(algorithm: CharSequence, size: Int, charset: Charset = DEFAULT_CHARSET): SecretKey {
    return this.byteArray(charset).passphraseToKey(algorithm, size)
}

fun ByteArray.passphraseToKey(algorithm: CharSequence, size: Int): SecretKey {
    //val random = SecureRandom.getInstance(CodecAlgorithm.SHA1PRNG_NAME)
    //random.setSeed(this)
    val algorithmString = algorithm.toString()
    val keyGenerator = cache.getOrLoad("passphraseToKey[$algorithmString]") {
        KeyGenerator.getInstance(algorithmString)
    } as KeyGenerator
    synchronized(keyGenerator) {
        keyGenerator.init(size, SecureRandom(this))
        return keyGenerator.generateKey()
    }
    //return SecretKeySpec(secretKey.encoded, algorithmString)
}

fun generateKeyPair(algorithm: CharSequence, size: Int): KeyPair {
    val algorithmString = algorithm.toString()
    val keyPairGenerator = cache.getOrLoad("generateKeyPair[$algorithmString]") {
        KeyPairGenerator.getInstance(algorithmString)
    } as KeyPairGenerator
    synchronized(keyPairGenerator) {
        keyPairGenerator.initialize(size, SecureRandom())
        return keyPairGenerator.generateKeyPair()
    }
}