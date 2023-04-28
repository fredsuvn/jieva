/**
 * Socket utilities.
 */
@file:JvmName("BtSocket")

package xyz.srclab.common.net

import java.net.InetSocketAddress
import java.net.ServerSocket

/**
 * Returns an available socket port.
 */
@JvmName("availablePort")
fun availableSocketPort(): Int {
    val serverSocket = ServerSocket(0)
    val port = serverSocket.localPort
    serverSocket.close()
    return port
}

/**
 * Returns an available socket address.
 */
@JvmName("availableAddress")
fun availableSocketAddress(): InetSocketAddress {
    return InetSocketAddress(availableSocketPort())
}

/**
 * Parses a string like `127.0.0.1:8080` to [InetSocketAddress].
 */
fun CharSequence.parseInetSocketAddress(): InetSocketAddress {
    val split = this.toString().split(":")
    if (split.size != 2) {
        throw IllegalArgumentException("Wrong InetSocketAddress format: $this")
    }
    return InetSocketAddress(split[0], split[1].toInt())
}