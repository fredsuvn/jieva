package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Current;

import java.lang.reflect.Method;

/**
 * @author sunqian
 */
public class CurrentTest {

    @Test
    public void testCurrent() {
        Current.set("123", "321");
        Assert.assertEquals(Current.get("123"), "321");
        Assert.assertEquals(Current.getOrElse("1234", "4321"), "4321");
        Assert.assertEquals(Current.getOrNull("1234"), (Object) null);
        Assert.expectThrows(IllegalArgumentException.class, () ->
                Current.getOrThrow("1234", k -> new IllegalArgumentException(k.toString())));

        @Nullable StackTraceElement stackTraceElement = new Caller().call();
        Assert.assertNotNull(stackTraceElement);
        Assert.assertEquals(stackTraceElement.getClassName(), CurrentTest.class.getName());
        @Nullable StackTraceElement stackTraceElement2 = new Caller().call2();
        Assert.assertNotNull(stackTraceElement2);
        Assert.assertEquals(stackTraceElement2.getClassName(), CurrentTest.class.getName());
    }

    private static class Caller {

        @Nullable
        public StackTraceElement call() {
            return Current.callerFrameOrNull(getClass());
        }

        @Nullable
        public StackTraceElement call2() {
            try {
                Method callMethod = Caller.class.getMethod("call2");
                return Current.callerFrameOrNull(callMethod);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
