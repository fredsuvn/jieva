package xyz.srclab.common.net;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.base.FsArray;
import xyz.srclab.common.collect.FsCollect;
import xyz.srclab.common.data.FsData;
import xyz.srclab.common.io.FsIO;

import java.io.IOException;
import java.net.InetSocketAddress;
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
    private final List<FsNetServerHandler<ServerSocket>> serverHandlers;
    private final List<FsNetChannelHandler<Socket, ?>> channelHandlers;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final @Nullable Consumer<ServerSocket> config;
    private final IntFunction<ByteBuffer> bufferGenerator;

    private final Set<ChannelImpl> channels = ConcurrentHashMap.newKeySet();

    private ServerSocket serverSocket;

    private SocketTcpServer(Builder builder) {
        this.port = builder.port;
        this.serverHandlers = FsCollect.immutableList(builder.serverHandlers);
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

    private void startAcceptLoop() throws Exception {
        serverSocket = buildServerSocket();
        threadPoolExecutor.execute(() -> {
            while (true) {
                Socket socket;
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    for (FsNetServerHandler<ServerSocket> serverHandler : serverHandlers) {
                        serverHandler.onStart(serverSocket, e);
                    }
                    continue;
                }

            }
        });
    }

    private void loopSocket() {
        for (ChannelImpl channel : channels) {
            byte[] bytes = null;
            try {
                bytes = FsIO.availableBytes(channel.socket.getInputStream());
                if (FsArray.isEmpty(bytes)) {
                    //channelHandlers.get(0).
                    continue;
                }
            } catch (IOException e) {
                channelHandlers.get(0).onException(channel, e);
                continue;
            }
            ByteBuffer buffer;
            if (channel.buffer == null) {
                buffer = ByteBuffer.wrap(bytes);
            } else if (channel.buffer.remaining() <= 0) {
                if (channel.buffer.capacity() >= bytes.length && channel.buffer.capacity() <= bytes.length * 1.5) {
                    buffer = channel.buffer;
                    buffer.position(0);
                    buffer.put(bytes);
                } else {
                    buffer = ByteBuffer.wrap(bytes);
                }
            } else {
                buffer = bufferGenerator.apply(bytes.length + channel.buffer.remaining());
                buffer.put(channel.buffer);
                buffer.put(bytes);
                buffer.flip();
            }
            channel.buffer = buffer;
            Object message = buffer;
            for (FsNetChannelHandler<Socket, ?> channelHandler : channelHandlers) {
                FsNetChannelHandler<Socket, Object> handler = Fs.as(channelHandler);
                try {
                    Object result = handler.onMessage(channel, message);
                    if (result == null) {
                        break;
                    }
                    message = result;
                } catch (Throwable e) {
                    handler.onException(channel, e);
                    break;
                }
            }
        }
    }

    private ServerSocket buildServerSocket() throws Exception {
        ServerSocket server = new ServerSocket(port);
        if (config != null) {
            config.accept(server);
        }
        return server;
    }

    private static final class ChannelImpl implements FsNetChannel<Socket> {

        private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(new byte[0]);

        private final Socket socket;
        private final ServerSocket serverSocket;
        private @Nullable InetSocketAddress remoteAddress;
        private @Nullable InetSocketAddress hostAddress;

        private @Nullable ByteBuffer buffer;

        private ChannelImpl(Socket socket, ServerSocket serverSocket) {
            this.socket = socket;
            this.serverSocket = serverSocket;
        }

        @Override
        public InetSocketAddress getRemoteAddress() {
            if (remoteAddress != null) {
                return remoteAddress;
            }
            remoteAddress = new InetSocketAddress(socket.getInetAddress(), serverSocket.getLocalPort());
            return remoteAddress;
        }

        @Override
        public InetSocketAddress getHostAddress() {
            if (hostAddress != null) {
                return hostAddress;
            }
            hostAddress = new InetSocketAddress(serverSocket.getInetAddress(), serverSocket.getLocalPort());
            return hostAddress;
        }

        @Override
        public boolean isAlive() {
            return socket.isConnected();
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
            try {
                FsIO.readBytesTo(data.toInputStream(), socket.getOutputStream());
            } catch (IOException e) {
                throw new FsNetException(e);
            }
        }

        @Override
        public void flush() {
            try {
                socket.getOutputStream().flush();
            } catch (IOException e) {
                throw new FsNetException(e);
            }
        }

        @Override
        public ByteBuffer getBuffer() {
            return buffer == null ? EMPTY_BUFFER : buffer;
        }

        @Override
        public Socket getChannel() {
            return socket;
        }
    }

    public static class Builder {
        private int port;
        private List<FsNetServerHandler<ServerSocket>> serverHandlers;
        private List<FsNetChannelHandler<Socket, ?>> channelHandlers;
        private ThreadPoolExecutor threadPoolExecutor;
        private @Nullable Consumer<ServerSocket> config;
        private @Nullable IntFunction<ByteBuffer> bufferGenerator;
    }
}
