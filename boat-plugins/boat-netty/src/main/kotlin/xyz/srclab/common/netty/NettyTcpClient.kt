package xyz.srclab.common.netty

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import xyz.srclab.common.base.asTyped
import xyz.srclab.common.net.parseInetSocketAddress
import xyz.srclab.common.net.tcp.TcpClient
import java.io.InputStream
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.util.function.Supplier

/**
 * Simple Netty Client implementation for [TcpClient].
 *
 * Note netty implementation doesn't support `send` and `receive` methods, do these operation in channel handlers.
 */
open class NettyTcpClient @JvmOverloads constructor(
    remoteAddress: SocketAddress,
    channelHandlers: List<Supplier<ChannelHandler>>,
    options: Map<ChannelOption<*>, Any?> = emptyMap()
) : TcpClient {

    private val group = NioEventLoopGroup()
    private val bootstrap = Bootstrap()

    //private var _localPort: Int = -1
    //
    //val localPort: Int
    //    get() = _localPort

    init {
        bootstrap.group(group).channel(NioSocketChannel::class.java)
            .remoteAddress(remoteAddress)
        bootstrap.handler(object : ChannelInitializer<SocketChannel>() {
            override fun initChannel(ch: SocketChannel) {
                val pipeline = ch.pipeline()
                for (channelHandler in channelHandlers) {
                    pipeline.addLast(channelHandler.get())
                }
            }
        })
        for (option in options) {
            bootstrap.option(option.key, option.value.asTyped())
        }
        //val f = bootstrap.bind(port).channel().closeFuture()
        //f.sync()
    }

    @JvmOverloads
    constructor(
        address: CharSequence,
        channelHandlers: List<Supplier<ChannelHandler>>,
        options: Map<ChannelOption<*>, Any?> = emptyMap()
    ) : this(address.parseInetSocketAddress(), channelHandlers, options)

    override fun connect() {
        bootstrap.connect()
    }

    override fun disconnect(immediately: Boolean) {
        group.shutdownGracefully()
    }

    override fun send(data: ByteBuffer) {
        throw UnsupportedOperationException("Do this operation in ChannelHandler of Netty.")
    }

    override fun receive(dest: ByteBuffer): Int {
        throw UnsupportedOperationException("Do this operation in ChannelHandler of Netty.")
    }

    override fun receive(): InputStream {
        throw UnsupportedOperationException("Do this operation in ChannelHandler of Netty.")
    }

    override fun receiveBytes(): ByteArray {
        throw UnsupportedOperationException("Do this operation in ChannelHandler of Netty.")
    }
}