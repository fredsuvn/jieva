package test.java.xyz.srclab.common.reflect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.collect.BSet;
import xyz.srclab.common.reflect.BField;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;

public class FieldTest {

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
            BField.getFields(ReflectClass.class),
            Arrays.asList(ReflectClass.class.getFields())
        );
        Assert.assertEquals(
            BField.getDeclaredFields(ReflectClass.class),
            Arrays.asList(ReflectClass.class.getDeclaredFields())
        );
        Assert.assertEquals(
            new HashSet<>(BField.getOwnedFields(SubReflectClass.class)),
            BSet.newSet(
                subPublicField, publicField, superPublicField,
                subProtectedField, subPrivateField, subPackageField
            )
        );

        Assert.assertEquals(
            BField.getOwnedField(ReflectClass.class, "protectedField"),
            ReflectClass.class.getDeclaredField("protectedField")
        );
        Assert.assertNull(BField.getOwnedFieldOrNull(ReflectClass.class, "superProtectedField"));
    }

    @Test
    public void testSearchField() {
        Assert.assertEquals(
            new HashSet<>(BField.searchFields(SubReflectClass.class, true, f -> f.getName().contains("ackage"))),
            BSet.newSet(subPackageField, packageField, superPackageField)
        );
    }

    @Test
    public void testFieldValue() {
        ReflectClass reflectClass = new ReflectClass();
        Assert.expectThrows(NoSuchFieldException.class, () ->
            BField.getFieldValue(
                ReflectClass.class, "superPrivateField", reflectClass, false)
        );
        Assert.assertEquals(
            BField.getStaticFieldValue(StaticClass.class, "STATIC_STRING"),
            StaticClass.STATIC_STRING
        );
    }
}
