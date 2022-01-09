package test.java.xyz.srclab.common.reflect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.reflect.BConstructor;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class ConstructorTest {

    @Test
    public void testFind() throws Exception {
        Assert.assertEquals(
            BConstructor.getConstructors(ReflectClass.class),
            Arrays.asList(ReflectClass.class.getConstructors())
        );
        Assert.assertEquals(
            BConstructor.getDeclaredConstructors(ReflectClass.class),
            Arrays.asList(ReflectClass.class.getDeclaredConstructors())
        );
        Assert.assertEquals(
            BConstructor.getDeclaredConstructor(ReflectClass.class, String.class),
            ReflectClass.class.getDeclaredConstructor(String.class)
        );
        Assert.assertEquals(
            BConstructor.getDeclaredConstructor(ReflectClass.class, String.class, String.class),
            ReflectClass.class.getDeclaredConstructor(String.class, String.class)
        );
    }

    @Test
    public void testInvoke() {
        Constructor<ReflectClass> classConstructor = BConstructor.getDeclaredConstructor(
            ReflectClass.class, String.class, String.class);
        Assert.assertNotNull(classConstructor);
        ReflectClass result = BConstructor.enforce(classConstructor, "1", "2");
        Assert.assertEquals(result, new ReflectClass("1 : 2"));
    }
}
