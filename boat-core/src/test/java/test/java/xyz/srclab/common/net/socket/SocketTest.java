package test.java.xyz.srclab.common.net.socket;

import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.net.socket.BSocket;

public class SocketTest {

    @Test
    public void testSocket() {
        BLog.info("Available socket port: {}", BSocket.availableSocketPort());
    }
}
