package test.java.xyz.srclab.common.net.socket;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.net.BSocket;

import java.net.InetSocketAddress;

public class SocketTest {

    @Test
    public void testSocket() {
        BLog.info("Available socket port: {}", BSocket.availablePort());
    }

    @Test
    public void testParseAddress() {
        String url = "localhost:8080";
        InetSocketAddress socketAddress = BSocket.parseInetSocketAddress(url);
        BLog.info("socketAddress: {}", socketAddress);
        Assert.assertEquals(socketAddress.getHostName(), "localhost");
        Assert.assertEquals(socketAddress.getPort(), 8080);
    }
}
