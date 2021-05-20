@file:JvmName("CodecKeys")

package xyz.srclab.common.codec

import xyz.srclab.common.lang.toBytes
import java.security.Key
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

fun ByteArray.toSecretKey(algorithm: String): SecretKey {
    return SecretKeySpec(this, algorithm)
}

fun CharSequence.toSecretKey(algorithm: String): SecretKey {
    return this.toBytes().toSecretKey(algorithm)
}

fun Any.toSecretKey(algorithm: String): SecretKey {
    return when (this) {
        is SecretKey -> this
        is ByteArray -> toSecretKey(algorithm)
        is CharSequence -> toSecretKey(algorithm)
        else -> throw UnsupportedOperationException("Unsupported key: $this")
    }
}

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