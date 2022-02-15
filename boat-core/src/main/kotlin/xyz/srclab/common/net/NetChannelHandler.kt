package xyz.srclab.common.net

import java.nio.ByteBuffer

/**
 * Handler for net channel.
 */
interface NetChannelHandler<T : NetContext> {

    fun onOpen(context: T)

    fun onReceive(context: T, data: ByteBuffer)

    fun onClose(context: T)
}