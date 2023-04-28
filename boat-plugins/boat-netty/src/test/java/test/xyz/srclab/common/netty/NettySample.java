package test.xyz.srclab.common.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import xyz.srclab.common.io.BBuffer;
import xyz.srclab.common.net.BSocket;
import xyz.srclab.common.netty.BNetty;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NettySample {

    public static void main(String[] args) throws Exception {
        //EventLoopGroup bossGroup = new NioEventLoopGroup();
        //EventLoopGroup workerGroup = new NioEventLoopGroup();
        //ServerBootstrap bootstrap = new ServerBootstrap();
        //bootstrap.group(bossGroup, workerGroup)
        //    .channel(NioServerSocketChannel.class)
        //    //.option(ChannelOption.SO_BACKLOG, 100)
        //    //.handler(new LoggingHandler(LogLevel.INFO))
        //    .childHandler(new ChannelInitializer<SocketChannel>() {
        //        @Override
        //        public void initChannel(SocketChannel ch) throws Exception {
        //            ChannelPipeline p = ch.pipeline();
        //            p.addLast(new EchoServerHandler());
        //        }
        //    });
        //bootstrap.bind(18888).channel().closeFuture();

        Executor executor = Executors.newFixedThreadPool(16);
        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.bind(BSocket.parseInetSocketAddress("localhost:18888"));
        Selector selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);
        executor.execute(() -> {
            while (true) {
                try {
                    selector.select();
                    Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                    while (keys.hasNext()) {
                        SelectionKey key = keys.next();
                        if (key.isAcceptable()) {
                            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                            java.nio.channels.SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                        }
                        if (key.isReadable()) {
                            java.nio.channels.SocketChannel channel = (java.nio.channels.SocketChannel) key.channel();
                            executor.execute(() -> {
                                try {
                                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                                    channel.read(buffer);
                                    buffer.flip();
                                    System.out.print("[" + Thread.currentThread() + "]" + (System.currentTimeMillis() / 1000) + ": ");
                                    System.out.println("nio.channelRead: " + BBuffer.getString(buffer));
                                    Thread.sleep(5000);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                        keys.remove();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread.sleep(1000);
        Socket client1 = new Socket();
        Socket client2 = new Socket();
        client1.connect(BSocket.parseInetSocketAddress("localhost:18888"));
        client2.connect(BSocket.parseInetSocketAddress("localhost:18888"));
        Thread.sleep(1000);
        client1.getOutputStream().write("Hello1, ".getBytes());
        client2.getOutputStream().write("Hello2, ".getBytes());
        Thread.sleep(1000);
        client1.getOutputStream().write("Client1!".getBytes());
        client2.getOutputStream().write("Client2!".getBytes());
    }

    @ChannelHandler.Sharable
    public static class EchoServerHandler extends ChannelInboundHandlerAdapter {

        //接收请求后的处理类
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            System.out.print("[" + Thread.currentThread() + "]" + (System.currentTimeMillis() / 1000) + ": ");
            System.out.println("EchoServerHandler.channelRead: " + BNetty.getString(byteBuf));
            Thread.sleep(5000);
        }

        //读取完成后处理方法
        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            System.out.println("EchoServerHandler.channelReadComplete");
            //ctx.flush();
        }

        //异常捕获处理方法
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            System.out.println("EchoServerHandler.exceptionCaught");
            // Close the connection when an exception is raised.
        }
    }
}
