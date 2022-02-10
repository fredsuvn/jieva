package xyz.srclab.common.netty

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import xyz.srclab.common.base.asTyped
import xyz.srclab.common.collect.newMap
import xyz.srclab.common.net.socket.availableSocketPort
import java.util.function.Supplier

/**
 * Simple Netty server.
 */
open class SimpleNettyServer(
    channelHandler: ChannelHandler?,
    childChannelHandlers: List<Supplier<ChannelHandler>>,
    options: Map<ChannelOption<*>, Any?>,
    childOptions: Map<ChannelOption<*>, Any?>,
    val port: Int
) {

    private val bossGroup = NioEventLoopGroup()
    private val workerGroup = NioEventLoopGroup()
    private val bootstrap = ServerBootstrap()

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
            bootstrap.option(option.key, option.value.asTyped())
        }
        for (option in childOptions) {
            bootstrap.childOption(option.key, option.value.asTyped())
        }
        //val f = bootstrap.bind(port).channel().closeFuture()
        //f.sync()
    }

    @JvmOverloads
    constructor(
        channelInboundHandlers: List<Supplier<ChannelHandler>>,
        options: Map<ChannelOption<*>, Any?>,
        childOptions: Map<ChannelOption<*>, Any?>,
        port: Int = availableSocketPort()
    ) : this(null, channelInboundHandlers, options, childOptions, port)

    @JvmOverloads
    constructor(
        channelInboundHandlers: List<Supplier<ChannelHandler>>,
        port: Int = availableSocketPort()
    ) : this(
        null,
        channelInboundHandlers,
        newMap(ChannelOption.SO_BACKLOG, 128),
        newMap(ChannelOption.SO_KEEPALIVE, true),
        port
    )

    fun start() {
        bootstrap.bind(port).channel().closeFuture()
    }

    fun startSync() {
        bootstrap.bind(port).channel().closeFuture().sync()
    }

    fun close() {
        workerGroup.shutdownGracefully()
        bossGroup.shutdownGracefully()
    }
}