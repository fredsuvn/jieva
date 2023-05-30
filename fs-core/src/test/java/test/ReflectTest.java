package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.reflect.FsReflect;
import xyz.srclab.common.reflect.FsType;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class ReflectTest {

    @Test
    public void testLastName() {
        Assert.assertEquals(FsReflect.getLastName(ReflectTest.class), "ReflectTest");
        Assert.assertEquals(FsReflect.getLastName(T1.class), "ReflectTest$T1");
    }

    @Test
    public void testType() {
        //parametrized
        Type t1 = new TypeRef<A<Integer>>() {
        }.getType();
        Assert.assertEquals(
            t1.toString(),
            "test.A<java.lang.Integer>"
        );
        Type t2 = new TypeRef<A<Integer>.B<String>>() {
        }.getType();
        Assert.assertEquals(
            t2.toString(),
            "test.A<java.lang.Integer>$B<java.lang.String>"
        );
        ParameterizedType p1 = FsType.parameterizedType(A.class, Arrays.asList(Integer.class));
        Assert.assertEquals(
            p1.toString(),
            "test.A<java.lang.Integer>"
        );
        Assert.assertEquals(
            t1,
            p1
        );
        ParameterizedType p2 = FsType.parameterizedType(A.B.class, p1, Arrays.asList(String.class));
        Assert.assertEquals(
            p2.toString(),
            "test.A<java.lang.Integer>$B<java.lang.String>"
        );
        Assert.assertEquals(
            t2,
            p2
        );

        //wildcard
        Type t3 = new TypeRef<List<? super Integer>>() {
        }.getParameterizedType().getActualTypeArguments()[0];
        Assert.assertEquals(
            t3.toString(),
            "? super java.lang.Integer"
        );
        Type w1 = FsType.wildcardType(null, Arrays.asList(Integer.class));
        Assert.assertEquals(
            w1.toString(),
            "? super java.lang.Integer"
        );
        Assert.assertEquals(
            t3,
            w1
        );
    }

    private static final class T1<T> {
    }
}

class A<T> {
    class B<U> {
    }
}
