package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.base.FsLogger;
import xyz.srclab.common.collect.FsCollect;
import xyz.srclab.common.reflect.FsType;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

public class TypeTest {

    @Test
    public void testLastName() {
        Assert.assertEquals(FsType.getLastName(TypeTest.class), "TypeTest");
        Assert.assertEquals(FsType.getLastName(T.class), "TypeTest$T");
    }

    @Test
    public void testTypeRef() {
        //parametrized
        Type t1 = new TypeRef<T<Integer>>() {
        }.getType();
        Assert.assertEquals(
            t1.toString(),
            "test.TypeTest$T<java.lang.Integer>"
        );
        Type t2 = new TypeRef<T<Integer>.V<String>>() {
        }.getType();
        Assert.assertEquals(
            t2.toString(),
            "test.TypeTest$T<java.lang.Integer>$V<java.lang.String>"
        );
        ParameterizedType p1 = FsType.parameterizedType(T.class, Arrays.asList(Integer.class));
        Assert.assertEquals(
            p1.toString(),
            "test.TypeTest$T<java.lang.Integer>"
        );
        Assert.assertEquals(
            t1,
            p1
        );
        ParameterizedType p2 = FsType.parameterizedType(T.V.class, p1, Arrays.asList(String.class));
        Assert.assertEquals(
            p2.toString(),
            "test.TypeTest$T<java.lang.Integer>$V<java.lang.String>"
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
        Assert.assertTrue(FsType.isAssignableFrom(int.class, Integer.class));
        Assert.assertTrue(FsType.isAssignableFrom(int.class, int.class));
        Assert.assertFalse(FsType.isAssignableFrom(int.class, Double.class));

        Assert.assertTrue(FsType.isAssignableFrom(
            new TypeRef<List<String>>() {
            }.getType(),
            new TypeRef<List<String>>() {
            }.getType()
        ));
        Assert.assertTrue(FsType.isAssignableFrom(
            new TypeRef<List<? extends CharSequence>>() {
            }.getType(),
            new TypeRef<List<String>>() {
            }.getType()
        ));
        Assert.assertTrue(FsType.isAssignableFrom(
            new TypeRef<List<? extends CharSequence>>() {
            }.getType(),
            new TypeRef<List<? extends String>>() {
            }.getType()
        ));
        Assert.assertTrue(FsType.isAssignableFrom(
            new TypeRef<List<? super String>>() {
            }.getType(),
            new TypeRef<List<CharSequence>>() {
            }.getType()
        ));
        Assert.assertTrue(FsType.isAssignableFrom(
            new TypeRef<List<? super String>>() {
            }.getType(),
            new TypeRef<List<? super CharSequence>>() {
            }.getType()
        ));
        Assert.assertFalse(FsType.isAssignableFrom(
            new TypeRef<List<List<List<List<? super String>>>>>() {
            }.getType(),
            new TypeRef<List<List<List<List<? super CharSequence>>>>>() {
            }.getType()
        ));
        Assert.assertFalse(FsType.isAssignableFrom(
            new TypeRef<List<? super String>>() {
            }.getType(),
            new TypeRef<List<? extends CharSequence>>() {
            }.getType()
        ));
        Assert.assertFalse(FsType.isAssignableFrom(
            new TypeRef<List<CharSequence>>() {
            }.getType(),
            new TypeRef<List<String>>() {
            }.getType()
        ));
        class TAF<
            F1,
            F2 extends F1,
            F3 extends CharSequence,
            F4 extends Number & CharSequence,
            F5 extends Collection<String>,
            F6 extends Collection<? extends CharSequence>,
            F7 extends Collection<? super String>
            > {

            private F1 f1 = null;
            private F2 f2 = null;
            private F3 f3 = null;
            private F4 f4 = null;
            private F5 f5 = null;
            private F6 f6 = null;
            private F7 f7 = null;
            private F2[] f2s = null;
            private F1[] f1s = f2s;
            private F1 f11 = f2;
            private List<? super CharSequence> ll = null;
            private List<? super String> list = ll;
        }

    }

    @Test
    public void testReplaceType() {
        Type t = new TypeRef<List<Map<String, List<String>>>>() {
        }.getType();
        Type tl = new TypeRef<List<String>>() {
        }.getType();
        Assert.assertEquals(
            FsType.replaceType(t, tl, Integer.class, true),
            new TypeRef<List<Map<String, Integer>>>() {
            }.getType()
        );
        Type tm = new TypeRef<Map<String, List<String>>>() {
        }.getType();
        Assert.assertEquals(
            FsType.replaceType(tm, String.class, Integer.class, true),
            new TypeRef<Map<Integer, List<Integer>>>() {
            }.getType()
        );
        Assert.assertEquals(
            FsType.replaceType(tm, String.class, Integer.class, false),
            new TypeRef<Map<Integer, List<String>>>() {
            }.getType()
        );
        Assert.assertEquals(
            FsType.replaceType(tm, tm, Integer.class, false),
            Integer.class
        );

        Type tw = new TypeRef<Map<String, ? extends List<String>>>() {
        }.getType();
        Assert.assertEquals(
            FsType.replaceType(tw, String.class, Integer.class, true),
            new TypeRef<Map<Integer, ? extends List<Integer>>>() {
            }.getType()
        );

        Type tg = new TypeRef<Map<String, ? extends List<String>>[]>() {
        }.getType();
        Assert.assertEquals(
            FsType.replaceType(tg, String.class, Integer.class, true),
            new TypeRef<Map<Integer, ? extends List<Integer>>[]>() {
            }.getType()
        );
    }

    @Test
    public void testGetTypeParameterMapping() throws NoSuchFieldException {
        Map<TypeVariable<?>, Type> map = FsType.getTypeParameterMapping(new TypeRef<X<String>>() {
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
        Map<TypeVariable<?>, Type> map2 = FsType.getTypeParameterMapping(
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
        ParameterizedType generic = FsType.getGenericSuperType(ZS.class, Z.class);
        FsLogger.system().info(generic);
        Assert.assertEquals(generic, new TypeRef<Z<String, Integer, Long, Boolean>>() {
        }.getType());
        generic = FsType.getGenericSuperType(new TypeRef<ZB<String>>() {
        }.getType(), Z.class);
        FsLogger.system().info(generic);
        Assert.assertEquals(generic, new TypeRef<Z<String, String, Long, Boolean>>() {
        }.getType());
        Assert.assertEquals(
            FsType.getGenericSuperType(new TypeRef<ZB<String>>() {
            }.getType(), ZB.class),
            new TypeRef<ZB<String>>() {
            }.getType()
        );

        Assert.assertNull(FsType.getGenericSuperType(Z.class, ZS.class));
        Assert.assertNull(FsType.getGenericSuperType(ZB.class, ZS.class));
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


