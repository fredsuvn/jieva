package xyz.srclab.common.net

import xyz.srclab.common.net.tcp.TcpServer
import xyz.srclab.common.net.udp.UdpServer
import java.net.InetSocketAddress
import java.net.SocketAddress

/**
 * Base net server interface.
 *
 * @see TcpServer
 * @see UdpServer
 */
interface NetServer {

    val bindAddress: SocketAddress

    val port: Int
        get() = (bindAddress as InetSocketAddress).port

    /**
     * Starts server.
     */
    fun start()

    /**
     * Stops server. Same as `stop(false)`.
     */
    fun stop() {
        stop(false)
    }

    /**
     * Stops server.
     */
    fun stop(immediately: Boolean)

    /**
     * Blocks current thread until the server has stopped.
     */
    fun await()
}