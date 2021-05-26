package test.java.xyz.srclab.common.reflect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.reflect.Reflects;
import xyz.srclab.common.test.TestLogger;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class ConstructorTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testFind() throws Exception {
        Assert.assertEquals(
            Reflects.constructors(ReflectClass.class),
            Arrays.asList(ReflectClass.class.getConstructors())
        );
        Assert.assertEquals(
            Reflects.declaredConstructors(ReflectClass.class),
            Arrays.asList(ReflectClass.class.getDeclaredConstructors())
        );
        Assert.assertEquals(
            Reflects.declaredConstructor(ReflectClass.class, String.class),
            ReflectClass.class.getDeclaredConstructor(String.class)
        );
        Assert.assertEquals(
            Reflects.declaredConstructor(ReflectClass.class, String.class, String.class),
            ReflectClass.class.getDeclaredConstructor(String.class, String.class)
        );
    }

    @Test
    public void testInvoke() {
        Constructor<ReflectClass> classConstructor = Reflects.declaredConstructor(
            ReflectClass.class, String.class, String.class);
        Assert.assertNotNull(classConstructor);
        ReflectClass result = Reflects.enforce(classConstructor, "1", "2");
        Assert.assertEquals(result, new ReflectClass("1 : 2"));
    }
}
