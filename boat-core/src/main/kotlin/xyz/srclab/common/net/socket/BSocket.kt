@file:JvmName("BSocket")

package xyz.srclab.common.net.socket

import java.net.InetSocketAddress
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

fun CharSequence.parseInetSocketAddress(): InetSocketAddress {
    val split = this.toString().split(":")
    if (split.size != 2) {
        throw IllegalArgumentException("Wrong InetSocketAddress format: $this")
    }
    return InetSocketAddress(split[0], split[1].toInt())
}