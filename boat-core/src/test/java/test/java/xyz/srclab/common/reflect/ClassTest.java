package test.java.xyz.srclab.common.reflect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.reflect.Reflects;
import xyz.srclab.common.test.TestLogger;

/**
 * @author sunqian
 */
public class ClassTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testToClass() {
        Class<NewClass> newClass = Reflects.toClass(
                "test.java.xyz.srclab.common.reflect.NewClass");
        Assert.assertEquals(newClass, NewClass.class);
    }

    @Test
    public void testToInstance() {
        NewClass newClass = Reflects.toInstance(
                "test.java.xyz.srclab.common.reflect.NewClass");
        Assert.assertEquals(newClass, new NewClass());
    }

    @Test
    public void testToWrapper() {
        Assert.assertEquals(Reflects.toWrapperClass(boolean.class), Boolean.class);
        Assert.assertEquals(Reflects.toWrapperClass(byte.class), Byte.class);
        Assert.assertEquals(Reflects.toWrapperClass(short.class), Short.class);
        Assert.assertEquals(Reflects.toWrapperClass(char.class), Character.class);
        Assert.assertEquals(Reflects.toWrapperClass(int.class), Integer.class);
        Assert.assertEquals(Reflects.toWrapperClass(long.class), Long.class);
        Assert.assertEquals(Reflects.toWrapperClass(float.class), Float.class);
        Assert.assertEquals(Reflects.toWrapperClass(double.class), Double.class);
        Assert.assertEquals(Reflects.toWrapperClass(void.class), Void.class);
    }

    @Test
    public void testShortName() {
        Assert.assertEquals(Reflects.shortName(getClass()), "ClassTest");
    }

    @Test
    public void testProperty() {
        Child child = new Child();
        Reflects.setProperty(child, "child", "123456");
        Assert.assertEquals(Reflects.getProperty(child, "child"), "123456");
        Reflects.setProperty(child, "child2", "234567");
        Assert.assertEquals(Reflects.getProperty(child, "child2"), "234567");
        Reflects.setProperty(child, "parent", "345678");
        Assert.assertEquals(Reflects.getProperty(child, "parent"), "345678");
        Reflects.setProperty(child, "parent2", "456789");
        Assert.assertEquals(Reflects.getProperty(child, "parent2"), "456789");
    }

    public static class Child extends Parent {

        private String child;
        private String child2;

        public String getChild() {
            return child;
        }

        public void setChild(String child) {
            this.child = child;
        }
    }

    public static class Parent {

        private String parent;
        private String parent2;

        public String getParent() {
            return parent;
        }

        public void setParent(String parent) {
            this.parent = parent;
        }
    }
}
