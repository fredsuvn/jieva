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
                "test.java.xyz.srclab.common.reflect.ClassKitTest$NewClass");
        Assert.assertEquals(newClass, NewClass.class);
    }

    public static class NewClass {

        static {
            testLogger.log("Create: " + NewClass.class);
        }
    }
}
