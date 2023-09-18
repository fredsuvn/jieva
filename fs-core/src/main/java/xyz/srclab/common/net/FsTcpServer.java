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
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/**
 * TCP/IP server interface, server endpoint of {@link FsNetEndpoint}.
 * <p>
 * A {@link FsTcpServer} implementation should support following types of handler:
 * <ul>
 *     <li>
 *         one {@link FsNetServerHandler}: to deal with server actions;
 *     </li>
 *     <li>
 *         a list of {@link FsNetClientHandler}: to deal with actions for each connection;
 *     </li>
 * </ul>
 *
 * @author fredsuvn
 */
public interface FsTcpServer extends FsNetEndpoint {

    /**
     * Returns new builder for this interface.
     * The returned builder is based on {@link ServerSocket}.
     */
    static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Starts this server and wait until the server has been closed.
     * This method is equivalent to {@link #start(boolean)}:
     * <pre>
     *     start(true);
     * </pre>
     */
    default void start() {
        start(true);
    }

    /**
     * Starts this server.
     * If given {@code block} is true, this method will block current thread until the server has been closed.
     *
     * @param block whether block current thread
     */
    void start(boolean block);

    /**
     * Builder for {@link FsTcpServer}.
     */
    class Builder {

        private static final int CREATED = 0;
        private static final int OPENED = 1;
        private static final int CLOSED = 2;
        private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(new byte[0]);
        private static final FsNetServerHandler EMPTY_SERVER_HANDLER = new FsNetServerHandler() {
        };

        private int port = 0;
        private int maxConnection = 50;
        private @Nullable InetAddress hostAddress;
        private @Nullable FsNetServerHandler serverHandler;
        private final List<FsNetChannelHandler<?>> channelHandlers = new LinkedList<>();
        private @Nullable IntFunction<ByteBuffer> bufferGenerator;
        private @Nullable Executor executor;
        private int bufferSize = FsIO.IO_BUFFER_SIZE;
        private @Nullable Consumer<ServerSocket> socketConfig;

        /**
         * Sets server port, maybe 0 to get an available one from system.
         */
        public Builder port(int port) {
            this.port = port;
            return this;
        }

        /**
         * Sets max connection number.
         */
        public Builder maxConnection(int maxConnection) {
            this.maxConnection = maxConnection;
            return this;
        }

        /**
         * Sets host address.
         */
        public Builder hostAddress(InetAddress hostAddress) {
            this.hostAddress = hostAddress;
            return this;
        }

        /**
         * Sets host name.
         */
        public Builder hostName(String hostName) {
            try {
                this.hostAddress = InetAddress.getByName(hostName);
                return this;
            } catch (UnknownHostException e) {
                throw new FsNetException(e);
            }
        }

        /**
         * Sets server handler.
         */
        public Builder serverHandler(FsNetServerHandler serverHandler) {
            this.serverHandler = serverHandler;
            return this;
        }

        /**
         * Adds channel handler.
         */
        public Builder addChannelHandler(FsNetChannelHandler<?> channelHandler) {
            this.channelHandlers.add(channelHandler);
            return this;
        }

        /**
         * Adds channel handlers.
         */
        public Builder addChannelHandlers(Iterable<FsNetChannelHandler<?>> channelHandlers) {
            FsCollect.toCollection(this.channelHandlers, channelHandlers);
            return this;
        }

        /**
         * Sets byte buffer generator: given an int returns a byte buffer with the int length.
         * The generated buffer's position must be 0, and limit must be capacity.
         */
        public Builder bufferGenerator(IntFunction<ByteBuffer> bufferGenerator) {
            this.bufferGenerator = bufferGenerator;
            return this;
        }

        /**
         * Sets executor, must be of multi-threads.
         */
        public Builder executor(Executor executor) {
            this.executor = executor;
            return this;
        }

        /**
         * Sets reference value of buffer size for byte buffer.
         */
        public Builder bufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        /**
         * Sets other socket config.
         */
        public Builder socketConfig(Consumer<ServerSocket> socketConfig) {
            this.socketConfig = socketConfig;
            return this;
        }

        /**
         * Builds the server.
         */
        public FsTcpServer build() {
            return new SocketTcpServer(this);
        }

        final class SocketTcpServer implements FsTcpServer {

            private final int port;
            private final int maxConnection;
            private final InetAddress hostAddress;
            private final FsNetServerHandler serverHandler;
            private final List<FsNetChannelHandler<?>> channelHandlers;
            private final IntFunction<ByteBuffer> bufferGenerator;
            private final Executor executor;
            private final int bufferSize;
            private final @Nullable Consumer<ServerSocket> socketConfig;

            private final CountDownLatch latch = new CountDownLatch(1);
            private volatile int state = CREATED;
            private final Set<ChannelImpl> channels = ConcurrentHashMap.newKeySet();
            private @Nullable ServerSocket serverSocket;

            private SocketTcpServer(Builder builder) {
                this.port = builder.port;
                this.maxConnection = builder.maxConnection;
                this.hostAddress = builder.hostAddress;
                this.serverHandler = Fs.notNull(builder.serverHandler, EMPTY_SERVER_HANDLER);
                this.channelHandlers = FsCollect.immutableList(builder.channelHandlers);
                if (channelHandlers.isEmpty()) {
                    throw new FsNetException("Channel handlers are empty.");
                }
                this.executor = builder.executor;
                if (executor == null) {
                    throw new FsNetException("Executor is null.");
                }
                this.socketConfig = builder.socketConfig;
                this.bufferGenerator = Fs.notNull(builder.bufferGenerator, ByteBuffer::allocate);
                this.bufferSize = builder.bufferSize;
                if (bufferSize <= 0) {
                    throw new FsNetException("Buffer size must > 0.");
                }
            }

            @Override
            public synchronized void start(boolean block) {
                if (state != CREATED) {
                    throw new FsNetException("The server has been opened or closed.");
                }
                state = OPENED;
                start0();
                if (block) {
                    try {
                        latch.await();
                    } catch (InterruptedException ignored) {
                    }
                }
            }

            @Override
            public synchronized boolean isOpened() {
                return state == OPENED;
            }

            @Override
            public synchronized boolean isClosed() {
                return state == CLOSED;
            }

            @Override
            public synchronized void close(@Nullable Duration timeout) {
                closeNow();
            }

            @Override
            public synchronized void closeNow() {
                if (state != OPENED) {
                    throw new FsNetException("The server has not been opened.");
                }
                if (state == CLOSED) {
                    return;
                }
                state = CLOSED;
                try {
                    serverSocket.close();
                    latch.countDown();
                } catch (IOException e) {
                    throw new FsNetException(e);
                }
            }

            @Override
            public synchronized ServerSocket getSource() {
                if (serverSocket == null) {
                    throw new FsNetException("Server has not been initialized.");
                }
                return serverSocket;
            }

            private void start0() {
                serverSocket = buildServerSocket();
                loopServerSocket();
            }

            private ServerSocket buildServerSocket() {
                try {
                    ServerSocket server;
                    if (hostAddress != null) {
                        server = new ServerSocket(port, maxConnection, hostAddress);
                    } else {
                        server = new ServerSocket(port, maxConnection);
                    }
                    if (socketConfig != null) {
                        socketConfig.accept(server);
                    }
                    return server;
                } catch (Exception e) {
                    throw new FsNetException(e);
                }
            }

            private void loopServerSocket() {
                executor.execute(() -> {
                    while (state == OPENED) {
                        try {
                            Socket socket;
                            ChannelImpl channel;
                            try {
                                socket = serverSocket.accept();
                                channel = new ChannelImpl(socket);
                            } catch (Throwable e) {
                                serverHandler.onException(new FsNetEndpointException(serverSocket, e));
                                continue;
                            }
                            try {
                                channels.add(channel);
                                serverHandler.onOpen(channel);
                            } catch (Throwable e) {
                                serverHandler.onException(channel, e);
                            }
                        } catch (Throwable e) {
                            //Ensure the loop continue
                        }
                    }
                });
                executor.execute(() -> {
                    while (state == OPENED) {
                        if (channels.isEmpty()) {
                            Fs.sleep(1);
                            continue;
                        }
                        for (ChannelImpl c : channels) {
                            try {
                                if (!c.isOpened()) {
                                    c.closeNow();
                                    continue;
                                }
                                doChannel(c);
                            } catch (Throwable e) {
                                //Ensure the loop continue
                            }
                        }
                    }
                });
            }

            private void doChannel(ChannelImpl channel) {
                byte[] bytes;
                try {
                    bytes = FsIO.availableBytes(channel.in);
                } catch (Throwable e) {
                    serverHandler.onException(channel, e);
                    return;
                }
                if (FsArray.isEmpty(bytes)) {
                    try {
                        serverHandler.onLoop(channel, true);
                        return;
                    } catch (Throwable e) {
                        serverHandler.onException(channel, e);
                        return;
                    }
                }
                try {
                    Object message = buildBuffer(channel.buffer, bytes);
                    for (FsNetChannelHandler<?> channelHandler : channelHandlers) {
                        FsNetChannelHandler<Object> handler = Fs.as(channelHandler);
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
                    try {
                        serverHandler.onLoop(channel, true);
                    } catch (Throwable e) {
                        serverHandler.onException(channel, e);
                        return;
                    }
                    channel.buffer = compactBuffer(channel.buffer);
                } catch (Throwable e) {
                    serverHandler.onException(channel, e);
                }
            }

            private ByteBuffer buildBuffer(@Nullable ByteBuffer last, byte[] bytes) {
                if (last == null) {
                    return ByteBuffer.wrap(bytes);
                }
                if (last.capacity() - last.limit() >= bytes.length) {
                    int pos = last.limit();
                    last.limit(last.capacity());
                    last.position(pos);
                    last.put(bytes);
                    last.flip();
                    return last;
                }
                ByteBuffer buffer = bufferGenerator.apply(last.remaining() + bytes.length);
                buffer.put(last);
                buffer.put(bytes);
                buffer.flip();
                return buffer;
            }

            private ByteBuffer compactBuffer(ByteBuffer last) {
                last.compact();
                last.flip();
                if (last.capacity() <= bufferSize || last.remaining() >= bufferSize) {
                    return last;
                }
                ByteBuffer buffer = bufferGenerator.apply(bufferSize);
                buffer.put(last);
                buffer.flip();
                return buffer;
            }

            private final class ChannelImpl implements FsNetChannel {

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
                public boolean isClosed() {
                    return socket.isClosed();
                }

                @Override
                public void close(@Nullable Duration timeout) {
                    closeNow();
                }

                @Override
                public void closeNow() {
                    try {
                        socket.close();
                        serverHandler.onClose(this);
                        channels.remove(this);
                    } catch (Throwable e) {
                        throw new FsNetException(e);
                    }
                }

                @Override
                public void send(FsData data) {
                    try {
                        setOut();
                        FsIO.readBytesTo(data.toInputStream(), out);
                    } catch (Throwable e) {
                        throw new FsNetException(e);
                    }
                }

                @Override
                public void flush() {
                    try {
                        setOut();
                        out.flush();
                    } catch (Throwable e) {
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
                    } catch (Throwable e) {
                        throw new FsNetException(e);
                    }
                }

                private void setOut() throws Exception {
                    if (out == null) {
                        synchronized (this) {
                            if (out == null) {
                                out = socket.getOutputStream();
                            }
                        }
                    }
                }
            }
        }
    }
}
