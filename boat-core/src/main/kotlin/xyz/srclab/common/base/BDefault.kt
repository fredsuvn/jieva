@file:JvmName("BDefault")

package xyz.srclab.common.base

import xyz.srclab.common.Boat
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * Default charset: UTF-8.
 */
@JvmField
val DEFAULT_CHARSET: Charset = StandardCharsets.UTF_8

/**
 * Default io buffer size: 8 * 1024.
 */
const val DEFAULT_IO_BUFFER_SIZE: Int = 8 * 1024

/**
 * Default serial version UID.
 */
@JvmField
val DEFAULT_SERIAL_VERSION = Boat.SERIAL_VERSION