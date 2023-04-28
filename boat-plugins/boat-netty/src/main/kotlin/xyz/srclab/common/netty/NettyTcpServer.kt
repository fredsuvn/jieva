package xyz.srclab.common.netty

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import xyz.srclab.common.base.asType
import xyz.srclab.common.collect.newMap
import xyz.srclab.common.net.availableSocketAddress
import xyz.srclab.common.net.tcp.TcpServer
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.util.function.Supplier

/**
 * Simple Netty server implementation for [TcpServer].
 */
open class NettyTcpServer @JvmOverloads constructor(
    override val bindAddress: SocketAddress,
    childChannelHandlers: List<Supplier<ChannelHandler>>,
    options: Map<ChannelOption<*>, Any?> = newMap(ChannelOption.SO_BACKLOG, 128),
    childOptions: Map<ChannelOption<*>, Any?> = newMap(ChannelOption.SO_KEEPALIVE, true),
    channelHandler: ChannelHandler? = null
) : TcpServer {

    private val bossGroup = NioEventLoopGroup()
    private val workerGroup = NioEventLoopGroup()
    private val bootstrap = ServerBootstrap()
    private var channelFuture: ChannelFuture? = null

    init {
        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel::class.java)
        if (channelHandler !== null) {
            bootstrap.handler(channelHandler)
        }
        bootstrap.childHandler(object : ChannelInitializer<SocketChannel>() {
            override fun initChannel(ch: SocketChannel) {
                val pipeline = ch.pipeline()
                for (childChannelHandler in childChannelHandlers) {
                    pipeline.addLast(childChannelHandler.get())
                }
            }
        })
        for (option in options) {
            bootstrap.option(option.key, option.value.asType())
        }
        for (option in childOptions) {
            bootstrap.childOption(option.key, option.value.asType())
        }
        //val f = bootstrap.bind(port).channel().closeFuture()
        //f.sync()
    }

    @JvmOverloads
    constructor(
        port: Int,
        childChannelHandlers: List<Supplier<ChannelHandler>>,
        options: Map<ChannelOption<*>, Any?> = newMap(ChannelOption.SO_BACKLOG, 128),
        childOptions: Map<ChannelOption<*>, Any?> = newMap(ChannelOption.SO_KEEPALIVE, true),
        channelHandler: ChannelHandler? = null
    ) : this(InetSocketAddress(port), childChannelHandlers, options, childOptions, channelHandler)

    @JvmOverloads
    constructor(
        childChannelHandlers: List<Supplier<ChannelHandler>>,
        options: Map<ChannelOption<*>, Any?> = newMap(ChannelOption.SO_BACKLOG, 128),
        childOptions: Map<ChannelOption<*>, Any?> = newMap(ChannelOption.SO_KEEPALIVE, true),
        channelHandler: ChannelHandler? = null
    ) : this(availableSocketAddress(), childChannelHandlers, options, childOptions, channelHandler)

    override fun start() {
        channelFuture = bootstrap.bind(bindAddress).channel().closeFuture()
    }

    override fun stop(immediately: Boolean) {
        workerGroup.shutdownGracefully()
        bossGroup.shutdownGracefully()
    }

    override fun await() {
        val channelFuture = this.channelFuture
        if (channelFuture !== null) {
            channelFuture.sync()
        }
    }
}