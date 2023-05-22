package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.base.FsDefault;
import xyz.srclab.common.base.FsLogger;
import xyz.srclab.common.io.FsIO;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

public class FsTest {

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
