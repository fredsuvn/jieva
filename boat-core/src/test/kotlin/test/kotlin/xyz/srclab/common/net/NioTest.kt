package test.kotlin.xyz.srclab.common.net

import org.testng.annotations.Test
import xyz.srclab.common.base.getBytes
import xyz.srclab.common.base.sleep
import xyz.srclab.common.net.socket.availableLocalhost
import xyz.srclab.common.run.runAsync
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

class NioTest {

    @Test(enabled = false)
    fun testNio() {
        val address = availableLocalhost()
        runAsync { startServer(address) }
        sleep(1000)
        val client = SocketChannel.open(address)
        while (true) {
            val buffer = ByteBuffer.wrap("hello".getBytes())
            client.write(buffer)
            sleep(500)
        }
    }

    private fun startServer(address: SocketAddress) {
        val selector = Selector.open()
        val serverSocket = ServerSocketChannel.open()
        println(">>> serverSocket: $serverSocket(${System.identityHashCode(serverSocket)})")
        serverSocket.configureBlocking(false)
        serverSocket.bind(address)
        serverSocket.register(selector, SelectionKey.OP_ACCEPT)
        while (true) {
            val count = selector.select()
            if (count > 0) {
                val keys = selector.selectedKeys()
                val it = keys.iterator()
                while (it.hasNext()) {
                    val next = it.next()
                    it.remove()
                    if (next.isAcceptable) {
                        val server = next.channel() as ServerSocketChannel
                        println(">>> Acceptable server: $server(${System.identityHashCode(server)})")
                        val client = server.accept()
                        client.configureBlocking(false)
                        println(">>> Acceptable client: $client(${System.identityHashCode(client)})")
                        client.register(selector, SelectionKey.OP_READ)
                    }
                    if (next.isReadable) {
                        val client = next.channel() as SocketChannel
                        println(">>> Readable: $client(${System.identityHashCode(client)})")
                        //channel.register(selector, SelectionKey.OP_READ)
                    }
                }
            }
            sleep(1000)
        }
    }
}