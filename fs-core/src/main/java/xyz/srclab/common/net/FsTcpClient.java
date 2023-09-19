package xyz.srclab.common.net;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.base.FsArray;
import xyz.srclab.common.collect.FsCollect;
import xyz.srclab.common.data.FsData;
import xyz.srclab.common.io.FsIO;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/**
 * TCP/IP client interface, client endpoint of {@link FsTcpEndpoint}.
 * <p>
 * A {@link FsTcpClient} implementation should support following types of handler:
 * <ul>
 *     <li>
 *         one {@link FsTcpClientHandler}: to deal with client actions;
 *     </li>
 *     <li>
 *         a list of {@link FsTcpClientHandler}: to deal with actions for the connection;
 *     </li>
 * </ul>
 *
 * @author fredsuvn
 */
public interface FsTcpClient extends FsTcpEndpoint {

    /**
     * Returns new builder for this interface.
     * The returned builder is based on {@link Socket}.
     */
    static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Starts this client and wait until the client has been closed.
     */
    void start();

    /**
     * Builder for {@link FsTcpClient}.
     */
    class Builder {

        private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(new byte[0]);
        private static final FsTcpClientHandler EMPTY_CLIENT_HANDLER = new FsTcpClientHandler() {
        };

        private int port = 0;
        private @Nullable InetAddress hostAddress;
        private @Nullable FsTcpClientHandler clientHandler;
        private final List<FsTcpChannelHandler<?>> channelHandlers = new LinkedList<>();
        private @Nullable IntFunction<ByteBuffer> bufferGenerator;
        private int bufferSize = FsIO.IO_BUFFER_SIZE;
        private @Nullable Consumer<Socket> socketConfig;
        private @Nullable Proxy proxy;

        /**
         * Sets server port.
         */
        public Builder port(int port) {
            this.port = port;
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
         * Sets client handler.
         */
        public Builder clientHandler(FsTcpClientHandler clientHandler) {
            this.clientHandler = clientHandler;
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
         * Sets reference value of buffer size for byte buffer.
         */
        public Builder bufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        /**
         * Sets other socket config.
         */
        public Builder socketConfig(Consumer<Socket> socketConfig) {
            this.socketConfig = socketConfig;
            return this;
        }

        /**
         * Sets proxy.
         */
        public Builder proxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        /**
         * Builds the client.
         */
        public FsTcpClient build() {
            return new SocketTcpClient(this);
        }

        private static final class SocketTcpClient implements FsTcpClient {

            private final int port;
            private final InetAddress hostAddress;
            private final FsTcpClientHandler clientHandler;
            private final List<FsTcpChannelHandler<?>> channelHandlers;
            private final IntFunction<ByteBuffer> bufferGenerator;
            private final int bufferSize;
            private final @Nullable Consumer<Socket> socketConfig;
            private final @Nullable Proxy proxy;

            private final FsTcpStates state = new FsTcpStates();
            private ChannelImpl channel;

            private SocketTcpClient(Builder builder) {
                this.port = builder.port;
                this.hostAddress = builder.hostAddress;
                this.clientHandler = Fs.notNull(builder.clientHandler, EMPTY_CLIENT_HANDLER);
                this.channelHandlers = FsCollect.immutableList(builder.channelHandlers);
                if (channelHandlers.isEmpty()) {
                    throw new FsNetException("Channel handlers are empty.");
                }
                this.socketConfig = builder.socketConfig;
                this.bufferGenerator = Fs.notNull(builder.bufferGenerator, ByteBuffer::allocate);
                this.bufferSize = builder.bufferSize;
                if (bufferSize <= 0) {
                    throw new FsNetException("Buffer size must > 0.");
                }
                this.proxy = builder.proxy;
            }

            @Override
            public synchronized void start() {
                synchronized (this) {
                    if (!state.isCreated()) {
                        throw new FsNetException("The client has been opened or closed.");
                    }
                }
                start0();
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
                    throw new FsNetException("The client has not been opened.");
                }
                if (state.isClosed()) {
                    return;
                }
                try {
                    channel.closeNow();
                    state.close();
                } catch (FsNetException e) {
                    throw e;
                } catch (Exception e) {
                    throw new FsNetException(e);
                }
            }

            @Override
            public Socket getSource() {
                if (channel == null) {
                    throw new FsNetException("Client has not been initialized.");
                }
                return channel.socket;
            }

            private void start0() {
                Socket socket = buildSocket();
                ChannelImpl channel = new ChannelImpl(socket);
                this.channel = channel;
                state.open();
                clientHandler.onOpen(channel);
                while (!channel.removed) {
                    try {
                        doChannel(channel);
                    } catch (Throwable e) {
                        clientHandler.onException(channel, e);
                    }
                }
            }

            private Socket buildSocket() {
                try {
                    Socket client;
                    if (proxy != null) {
                        client = new Socket(proxy);
                    } else if (hostAddress != null) {
                        client = new Socket(hostAddress, port);
                    } else {
                        client = new Socket();
                    }
                    if (socketConfig != null) {
                        socketConfig.accept(client);
                    }
                    return client;
                } catch (Exception e) {
                    throw new FsNetException(e);
                }
            }

            private void doChannel(ChannelImpl channel) throws Exception {
                byte[] bytes;
                InputStream in = channel.in;
                boolean isClosed = channel.socket.isClosed();
                int available = in.available();
                if (available == 0) {
                    if (isClosed) {
                        channel.closeNow();
                        clientHandler.onClose(channel);
                        channel.removed = true;
                        return;
                    }
                    bytes = null;
                } else {
                    bytes = new byte[available];
                    int r = in.read(bytes);
                    if (r < bytes.length) {
                        bytes = Arrays.copyOf(bytes, r);
                    }
                }
                if (FsArray.isEmpty(bytes)) {
                    clientHandler.onLoop(channel, false);
                    return;
                }
                Object message = FsNet.growBuffer(channel.buffer, bytes, bufferGenerator);
                for (FsTcpChannelHandler<?> channelHandler : channelHandlers) {
                    FsTcpChannelHandler<Object> handler = Fs.as(channelHandler);
                    Object result = handler.onMessage(channel, message);
                    if (result == null) {
                        break;
                    }
                    message = result;
                }
                clientHandler.onLoop(channel, true);
                channel.buffer = FsNet.compactBuffer(channel.buffer, bufferSize, bufferGenerator);
            }

            private final class ChannelImpl implements FsTcpChannel {

                private final Socket socket;
                private final InputStream in;
                private volatile @Nullable OutputStream out;
                private @Nullable ByteBuffer buffer;
                private volatile boolean removed = false;

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
                        if (!socket.isClosed()) {
                            socket.close();
                        }
                    } catch (Throwable e) {
                        throw new FsNetException(e);
                    }
                }

                @Override
                public synchronized void send(FsData data) {
                    try {
                        setOut();
                        FsIO.readBytesTo(data.toInputStream(), out);
                    } catch (Throwable e) {
                        throw new FsNetException(e);
                    }
                }

                @Override
                public synchronized void flush() {
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

                @Override
                public ByteBuffer newBuffer(int size) {
                    return bufferGenerator.apply(size);
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
