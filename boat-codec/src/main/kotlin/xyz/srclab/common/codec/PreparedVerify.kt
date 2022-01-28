package xyz.srclab.common.codec

import xyz.srclab.common.base.remainingLength
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * Verify operation with prepared data.
 */
interface PreparedVerify {

    fun verify(sign: ByteArray): Boolean {
        return verify(sign, 0)
    }

    fun verify(sign: ByteArray, offset: Int): Boolean {
        return verify(sign, offset, remainingLength(sign.size, offset))
    }

    fun verify(sign: ByteArray, offset: Int, length: Int): Boolean

    fun verify(sign: ByteBuffer): Boolean

    fun verify(sign: InputStream): Boolean {
        return verify(sign.readBytes())
    }
}