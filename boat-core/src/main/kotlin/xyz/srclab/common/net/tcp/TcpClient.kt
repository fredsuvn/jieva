package xyz.srclab.common.net.tcp

/**
 * Server with TCP/IP.
 */
interface TcpClient {

    /**
     * Connects to server.
     */
    fun connect()

    /**
     * Disconnects to server. Same as `disconnect(false)`.
     */
    fun disconnect() {
        disconnect(false)
    }

    /**
     * Disconnects to server.
     */
    fun disconnect(immediately: Boolean)

    /**
     * Blocks current thread until the server has stopped.
     */
    fun await()
}