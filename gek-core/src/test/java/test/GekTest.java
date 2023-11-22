package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fsgek.common.base.*;
import xyz.fsgek.common.io.GekIO;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

public class GekTest {

    @Test
    public void testThrow() {
        GekLog.getInstance().info(GekTrace.toString(
            new IllegalArgumentException(new IllegalStateException(new NullPointerException())))
        );
        GekLog.getInstance().info(GekTrace.toString(
            new IllegalArgumentException(new IllegalStateException(new NullPointerException())),
            " : ")
        );
    }

    @Test
    public void testFindCallerStackTrace() {
        T1.invoke1();
    }

    @Test
    public void testEqual() {
        Assert.assertTrue(Gek.equals(new int[]{1, 2, 3}, new int[]{1, 2, 3}));
        Assert.assertFalse(Gek.equals(new int[]{1, 2}, new int[]{1, 2, 3}));
        Assert.assertFalse(Gek.equalsWith(new int[]{1, 2, 3}, new int[]{1, 2, 3}, false, false));
        Assert.assertTrue(Gek.equals(
            new Object[]{new int[]{1, 2, 3}, new int[]{1, 2, 3}},
            new Object[]{new int[]{1, 2, 3}, new int[]{1, 2, 3}}
        ));
        Assert.assertFalse(Gek.equals(
            new Object[]{new int[]{1, 2, 3}, new int[]{1, 2}},
            new Object[]{new int[]{1, 2, 3}, new int[]{1, 2, 3}}
        ));
        Assert.assertFalse(Gek.equalsWith(
            new Object[]{new int[]{1, 2, 3}, new int[]{1, 2, 3}},
            new Object[]{new int[]{1, 2, 3}, new int[]{1, 2, 3}}, false, true
        ));
    }

    @Test
    public void testRes() throws IOException {
        URL f1 = Gek.findRes("/t2/f1.txt");
        Assert.assertEquals(GekIO.readString(f1.openStream(), GekChars.defaultCharset()), "f1.txt");
        Set<URL> set = Gek.findAllRes("/t2/f2.txt");
        for (URL url : set) {
            Assert.assertEquals(GekIO.readString(url.openStream(), GekChars.defaultCharset()), "f2.txt");
        }
    }

    @Test
    public void testSystem() {
        GekLog.getInstance().info(GekSystem.getJavaVersion());
        GekLog.getInstance().info(GekSystem.javaMajorVersion());
        GekLog.getInstance().info(GekChars.nativeCharset());
        GekLog.getInstance().info(GekSystem.getOsName());
        GekLog.getInstance().info(GekSystem.isWindows());
        GekLog.getInstance().info(GekSystem.isLinux());
        GekLog.getInstance().info(GekSystem.isBsd());
        GekLog.getInstance().info(GekSystem.isMac());
        GekLog.getInstance().info(GekSystem.isJdk9OrHigher());
    }

    @Test
    public void testEnum() {
        Assert.assertEquals(Te.A, Gek.findEnum(Te.class, 0));
        Assert.assertEquals(Te.B, Gek.findEnum(Te.class, "B", false));
        Assert.assertEquals(Te.C, Gek.findEnum(Te.class, "c", true));
        Assert.assertNull(Gek.findEnum(Te.class, 10));
        Assert.assertNull(Gek.findEnum(Te.class, "d", false));
        Assert.expectThrows(IllegalArgumentException.class, () -> Gek.findEnum(Te.class, -1));
        Assert.expectThrows(IllegalArgumentException.class, () -> Gek.findEnum(Gek.class, -1));
        Assert.expectThrows(IllegalArgumentException.class, () -> Gek.findEnum(Gek.class, "a", true));
    }

    public enum Te {
        A, B, C
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
            StackTraceElement element1 = GekTrace.findCallerTrace(T1.class.getName(), "invoke1");
            Assert.assertEquals(element1.getClassName(), GekTest.class.getName());
            Assert.assertEquals(element1.getMethodName(), "testFindCallerStackTrace");
            StackTraceElement element2 = GekTrace.findCallerTrace(T2.class.getName(), "invoke2");
            Assert.assertEquals(element2.getClassName(), T1.class.getName());
            Assert.assertEquals(element2.getMethodName(), "invoke1");
            StackTraceElement element3 = GekTrace.findCallerTrace(T3.class.getName(), "invoke3");
            Assert.assertEquals(element3.getClassName(), T2.class.getName());
            Assert.assertEquals(element3.getMethodName(), "invoke2");
        }
    }
}
