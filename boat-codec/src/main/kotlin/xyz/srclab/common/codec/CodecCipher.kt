package xyz.srclab.common.codec

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME

/**
 * Codec cipher.
 *
 * Note cipher may not thread-safe, do not share it.
 *
 * @author sunqian
 */
interface CodecCipher {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val name: String
        @JvmName("name") get
}