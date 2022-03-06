@file:JvmName("BDefault")

package xyz.srclab.common.base

import xyz.srclab.common.Boat
import java.nio.charset.Charset

/**
 * Returns default charset, generally it is _UTF-8_.
 */
@JvmName("charset")
fun defaultCharset(): Charset = utf8()

/**
 * Returns default buffer size, generally it is _8 * 1024_
 */
@JvmName("bufferSize")
fun defaultBufferSize(): Int = 8 * 1024

/**
 * Returns default serial version.
 */
@JvmName("serialVersion")
fun defaultSerialVersion(): Long = Boat.SERIAL_VERSION