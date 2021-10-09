package test.java.xyz.srclab.common.reflect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.collect.Collects;
import xyz.srclab.common.reflect.Reflects;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author sunqian
 */
public class MethodTest {

    Method superPublicMethod = SuperReflectClass.class.getDeclaredMethod("superPublicMethod");
    Method superProtectedMethod = SuperReflectClass.class.getDeclaredMethod("superProtectedMethod");
    Method superPrivateMethod = SuperReflectClass.class.getDeclaredMethod("superPrivateMethod");
    Method superPackageMethod = SuperReflectClass.class.getDeclaredMethod("superPackageMethod");
    Method publicMethod = ReflectClass.class.getDeclaredMethod("publicMethod");
    Method protectedMethod = ReflectClass.class.getDeclaredMethod("protectedMethod");
    Method privateMethod = ReflectClass.class.getDeclaredMethod("privateMethod");
    Method packageMethod = ReflectClass.class.getDeclaredMethod("packageMethod");
    Method subPublicMethod = SubReflectClass.class.getDeclaredMethod("subPublicMethod");
    Method subProtectedMethod = SubReflectClass.class.getDeclaredMethod("subProtectedMethod");
    Method subPrivateMethod = SubReflectClass.class.getDeclaredMethod("subPrivateMethod");
    Method subPackageMethod = SubReflectClass.class.getDeclaredMethod("subPackageMethod");

    public MethodTest() throws NoSuchMethodException {
    }

    @Test
    public void testFind() throws Exception {
        Assert.assertEquals(
            Reflects.getMethods(ReflectClass.class),
            Arrays.asList(ReflectClass.class.getMethods())
        );
        Assert.assertEquals(
            Reflects.getDeclaredMethods(ReflectClass.class),
            Arrays.asList(ReflectClass.class.getDeclaredMethods())
        );
        Assert.assertEquals(
            Collects.sorted(Reflects.getOwnedMethods(SubReflectClass.class), Comparator.comparing(Method::toString)),
            Collects.sorted(Arrays.asList(
                subPublicMethod,
                publicMethod,
                ReflectClass.class.getMethod("equals", Object.class),
                ReflectClass.class.getMethod("hashCode"),
                ReflectClass.class.getMethod("wait"),
                ReflectClass.class.getMethod("wait", long.class),
                ReflectClass.class.getMethod("wait", long.class, int.class),
                ReflectClass.class.getMethod("toString"),
                ReflectClass.class.getMethod("getClass"),
                ReflectClass.class.getMethod("notify"),
                ReflectClass.class.getMethod("notifyAll"),
                superPublicMethod,
                subProtectedMethod,
                subPrivateMethod,
                subPackageMethod
            ), Comparator.comparing(Method::toString))
        );

        Assert.assertEquals(
            Reflects.getOwnedMethodOrNull(ReflectClass.class, "protectedMethod"),
            ReflectClass.class.getDeclaredMethod("protectedMethod")
        );
        Assert.assertNull(Reflects.getOwnedMethodOrNull(ReflectClass.class, "superProtectedMethod"));

        Assert.assertEquals(
            Reflects.searchMethods(SubReflectClass.class, true, m -> m.getName().contains("ackage")),
            Arrays.asList(subPackageMethod, packageMethod, superPackageMethod)
        );
    }

    @Test
    public void testInvoke() {
        ReflectClass reflectClass = new ReflectClass();
        Assert.assertEquals(
            Reflects.invoke(superPublicMethod, reflectClass),
            "superPublicField"
        );
        Assert.assertEquals(
            Reflects.enforce(privateMethod, reflectClass),
            "privateField"
        );
        Assert.expectThrows(IllegalAccessException.class, () ->
            Reflects.invoke(superPrivateMethod, reflectClass)
        );
        Assert.assertEquals(
            Reflects.enforce(superPrivateMethod, reflectClass),
            "superPrivateField"
        );
    }
}
