package test;

import org.testng.annotations.Test;
import xyz.fslabo.common.base.*;
import xyz.fslabo.common.io.JieIO;

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
        Process process = Jie.process().command(command).start();
        process.waitFor();
        String output = JieIO.readString(process.getInputStream(), JieChars.nativeCharset());
        process.destroy();
        System.out.println(output);
    }

    @Test
    public void testPing() throws InterruptedException {
        Process process = Jie.process().command("ping", "-n", "5", "127.0.0.1").start();
        Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        Jie.thread().task(() -> {
            while (true) {
                String output = JieIO.avalaibleString(process.getInputStream(), JieChars.nativeCharset());
                if (output == null) {
                    semaphore.release();
                    return;
                }
                if (GekString.isNotEmpty(output)) {
                    GekLog.getInstance().info(output);
                }
                Jie.sleep(1);
            }
        }).start();
        process.waitFor();
        while (semaphore.hasQueuedThreads()) {
            Jie.sleep(1000);
        }
        process.destroy();
    }

    @Test
    public void testPingToOutput() throws InterruptedException {
        ByteArrayOutputStream dest = new ByteArrayOutputStream();
        Process process = Jie.process().command("ping", "-n", "5", "127.0.0.1").output(dest).start();
        process.waitFor();
        process.destroy();
        System.out.println(GekString.of(dest.toByteArray(), JieChars.nativeCharset()));
    }
}
