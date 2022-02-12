package xyz.srclab.common.net.tcp

import xyz.srclab.common.base.DEFAULT_IO_BUFFER_SIZE
import xyz.srclab.common.run.newCachedThreadPoolRunner
import java.net.InetSocketAddress
import java.util.concurrent.Executor

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

    companion object {

        /**
         * Creates a new simple [TcpServer].
         */
        @JvmOverloads
        @JvmStatic
        fun simpleServer(
            bindAddress: InetSocketAddress,
            channelHandler: TcpChannelHandler,
            executor: Executor = newCachedThreadPoolRunner().asExecutor(),
            bufferSize: Int = DEFAULT_IO_BUFFER_SIZE,
            directBuffer: Boolean = false
        ): TcpServer {
            return SimpleTcpServer(bindAddress, channelHandler, executor, bufferSize, directBuffer)
        }
    }
}