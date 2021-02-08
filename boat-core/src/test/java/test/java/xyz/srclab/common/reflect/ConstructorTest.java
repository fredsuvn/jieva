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
                Reflects.constructors(NewClass.class),
                Arrays.asList(NewClass.class.getConstructors())
        );
        Assert.assertEquals(
                Reflects.declaredConstructors(NewClass.class),
                Arrays.asList(NewClass.class.getDeclaredConstructors())
        );
        Assert.assertEquals(
                Reflects.declaredConstructor(NewClass.class, String.class),
                NewClass.class.getDeclaredConstructor(String.class)
        );
        Assert.assertEquals(
                Reflects.declaredConstructor(NewClass.class, String.class, String.class),
                NewClass.class.getDeclaredConstructor(String.class, String.class)
        );
    }

    @Test
    public void testInvoke() {
        Constructor<NewClass> classConstructor = Reflects.declaredConstructor(
                NewClass.class, String.class, String.class);
        Assert.assertNotNull(classConstructor);
        NewClass result = Reflects.invokeForcibly(classConstructor, "1", "2");
        Assert.assertEquals(result, new NewClass("1 : 2"));
    }
}
