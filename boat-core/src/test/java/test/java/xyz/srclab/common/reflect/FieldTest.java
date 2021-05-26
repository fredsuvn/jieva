package test.java.xyz.srclab.common.reflect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.reflect.Reflects;
import xyz.srclab.common.test.TestLogger;

import java.lang.reflect.Field;
import java.util.Arrays;

public class FieldTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    Field superPublicField = SuperReflectClass.class.getDeclaredField("superPublicField");
    Field superProtectedField = SuperReflectClass.class.getDeclaredField("superProtectedField");
    Field superPrivateField = SuperReflectClass.class.getDeclaredField("superPrivateField");
    Field superPackageField = SuperReflectClass.class.getDeclaredField("superPackageField");
    Field publicField = ReflectClass.class.getDeclaredField("publicField");
    Field protectedField = ReflectClass.class.getDeclaredField("protectedField");
    Field privateField = ReflectClass.class.getDeclaredField("privateField");
    Field packageField = ReflectClass.class.getDeclaredField("packageField");
    Field subPublicField = SubReflectClass.class.getDeclaredField("subPublicField");
    Field subProtectedField = SubReflectClass.class.getDeclaredField("subProtectedField");
    Field subPrivateField = SubReflectClass.class.getDeclaredField("subPrivateField");
    Field subPackageField = SubReflectClass.class.getDeclaredField("subPackageField");

    public FieldTest() throws NoSuchFieldException {
    }

    @Test
    public void testField() throws Exception {
        Assert.assertEquals(
            Reflects.fields(ReflectClass.class),
            Arrays.asList(ReflectClass.class.getFields())
        );
        Assert.assertEquals(
            Reflects.declaredFields(ReflectClass.class),
            Arrays.asList(ReflectClass.class.getDeclaredFields())
        );
        Assert.assertEquals(
            Reflects.ownedFields(SubReflectClass.class),
            Arrays.asList(
                subPublicField, publicField, superPublicField,
                subProtectedField, subPrivateField, subPackageField
            )
        );

        Assert.assertEquals(
            Reflects.ownedField(ReflectClass.class, "protectedField"),
            ReflectClass.class.getDeclaredField("protectedField")
        );
        Assert.assertNull(Reflects.ownedFieldOrNull(ReflectClass.class, "superProtectedField"));

        Assert.assertEquals(
            Reflects.searchFields(SubReflectClass.class, true, f -> f.getName().contains("ackage")),
            Arrays.asList(subPackageField, packageField, superPackageField)
        );
    }

    @Test
    public void testFieldValue() {
        ReflectClass reflectClass = new ReflectClass();

        Assert.assertEquals(
            Reflects.getDeepFieldValue(
                ReflectClass.class, "superPrivateField", reflectClass, true),
            "superPrivateField"
        );
        Reflects.setDeepFieldValue(
            ReflectClass.class,
            "superPrivateField",
            reflectClass,
            "superPrivateField2",
            true
        );
        Assert.assertEquals(
            Reflects.getDeepFieldValue(
                ReflectClass.class, "superPrivateField", reflectClass, true),
            "superPrivateField2"
        );

        Assert.expectThrows(IllegalAccessException.class, () ->
            Reflects.getDeepFieldValue(
                ReflectClass.class, "superPrivateField", reflectClass, false)
        );
        Assert.expectThrows(NoSuchFieldException.class, () ->
            Reflects.getFieldValue(
                ReflectClass.class, "superPrivateField", reflectClass, false)
        );

        Assert.assertEquals(
            Reflects.getStaticFieldValue(StaticClass.class, "STATIC_STRING"),
            StaticClass.STATIC_STRING
        );
    }
}
