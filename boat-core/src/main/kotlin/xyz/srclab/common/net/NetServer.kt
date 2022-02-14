package xyz.srclab.common.net

import xyz.srclab.common.net.tcp.TcpServer
import xyz.srclab.common.net.udp.UdpServer

/**
 * Base net server interface.
 *
 * @see TcpServer
 * @see UdpServer
 */
interface NetServer {

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