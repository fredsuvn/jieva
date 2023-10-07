package xyz.fsgik.common.net.udp;

import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.annotations.ThreadSafe;
import xyz.fsgik.common.base.Fs;
import xyz.fsgik.common.collect.FsCollect;
import xyz.fsgik.common.io.FsIO;
import xyz.fsgik.common.net.FsNetException;
import xyz.fsgik.common.net.FsServerStates;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
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
@ThreadSafe
public interface FsUdpServer extends FsUdpClient {

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
     * Returns whether this server is opened.
     */
    boolean isOpened();

    /**
     * Returns whether this server is closed.
     */
    boolean isClosed();

    /**
     * Closes this server, blocks current thread for buffered operations.
     */
    default void close() {
        close(null);
    }

    /**
     * Closes this server, blocks current thread for buffered operations in given timeout.
     *
     * @param timeout given timeout, maybe null to always wait
     */
    void close(@Nullable Duration timeout);

    /**
     * Closes this server immediately, without blocking and buffered operations.
     */
    void closeNow();

    /**
     * Returns a new builder configured with this server.
     */
    Builder toBuilder();

    /**
     * Builder for {@link FsUdpServer}, based on {@link DatagramSocket}.
     */
    class Builder extends FsUdpClient.Builder {

        private static final byte[] EMPTY_ARRAY = new byte[0];
        private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(EMPTY_ARRAY);
        private static final FsUdpServerHandler EMPTY_SERVER_HANDLER = new FsUdpServerHandler() {
        };
        private final List<FsUdpPacketHandler<?>> packetHandlers = new LinkedList<>();
        private @Nullable FsUdpServerHandler serverHandler;
        private @Nullable ExecutorService executor;
        private int packetBufferSize = FsIO.IO_BUFFER_SIZE;

        /**
         * Sets local port, maybe 0 to get an available one from system.
         */
        @Override
        public Builder port(int port) {
            super.port(port);
            return this;
        }

        /**
         * Sets local address.
         */
        @Override
        public Builder address(InetAddress address) {
            super.address(address);
            return this;
        }

        /**
         * Sets local host name.
         */
        @Override
        public Builder hostName(String hostName) {
            super.hostName(hostName);
            return this;
        }

        /**
         * Sets other socket config.
         */
        @Override
        public Builder socketConfig(Consumer<DatagramSocket> socketConfig) {
            super.socketConfig(socketConfig);
            return this;
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
        public Builder executor(ExecutorService executor) {
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
         * Builds the server.
         */
        public FsUdpServer build() {
            return new SocketUdpServer(this);
        }

        private static final class SocketUdpServer implements FsUdpServer, FsUdpClient {

            private final int port;
            private final InetAddress address;
            private final FsUdpServerHandler serverHandler;
            private final List<FsUdpPacketHandler<?>> packetHandlers;
            private final ExecutorService executor;
            private final int packetBufferSize;
            private final @Nullable Consumer<DatagramSocket> socketConfig;

            private final CountDownLatch latch = new CountDownLatch(1);
            private final AtomicInteger packetCounter = new AtomicInteger();
            private final FsServerStates state = new FsServerStates();
            private @Nullable DatagramSocket serverSocket;

            private SocketUdpServer(FsUdpServer.Builder builder) {
                this.port = builder.port;
                this.address = builder.address;
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
                        latch.await();
                    } catch (InterruptedException ignored) {
                    }
                }
            }

            @Override
            public synchronized void send(FsUdpPacket packet) {
                ByteBuffer buffer = packet.getData();
                DatagramPacket datagramPacket;
                if (buffer.hasArray()) {
                    datagramPacket = new DatagramPacket(buffer.array(), buffer.arrayOffset(), buffer.remaining());
                } else {
                    byte[] bytes = FsIO.getBytes(buffer);
                    datagramPacket = new DatagramPacket(bytes, bytes.length);
                }
                datagramPacket.setSocketAddress(packet.getHeader().getInetSocketAddress());
                try {
                    serverSocket.send(datagramPacket);
                } catch (IOException e) {
                    throw new FsNetException(e);
                }
            }

            @Override
            public InetAddress getAddress() {
                if (serverSocket == null) {
                    throw new FsNetException("Server has not been initialized.");
                }
                return serverSocket.getLocalAddress();
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
                try {
                    close0();
                    if (timeout == null) {
                        latch.await();
                    } else {
                        latch.await(timeout.toMillis(), TimeUnit.MILLISECONDS);
                    }
                } catch (FsNetException e) {
                    throw e;
                } catch (InterruptedException e) {
                    //do nothing
                } catch (Exception e) {
                    throw new FsNetException(e);
                } finally {
                    state.close();
                }
            }

            @Override
            public synchronized void closeNow() {
                try {
                    close0();
                    executor.shutdown();
                    latch.countDown();
                } catch (FsNetException e) {
                    throw e;
                } catch (Exception e) {
                    throw new FsNetException(e);
                } finally {
                    state.close();
                }
            }

            @Override
            public DatagramSocket getSource() {
                if (serverSocket == null) {
                    throw new FsNetException("Server has not been initialized.");
                }
                return serverSocket;
            }

            @Override
            public FsUdpServer.Builder toBuilder() {
                return newBuilder()
                    .port(port)
                    .address(address)
                    .serverHandler(serverHandler)
                    .addPacketHandlers(packetHandlers)
                    .executor(executor)
                    .packetBufferSize(packetBufferSize)
                    .socketConfig(socketConfig);
            }

            private void start0() {
                serverSocket = buildServerSocket();
                loopServerSocket();
            }

            private void close0() {
                if (!state.isOpened() || serverSocket == null) {
                    throw new FsNetException("The server has not been opened.");
                }
                if (state.isClosed()) {
                    return;
                }
                serverSocket.close();
            }

            private DatagramSocket buildServerSocket() {
                try {
                    DatagramSocket server;
                    if (address != null) {
                        server = new DatagramSocket(port, address);
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
                    byte[] data = new byte[packetBufferSize];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    while (!serverSocket.isClosed()) {
                        try {
                            serverSocket.receive(packet);
                            FsUdpPacket udpPacket = FsUdpPacket.from(packet);
                            ByteBuffer buffer = udpPacket.getData().asReadOnlyBuffer();
                            packetCounter.incrementAndGet();
                            executor.execute(() -> {
                                try {
                                    doPacket(udpPacket.getHeader(), buffer);
                                } catch (Throwable e) {
                                    serverHandler.onException(udpPacket.getHeader(), this, e, compactBuffer(buffer));
                                } finally {
                                    packetCounter.decrementAndGet();
                                }
                            });
                        } catch (Throwable e) {
                            //Ensure the loop continue
                        }
                    }
                    while (packetCounter.get() > 0) {
                        Fs.sleep(1);
                    }
                    latch.countDown();
                });
            }

            private void doPacket(FsUdpHeader header, ByteBuffer buffer) {
                Object packet = buffer;
                for (FsUdpPacketHandler<?> packetHandler : packetHandlers) {
                    FsUdpPacketHandler<Object> handler = Fs.as(packetHandler);
                    Object result = handler.onPacket(header, this, packet);
                    if (result == null) {
                        break;
                    }
                    packet = result;
                }
            }

            private ByteBuffer compactBuffer(ByteBuffer buffer) {
                if (buffer.position() == 0) {
                    return buffer;
                }
                if (!buffer.hasRemaining()) {
                    return EMPTY_BUFFER;
                }
                return ByteBuffer.wrap(FsIO.getBytes(buffer)).asReadOnlyBuffer();
            }
        }
    }
}
