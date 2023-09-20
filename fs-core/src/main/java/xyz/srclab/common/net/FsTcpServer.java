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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
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

        private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(new byte[0]);
        private static final FsTcpServerHandler EMPTY_SERVER_HANDLER = new FsTcpServerHandler() {
        };

        private int port = 0;
        private int maxConnection = 50;
        private @Nullable InetAddress hostAddress;
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
            private final InetAddress hostAddress;
            private final FsTcpServerHandler serverHandler;
            private final List<FsTcpChannelHandler<?>> channelHandlers;
            private final IntFunction<ByteBuffer> bufferGenerator;
            private final Executor executor;
            private final int channelBufferSize;
            private final @Nullable Consumer<ServerSocket> socketConfig;

            private final CountDownLatch latch = new CountDownLatch(1);
            private final FsTcpStates state = new FsTcpStates();
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
                        latch.await();
                    } catch (InterruptedException ignored) {
                    }
                }
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
            public void close(@Nullable Duration timeout) {
                closeNow();
            }

            @Override
            public synchronized void closeNow() {
                if (!state.isOpened()) {
                    throw new FsNetException("The server has not been opened.");
                }
                if (state.isClosed()) {
                    return;
                }
                try {
                    serverSocket.close();
                    state.close();
                    latch.countDown();
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
                                    latch.countDown();
                                    break;
                                }
                                continue;
                            }
                            channel.handling = true;
                            channels.add(channel);
                            executor.execute(() -> {
                                try {
                                    serverHandler.onOpen(channel);
                                } catch (Throwable e) {
                                    serverHandler.onException(channel, e, EMPTY_BUFFER);
                                } finally {
                                    channel.handling = false;
                                }
                            });
                        } catch (Throwable e) {
                            //Ensure the loop continue
                        }
                    }
                });
                executor.execute(() -> {
                    while (true) {
                        if (channels.isEmpty()) {
                            Fs.sleep(1);
                            continue;
                        }
                        int count = 0;
                        Iterator<ChannelImpl> it = channels.iterator();
                        while (it.hasNext()) {
                            ChannelImpl c = it.next();
                            if (c.removed) {
                                it.remove();
                            }
                            if (c.handling) {
                                continue;
                            }
                            c.handling = true;
                            try {
                                executor.execute(() -> {
                                    try {
                                        doChannel(c);
                                    } catch (Throwable e) {
                                        serverHandler.onException(c, e);
                                    } finally {
                                        c.handling = false;
                                    }
                                });
                            } catch (Throwable e) {
                                //Ensure the loop continue
                            }
                        }
                        if (channels.isEmpty() && serverSocket.isClosed()) {
                            state.close();
                            break;
                        }
                    }
                });
            }

            private void doChannel(ChannelImpl channel) throws Exception {
                byte[] bytes;
                InputStream in = channel.in;
                int available = in.available();
                ByteBuffer oldBuffer = channel.buffer;
                if (available == 0) {
                    if (channel.isClosed()) {
                        serverHandler.onClose(channel, oldBuffer.asReadOnlyBuffer());
                        channel.removed = true;
                        return;
                    }
                    bytes = null;
                } else {
                    int maxRead = channelBufferSize - oldBuffer.remaining();
                    int needRead = Math.min(available, maxRead);
                    if (needRead <= 0) {
                        bytes = null;
                    } else {
                        bytes = new byte[needRead];
                        int rc = in.read(bytes);
                        if (rc < bytes.length) {
                            bytes = Arrays.copyOf(bytes, rc);
                        }
                    }
                }
                if (FsArray.isEmpty(bytes)) {
                    ByteBuffer rb = oldBuffer.asReadOnlyBuffer();
                    serverHandler.onLoop(channel, false, rb);
                    channel.buffer = afterConsuming(oldBuffer, rb);
                    return;
                }
                ByteBuffer buffer = beforeConsuming(oldBuffer, bytes);
                ByteBuffer rb = buffer.asReadOnlyBuffer();
                Object message = rb;
                for (FsTcpChannelHandler<?> channelHandler : channelHandlers) {
                    FsTcpChannelHandler<Object> handler = Fs.as(channelHandler);
                    Object result = handler.onMessage(channel, message);
                    if (result == null) {
                        break;
                    }
                    message = result;
                }
                ByteBuffer afterMessage = afterConsuming(buffer, rb);
                ByteBuffer readonlyAfterMessage = afterMessage.asReadOnlyBuffer();
                serverHandler.onLoop(channel, true, readonlyAfterMessage);
                channel.buffer = afterConsuming(afterMessage, readonlyAfterMessage);
            }

            private ByteBuffer beforeConsuming(ByteBuffer src, byte[] newBytes) {
                if (src.capacity() - src.limit() <= newBytes.length) {
                    src.compact();
                    src.put(newBytes);
                    src.flip();
                    return src;
                }
                ByteBuffer newBuffer = bufferGenerator.apply(src.remaining() + newBytes.length);
                newBuffer.put(src);
                newBuffer.put(newBytes);
                newBuffer.flip();
                return newBuffer;
            }

            private ByteBuffer afterConsuming(ByteBuffer src, ByteBuffer rb) {
                src.position(rb.position());
                src.compact();
                src.flip();
                return src;
            }

            private static final class ChannelImpl implements FsTcpChannel {

                private final Socket socket;
                private final InputStream in;
                private final OutputStream out;
                private @Nullable ByteBuffer buffer;
                private volatile boolean handling = false;
                private volatile boolean removed = false;

                private ChannelImpl(Socket socket) {
                    this.socket = socket;
                    try {
                        this.in = socket.getInputStream();
                        this.out = socket.getOutputStream();
                    } catch (IOException e) {
                        throw new FsNetException(e);
                    }
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
                public void close(@Nullable Duration timeout) {
                    closeNow();
                }

                @Override
                public synchronized void closeNow() {
                    try {
                        if (!socket.isClosed()) {
                            socket.close();
                        }
                    } catch (Throwable e) {
                        throw new FsNetException(e);
                    }
                }

                @Override
                public synchronized void send(FsData data) {
                        FsIO.readBytesTo(data.toInputStream(), out);
                }

                @Override
                public synchronized void flush() {
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
            }
        }
    }
}
