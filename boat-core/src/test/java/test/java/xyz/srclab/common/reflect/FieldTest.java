package test.java.xyz.srclab.common.reflect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.reflect.Reflects;
import xyz.srclab.common.test.TestLogger;

import java.lang.reflect.Field;
import java.util.Arrays;

public class FieldTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

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

    public FieldTest() throws NoSuchFieldException {
    }

    @Test
    public void testFind() throws Exception {
        Assert.assertEquals(
                Reflects.fields(NewClass.class),
                Arrays.asList(NewClass.class.getFields())
        );
        Assert.assertEquals(
                Reflects.declaredFields(NewClass.class),
                Arrays.asList(NewClass.class.getDeclaredFields())
        );
        Assert.assertEquals(
                Reflects.ownedFields(SubNewClass.class),
                Arrays.asList(
                        subPublicField, publicField, superPublicField,
                        subProtectedField, subPrivateField, subPackageField
                )
        );

        Assert.assertEquals(
                Reflects.ownedField(NewClass.class, "protectedField"),
                NewClass.class.getDeclaredField("protectedField")
        );
        Assert.assertNull(Reflects.ownedFieldOrNull(NewClass.class, "superProtectedField"));

        Assert.assertEquals(
                Reflects.searchFields(SubNewClass.class, f -> f.getName().contains("ackage")),
                Arrays.asList(subPackageField, packageField, superPackageField)
        );
    }

    @Test
    public void testInvoke() {
        NewClass newClass = new NewClass();

        Assert.assertEquals(
                Reflects.getFieldValue(
                        NewClass.class, "superPrivateField", newClass, true, true),
                "superPrivateField"
        );
        Reflects.setFieldValue(
                NewClass.class,
                "superPrivateField",
                newClass,
                "superPrivateField2",
                true,
                true);
        Assert.assertEquals(
                Reflects.getFieldValue(
                        NewClass.class, "superPrivateField", newClass, true, true),
                "superPrivateField2"
        );

        Assert.expectThrows(NoSuchFieldException.class, () ->
                Reflects.getFieldValue(
                        NewClass.class, "superPrivateField", newClass, false, true)
        );

        Assert.assertEquals(
                Reflects.getFieldValue(
                        newClass, "superPrivateField", true, true),
                "superPrivateField2"
        );
        Reflects.setFieldValue(
                newClass,
                "superPrivateField",
                "superPrivateField222",
                true,
                true);
        Assert.assertEquals(
                Reflects.getFieldValue(
                        NewClass.class, "superPrivateField", newClass, true, true),
                "superPrivateField222"
        );
    }
}
