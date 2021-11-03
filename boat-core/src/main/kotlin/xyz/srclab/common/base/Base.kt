package xyz.srclab.common.base

import xyz.srclab.common.base.ThreadSafePolicy.*
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * Base class loader.
 */
object BoatClassLoader : ClassLoader() {

    @JvmOverloads
    fun loadClass(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size): Class<*> {
        return super.defineClass(null, bytes, offset, length)
    }

    fun loadClass(inputStream: InputStream): Class<*> {
        return loadClass(inputStream.readBytes())
    }

    fun loadClass(byteBuffer: ByteBuffer): Class<*> {
        return super.defineClass(null, byteBuffer, null)
    }
}

/**
 * Used to specify policy of thread-safe: [NONE], [SYNCHRONIZED] or [THREAD_LOCAL].
 */
enum class ThreadSafePolicy {

    /**
     * None thread-safe policy.
     */
    NONE,

    /**
     * Synchronization policy.
     */
    SYNCHRONIZED,

    /**
     * Thread-local policy.
     */
    THREAD_LOCAL
}