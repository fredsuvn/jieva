package xyz.srclab.common.net.udp;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.collect.FsCollect;
import xyz.srclab.common.io.FsIO;
import xyz.srclab.common.net.FsNetException;
import xyz.srclab.common.net.FsNetServerException;
import xyz.srclab.common.net.FsServerStates;

import java.net.*;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * UDP server interface.
 * The implementation should use {@link FsUdpPacket} to represents datagram packet of UDP.
 * And should support following types of callback handler:
 * <ul>
 *     <li>
 *         one {@link FsUdpServerHandler}: to callback for server events;
 *     </li>
 *     <li>
 *         a list of {@link FsUdpPacketHandler}: to callback for received packet events;
 *     </li>
 * </ul>
 *
 * @author fredsuvn
 */
public interface FsUdpServer {

    /**
     * Returns new builder for this interface.
     * The returned builder is based on {@link DatagramSocket}.
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
     * Returns whether this server is opened.
     */
    boolean isOpened();

    /**
     * Returns whether this server is closed.
     */
    boolean isClosed();

    /**
     * Closes this server, blocks and waits for buffered operations.
     */
    default void close() {
        close(null);
    }

    /**
     * Closes this server, blocks and waits for buffered operations in given timeout.
     *
     * @param timeout given timeout
     */
    void close(@Nullable Duration timeout);

    /**
     * Closes this server immediately, without blocking and waiting for buffered operations.
     */
    void closeNow();

    /**
     * Returns bound address of this server.
     */
    InetAddress getAddress();

    /**
     * Returns bound port of this server.
     */
    int getPort();

    /**
     * Returns bound socket address of this server.
     */
    SocketAddress getSocketAddress();

    /**
     * Returns underlying object which implements this interface, such as {@link DatagramSocket}.
     */
    Object getSource();

    /**
     * Builder for {@link FsUdpServer}, based on {@link DatagramSocket}.
     */
    class Builder {

        private static final FsUdpServerHandler EMPTY_SERVER_HANDLER = new FsUdpServerHandler() {
        };

        private int port = 0;
        private @Nullable InetAddress address;
        private @Nullable FsUdpServerHandler serverHandler;
        private final List<FsUdpPacketHandler<?>> packetHandlers = new LinkedList<>();
        private @Nullable Executor executor;
        private int packetBufferSize = FsIO.IO_BUFFER_SIZE;
        private @Nullable Consumer<DatagramSocket> socketConfig;

        /**
         * Sets server port, maybe 0 to get an available one from system.
         */
        public Builder port(int port) {
            this.port = port;
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
         * Sets host name.
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
        public Builder serverHandler(FsUdpServerHandler serverHandler) {
            this.serverHandler = serverHandler;
            return this;
        }

        /**
         * Adds packet handler.
         */
        public Builder addPacketHandler(FsUdpPacketHandler<?> packetHandler) {
            this.packetHandlers.add(packetHandler);
            return this;
        }

        /**
         * Adds packet handlers.
         */
        public Builder addPacketHandlers(Iterable<FsUdpPacketHandler<?>> packetHandlers) {
            FsCollect.toCollection(this.packetHandlers, packetHandlers);
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
         * Sets buffer size of the packet.
         */
        public Builder packetBufferSize(int packetBufferSize) {
            this.packetBufferSize = packetBufferSize;
            return this;
        }

        /**
         * Sets other socket config.
         */
        public Builder socketConfig(Consumer<DatagramSocket> socketConfig) {
            this.socketConfig = socketConfig;
            return this;
        }

        /**
         * Builds the server.
         */
        public FsUdpServer build() {
            return new SocketUdpServer(this);
        }

        private static final class SocketUdpServer implements FsUdpServer {

            private final int port;
            private final InetAddress hostAddress;
            private final FsUdpServerHandler serverHandler;
            private final List<FsUdpPacketHandler<?>> packetHandlers;
            private final Executor executor;
            private final int packetBufferSize;
            private final @Nullable Consumer<DatagramSocket> socketConfig;

            private final CountDownLatch serverLatch = new CountDownLatch(1);
            private final CountDownLatch packetLatch = new CountDownLatch(1);
            private final AtomicInteger handlingCounter = new AtomicInteger();
            private final FsServerStates state = new FsServerStates();
            private @Nullable DatagramSocket serverSocket;

            private SocketUdpServer(Builder builder) {
                this.port = builder.port;
                this.hostAddress = builder.address;
                this.serverHandler = Fs.notNull(builder.serverHandler, EMPTY_SERVER_HANDLER);
                this.packetHandlers = FsCollect.immutableList(builder.packetHandlers);
                if (packetHandlers.isEmpty()) {
                    throw new FsNetException("Packet handlers are empty.");
                }
                this.executor = builder.executor;
                if (executor == null) {
                    throw new FsNetException("Executor is null.");
                }
                this.socketConfig = builder.socketConfig;
                this.packetBufferSize = builder.packetBufferSize;
                if (packetBufferSize <= 0) {
                    throw new FsNetException("Packet buffer size must > 0.");
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
                        packetLatch.await();
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
                        packetLatch.await();
                    } catch (InterruptedException e) {
                        throw new FsNetException(e);
                    }
                } else {
                    try {
                        long n1 = System.currentTimeMillis();
                        long millis = timeout.toMillis();
                        serverLatch.await(millis, TimeUnit.MILLISECONDS);
                        long n2 = System.currentTimeMillis();
                        packetLatch.await(Math.max(1, millis - (n2 - n1)), TimeUnit.MILLISECONDS);
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
                } catch (Exception e) {
                    throw new FsNetException(e);
                }
            }

            @Override
            public DatagramSocket getSource() {
                if (serverSocket == null) {
                    throw new FsNetException("Server has not been initialized.");
                }
                return serverSocket;
            }

            private void start0() {
                serverSocket = buildServerSocket();
                loopServerSocket();
            }

            private DatagramSocket buildServerSocket() {
                try {
                    DatagramSocket server;
                    if (hostAddress != null) {
                        server = new DatagramSocket(port, hostAddress);
                    } else {
                        server = new DatagramSocket(port);
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
                    byte[] buffer = new byte[packetBufferSize];
                    while (!serverSocket.isClosed()) {
                        try {
                            Object result = receive(buffer);
                            if (result == null) {
                                break;
                            }
                        } catch (Throwable e) {
                            //Ensure the loop continue
                        }
                    }
                });
            }

            @Nullable
            private Object receive(byte[] buffer) {
                DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);
                FsUdpPacket udpPacket;
                try {
                    serverSocket.receive(packet);
                } catch (Throwable e) {
                    serverHandler.onException(new FsNetServerException(serverSocket, e));
                    if (serverSocket.isClosed()) {
                        state.close();
                        serverLatch.countDown();
                        return null;
                    }
                    return true;
                }
                udpPacket = FsUdpPacket.from(packet);
                executor.execute(() -> {
                    ByteBuffer buf = udpPacket.getData().asReadOnlyBuffer();
                    Object msg = buf;
                    try {
                        handlingCounter.incrementAndGet();
                        for (FsUdpPacketHandler<?> packetHandler : packetHandlers) {
                            FsUdpPacketHandler<Object> handler = Fs.as(packetHandler);
                            Object result = handler.onPacket(udpPacket.getHeader(), msg);
                            if (result == null) {
                                break;
                            }
                            msg = result;
                        }
                    } catch (Throwable e) {
                        serverHandler.onException(udpPacket.getHeader(), e, buf.slice());
                    } finally {
                        int c = handlingCounter.decrementAndGet();
                        if (c <= 0 && serverSocket.isClosed()) {
                            packetLatch.countDown();
                        }
                    }
                });
                return true;
            }
        }
    }
}
