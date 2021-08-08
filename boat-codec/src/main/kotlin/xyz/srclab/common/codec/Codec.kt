package xyz.srclab.common.codec

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME

/**
 * Codec interface, represents a type of codec way.
 *
 * Note [Codec] **may not thread-safe**.
 *
 * @author sunqian
 *
 * @see EncodeCodec
 * @see DigestCodec
 * @see MacCodec
 * @see CipherCodec
 * @see CodecAlgorithm
 */
interface Codec {

    @get:JvmName("algorithm")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val algorithm: String
}