package xyz.srclab.common.codec

import xyz.srclab.annotations.concurrent.ThreadSafe
import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME

/**
 * Codec interface.
 *
 * Cipher is thread-safe.
 *
 * @author sunqian
 */
@ThreadSafe
interface Codec {

    @get:JvmName("name")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val name: String
}