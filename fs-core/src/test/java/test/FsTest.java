package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.*;
import xyz.srclab.common.io.FsIO;

import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class FsTest {

    private static final String ECHO_CONTENT = "hello world!";

    @Test
    public void testThrow() {
        FsLogger.system().info(Fs.stackTraceToString(
            new IllegalArgumentException(new IllegalStateException(new NullPointerException())))
        );
        FsLogger.system().info(Fs.stackTraceToString(
            new IllegalArgumentException(new IllegalStateException(new NullPointerException())),
            " : ")
        );
    }

    @Test
    public void testFindStackTraceCaller() {
        T1.invoke1();
    }

    @Test
    public void testEqual() {
        Assert.assertTrue(Fs.equals(new int[]{1, 2, 3}, new int[]{1, 2, 3}));
        Assert.assertFalse(Fs.equals(new int[]{1, 2}, new int[]{1, 2, 3}));
        Assert.assertFalse(Fs.equalsWith(new int[]{1, 2, 3}, new int[]{1, 2, 3}, false, false));
        Assert.assertTrue(Fs.equals(
            new Object[]{new int[]{1, 2, 3}, new int[]{1, 2, 3}},
            new Object[]{new int[]{1, 2, 3}, new int[]{1, 2, 3}}
        ));
        Assert.assertFalse(Fs.equals(
            new Object[]{new int[]{1, 2, 3}, new int[]{1, 2}},
            new Object[]{new int[]{1, 2, 3}, new int[]{1, 2, 3}}
        ));
        Assert.assertFalse(Fs.equalsWith(
            new Object[]{new int[]{1, 2, 3}, new int[]{1, 2, 3}},
            new Object[]{new int[]{1, 2, 3}, new int[]{1, 2, 3}}, false, true
        ));
    }

    @Test
    public void testRes() throws IOException {
        URL f1 = Fs.findRes("/t2/f1.txt");
        Assert.assertEquals(FsIO.readString(f1.openStream(), FsDefault.charset(), true), "f1.txt");
        Set<URL> set = Fs.findAllRes("/t2/f2.txt");
        for (URL url : set) {
            Assert.assertEquals(FsIO.readString(url.openStream(), FsDefault.charset(), true), "f2.txt");
        }
    }

    @Test
    public void testProcess() throws InterruptedException {
        if (FsSystem.isLinux() || FsSystem.isMac() || FsSystem.isBsd()) {
            testEcho("echo " + ECHO_CONTENT);
        }
        if (FsSystem.isWindows()) {
            testEcho("cmd.exe /c echo " + ECHO_CONTENT);
        }
    }

    @Test
    public void testPing() throws InterruptedException {
        Process process = Fs.runProcess("ping", "-n", "5", "127.0.0.1");
        Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        Fs.runThread(() -> {
            while (true) {
                String output = FsIO.avalaibleString(process.getInputStream(), FsSystem.nativeCharset());
                if (output == null) {
                    semaphore.release();
                    return;
                }
                if (!FsString.isEmpty(output)) {
                    FsLogger.system().info(output);
                }
                Fs.sleep(1);
            }
        });
        process.waitFor();
        while (semaphore.hasQueuedThreads()) {
            Fs.sleep(1000);
        }
        process.destroy();
    }

    private void testEcho(String command) throws InterruptedException {
        Process process = Fs.runProcess(command);
        process.waitFor();
        String output = FsIO.avalaibleString(process.getInputStream(), FsSystem.nativeCharset());
        FsLogger.system().info(output);
        Assert.assertEquals(output, ECHO_CONTENT + FsSystem.getLineSeparator());
        process.destroy();
    }

    @Test
    public void testThread() throws InterruptedException {
        Thread thread = Fs.runThread("hahaha", () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Assert.assertEquals(thread.getName(), "hahaha");
        Assert.assertFalse(thread.isDaemon());
        thread.join();
        Assert.assertFalse(thread.isAlive());
    }

    @Test
    public void testSystem() {
        FsLogger.system().info(FsSystem.getJavaVersion());
        FsLogger.system().info(FsSystem.javaMajorVersion());
        FsLogger.system().info(FsSystem.nativeCharset());
        FsLogger.system().info(FsSystem.getOsName());
        FsLogger.system().info(FsSystem.isWindows());
        FsLogger.system().info(FsSystem.isLinux());
        FsLogger.system().info(FsSystem.isBsd());
        FsLogger.system().info(FsSystem.isMac());
        FsLogger.system().info(FsSystem.isJdk9OrHigher());
    }

    private static final class T1 {
        public static void invoke1() {
            T2.invoke2();
        }
    }

    private static final class T2 {
        public static void invoke2() {
            T3.invoke3();
        }
    }

    private static final class T3 {
        public static void invoke3() {
            StackTraceElement element1 = Fs.findStackTraceCaller(T1.class.getName(), "invoke1");
            Assert.assertEquals(element1.getClassName(), FsTest.class.getName());
            Assert.assertEquals(element1.getMethodName(), "testFindStackTraceCaller");
            StackTraceElement element2 = Fs.findStackTraceCaller(T2.class.getName(), "invoke2");
            Assert.assertEquals(element2.getClassName(), T1.class.getName());
            Assert.assertEquals(element2.getMethodName(), "invoke1");
            StackTraceElement element3 = Fs.findStackTraceCaller(T3.class.getName(), "invoke3");
            Assert.assertEquals(element3.getClassName(), T2.class.getName());
            Assert.assertEquals(element3.getMethodName(), "invoke2");
        }
    }
}
