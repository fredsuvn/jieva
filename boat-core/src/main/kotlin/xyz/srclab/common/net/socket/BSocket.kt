@file:JvmName("BSocket")

package xyz.srclab.common.net.socket

import java.net.ServerSocket

/**
 * Returns available socket port.
 */
fun availableSocketPort(): Int {
    val serverSocket = ServerSocket(0)
    val port = serverSocket.localPort
    serverSocket.close()
    return port
}