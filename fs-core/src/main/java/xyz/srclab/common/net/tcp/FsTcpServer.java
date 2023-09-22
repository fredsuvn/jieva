package xyz.srclab.common.net.tcp;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.collect.FsCollect;
import xyz.srclab.common.data.FsData;
import xyz.srclab.common.io.FsIO;
import xyz.srclab.common.net.FsNetException;
import xyz.srclab.common.net.FsNetServerException;
import xyz.srclab.common.net.FsServerStates;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/**
 * TCP/IP server interface, server endpoint of {@link FsTcpEndpoint}.
 * The implementation should use {@link FsTcpChannel} to represents connection between server and remote endpoints.
 * And should support following types of callback handler:
 * <ul>
 *     <li>
 *         one {@link FsTcpServerHandler}: to callback for server events;
 *     </li>
 *     <li>
 *         a list of {@link FsTcpClientHandler}: to callback for connection events;
 *     </li>
 * </ul>
 *
 * @author fredsuvn
 */
public interface FsTcpServer extends FsTcpEndpoint {

    /**
     * Returns new builder for this interface.
     * The returned builder is based on {@link ServerSocket}.
     */
    static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Starts this server and wait until the server and all connections have been closed.
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
     * If given {@code block} is true, this method will block current thread until
     * the server and all connections have been closed.
     *
     * @param block whether block current thread
     */
    void start(boolean block);

    /**
     * Builder for {@link FsTcpServer}, based on {@link ServerSocket}.
     */
    class Builder {

        private static final byte[] EMPTY_ARRAY = new byte[0];
        private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(EMPTY_ARRAY);
        private static final FsTcpServerHandler EMPTY_SERVER_HANDLER = new FsTcpServerHandler() {
        };

        private int port = 0;
        private int maxConnection = 50;
        private @Nullable InetAddress address;
        private @Nullable FsTcpServerHandler serverHandler;
        private final List<FsTcpChannelHandler<?>> channelHandlers = new LinkedList<>();
        private @Nullable IntFunction<ByteBuffer> bufferGenerator;
        private @Nullable Executor executor;
        private int channelBufferSize = FsIO.IO_BUFFER_SIZE;
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
         * Sets server address.
         */
        public Builder address(InetAddress address) {
            this.address = address;
            return this;
        }

        /**
         * Sets server host name.
         */
        public Builder hostName(String hostName) {
            try {
                this.address = InetAddress.getByName(hostName);
                return this;
            } catch (UnknownHostException e) {
                throw new FsNetException(e);
            }
        }

        /**
         * Sets server handler.
         */
        public Builder serverHandler(FsTcpServerHandler serverHandler) {
            this.serverHandler = serverHandler;
            return this;
        }

        /**
         * Adds channel handler.
         */
        public Builder addChannelHandler(FsTcpChannelHandler<?> channelHandler) {
            this.channelHandlers.add(channelHandler);
            return this;
        }

        /**
         * Adds channel handlers.
         */
        public Builder addChannelHandlers(Iterable<FsTcpChannelHandler<?>> channelHandlers) {
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
         * Sets buffer size of the channel.
         */
        public Builder channelBufferSize(int channelBufferSize) {
            this.channelBufferSize = channelBufferSize;
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

        private static final class SocketTcpServer implements FsTcpServer {

            private final int port;
            private final int maxConnection;
            private final InetAddress address;
            private final FsTcpServerHandler serverHandler;
            private final List<FsTcpChannelHandler<?>> channelHandlers;
            private final IntFunction<ByteBuffer> bufferGenerator;
            private final Executor executor;
            private final int channelBufferSize;
            private final @Nullable Consumer<ServerSocket> socketConfig;

            private final CountDownLatch serverLatch = new CountDownLatch(1);
            private final CountDownLatch connectionLatch = new CountDownLatch(1);
            private final FsServerStates state = new FsServerStates();
            private final Set<ChannelImpl> channels = ConcurrentHashMap.newKeySet();
            private @Nullable ServerSocket serverSocket;

            private SocketTcpServer(Builder builder) {
                this.port = builder.port;
                this.maxConnection = builder.maxConnection;
                this.address = builder.address;
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
                this.channelBufferSize = builder.channelBufferSize;
                if (channelBufferSize <= 0) {
                    throw new FsNetException("Channel buffer size must > 0.");
                }
            }

            @Override
            public synchronized void start(boolean block) {
                if (!state.isCreated()) {
                    throw new FsNetException("The server has been opened or closed.");
                }
                start0();
                state.open();
                if (block) {
                    try {
                        serverLatch.await();
                        connectionLatch.await();
                    } catch (InterruptedException ignored) {
                    }
                }
            }

            @Override
            public InetAddress getAddress() {
                if (serverSocket == null) {
                    throw new FsNetException("Server has not been initialized.");
                }
                return serverSocket.getInetAddress();
            }

            @Override
            public int getPort() {
                if (serverSocket == null) {
                    throw new FsNetException("Server has not been initialized.");
                }
                return serverSocket.getLocalPort();
            }

            @Override
            public SocketAddress getSocketAddress() {
                if (serverSocket == null) {
                    throw new FsNetException("Server has not been initialized.");
                }
                return serverSocket.getLocalSocketAddress();
            }

            @Override
            public boolean isOpened() {
                return state.isOpened();
            }

            @Override
            public boolean isClosed() {
                return state.isClosed();
            }

            @Override
            public synchronized void close(@Nullable Duration timeout) {
                closeNow();
                if (timeout == null) {
                    try {
                        serverLatch.await();
                        connectionLatch.await();
                    } catch (InterruptedException e) {
                        throw new FsNetException(e);
                    }
                } else {
                    try {
                        long n1 = System.currentTimeMillis();
                        long millis = timeout.toMillis();
                        serverLatch.await(millis, TimeUnit.MILLISECONDS);
                        long n2 = System.currentTimeMillis();
                        connectionLatch.await(Math.max(1, millis - (n2 - n1)), TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        throw new FsNetException(e);
                    }
                }
            }

            @Override
            public synchronized void closeNow() {
                if (!state.isOpened() || serverSocket == null) {
                    throw new FsNetException("The server has not been opened.");
                }
                if (state.isClosed()) {
                    return;
                }
                try {
                    serverSocket.close();
                    state.close();
                    serverLatch.countDown();
                } catch (IOException e) {
                    throw new FsNetException(e);
                }
            }

            @Override
            public ServerSocket getSource() {
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
                    if (address != null) {
                        server = new ServerSocket(port, maxConnection, address);
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
                    while (true) {
                        try {
                            Socket socket;
                            ChannelImpl channel;
                            try {
                                socket = serverSocket.accept();
                                channel = new ChannelImpl(socket);
                            } catch (Throwable e) {
                                serverHandler.onException(new FsNetServerException(serverSocket, e));
                                if (serverSocket.isClosed()) {
                                    state.close();
                                    serverLatch.countDown();
                                    break;
                                }
                                continue;
                            }
                            executor.execute(() -> {
                                try {
                                    serverHandler.onOpen(channel);
                                } catch (Throwable e) {
                                    serverHandler.onException(channel, e, EMPTY_BUFFER);
                                } finally {
                                    channels.add(channel);
                                }
                            });
                        } catch (Throwable e) {
                            //Ensure the loop continue
                        }
                    }
                });
                executor.execute(() -> {
                    while (true) {
                        Iterator<ChannelImpl> it = channels.iterator();
                        while (it.hasNext()) {
                            ChannelImpl channel = it.next();
                            if (channel.closed) {
                                it.remove();
                            }
                            if (channel.lock) {
                                continue;
                            }
                            channel.lock = true;
                            try {
                                executor.execute(() -> {
                                    try {
                                        doChannel(channel);
                                    } catch (Throwable e) {
                                        compactBuffer(channel);
                                        serverHandler.onException(channel, e, channel.buffer);
                                    } finally {
                                        channel.lock = false;
                                    }
                                });
                            } catch (Throwable e) {
                                //Ensure the loop continue
                            }
                        }
                        if (channels.isEmpty() && serverSocket.isClosed()) {
                            state.close();
                            serverLatch.countDown();
                            connectionLatch.countDown();
                            break;
                        }
                        Fs.sleep(1);
                    }
                });
            }

            private void doChannel(ChannelImpl channel) {
                if (channel.closed) {
                    return;
                }
                byte[] newBytes = channel.availableOrClosed();
                if (newBytes == null) {
                    //null means channel closed or error
                    try {
                        channel.closeNow();
                        compactBuffer(channel);
                        serverHandler.onClose(channel, channel.buffer);
                    } catch (Throwable e) {
                        compactBuffer(channel);
                        serverHandler.onException(channel, e, channel.buffer);
                    } finally {
                        channel.closed = true;
                    }
                    return;
                }
                if (newBytes.length == 0) {
                    compactBuffer(channel);
                    serverHandler.onLoop(channel, false, channel.buffer);
                    return;
                }
                compactBuffer(channel, newBytes);
                Object message = channel.buffer;
                for (FsTcpChannelHandler<?> channelHandler : channelHandlers) {
                    FsTcpChannelHandler<Object> handler = Fs.as(channelHandler);
                    Object result = handler.onMessage(channel, message);
                    if (result == null) {
                        break;
                    }
                    message = result;
                }
                compactBuffer(channel);
                serverHandler.onLoop(channel, true, channel.buffer);
            }

            private void compactBuffer(ChannelImpl channel) {
                if (channel.buffer.position() == 0) {
                    //not consumed
                    return;
                }
                if (channel.buffer.remaining() <= 0) {
                    channel.buffer = EMPTY_BUFFER;
                    return;
                }
                ByteBuffer newBuffer = bufferGenerator.apply(channel.buffer.remaining());
                newBuffer.put(channel.buffer);
                newBuffer.flip();
                channel.buffer = newBuffer.asReadOnlyBuffer();
            }

            private void compactBuffer(ChannelImpl channel, byte[] newBytes) {
                if (channel.buffer.remaining() <= 0) {
                    channel.buffer = ByteBuffer.wrap(newBytes).asReadOnlyBuffer();
                    return;
                }
                int newCapacity = channel.buffer.remaining() + newBytes.length;
                ByteBuffer newBuffer = bufferGenerator.apply(newCapacity);
                newBuffer.put(channel.buffer);
                newBuffer.put(newBytes);
                newBuffer.flip();
                channel.buffer = newBuffer.asReadOnlyBuffer();
            }

            private final class ChannelImpl implements FsTcpChannel {

                private final Socket socket;
                private volatile boolean lock = false;
                private volatile boolean closed = false;
                private volatile ByteBuffer buffer = EMPTY_BUFFER;

                private volatile @Nullable OutputStream out;

                private ChannelImpl(Socket socket) {
                    this.socket = socket;
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
                    return socket.isConnected();
                }

                @Override
                public boolean isClosed() {
                    return socket.isClosed();
                }

                @Override
                public synchronized void close(@Nullable Duration timeout) {
                    if (socket.isClosed()) {
                        return;
                    }
                    if (out != null) {
                        try {
                            out.flush();
                        } catch (IOException e) {
                            throw new FsNetException(e);
                        }
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        throw new FsNetException(e);
                    }
                }

                @Override
                public synchronized void closeNow() {
                    if (socket.isClosed()) {
                        return;
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        throw new FsNetException(e);
                    }
                }

                @Override
                public synchronized void send(FsData data) {
                    if (out == null) {
                        try {
                            out = socket.getOutputStream();
                        } catch (IOException e) {
                            throw new FsNetException(e);
                        }
                    }
                    FsIO.readBytesTo(data.toInputStream(), out);
                }

                @Override
                public synchronized void sendAndFlush(FsData data) {
                    if (out == null) {
                        try {
                            out = socket.getOutputStream();
                        } catch (IOException e) {
                            throw new FsNetException(e);
                        }
                    }
                    FsIO.readBytesTo(data.toInputStream(), out);
                    try {
                        out.flush();
                    } catch (IOException e) {
                        throw new FsNetException(e);
                    }
                }

                @Override
                public synchronized void flush() {
                    if (out == null) {
                        return;
                    }
                    try {
                        out.flush();
                    } catch (IOException e) {
                        throw new FsNetException(e);
                    }
                }

                @Override
                public Object getSource() {
                    return socket;
                }

                @Nullable
                byte[] availableOrClosed() {
                    InputStream in;
                    try {
                        in = socket.getInputStream();
                    } catch (IOException e) {
                        return null;
                    }
                    int available;
                    try {
                        available = in.available();
                    } catch (IOException e) {
                        return null;
                    }
                    if (available == 0) {
                        if (socket.isClosed()) {
                            return null;
                        }
                        return EMPTY_ARRAY;
                    }
                    int maxRead = channelBufferSize - buffer.remaining();
                    int needRead = Math.min(available, maxRead);
                    if (needRead <= 0) {
                        return EMPTY_ARRAY;
                    }
                    byte[] newBytes = new byte[needRead];
                    int readCount;
                    try {
                        readCount = in.read(newBytes);
                    } catch (IOException e) {
                        return null;
                    }
                    if (readCount < newBytes.length) {
                        return Arrays.copyOf(newBytes, readCount);
                    }
                    return newBytes;
                }
            }
        }
    }
}
