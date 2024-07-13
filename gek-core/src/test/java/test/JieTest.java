package test;

import org.apache.commons.beanutils.BeanUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.common.base.*;
import xyz.fslabo.common.io.JieIO;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

public class JieTest {

    @Test
    public void testThrow() {
        BeanUtils.copyProperties(null, null);
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
        Assert.assertTrue(Jie.equals(new int[]{1, 2, 3}, new int[]{1, 2, 3}));
        Assert.assertFalse(Jie.equals(new int[]{1, 2}, new int[]{1, 2, 3}));
        Assert.assertFalse(Jie.equalsWith(new int[]{1, 2, 3}, new int[]{1, 2, 3}, false, false));
        Assert.assertTrue(Jie.equals(
            new Object[]{new int[]{1, 2, 3}, new int[]{1, 2, 3}},
            new Object[]{new int[]{1, 2, 3}, new int[]{1, 2, 3}}
        ));
        Assert.assertFalse(Jie.equals(
            new Object[]{new int[]{1, 2, 3}, new int[]{1, 2}},
            new Object[]{new int[]{1, 2, 3}, new int[]{1, 2, 3}}
        ));
        Assert.assertFalse(Jie.equalsWith(
            new Object[]{new int[]{1, 2, 3}, new int[]{1, 2, 3}},
            new Object[]{new int[]{1, 2, 3}, new int[]{1, 2, 3}}, false, true
        ));
    }

    @Test
    public void testRes() throws IOException {
        URL f1 = Jie.findRes("/t2/f1.txt");
        Assert.assertEquals(JieIO.readString(f1.openStream(), JieChars.defaultCharset()), "f1.txt");
        Set<URL> set = Jie.findAllRes("/t2/f2.txt");
        for (URL url : set) {
            Assert.assertEquals(JieIO.readString(url.openStream(), JieChars.defaultCharset()), "f2.txt");
        }
    }

    @Test
    public void testSystem() {
        GekLog.getInstance().info(GekSystem.getJavaVersion());
        GekLog.getInstance().info(GekSystem.javaMajorVersion());
        GekLog.getInstance().info(JieChars.nativeCharset());
        GekLog.getInstance().info(GekSystem.getOsName());
        GekLog.getInstance().info(GekSystem.isWindows());
        GekLog.getInstance().info(GekSystem.isLinux());
        GekLog.getInstance().info(GekSystem.isBsd());
        GekLog.getInstance().info(GekSystem.isMac());
        GekLog.getInstance().info(GekSystem.isJdk9OrHigher());
    }

    @Test
    public void testEnum() {
        Assert.assertEquals(Te.A, Jie.findEnum(Te.class, 0));
        Assert.assertEquals(Te.B, Jie.findEnum(Te.class, "B", false));
        Assert.assertEquals(Te.C, Jie.findEnum(Te.class, "c", true));
        Assert.assertNull(Jie.findEnum(Te.class, 10));
        Assert.assertNull(Jie.findEnum(Te.class, "d", false));
        Assert.expectThrows(IllegalArgumentException.class, () -> Jie.findEnum(Te.class, -1));
        Assert.expectThrows(IllegalArgumentException.class, () -> Jie.findEnum(Jie.class, -1));
        Assert.expectThrows(IllegalArgumentException.class, () -> Jie.findEnum(Jie.class, "a", true));
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
            Assert.assertEquals(element1.getClassName(), JieTest.class.getName());
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
