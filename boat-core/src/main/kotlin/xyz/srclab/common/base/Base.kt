package xyz.srclab.common.base

import xyz.srclab.common.base.Next.*
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * Base class loader of `boat`.
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
 * Represents next operation, usually used for the object which delegate performance to a group of handlers.
 *
 * For three values:
 *
 * * If one of handlers returns [CONTINUE], means that handler failed to convert and suggests continue to next handler;
 * * If returns [BREAK], means that handler failed to convert and suggests break handler chain;
 * * If returns [COMPLETE], means that handler success and complete conversation;
 */
enum class Next {

    /**
     * Represents current handler failed to convert and suggests continue to next handler.
     */
    CONTINUE,

    /**
     * Represents current handler failed to convert and suggests break handler chain.
     */
    BREAK,

    /**
     * Represents current handler success and complete conversation.
     */
    COMPLETE,
}

/**
 * Used to specify policy of thread-safe.
 */
enum class ThreadSafePolicy {

    /**
     * Uses none thread-safe policy.
     */
    NONE,

    /**
     * Uses synchronization policy.
     */
    SYNCHRONIZED,

    /**
     * Uses thread local policy.
     */
    THREAD_LOCAL
}