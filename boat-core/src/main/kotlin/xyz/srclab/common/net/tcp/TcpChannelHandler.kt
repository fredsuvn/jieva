package xyz.srclab.common.net.tcp

import java.nio.ByteBuffer

/**
 * Handler for TCP channel.
 */
interface TcpChannelHandler {

    fun onOpen(context: TcpContext)

    fun onReceive(context: TcpContext, data: ByteBuffer)

    fun onClose(context: TcpContext)
}