package xyz.srclab.common.net;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.base.FsArray;
import xyz.srclab.common.collect.FsCollect;
import xyz.srclab.common.data.FsData;
import xyz.srclab.common.io.FsIO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/**
 * Implementation of {@link FsTcpServer} with {@link ServerSocket}.
 *
 * @author fredsuvn
 */
public class SocketTcpServer implements FsTcpServer<ServerSocket> {

    private final int port;
    private final FsNetServerHandler<ServerSocket> serverHandler;
    private final List<FsNetChannelHandler< ?>> channelHandlers;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final @Nullable Consumer<ServerSocket> config;
    private final IntFunction<ByteBuffer> bufferGenerator;

    private final Set<ChannelImpl> channels = ConcurrentHashMap.newKeySet();

    private ServerSocket serverSocket;

    private SocketTcpServer(Builder builder) {
        this.port = builder.port;
        this.serverHandler = builder.serverHandler;
        this.channelHandlers = FsCollect.immutableList(builder.channelHandlers);
        this.threadPoolExecutor = builder.threadPoolExecutor;
        this.config = builder.config;
        this.bufferGenerator = builder.bufferGenerator == null ? ByteBuffer::allocateDirect : builder.bufferGenerator;
    }

    @Override
    public void start(boolean block) {

    }

    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public void close(@Nullable Duration timeout) {

    }

    @Override
    public void closeNow() {

    }

    @Override
    public ServerSocket getServer() {
        return serverSocket;
    }

    // private void startAcceptLoop() throws Exception {
    //     serverSocket = buildServerSocket();
    //     threadPoolExecutor.execute(() -> {
    //         while (true) {
    //             Socket socket;
    //             try {
    //                 socket = serverSocket.accept();
    //             } catch (IOException e) {
    //                 for (FsNetServerHandler<ServerSocket> serverHandler : serverHandlers) {
    //                     serverHandler.onStart(serverSocket, e);
    //                 }
    //                 continue;
    //             }
    //
    //         }
    //     });
    // }

    private void loopSocket() {
        for (ChannelImpl channel : channels) {
        }
    }

    private void doChannel(ChannelImpl channel) {
        byte[] bytes = null;
        try {
            bytes = FsIO.availableBytes(channel.in);
            if (FsArray.isEmpty(bytes)) {
                for (FsNetChannelHandler<?> channelHandler : channelHandlers) {
                    channelHandler.onLoop(channel, false);
                }
                return;
            }
        } catch (Exception e) {
            serverHandler.onException(channel, e);
            return;
        }
        if (channel.buffer == null) {
            channel.buffer = ByteBuffer.wrap(bytes);
        } else if (channel.buffer.capacity() - channel.buffer.limit() >= bytes.length){
            channel.buffer.position(channel.buffer.limit());
            channel.buffer.put(bytes);
            channel.buffer.flip();
        } else {
              ByteBuffer  buffer = bufferGenerator.apply(bytes.length + channel.buffer.remaining());
                buffer.put(channel.buffer);
                buffer.put(bytes);
                buffer.flip();
            channel.buffer = buffer;
        }
        Object message = channel.buffer;
        for (FsNetChannelHandler< ?> channelHandler : channelHandlers) {
            FsNetChannelHandler< Object> handler = Fs.as(channelHandler);
            try {
                Object result = handler.onMessage(channel, message);
                if (result == null) {
                    break;
                }
                message = result;
            } catch (Throwable e) {
                serverHandler.onException(channel, e);
                return;
            }
        }
        for (FsNetChannelHandler< ?> channelHandler : channelHandlers) {
            FsNetChannelHandler< Object> handler = Fs.as(channelHandler);
            try {
                handler.onLoop(channel, true);
            } catch (Throwable e) {
                serverHandler.onException(channel, e);
                return;
            }
        }
        if (channel.buffer.hasRemaining()) {
            channel.buffer.compact();
        }
    }

    private ServerSocket buildServerSocket() throws Exception {
        ServerSocket server = new ServerSocket(port);
        if (config != null) {
            config.accept(server);
        }
        return server;
    }

    private static final class ChannelImpl implements FsNetChannel {

        private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(new byte[0]);

        private final Socket socket;
        private final InputStream in;
        private volatile @Nullable OutputStream out;
        private @Nullable ByteBuffer buffer;

        private ChannelImpl(Socket socket) {
            this.socket = socket;
            this.in = getIn(socket);
        }

        @Override
        public InetAddress getRemoteAddress() {
            return socket.getInetAddress();
        }

        @Override
        public int getRemotePort() {
            return socket.getPort();
        }

        @Override
        public InetAddress getLocalAddress() {
            return socket.getLocalAddress();
        }

        @Override
        public int getLocalPort() {
            return socket.getLocalPort();
        }

        @Override
        public boolean isOpened() {
            return !socket.isClosed() && socket.isBound() && socket.isConnected();
        }

        @Override
        public void close(@Nullable Duration timeout) {
            closeNow();
        }

        @Override
        public void closeNow() {
            try {
                socket.close();
            } catch (IOException e) {
                throw new FsNetException(e);
            }
        }

        @Override
        public void send(FsData data) {
            setOut();
            FsIO.readBytesTo(data.toInputStream(), out);
        }

        @Override
        public void flush() {
            setOut();
            try {
                out.flush();
            } catch (IOException e) {
                throw new FsNetException(e);
            }
        }

        @Override
        public ByteBuffer getBuffer() {
            return buffer == null ? EMPTY_BUFFER : buffer;
        }

        @Override
        public Object getSource() {
            return socket;
        }

        private InputStream getIn(Socket socket) {
            try {
                return socket.getInputStream();
            } catch (IOException e) {
                throw new FsNetException(e);
            }
        }

        private void setOut() {
            if (out == null) {
                synchronized (this) {
                    if (out == null) {
                        try {
                            out = socket.getOutputStream();
                        } catch (IOException e) {
                            throw new FsNetException(e);
                        }
                    }
                }
            }
        }
    }

    public static class Builder {
        private int port;
        private FsNetServerHandler<ServerSocket> serverHandler;
        private List<FsNetChannelHandler<?>> channelHandlers;
        private ThreadPoolExecutor threadPoolExecutor;
        private @Nullable Consumer<ServerSocket> config;
        private @Nullable IntFunction<ByteBuffer> bufferGenerator;
    }
}
