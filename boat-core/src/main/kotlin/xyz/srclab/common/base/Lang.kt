package xyz.srclab.common.base

import xyz.srclab.common.base.JumpStatement.*
import xyz.srclab.common.base.ThreadSafePolicy.*
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * Jump statement for process control: [CONTINUE], [BREAK] or [RETURN].
 */
enum class JumpStatement {

    /**
     * Stops the current execution of the iteration and proceeds to the next iteration in the loop.
     */
    CONTINUE,

    /**
     * Stops loop.
     */
    BREAK,

    /**
     * Stops the current execution of the method and returns.
     */
    RETURN,
    ;

    fun isContinue(): Boolean {
        return this == CONTINUE
    }

    fun isBreak(): Boolean {
        return this == BREAK
    }

    fun isReturn(): Boolean {
        return this == RETURN
    }
}

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