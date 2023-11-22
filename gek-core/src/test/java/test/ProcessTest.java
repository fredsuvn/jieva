package test;

import org.testng.annotations.Test;
import xyz.fsgek.common.base.*;
import xyz.fsgek.common.io.GekIO;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Semaphore;

public class ProcessTest {

    private static final String ECHO_CONTENT = "hello world!";

    @Test
    public void testEcho() throws InterruptedException {
        if (GekSystem.isLinux() || GekSystem.isMac() || GekSystem.isBsd()) {
            testEcho("echo " + ECHO_CONTENT);
        }
        if (GekSystem.isWindows()) {
            testEcho("cmd.exe /c echo " + ECHO_CONTENT);
        }
    }

    private void testEcho(String command) throws InterruptedException {
        Process process = Gek.process().command(command).start();
        process.waitFor();
        String output = GekIO.readString(process.getInputStream(), GekChars.nativeCharset());
        process.destroy();
        System.out.println(output);
    }

    @Test
    public void testPing() throws InterruptedException {
        Process process = Gek.process().command("ping", "-n", "5", "127.0.0.1").start();
        Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        Gek.thread().task(() -> {
            while (true) {
                String output = GekIO.avalaibleString(process.getInputStream(), GekChars.nativeCharset());
                if (output == null) {
                    semaphore.release();
                    return;
                }
                if (GekString.isNotEmpty(output)) {
                    GekLog.getInstance().info(output);
                }
                Gek.sleep(1);
            }
        }).start();
        process.waitFor();
        while (semaphore.hasQueuedThreads()) {
            Gek.sleep(1000);
        }
        process.destroy();
    }

    @Test
    public void testPingToOutput() throws InterruptedException {
        ByteArrayOutputStream dest = new ByteArrayOutputStream();
        Process process = Gek.process().command("ping", "-n", "5", "127.0.0.1").output(dest).start();
        process.waitFor();
        process.destroy();
        System.out.println(GekString.of(dest.toByteArray(), GekChars.nativeCharset()));
    }
}
