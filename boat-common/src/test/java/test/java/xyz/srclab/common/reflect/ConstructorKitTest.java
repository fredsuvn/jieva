package test.java.xyz.srclab.common.reflect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.reflect.ConstructorKit;
import xyz.srclab.common.test.TestLogger;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class ConstructorKitTest {

    private static final TestLogger testLogger = TestLogger.DEFAULT;

    @Test
    public void testFind() throws Exception {
        Assert.assertEquals(
                ConstructorKit.findConstructors(NewClass.class),
                Arrays.asList(NewClass.class.getConstructors())
        );
        Assert.assertEquals(
                ConstructorKit.findDeclaredConstructors(NewClass.class),
                Arrays.asList(NewClass.class.getDeclaredConstructors())
        );
        Assert.assertEquals(
                ConstructorKit.findDeclaredConstructor(NewClass.class, String.class),
                NewClass.class.getDeclaredConstructor(String.class)
        );
        Assert.assertEquals(
                ConstructorKit.findDeclaredConstructor(NewClass.class, String.class, String.class),
                NewClass.class.getDeclaredConstructor(String.class, String.class)
        );
    }

    @Test
    public void testInvoke() {
        Constructor<NewClass> classConstructor = ConstructorKit.findDeclaredConstructor(
                NewClass.class, String.class, String.class);
        Assert.assertNotNull(classConstructor);
        NewClass result = ConstructorKit.invokeForcibly(classConstructor, "1", "2");
        Assert.assertEquals(result, new NewClass("1 : 2"));
    }
}
