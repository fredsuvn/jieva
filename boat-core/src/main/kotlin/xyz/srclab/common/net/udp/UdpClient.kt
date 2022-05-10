package xyz.srclab.common.net.udp

import xyz.srclab.common.base.toByteArray
import xyz.srclab.common.base.defaultBufferSize
import xyz.srclab.common.base.defaultCharset
import xyz.srclab.common.base.remainingLength
import xyz.srclab.common.io.getBytes
import java.io.InputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.nio.charset.Charset

/**
 * Client for UDP.
 */
interface UdpClient {

    fun send(address: SocketAddress, data: ByteArray) {
        send(address, data, 0)
    }

    fun send(address: SocketAddress, data: ByteArray, offset: Int = 0) {
        send(address, data, offset, remainingLength(data.size, offset))
    }

    fun send(
        address: SocketAddress,
        data: ByteArray,
        offset: Int = 0,
        length: Int = remainingLength(data.size, offset)
    ) {
        val buffer = ByteBuffer.wrap(data, offset, length)
        send(address, buffer)
    }

    fun send(address: SocketAddress, data: ByteBuffer)

    fun send(address: SocketAddress, data: InputStream)

    fun send(address: SocketAddress, data: String) {
        send(address, data, defaultCharset())
    }

    fun send(address: SocketAddress, data: String, charset: Charset) {
        send(address, data.toByteArray(charset))
    }

    companion object {

        @JvmOverloads
        @JvmStatic
        fun bioClient(bufferSize: Int = defaultBufferSize()): UdpClient {
            return BIOUdpClient(bufferSize)
        }

        @JvmStatic
        fun nioClient(): UdpClient {
            return NIOUdpClient()
        }

        private class BIOUdpClient(
            bufferSize: Int
        ) : UdpClient {

            private val socket = DatagramSocket().let {
                it.sendBufferSize = bufferSize
                it
            }

            override fun send(address: SocketAddress, data: ByteArray, offset: Int, length: Int) {
                val dataPkg = DatagramPacket(data, offset, length, address)
                socket.send(dataPkg)
            }

            override fun send(address: SocketAddress, data: ByteBuffer) {
                send(address, data.getBytes())
            }

            override fun send(address: SocketAddress, data: InputStream) {
                send(address, data.readBytes())
            }
        }

        private class NIOUdpClient : UdpClient {

            private val channel = DatagramChannel.open()

            override fun send(address: SocketAddress, data: ByteArray, offset: Int, length: Int) {
                channel.send(ByteBuffer.wrap(data, offset, length), address)
            }

            override fun send(address: SocketAddress, data: ByteBuffer) {
                channel.send(data, address)
            }

            override fun send(address: SocketAddress, data: InputStream) {
                send(address, data.readBytes())
            }
        }
    }
}