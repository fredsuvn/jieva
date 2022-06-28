@file:JvmName("UnsafeBt")

package xyz.srclab.common.base

import sun.misc.Unsafe
import java.lang.reflect.Field
import java.nio.ByteBuffer

private val unsafe: Unsafe = run {
    val unsafeClass = Class.forName("sun.misc.Unsafe")
    val unsafeField: Field = unsafeClass.getDeclaredField("theUnsafe")
    unsafeField.isAccessible = true
    unsafeField.get(null).asType()
}

/**
 * Cleans [this] buffer.
 */
fun ByteBuffer.invokeCleaner() {
    val invokeCleaner = unsafe.javaClass.getMethod("invokeCleaner", ByteBuffer::class.java)
    invokeCleaner.isAccessible = true
    invokeCleaner.invoke(unsafe, this)
}