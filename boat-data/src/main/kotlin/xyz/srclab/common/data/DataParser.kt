package xyz.srclab.common.data

import xyz.srclab.common.base.remLength
import xyz.srclab.common.io.asInputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer

/**
 * Parser to convert or parse between java object and [T].
 *
 * @see DataElement
 */
interface DataParser<T : DataElement> {

    fun toString(obj: Any?): String

    fun toBytes(obj: Any?): ByteArray

    fun toInputStream(obj: Any?): InputStream {
        return toBytes(obj).asInputStream()
    }

    fun toByteBuffer(obj: Any?): ByteBuffer {
        return ByteBuffer.wrap(toBytes(obj))
    }

    fun toDataElement(obj: Any?): T

    fun write(obj: Any?, output: OutputStream): Long {
        return toInputStream(obj).copyTo(output)
    }

    fun parse(chars: CharSequence): T

    fun parse(bytes: ByteArray): T {
        return parse(bytes, 0)
    }

    fun parse(bytes: ByteArray, offset: Int): T {
        return parse(bytes, offset, remLength(bytes.size, offset))
    }

    fun parse(bytes: ByteArray, offset: Int, length: Int): T

    fun parse(input: InputStream): T

    fun parse(byteBuffer: ByteBuffer): T
}