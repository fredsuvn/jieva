package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.base.FsLogger;
import xyz.srclab.common.collect.FsCollect;
import xyz.srclab.common.reflect.FsReflect;
import xyz.srclab.common.reflect.FsType;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ReflectTest {

    @Test
    public void testLastName() {
        Assert.assertEquals(FsReflect.getLastName(ReflectTest.class), "ReflectTest");
        Assert.assertEquals(FsReflect.getLastName(T.class), "ReflectTest$T");
    }

    @Test
    public void testTypeRef() {
        //parametrized
        Type t1 = new TypeRef<T<Integer>>() {
        }.getType();
        Assert.assertEquals(
            t1.toString(),
            "test.ReflectTest$T<java.lang.Integer>"
        );
        Type t2 = new TypeRef<T<Integer>.V<String>>() {
        }.getType();
        Assert.assertEquals(
            t2.toString(),
            "test.ReflectTest$T<java.lang.Integer>$V<java.lang.String>"
        );
        ParameterizedType p1 = FsType.parameterizedType(T.class, Arrays.asList(Integer.class));
        Assert.assertEquals(
            p1.toString(),
            "test.ReflectTest$T<java.lang.Integer>"
        );
        Assert.assertEquals(
            t1,
            p1
        );
        ParameterizedType p2 = FsType.parameterizedType(T.V.class, p1, Arrays.asList(String.class));
        Assert.assertEquals(
            p2.toString(),
            "test.ReflectTest$T<java.lang.Integer>$V<java.lang.String>"
        );
        Assert.assertEquals(
            t2,
            p2
        );

        //wildcard
        Type t3 = new TypeRef<List<? super Integer>>() {
        }.asParameterized().getActualTypeArguments()[0];
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
        Type t4 = new TypeRef<List<? extends Integer>>() {
        }.asParameterized().getActualTypeArguments()[0];
        Assert.assertEquals(
            t4.toString(),
            "? extends java.lang.Integer"
        );
        Type w2 = FsType.wildcardType(Arrays.asList(Integer.class), null);
        Assert.assertEquals(
            w2.toString(),
            "? extends java.lang.Integer"
        );
        Assert.assertEquals(
            t4,
            w2
        );

        //generic array
        Type t5 = new TypeRef<List<? extends Integer>[]>() {
        }.getType();
        Assert.assertEquals(
            t5.toString(),
            "java.util.List<? extends java.lang.Integer>[]"
        );
        Type g1 = FsType.genericArrayType(
            FsType.parameterizedType(List.class, Arrays.asList(
                FsType.wildcardType(Arrays.asList(Integer.class), null))));
        Assert.assertEquals(
            g1.toString(),
            "java.util.List<? extends java.lang.Integer>[]"
        );
        Assert.assertEquals(
            t5,
            g1
        );

        //R
        Assert.assertEquals(
            new R1().getType(),
            String.class
        );
        Assert.assertEquals(
            new R2<Integer>() {
            }.getType(),
            Integer.class
        );
    }

    @Test
    public void testAssignableFrom() {
        Assert.assertTrue(FsReflect.isAssignableFrom(int.class, Integer.class));
        Assert.assertTrue(FsReflect.isAssignableFrom(int.class, int.class));
        Assert.assertFalse(FsReflect.isAssignableFrom(int.class, Double.class));
    }

    @Test
    public void testGetTypeParameterMapping() throws NoSuchFieldException {
        Map<TypeVariable<?>, Type> map = FsReflect.getTypeParameterMapping(new TypeRef<X<String>>() {
        }.getType());
        // R(1661070039)=V(23805079)
        // K(532385198)=class java.lang.Integer(33524623)
        // U(1028083665)=class java.lang.Double(1703953258)
        // T(261012453)=class java.lang.Float(1367097467)
        // A(844996153)=A(726237730)
        // B(1498084403)=A(844996153)
        // V(23805079)=class java.lang.Long(746023354)
        FsLogger.system().info(FsCollect.mapMap(
            map.entrySet(),
            it -> it.getKey() + "(" + Fs.systemHash(it.getKey()) + ")",
            it -> it.getValue() + "(" + Fs.systemHash(it.getValue()) + ")"
        ));
        Map<TypeVariable<?>, Type> map2 = FsReflect.getTypeParameterMapping(
            T.class.getDeclaredField("x").getGenericType());
        // R(1661070039)=V(23805079)
        // K(532385198)=class java.lang.Integer(33524623)
        // U(1028083665)=class java.lang.Double(1703953258)
        // T(261012453)=class java.lang.Float(1367097467)
        // A(844996153)=A(726237730)
        // B(1498084403)=A(844996153)
        // V(23805079)=class java.lang.Long(746023354)
        FsLogger.system().info(FsCollect.mapMap(
            map2.entrySet(),
            it -> it.getKey() + "(" + Fs.systemHash(it.getKey()) + ")",
            it -> it.getValue() + "(" + Fs.systemHash(it.getValue()) + ")"
        ));
    }

    @Test
    public void testGetGenericSuperType() {
        ParameterizedType generic = FsReflect.getGenericSuperType(ZS.class, Z.class);
        FsLogger.system().info(generic);
        Assert.assertEquals(generic, new TypeRef<Z<String, Integer, Long, Boolean>>() {
        }.getType());
        generic = FsReflect.getGenericSuperType(new TypeRef<ZB<String>>() {
        }.getType(), Z.class);
        FsLogger.system().info(generic);
        Assert.assertEquals(generic, new TypeRef<Z<String, String, Long, Boolean>>() {
        }.getType());
        Assert.assertEquals(
            FsReflect.getGenericSuperType(new TypeRef<ZB<String>>() {
            }.getType(), ZB.class),
            new TypeRef<ZB<String>>() {
            }.getType()
        );
    }

    private static final class T<W> {
        private final X<W> x = null;

        public class V<U> {
        }
    }

    private static class X<A> extends Y<A, Integer, Long> {
    }

    private static class Y<A, K, V extends Long> implements Z<A, Float, Double, V> {
    }

    private static interface Z<B, T, U, R> {
    }

    private static class ZS implements Z<String, Integer, Long, Boolean> {
    }

    private static class ZB<M> implements Z<M, M, Long, Boolean> {
    }

    private static class R1 extends R2<String> {
    }

    private static class R2<Tx> extends R3<Tx> {
    }

    private static class R3<Tx> extends TypeRef<Tx> {
    }
}


