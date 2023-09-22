//package xyz.srclab.common.net.udp;
//
//import xyz.srclab.annotations.Nullable;
//import xyz.srclab.common.base.Fs;
//import xyz.srclab.common.io.FsIO;
//import xyz.srclab.common.net.FsNetException;
//import xyz.srclab.common.net.FsNetServerException;
//import xyz.srclab.common.net.FsServerStates;
//
//import java.net.*;
//import java.nio.ByteBuffer;
//import java.time.Duration;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.function.Consumer;
//
///**
// * UDP client interface.
// *
// * @author fredsuvn
// */
//public interface FsUdpClient {
//
//    /**
//     * Returns new builder for this interface.
//     * The returned builder is based on {@link DatagramSocket}.
//     */
//    static Builder newBuilder() {
//        return new Builder();
//    }
//
//    /**
//     * Starts this server and wait until the server and all connections have been closed.
//     * This method is equivalent to {@link #start(boolean)}:
//     * <pre>
//     *     start(true);
//     * </pre>
//     */
//    default void start() {
//        start(true);
//    }
//
//    /**
//     * Starts this server.
//     * If given {@code block} is true, this method will block current thread until
//     * the server and all connections have been closed.
//     *
//     * @param block whether block current thread
//     */
//    void start(boolean block);
//
//    /**
//     * Returns whether this server is opened.
//     */
//    boolean isOpened();
//
//    /**
//     * Returns whether this server is closed.
//     */
//    boolean isClosed();
//
//    /**
//     * Closes this server, blocks and waits for buffered operations.
//     */
//    default void close() {
//        close(null);
//    }
//
//    /**
//     * Closes this server, blocks and waits for buffered operations in given timeout.
//     *
//     * @param timeout given timeout
//     */
//    void close(@Nullable Duration timeout);
//
//    /**
//     * Returns bound address of this client.
//     */
//    InetAddress getAddress();
//
//    /**
//     * Returns bound port of this client.
//     */
//    int getPort();
//
//    /**
//     * Returns bound socket address of this client.
//     */
//    SocketAddress getSocketAddress();
//
//    /**
//     * Returns underlying object which implements this interface, such as {@link DatagramSocket}.
//     */
//    Object getSource();
//
//    /**
//     * Builder for {@link FsUdpClient}, based on {@link DatagramSocket}.
//     */
//    class Builder {
//
//        private static final FsUdpServerHandler EMPTY_SERVER_HANDLER = new FsUdpServerHandler() {
//        };
//
//        private int port = 0;
//        private @Nullable InetAddress address;
//        private int packetBufferSize = FsIO.IO_BUFFER_SIZE;
//        private @Nullable Consumer<DatagramSocket> socketConfig;
//        private @Nullable Proxy proxy;
//
//        /**
//         * Sets local port, maybe 0 to get an available one from system.
//         */
//        public Builder port(int port) {
//            this.port = port;
//            return this;
//        }
//
//        /**
//         * Sets local address.
//         */
//        public Builder address(InetAddress address) {
//            this.address = address;
//            return this;
//        }
//
//        /**
//         * Sets local host name.
//         */
//        public Builder hostName(String hostName) {
//            try {
//                this.address = InetAddress.getByName(hostName);
//                return this;
//            } catch (UnknownHostException e) {
//                throw new FsNetException(e);
//            }
//        }
//
//        /**
//         * Sets buffer size of the packet.
//         */
//        public Builder packetBufferSize(int packetBufferSize) {
//            this.packetBufferSize = packetBufferSize;
//            return this;
//        }
//
//        /**
//         * Sets other socket config.
//         */
//        public Builder socketConfig(Consumer<DatagramSocket> socketConfig) {
//            this.socketConfig = socketConfig;
//            return this;
//        }
//
//        /**
//         * Sets proxy.
//         */
//        public Builder proxy(Proxy proxy) {
//            this.proxy = proxy;
//            return this;
//        }
//
//        /**
//         * Builds the client.
//         */
//        public FsUdpClient build() {
//            return new SocketUdpClient(this);
//        }
//
//        private static final class SocketUdpClient implements FsUdpClient {
//
//            private final int port;
//            private final InetAddress address;
//            private final int packetBufferSize;
//            private final @Nullable Consumer<DatagramSocket> socketConfig;
//            private final @Nullable Proxy proxy;
//            private @Nullable DatagramSocket serverSocket;
//
//            private SocketUdpClient(Builder builder) {
//                this.port = builder.port;
//                this.address = builder.address;
//                this.socketConfig = builder.socketConfig;
//                this.packetBufferSize = builder.packetBufferSize;
//                if (packetBufferSize <= 0) {
//                    throw new FsNetException("Packet buffer size must > 0.");
//                }
//                this.proxy = builder.proxy;
//            }
//
//            @Override
//            public InetAddress getAddress() {
//                if (serverSocket == null) {
//                    throw new FsNetException("Server has not been initialized.");
//                }
//                return serverSocket.getInetAddress();
//            }
//
//            @Override
//            public int getPort() {
//                if (serverSocket == null) {
//                    throw new FsNetException("Server has not been initialized.");
//                }
//                return serverSocket.getLocalPort();
//            }
//
//            @Override
//            public SocketAddress getSocketAddress() {
//                if (serverSocket == null) {
//                    throw new FsNetException("Server has not been initialized.");
//                }
//                return serverSocket.getLocalSocketAddress();
//            }
//
//            @Override
//            public boolean isOpened() {
//                return state.isOpened();
//            }
//
//            @Override
//            public boolean isClosed() {
//                return state.isClosed();
//            }
//
//            @Override
//            public synchronized void close(@Nullable Duration timeout) {
//                closeNow();
//                if (timeout == null) {
//                    try {
//                        serverLatch.await();
//                        packetLatch.await();
//                    } catch (InterruptedException e) {
//                        throw new FsNetException(e);
//                    }
//                } else {
//                    try {
//                        long n1 = System.currentTimeMillis();
//                        long millis = timeout.toMillis();
//                        serverLatch.await(millis, TimeUnit.MILLISECONDS);
//                        long n2 = System.currentTimeMillis();
//                        packetLatch.await(Math.max(1, millis - (n2 - n1)), TimeUnit.MILLISECONDS);
//                    } catch (InterruptedException e) {
//                        throw new FsNetException(e);
//                    }
//                }
//            }
//
//            @Override
//            public synchronized void closeNow() {
//                if (!state.isOpened() || serverSocket == null) {
//                    throw new FsNetException("The server has not been opened.");
//                }
//                if (state.isClosed()) {
//                    return;
//                }
//                try {
//                    serverSocket.close();
//                    state.close();
//                    serverLatch.countDown();
//                } catch (Exception e) {
//                    throw new FsNetException(e);
//                }
//            }
//
//            @Override
//            public DatagramSocket getSource() {
//                if (serverSocket == null) {
//                    throw new FsNetException("Server has not been initialized.");
//                }
//                return serverSocket;
//            }
//
//            private void start0() {
//                serverSocket = buildServerSocket();
//                loopServerSocket();
//            }
//
//            private DatagramSocket buildServerSocket() {
//                try {
//                    DatagramSocket server;
//                    if (address != null) {
//                        server = new DatagramSocket(port, address);
//                    } else {
//                        server = new DatagramSocket(port);
//                    }
//                    if (socketConfig != null) {
//                        socketConfig.accept(server);
//                    }
//                    return server;
//                } catch (Exception e) {
//                    throw new FsNetException(e);
//                }
//            }
//
//            private void loopServerSocket() {
//                executor.execute(() -> {
//                    byte[] buffer = new byte[packetBufferSize];
//                    while (!serverSocket.isClosed()) {
//                        try {
//                            Object result = receive(buffer);
//                            if (result == null) {
//                                break;
//                            }
//                        } catch (Throwable e) {
//                            //Ensure the loop continue
//                        }
//                    }
//                });
//            }
//
//            @Nullable
//            private Object receive(byte[] buffer) {
//                DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);
//                FsUdpPacket udpPacket;
//                try {
//                    serverSocket.receive(packet);
//                } catch (Throwable e) {
//                    serverHandler.onException(new FsNetServerException(serverSocket, e));
//                    if (serverSocket.isClosed()) {
//                        state.close();
//                        serverLatch.countDown();
//                        return null;
//                    }
//                    return true;
//                }
//                udpPacket = FsUdpPacket.from(packet);
//                executor.execute(() -> {
//                    ByteBuffer buf = udpPacket.getData().asReadOnlyBuffer();
//                    Object msg = buf;
//                    try {
//                        handlingCounter.incrementAndGet();
//                        for (FsUdpPacketHandler<?> packetHandler : packetHandlers) {
//                            FsUdpPacketHandler<Object> handler = Fs.as(packetHandler);
//                            Object result = handler.onPacket(udpPacket.getHeader(), msg);
//                            if (result == null) {
//                                break;
//                            }
//                            msg = result;
//                        }
//                    } catch (Throwable e) {
//                        serverHandler.onException(udpPacket.getHeader(), e, buf.slice());
//                    } finally {
//                        int c = handlingCounter.decrementAndGet();
//                        if (c <= 0 && serverSocket.isClosed()) {
//                            packetLatch.countDown();
//                        }
//                    }
//                });
//                return true;
//            }
//        }
//    }
//}
