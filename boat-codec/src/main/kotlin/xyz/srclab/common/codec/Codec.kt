package xyz.srclab.common.codec

import xyz.srclab.annotations.concurrent.ThreadSafe
import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME

/**
 * Codec interface, represents a type of codec way.
 *
 * @author sunqian
 *
 * @see EncodeCodec
 * @see DigestCodec
 * @see MacCodec
 * @see CipherCodec
 * @see CodecAlgorithm
 */
@ThreadSafe
interface Codec {

    @get:JvmName("name")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val name: String
}