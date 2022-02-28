package xyz.srclab.common.net.tcp

import java.nio.ByteBuffer

/**
 * Listener for TCP connection.
 */
interface TcpListener {

    fun onOpen(context: TcpContext)

    fun onReceive(context: TcpContext, data: ByteBuffer)

    fun onClose(context: TcpContext)
}