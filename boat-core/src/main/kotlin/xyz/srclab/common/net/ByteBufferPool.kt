package xyz.srclab.common.net

import xyz.srclab.common.base.DEFAULT_IO_BUFFER_SIZE
import xyz.srclab.common.base.epochMillis
import xyz.srclab.common.io.newByteBuffer
import xyz.srclab.common.net.PooledByteBuffer.Companion.asPooledByteBuffer
import java.nio.ByteBuffer

/**
 * Pool for byte buffer.
 *
 * Use [getBuffer] to get a pooled buffer, and [releaseBuffer] to return.
 */
interface ByteBufferPool {

    /**
     * Returns an unused [ByteBuffer].
     */
    fun getBuffer(): PooledByteBuffer

    /**
     * Releases given [buffer] which come from [getBuffer].
     */
    fun releaseBuffer(buffer: PooledByteBuffer)

    companion object {

        /**
         * Returns a simple non-thread-safe [ByteBufferPool].
         */
        @JvmOverloads
        @JvmStatic
        fun simpleByteBufferPool(
            coreSize: Int = 10,
            maxSize: Int = 20,
            bufferSize: Int = DEFAULT_IO_BUFFER_SIZE,
            bufferKeepAliveMillis: Long = 60_000,
        ): ByteBufferPool {
            return SimpleByteBufferPool(coreSize, maxSize, bufferKeepAliveMillis, bufferSize)
        }

        private class SimpleByteBufferPool(
            private val coreSize: Int,
            private val maxSize: Int,
            private val bufferKeepAliveMillis: Long,
            private val bufferSize: Int
        ) : ByteBufferPool {

            private var coreNode: BufferNode
            private var extNode: BufferNode? = null
            private var extSize: Int = 0
            private var lastUseExtTime: Long = -1

            init {
                //Using direct buffer in initializing.
                coreNode = BufferNode(newByteBuffer(bufferSize, true))
                val head = coreNode
                var i = 1
                while (i < coreSize) {
                    val newNode = BufferNode(newByteBuffer(bufferSize, true))
                    coreNode.next = newNode
                    coreNode = newNode
                    i++
                }
                coreNode.next = head
            }

            override fun getBuffer(): PooledByteBuffer {
                val buffer = getBuffer0()
                val buf = buffer.asByteBuffer()
                buf.clear()
                return buffer
            }

            private fun getBuffer0(): PooledByteBuffer {
                val cur = coreNode
                if (!cur.inUse) {
                    cur.inUse = true
                    coreNode = cur.next!!
                    tryReleaseExt()
                    return cur
                }
                coreNode = coreNode.next!!
                while (coreNode !== cur) {
                    if (!coreNode.inUse) {
                        val node = coreNode
                        node.inUse = true
                        coreNode = node.next!!
                        tryReleaseExt()
                        return node
                    }
                    coreNode = coreNode.next!!
                }

                //Using heap buffer for ext nodes
                if (extSize == 0) {
                    val newNode = BufferNode(newByteBuffer(bufferSize, false), true)
                    extNode = newNode
                    extNode!!.next = newNode
                    extSize = 1
                    lastUseExtTime = epochMillis()
                    return newNode
                }
                val curExt = extNode!!
                if (!curExt.inUse) {
                    curExt.inUse = true
                    extNode = curExt.next!!
                    lastUseExtTime = epochMillis()
                    return curExt
                }
                extNode = extNode!!.next!!
                while (extNode !== curExt) {
                    if (!extNode!!.inUse) {
                        val node = extNode!!
                        node.inUse = true
                        extNode = node.next!!
                        lastUseExtTime = epochMillis()
                        return node
                    }
                    extNode = extNode!!.next!!
                }
                if (extSize < maxSize - coreSize) {
                    val newNode = BufferNode(newByteBuffer(bufferSize, false), true)
                    newNode.next = extNode!!.next
                    extNode!!.next = newNode
                    lastUseExtTime = epochMillis()
                    extSize++
                    return newNode
                }
                lastUseExtTime = epochMillis()

                //Using heap buffer if node not enough.
                val buffer = newByteBuffer(bufferSize, false)
                return buffer.asPooledByteBuffer()
            }

            private fun tryReleaseExt() {
                if (extSize > 0 && epochMillis() - lastUseExtTime > bufferKeepAliveMillis) {
                    extNode = null
                    extSize = 0
                }
            }

            override fun releaseBuffer(buffer: PooledByteBuffer) {
                if (buffer is BufferNode) {
                    buffer.inUse = false
                }
            }

            private data class BufferNode(
                val buffer: ByteBuffer,
                var inUse: Boolean = false,
                var next: BufferNode? = null
            ) : PooledByteBuffer {
                override fun asByteBuffer(): ByteBuffer = buffer
            }
        }
    }
}