package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.common.base.JieLog;
import xyz.fslabo.common.invoke.Invoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class InvokeTest {

    @Test
    public void testInvoke() throws NoSuchMethodException {
        Constructor<?> constructor = TT.class.getDeclaredConstructor();
        Method helloStatic = TT.class.getDeclaredMethod("helloStatic", String.class, String.class);
        Method helloVirtual = TT.class.getDeclaredMethod("helloVirtual", String.class, String.class);
        testInvoke0(constructor, helloStatic, helloVirtual, true);
        testInvoke0(constructor, helloStatic, helloVirtual, false);
    }

    private void testInvoke0(Constructor<?> constructor, Method helloStatic, Method helloVirtual, boolean reflect) {
        constructor.setAccessible(true);
        helloStatic.setAccessible(true);
        helloVirtual.setAccessible(true);
        TT tt = (TT) (reflect ? Invoker.reflect(constructor) : Invoker.unreflect(constructor)).invoke(null);
        JieLog.of().info(tt);
        Assert.assertNotNull(tt);
        Assert.assertEquals(
            (reflect ? Invoker.reflect(helloStatic) : Invoker.unreflect(helloStatic))
                .invoke(null, "a", "b"),
            "helloStatic: a, b"
        );
        Assert.assertEquals(
            (reflect ? Invoker.reflect(helloVirtual) : Invoker.unreflect(helloVirtual))
                .invoke(tt, "a", "b"),
            "helloVirtual: a, b"
        );
    }

    private static class TT {

        public static String helloStatic(String a, String b) {
            return "helloStatic: " + a + ", " + b;
        }

        public String helloVirtual(String a, String b) {
            return "helloVirtual: " + a + ", " + b;
        }
    }
}


