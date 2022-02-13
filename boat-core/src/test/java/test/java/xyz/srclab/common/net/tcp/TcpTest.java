package test.java.xyz.srclab.common.net.tcp;

import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.io.BBuffer;
import xyz.srclab.common.net.socket.BSocket;
import xyz.srclab.common.net.tcp.TcpChannelHandler;
import xyz.srclab.common.net.tcp.TcpClient;
import xyz.srclab.common.net.tcp.TcpContext;
import xyz.srclab.common.net.tcp.TcpServer;
import xyz.srclab.common.run.AsyncRunner;
import xyz.srclab.common.run.RunLatch;
import xyz.srclab.common.run.Runner;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

public class TcpTest {

    private static final String SERVER_OPEN = "SERVER_OPEN";
    private static final String SERVER_RECEIVE = "SERVER_RECEIVE";
    private static final String CLIENT_MESSAGE = "CLIENT_RECEIVE";
    private static final String SERVER_CLOSE = "SERVER_CLOSE";

    private static String serverSentMessage = "";
    private static String serverReceivedMessage = "";
    private static String clientReceivedMessage = "";

    @Test
    public void testTcp() {
        int port = BSocket.availableSocketPort();
        String address = "localhost:" + port;
        RunLatch latch = RunLatch.newRunLatch(0);

        TcpServer tcpServer = TcpServer.nioServer(
            BSocket.parseInetSocketAddress(address),
            new ServerHandler(latch)
        );
        tcpServer.start();

        TcpClient tcpClient = TcpClient.simpleClient(address);
        tcpClient.connect();

        latch.lock();
        BLog.info("Send message1: {}", CLIENT_MESSAGE);
        tcpClient.send(CLIENT_MESSAGE);
        latch.await();
        latch.lock();
        BLog.info("Send message2: {}", CLIENT_MESSAGE);
        tcpClient.send(CLIENT_MESSAGE);
        latch.await();
        Assert.assertEquals(
            serverReceivedMessage,
            CLIENT_MESSAGE + CLIENT_MESSAGE
        );
        String received1 = tcpClient.receiveString();
        BLog.info("Client receive1: {}", received1);
        clientReceivedMessage += received1;
        Assert.assertEquals(
            clientReceivedMessage,
            SERVER_OPEN + SERVER_RECEIVE + SERVER_RECEIVE
        );

        latch.lock();
        BLog.info("Send message3: {}", CLIENT_MESSAGE);
        tcpClient.send(CLIENT_MESSAGE);
        latch.await();
        String received3 = tcpClient.receiveString(1000);
        BLog.info("Client receive3: {}", received3);
        clientReceivedMessage += received3;
        Assert.assertEquals(
            clientReceivedMessage,
            SERVER_OPEN + SERVER_RECEIVE + SERVER_RECEIVE + SERVER_RECEIVE
        );

        latch.lock();
        tcpClient.disconnect();
        latch.await();

        Assert.assertEquals(
            serverSentMessage,
            SERVER_OPEN + SERVER_RECEIVE + SERVER_RECEIVE + SERVER_RECEIVE + SERVER_CLOSE
        );
    }

    @Test
    public void testOpenClose() {

        int clientCount = 1000;
        RunLatch latch = RunLatch.newRunLatch(clientCount * 4);

        InetSocketAddress address = BSocket.availableLocalhost();
        AtomicInteger openCount = new AtomicInteger(0);
        AtomicInteger closeCount = new AtomicInteger(0);
        AtomicInteger receiveCount = new AtomicInteger(0);
        TcpServer tcpServer = TcpServer.nioServer(
            address,
            new TcpChannelHandler() {
                @Override
                public void onOpen(@NotNull TcpContext context) {
                    openCount.incrementAndGet();
                    latch.unlock();
                }

                @Override
                public void onReceive(@NotNull TcpContext context, @NotNull ByteBuffer data) {
                    receiveCount.incrementAndGet();
                    latch.unlock();
                }

                @Override
                public void onClose(@NotNull TcpContext context) {
                    closeCount.incrementAndGet();
                    latch.unlock();
                }
            }
        );
        tcpServer.start();

        Runner runner = AsyncRunner.INSTANCE;
        for (int i = 0; i < clientCount; i++) {
            runner.run(() -> {
                TcpClient tcpClient = TcpClient.simpleClient(address);
                tcpClient.connect();
                tcpClient.send("123");
                tcpClient.disconnect();
                latch.unlock();
            });
        }
        latch.await();
        tcpServer.stop();
        BLog.info("openCount: {}", openCount);
        BLog.info("closeCount: {}", closeCount);
        BLog.info("receiveCount: {}", receiveCount);
        Assert.assertEquals(openCount.get(), clientCount);
        Assert.assertEquals(closeCount.get(), clientCount);
        Assert.assertEquals(receiveCount.get(), clientCount);
    }

    private static final class ServerHandler implements TcpChannelHandler {

        private final RunLatch latch;

        public ServerHandler(RunLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onOpen(@NotNull TcpContext context) {
            serverSentMessage += SERVER_OPEN;
            context.write(SERVER_OPEN.getBytes());
            BLog.info("Server open: {}", context.getRemoteAddress());
        }

        @Override
        public void onReceive(@NotNull TcpContext context, @NotNull ByteBuffer data) {
            String read = BBuffer.getString(data);
            serverSentMessage += SERVER_RECEIVE;
            serverReceivedMessage += read;
            context.write(SERVER_RECEIVE.getBytes());
            BLog.info("Server receive: {}@{}", context.getRemoteAddress(), read);
            latch.unlock();
        }

        @Override
        public void onClose(@NotNull TcpContext context) {
            serverSentMessage += SERVER_CLOSE;
            //context.write(SERVER_CLOSE.getBytes());
            BLog.info("Server close: {}", context.getRemoteAddress());
            latch.unlock();
        }
    }
}
