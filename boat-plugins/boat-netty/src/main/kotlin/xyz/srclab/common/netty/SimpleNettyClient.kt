package xyz.srclab.common.netty

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import xyz.srclab.common.base.asTyped
import xyz.srclab.common.net.socket.parseInetSocketAddress
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.util.function.Supplier

/**
 * Simple Netty Client.
 */
open class SimpleNettyClient @JvmOverloads constructor(
    val remoteAddress: SocketAddress,
    channelHandlers: List<Supplier<ChannelHandler>>,
    options: Map<ChannelOption<*>, Any?> = emptyMap()
) {

    private val group = NioEventLoopGroup()
    private val bootstrap = Bootstrap()

    private var _localPort: Int = -1

    val localPort: Int
        get() = _localPort

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
        host: CharSequence,
        port: Int,
        channelHandlers: List<Supplier<ChannelHandler>>,
        options: Map<ChannelOption<*>, Any?> = emptyMap()
    ) : this(InetSocketAddress(host.toString(), port), channelHandlers, options)

    @JvmOverloads
    constructor(
        address: CharSequence,
        channelHandlers: List<Supplier<ChannelHandler>>,
        options: Map<ChannelOption<*>, Any?> = emptyMap()
    ) : this(address.parseInetSocketAddress(), channelHandlers, options)

    fun start() {
        bootstrap.connect()
    }

    fun startSync() {
        bootstrap.connect().channel().closeFuture().sync()
    }

    fun close() {
        group.shutdownGracefully()
    }
}