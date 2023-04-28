package test.java.xyz.srclab.common.reflect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.reflect.BClass;

/**
 * @author sunqian
 */
public class ClassTest {

    @Test
    public void testToClass() {
        Class<ReflectClass> newClass = BClass.forName(
            "test.java.xyz.srclab.common.reflect.ReflectClass");
        Assert.assertEquals(newClass, ReflectClass.class);
    }

    @Test
    public void testNewInstance() {
        String className = ReflectClass.class.getName();
        Assert.assertEquals(BClass.instForName(className), new ReflectClass());
    }

    @Test
    public void testToWrapper() {
        Assert.assertEquals(BClass.toWrapperClass(boolean.class), Boolean.class);
        Assert.assertEquals(BClass.toWrapperClass(byte.class), Byte.class);
        Assert.assertEquals(BClass.toWrapperClass(short.class), Short.class);
        Assert.assertEquals(BClass.toWrapperClass(char.class), Character.class);
        Assert.assertEquals(BClass.toWrapperClass(int.class), Integer.class);
        Assert.assertEquals(BClass.toWrapperClass(long.class), Long.class);
        Assert.assertEquals(BClass.toWrapperClass(float.class), Float.class);
        Assert.assertEquals(BClass.toWrapperClass(double.class), Double.class);
        Assert.assertEquals(BClass.toWrapperClass(void.class), Void.class);
    }

    @Test
    public void testShortName() {
        Assert.assertEquals(BClass.getShortName(getClass()), "ClassTest");
    }
}
