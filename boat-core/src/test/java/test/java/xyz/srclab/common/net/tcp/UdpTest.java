package test.java.xyz.srclab.common.net.tcp;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.io.BBuffer;
import xyz.srclab.common.net.socket.BSocket;
import xyz.srclab.common.net.udp.UdpChannelHandler;
import xyz.srclab.common.net.udp.UdpClient;
import xyz.srclab.common.net.udp.UdpServer;
import xyz.srclab.common.run.RunLatch;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class UdpTest {

    private static final String UDP_MESSAGE = "UDP_MESSAGE";

    @Test
    public void testUdp() throws Exception {
        int port = BSocket.availableSocketPort();
        String address = "localhost:" + port;
        InetSocketAddress socketAddress = BSocket.parseInetSocketAddress(address);
        RunLatch latch = RunLatch.newRunLatch();
        UdpServer udpServer = UdpServer.nioServer(socketAddress, new ServerHandler(latch));
        udpServer.start();
        UdpClient bioClient = UdpClient.bioClient();
        bioClient.send(socketAddress, UDP_MESSAGE);
        latch.await();
        latch.lockUp();
        UdpClient nioClient = UdpClient.nioClient();
        nioClient.send(socketAddress, UDP_MESSAGE);
        latch.await();
        udpServer.stop();
    }

    @Test
    public void testUdpBroadcast() {

    }

    private static final class ServerHandler implements UdpChannelHandler {

        private final RunLatch latch;

        public ServerHandler(RunLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onReceive(@NotNull SocketAddress remoteAddress, @NotNull ByteBuffer data) {
            String message = BBuffer.getString(data);
            BLog.info("Received udp message: {}", message);
            latch.lockDown();
        }
    }
}
