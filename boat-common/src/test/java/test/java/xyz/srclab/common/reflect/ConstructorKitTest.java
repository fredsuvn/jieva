package test.java.xyz.srclab.common.reflect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.collection.ListOps;
import xyz.srclab.common.reflect.ConstructorKit;
import xyz.srclab.common.test.TestLogger;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;

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
                ListOps.sorted(
                        ConstructorKit.findOwnedConstructors(NewClass.class),
                        Comparator.comparing(Constructor::toString)
                ),
                ListOps.sorted(
                        Arrays.asList(NewClass.class.getDeclaredConstructors()),
                        Comparator.comparing(Constructor::toString)
                )
        );
        Assert.assertEquals(
                ConstructorKit.findOwnedConstructor(NewClass.class, String.class),
                NewClass.class.getDeclaredConstructor(String.class)
        );
        Assert.assertEquals(
                ConstructorKit.findOwnedConstructor(NewClass.class, String.class, String.class),
                NewClass.class.getDeclaredConstructor(String.class, String.class)
        );
    }

    @Test
    public void testInvoke() {
        Constructor<NewClass> classConstructor = ConstructorKit.findOwnedConstructor(
                NewClass.class, String.class, String.class);
        Assert.assertNotNull(classConstructor);
        NewClass result = ConstructorKit.invokeForcibly(classConstructor, "1", "2");
        Assert.assertEquals(result, new NewClass("1 : 2"));
    }
}
