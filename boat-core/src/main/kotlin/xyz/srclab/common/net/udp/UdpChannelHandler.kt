package xyz.srclab.common.net.udp

import java.net.SocketAddress
import java.nio.ByteBuffer

/**
 * Handler for UDP channel.
 */
interface UdpChannelHandler {

    fun onReceive(remoteAddress: SocketAddress, data: ByteBuffer)
}