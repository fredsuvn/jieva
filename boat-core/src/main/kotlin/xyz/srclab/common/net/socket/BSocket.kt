@file:JvmName("BSocket")

package xyz.srclab.common.net.socket

import java.net.ServerSocket

/**
 * Return available socket port.
 */
fun availableSocketPort(): Int {
    val serverSocket = ServerSocket(0)
    val port = serverSocket.localPort
    serverSocket.close()
    return port
}