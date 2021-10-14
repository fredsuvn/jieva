package xyz.srclab.common.io

import org.apache.commons.io.input.CharSequenceReader
import java.io.Reader

/**
 * Wraps chars as [Reader].
 */
open class CharsReaderWrapper @JvmOverloads constructor(
    chars: CharSequence,
    offset: Int = 0,
    length: Int = chars.length - offset
) : CharSequenceReader(chars, offset, offset + length) {

    @JvmOverloads
    constructor(
        chars: CharArray,
        offset: Int = 0,
        length: Int = chars.size - offset
    ) : this(String(chars), offset, length)
}