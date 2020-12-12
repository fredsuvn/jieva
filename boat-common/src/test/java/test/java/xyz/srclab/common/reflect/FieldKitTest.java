package test.java.xyz.srclab.common.reflect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.reflect.FieldKit;
import xyz.srclab.common.test.TestLogger;

import java.lang.reflect.Field;
import java.util.Arrays;

public class FieldKitTest {

    private static final TestLogger testLogger = TestLogger.DEFAULT;

    @Test
    public void testFind() throws Exception {
        Field superPublicField = SuperNewClass.class.getDeclaredField("superPublicField");
        Field superProtectedField = SuperNewClass.class.getDeclaredField("superProtectedField");
        Field superPrivateField = SuperNewClass.class.getDeclaredField("superPrivateField");
        Field superPackageField = SuperNewClass.class.getDeclaredField("superPackageField");
        Field publicField = NewClass.class.getDeclaredField("publicField");
        Field protectedField = NewClass.class.getDeclaredField("protectedField");
        Field privateField = NewClass.class.getDeclaredField("privateField");
        Field packageField = NewClass.class.getDeclaredField("packageField");
        Field subPublicField = SubNewClass.class.getDeclaredField("subPublicField");
        Field subProtectedField = SubNewClass.class.getDeclaredField("subProtectedField");
        Field subPrivateField = SubNewClass.class.getDeclaredField("subPrivateField");
        Field subPackageField = SubNewClass.class.getDeclaredField("subPackageField");


        Assert.assertEquals(
                FieldKit.findFields(NewClass.class),
                Arrays.asList(NewClass.class.getFields())
        );
        Assert.assertEquals(
                FieldKit.findDeclaredFields(NewClass.class),
                Arrays.asList(NewClass.class.getDeclaredFields())
        );
        Assert.assertEquals(
                FieldKit.findOwnedFields(SubNewClass.class),
                Arrays.asList(
                        subPublicField, publicField, superPublicField,
                        subProtectedField, subPrivateField, subPackageField
                )
        );

        Assert.assertEquals(
                FieldKit.findOwnedField(NewClass.class, "protectedField"),
                NewClass.class.getDeclaredField("protectedField")
        );
        Assert.assertNull(FieldKit.findOwnedField(NewClass.class, "superProtectedField"));
    }

    @Test
    public void testInvoke() {
        NewClass newClass = new NewClass();

        Assert.assertEquals(
                FieldKit.getFieldValue(
                        NewClass.class, "superPrivateField", newClass, true, true),
                "superPrivateField"
        );
        FieldKit.setFieldValue(
                NewClass.class,
                "superPrivateField",
                newClass,
                "superPrivateField2",
                true,
                true);
        Assert.assertEquals(
                FieldKit.getFieldValue(
                        NewClass.class, "superPrivateField", newClass, true, true),
                "superPrivateField2"
        );

        Assert.expectThrows(NoSuchFieldException.class, () ->
                FieldKit.getFieldValue(
                        NewClass.class, "superPrivateField", newClass, false, true)
        );
    }
}
