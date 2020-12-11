package test.java.xyz.srclab.common.reflect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.collection.ListOps;
import xyz.srclab.common.reflect.FieldKit;
import xyz.srclab.common.test.TestLogger;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;

public class FieldKitTest {

    private static final TestLogger testLogger = TestLogger.DEFAULT;

    @Test
    public void testFind() throws Exception {
        Assert.assertEquals(
                FieldKit.findFields(NewClass.class),
                Arrays.asList(NewClass.class.getFields())
        );
        Assert.assertEquals(
                FieldKit.findDeclaredFields(NewClass.class),
                Arrays.asList(NewClass.class.getDeclaredFields())
        );
        Assert.assertEquals(
                ListOps.sorted(
                        FieldKit.findOwnedFields(NewClass.class),
                        Comparator.comparing(Field::toString)
                ),
                ListOps.sorted(
                        Arrays.asList(NewClass.class.getDeclaredFields()),
                        Comparator.comparing(Field::toString)
                )
        );
        Assert.assertEquals(
                FieldKit.findOwnedField(NewClass.class, "protectedParam"),
                NewClass.class.getDeclaredField("protectedParam")
        );
        Assert.assertEquals(
                FieldKit.findOwnedField(NewClass.class, "privateParam"),
                NewClass.class.getDeclaredField("privateParam")
        );
    }

    @Test
    public void testInvoke() {
        NewClass newClass = new NewClass();
        Assert.assertEquals(
                FieldKit.getFieldValue(
                        NewClass.class, "privateParam", newClass, true, false, true),
                "privateParam"
        );
        FieldKit.setFieldValue(
                NewClass.class,
                "privateParam",
                newClass,
                "FieldKit.setFieldValue",
                true,
                false,
                true);
        Assert.assertEquals(
                FieldKit.getFieldValue(
                        NewClass.class, "privateParam", newClass, true, false, true),
                "FieldKit.setFieldValue"
        );
    }
}
