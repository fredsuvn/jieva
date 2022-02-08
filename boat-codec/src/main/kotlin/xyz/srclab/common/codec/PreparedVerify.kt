package xyz.srclab.common.codec

import xyz.srclab.common.base.remainingLength
import xyz.srclab.common.io.toBytes
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

    fun verify(sign: ByteBuffer): Boolean {
        if (sign.hasArray()) {
            val startPos = sign.position()
            val array = sign.array()
            val arrayOffset = sign.arrayOffset() + startPos
            sign.position(sign.limit())
            return verify(array, arrayOffset, sign.remaining())
        }
        return verify(sign.toBytes())
    }

    fun verify(sign: InputStream): Boolean {
        return verify(sign.readBytes())
    }

    companion object {

        @JvmStatic
        fun PreparedVerify.toSync(lock: Any): PreparedVerify {
            return SyncPreparedVerify(this, lock)
        }

        private class SyncPreparedVerify(
            private val preparedVerify: PreparedVerify,
            private val lock: Any
        ) : PreparedVerify {

            override fun verify(sign: ByteArray): Boolean {
                return synchronized(lock) {
                    preparedVerify.verify(sign)
                }
            }

            override fun verify(sign: ByteArray, offset: Int): Boolean {
                return synchronized(lock) {
                    preparedVerify.verify(sign, offset)
                }
            }

            override fun verify(sign: ByteArray, offset: Int, length: Int): Boolean {
                return synchronized(lock) {
                    preparedVerify.verify(sign, offset, length)
                }
            }

            override fun verify(sign: ByteBuffer): Boolean {
                return synchronized(lock) {
                    preparedVerify.verify(sign)
                }
            }

            override fun verify(sign: InputStream): Boolean {
                return synchronized(lock) {
                    preparedVerify.verify(sign)
                }
            }
        }
    }
}