package xyz.srclab.common.codec

import xyz.srclab.annotations.concurrent.ThreadSafe
import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME

/**
 * Codec cipher.
 *
 * Cipher is thread-safe.
 *
 * @author sunqian
 */
@ThreadSafe
interface CodecCipher {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val name: String
        @JvmName("name") get
}