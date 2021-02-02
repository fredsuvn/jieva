package xyz.srclab.common.serialize.json

import java.nio.ByteBuffer

/**
 * @author sunqian
 */
internal object Utils {
    fun bufferToBytes(buffer: ByteBuffer): ByteArray {
        val bytesArray = ByteArray(buffer.remaining())
        buffer[bytesArray, 0, bytesArray.size]
        return bytesArray
    }
}