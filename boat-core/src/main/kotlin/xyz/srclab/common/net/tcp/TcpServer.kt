package xyz.srclab.common.net.tcp

/**
 * Server with TCP/IP.
 */
interface TcpServer {

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