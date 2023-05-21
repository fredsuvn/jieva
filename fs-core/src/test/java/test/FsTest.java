package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Fs;

public class FsTest {

    @Test
    public void testThrow() {
        Out.println(Fs.stackTraceToString(
            new IllegalArgumentException(new IllegalStateException(new NullPointerException())))
        );
        Out.println(Fs.stackTraceToString(
            new IllegalArgumentException(new IllegalStateException(new NullPointerException())),
            " : ")
        );
    }

    @Test
    public void testFindStackTraceCaller() {
        T1.invoke1();
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
