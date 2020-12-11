package test.java.xyz.srclab.common.reflect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.reflect.ClassKit;
import xyz.srclab.common.test.TestLogger;

/**
 * @author sunqian
 */
public class ClassKitTest {

    private static final TestLogger testLogger = TestLogger.DEFAULT;

    @Test
    public void testToClass() {
        Class<NewClass> newClass = ClassKit.toClass(
                "test.java.xyz.srclab.common.reflect.NewClass");
        Assert.assertEquals(newClass, NewClass.class);
    }

    @Test
    public void testToInstance() {
        NewClass newClass = ClassKit.toInstance(
                "test.java.xyz.srclab.common.reflect.NewClass");
        Assert.assertEquals(newClass, new NewClass());
    }

    @Test
    public void testToWrapper() {
        Assert.assertEquals(ClassKit.toWrapperClass(boolean.class), Boolean.class);
        Assert.assertEquals(ClassKit.toWrapperClass(byte.class), Byte.class);
        Assert.assertEquals(ClassKit.toWrapperClass(short.class), Short.class);
        Assert.assertEquals(ClassKit.toWrapperClass(char.class), Character.class);
        Assert.assertEquals(ClassKit.toWrapperClass(int.class), Integer.class);
        Assert.assertEquals(ClassKit.toWrapperClass(long.class), Long.class);
        Assert.assertEquals(ClassKit.toWrapperClass(float.class), Float.class);
        Assert.assertEquals(ClassKit.toWrapperClass(double.class), Double.class);
        Assert.assertEquals(ClassKit.toWrapperClass(void.class), Void.class);
    }
}
