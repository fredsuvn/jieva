package xyz.srclab.common.io

import java.io.OutputStream
import java.nio.ByteBuffer

/**
 * Makes [ByteBuffer] as [OutputStream].
 */
open class ByteBufferOutputStream(
    private val source: ByteBuffer
) : OutputStream() {

    override fun write(b: Int) {
        source.put(b.toByte())
    }

    override fun write(b: ByteArray) {
        source.put(b)
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        source.put(b, off, len)
    }
}