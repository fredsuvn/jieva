package xyz.fsgik.common.net.udp;

import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.annotations.ThreadSafe;
import xyz.fsgik.common.base.FsBytes;
import xyz.fsgik.common.net.FsNetException;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * UDP client interface.
 *
 * @author fredsuvn
 */
@ThreadSafe
public interface FsUdpClient {

    /**
     * Returns new builder for this interface.
     * The returned builder is based on {@link DatagramSocket}.
     */
    static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Sends data packet.
     *
     * @param packet the data packet
     */
    void send(FsUdpPacket packet);

    /**
     * Returns bound address of this client.
     */
    InetAddress getAddress();

    /**
     * Returns bound port of this client.
     */
    int getPort();

    /**
     * Returns bound socket address of this client.
     */
    SocketAddress getSocketAddress();

    /**
     * Returns underlying object which implements this interface, such as {@link DatagramSocket}.
     */
    Object getSource();

    /**
     * Returns a new builder configured with this server.
     */
    Builder toBuilder();

    /**
     * Builder for {@link FsUdpClient}, based on {@link DatagramSocket}.
     */
    class Builder {

        protected int port = 0;
        protected @Nullable InetAddress address;
        protected @Nullable Consumer<DatagramSocket> socketConfig;

        /**
         * Sets local port, maybe 0 to get an available one from system.
         */
        public Builder port(int port) {
            this.port = port;
            return this;
        }

        /**
         * Sets local address.
         */
        public Builder address(InetAddress address) {
            this.address = address;
            return this;
        }

        /**
         * Sets local host name.
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
         * Sets other socket config.
         */
        public Builder socketConfig(Consumer<DatagramSocket> socketConfig) {
            this.socketConfig = socketConfig;
            return this;
        }

        /**
         * Builds the client.
         */
        public FsUdpClient build() {
            return new SocketUdpClient(this);
        }

        private static final class SocketUdpClient implements FsUdpClient {

            private final int port;
            private final InetAddress address;
            private final @Nullable Consumer<DatagramSocket> socketConfig;
            private final DatagramSocket socket;

            private SocketUdpClient(Builder builder) {
                this.port = builder.port;
                this.address = builder.address;
                this.socketConfig = builder.socketConfig;
                this.socket = buildSocket();
            }

            @Override
            public synchronized void send(FsUdpPacket packet) {
                ByteBuffer buffer = packet.getData();
                DatagramPacket datagramPacket;
                if (buffer.hasArray()) {
                    datagramPacket = new DatagramPacket(buffer.array(), buffer.arrayOffset(), buffer.remaining());
                } else {
                    byte[] bytes = FsBytes.getBytes(buffer);
                    datagramPacket = new DatagramPacket(bytes, bytes.length);
                }
                datagramPacket.setSocketAddress(packet.getHeader().getInetSocketAddress());
                try {
                    socket.send(datagramPacket);
                } catch (IOException e) {
                    throw new FsNetException(e);
                }
            }

            @Override
            public InetAddress getAddress() {
                return socket.getLocalAddress();
            }

            @Override
            public int getPort() {
                return socket.getLocalPort();
            }

            @Override
            public SocketAddress getSocketAddress() {
                return socket.getLocalSocketAddress();
            }

            @Override
            public DatagramSocket getSource() {
                if (socket == null) {
                    throw new FsNetException("Server has not been initialized.");
                }
                return socket;
            }

            @Override
            public Builder toBuilder() {
                return newBuilder()
                    .port(port)
                    .address(address)
                    .socketConfig(socketConfig);
            }

            private DatagramSocket buildSocket() {
                try {
                    DatagramSocket socket;
                    if (address != null) {
                        socket = new DatagramSocket(port, address);
                    } else {
                        socket = new DatagramSocket(port);
                    }
                    if (socketConfig != null) {
                        socketConfig.accept(socket);
                    }
                    return socket;
                } catch (Exception e) {
                    throw new FsNetException(e);
                }
            }
        }
    }
}
