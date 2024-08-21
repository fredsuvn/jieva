package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.base.GekLog;
import xyz.fslabo.common.base.obj.GekObj;
import xyz.fslabo.common.coll.JieColl;
import xyz.fslabo.common.reflect.JieReflect;
import xyz.fslabo.common.reflect.JieType;
import xyz.fslabo.common.reflect.TypeRef;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;


public class ReflectTest {

    @Test
    public void testLastName() {
        Assert.assertEquals(JieReflect.getLastName(ReflectTest.class), "ReflectTest");
        Assert.assertEquals(JieReflect.getLastName(T.class), "ReflectTest$T");
    }

    @Test
    public void testArrayClass() {
        Assert.assertEquals(
            JieReflect.arrayClass(Object.class),
            Object[].class
        );
        Assert.assertEquals(
            JieReflect.arrayClass(new TypeRef<List<? extends String>>() {
            }.getType()),
            List[].class
        );
        Assert.assertEquals(
            JieReflect.arrayClass(new TypeRef<List<? extends String>[]>() {
            }.getType()),
            List[][].class
        );
        Assert.assertEquals(
            JieReflect.arrayClass(new TypeRef<List<? extends String>[][]>() {
            }.getType()),
            List[][][].class
        );
    }

    @Test
    public void testGekObj() {
        Assert.assertEquals(
            GekObj.wrap(null, new TypeRef<List<?>>() {
            }),
            GekObj.wrap(null, new TypeRef<List<?>>() {
            })
        );
        GekObj<?> cType = GekObj.wrap(null, String.class);
        Assert.assertEquals(
            cType.toClassObj().getType(),
            String.class
        );
        GekObj<?> pType = GekObj.wrap(null, new TypeRef<Map<String, Integer>>() {
        }.getType());
        Assert.assertEquals(
            pType.toParameterizedObj().getType(),
            new TypeRef<Map<String, Integer>>() {
            }.getType()
        );
        Assert.assertEquals(
            pType.toParameterizedObj().getActualTypeArgument(0),
            String.class
        );
        Assert.assertEquals(
            pType.toParameterizedObj().getActualTypeArgument(1),
            Integer.class
        );
        GekObj<?> wType = GekObj.wrap(null,
            JieType.wildcard(Collections.singletonList(String.class), null));
        Assert.assertEquals(
            wType.toWildcardObj().getType(),
            JieType.wildcard(Collections.singletonList(String.class), null)
        );
        Assert.assertEquals(
            wType.toWildcardObj().getUpperBound(),
            String.class
        );
        wType = GekObj.wrap(null,
            JieType.wildcard(null, Collections.singletonList(Integer.class)));
        Assert.assertEquals(
            wType.toWildcardObj().getLowerBound(),
            Integer.class
        );
        GekObj<?> gType = GekObj.wrap(null, new TypeRef<Map<String, Integer>[]>() {
        }.getType());
        Assert.assertEquals(
            gType.toGenericArrayObj().getType(),
            new TypeRef<Map<String, Integer>[]>() {
            }.getType()
        );
        Assert.assertEquals(
            gType.toGenericArrayObj().getType().getGenericComponentType(),
            new TypeRef<Map<String, Integer>>() {
            }.getType()
        );
        class OT<OTP extends Float> {
        }
        GekObj<?> tType = GekObj.wrap(null, OT.class.getTypeParameters()[0]);
        Assert.assertEquals(
            tType.toTypeVariableObj().getType(),
            OT.class.getTypeParameters()[0]
        );
        Assert.assertEquals(
            tType.toTypeVariableObj().getBound(0),
            Float.class
        );
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
        ParameterizedType p1 = JieType.parameterized(T.class, Arrays.asList(Integer.class));
        Assert.assertEquals(
            p1.toString(),
            "test.ReflectTest$T<java.lang.Integer>"
        );
        Assert.assertEquals(
            t1,
            p1
        );
        ParameterizedType p2 = JieType.parameterized(T.V.class, p1, Arrays.asList(String.class));
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
        Type w1 = JieType.wildcard(null, Arrays.asList(Integer.class));
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
        Type w2 = JieType.wildcard(Arrays.asList(Integer.class), null);
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
        Type g1 = JieType.array(
            JieType.parameterized(List.class, Arrays.asList(
                JieType.wildcard(Arrays.asList(Integer.class), null))));
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
        Assert.assertTrue(JieReflect.isAssignable(int.class, Integer.class));
        Assert.assertTrue(JieReflect.isAssignable(int.class, int.class));
        Assert.assertFalse(JieReflect.isAssignable(int.class, Double.class));

        Assert.assertTrue(JieReflect.isAssignable(
            new TypeRef<List<String>>() {
            }.getType(),
            new TypeRef<List<String>>() {
            }.getType()
        ));
        Assert.assertTrue(JieReflect.isAssignable(
            new TypeRef<List<? extends CharSequence>>() {
            }.getType(),
            new TypeRef<List<String>>() {
            }.getType()
        ));
        Assert.assertTrue(JieReflect.isAssignable(
            new TypeRef<List<? extends CharSequence>>() {
            }.getType(),
            new TypeRef<List<? extends String>>() {
            }.getType()
        ));
        Assert.assertTrue(JieReflect.isAssignable(
            new TypeRef<List<? super String>>() {
            }.getType(),
            new TypeRef<List<CharSequence>>() {
            }.getType()
        ));
        Assert.assertTrue(JieReflect.isAssignable(
            new TypeRef<List<? super String>>() {
            }.getType(),
            new TypeRef<List<? super CharSequence>>() {
            }.getType()
        ));
        Assert.assertFalse(JieReflect.isAssignable(
            new TypeRef<List<List<List<List<? super String>>>>>() {
            }.getType(),
            new TypeRef<List<List<List<List<? super CharSequence>>>>>() {
            }.getType()
        ));
        Assert.assertFalse(JieReflect.isAssignable(
            new TypeRef<List<? super String>>() {
            }.getType(),
            new TypeRef<List<? extends CharSequence>>() {
            }.getType()
        ));
        Assert.assertFalse(JieReflect.isAssignable(
            new TypeRef<List<CharSequence>>() {
            }.getType(),
            new TypeRef<List<String>>() {
            }.getType()
        ));
        Assert.assertTrue(JieReflect.isAssignable(
            new TypeRef<Collection<?>>() {
            }.getType(),
            new TypeRef<List<? extends List<? extends List<? extends String>>>>() {
            }.getType()
        ));
        Assert.assertTrue(JieReflect.isAssignable(
            new TypeRef<Collection<?>>() {
            }.getType(),
            new TypeRef<Collection<?>>() {
            }.getType()
        ));
        Assert.assertFalse(JieReflect.isAssignable(
            new TypeRef<Collection<Object>>() {
            }.getType(),
            new TypeRef<Collection<?>>() {
            }.getType()
        ));
        Assert.assertTrue(JieReflect.isAssignable(
            new TypeRef<List<? extends List<? extends List<? extends CharSequence>>>>() {
            }.getType(),
            new TypeRef<List<? extends List<? extends List<? extends String>>>>() {
            }.getType()
        ));
        Assert.assertFalse(JieReflect.isAssignable(
            new TypeRef<List<? extends List<? extends List<? extends String>>>>() {
            }.getType(),
            new TypeRef<List<? extends List<? extends List<? extends CharSequence>>>>() {
            }.getType()
        ));
        Assert.assertFalse(JieReflect.isAssignable(
            new TypeRef<List<? extends List<? extends List<CharSequence>>>>() {
            }.getType(),
            new TypeRef<List<? extends List<? extends List<? extends String>>>>() {
            }.getType()
        ));
        Assert.assertTrue(JieReflect.isAssignable(
            new TypeRef<Collection[]>() {
            }.getType(),
            new TypeRef<List<? extends List<? extends List<? extends String>>>[]>() {
            }.getType()
        ));
        Assert.assertTrue(JieReflect.isAssignable(
            new TypeRef<Collection<? extends Object>[]>() {
            }.getType(),
            new TypeRef<List<? extends List<? extends List<? extends String>>>[]>() {
            }.getType()
        ));
        Assert.assertFalse(JieReflect.isAssignable(
            new TypeRef<Collection<Object>[]>() {
            }.getType(),
            new TypeRef<List<? extends List<? extends List<? extends String>>>[]>() {
            }.getType()
        ));
        Assert.assertFalse(JieReflect.isAssignable(
            new TypeRef<Collection[]>() {
            }.getType(),
            new TypeRef<List<? extends List<? extends List<? extends String>>>>() {
            }.getType()
        ));
        Assert.assertFalse(JieReflect.isAssignable(
            new TypeRef<Collection<Object>[]>() {
            }.getType(),
            new TypeRef<List<? extends List<? extends List<? extends String>>>>() {
            }.getType()
        ));
        Assert.assertTrue(JieReflect.isAssignable(
            new TypeRef<Collection<?>[][]>() {
            }.getType(),
            new TypeRef<List<? extends List<? extends List<? extends String>>>[][]>() {
            }.getType()
        ));
        //        List<? extends List<? extends List<? extends String>>>[][] l = null;
        //        Collection<? extends Object>[][] c = l;
        Assert.assertFalse(JieReflect.isAssignable(
            new TypeRef<Map<String, String>>() {
            }.getType(),
            Map.class
        ));
        Assert.assertTrue(JieReflect.isAssignable(
            Object[].class,
            new TypeRef<Map<String, String>[]>() {
            }.getType()
        ));
        Assert.assertTrue(JieReflect.isAssignable(
            Object[].class,
            Object[][].class
        ));
        class TAF<
            F1,
            F2 extends F1,
            F3 extends CharSequence,
            F4 extends Number & CharSequence,
            F5 extends Collection<String>,
            F6 extends Collection<? extends CharSequence>,
            F7 extends Collection<? super String>,
            F8 extends F2
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
            private F8 f8 = null;
            private F1 f11 = f2;
            private F1 f12 = f8;
            private List<? super CharSequence> ll = null;
            private List<? super String> list = ll;
        }
        TypeVariable<?>[] tvs = TAF.class.getTypeParameters();
        Assert.assertTrue(JieReflect.isAssignable(
            tvs[0],
            tvs[1]
        ));
        Assert.assertFalse(JieReflect.isAssignable(
            tvs[0],
            tvs[2]
        ));
        Assert.assertTrue(JieReflect.isAssignable(
            tvs[0],
            tvs[7]
        ));
        Assert.assertTrue(JieReflect.isAssignable(
            JieReflect.getField(TAF.class, "f1s").getGenericType(),
            JieReflect.getField(TAF.class, "f2s").getGenericType()
        ));
        Assert.assertFalse(JieReflect.isAssignable(
            JieReflect.getField(TAF.class, "f2s").getGenericType(),
            JieReflect.getField(TAF.class, "f1s").getGenericType()
        ));
        Assert.assertFalse(JieReflect.isAssignable(
            tvs[5],
            new TypeRef<List<String>>() {
            }.getType()
        ));
        Assert.assertFalse(JieReflect.isAssignable(
            tvs[5],
            new TypeRef<List<Integer>>() {
            }.getType()
        ));
        Assert.assertFalse(JieReflect.isAssignable(
            new TypeRef<Collection<CharSequence>>() {
            }.getType(),
            tvs[6]
        ));
    }

    @Test
    public void testReplaceType() {
        Type t = new TypeRef<List<Map<String, List<String>>>>() {
        }.getType();
        Type tl = new TypeRef<List<String>>() {
        }.getType();
        Assert.assertEquals(
            JieReflect.replaceType(t, tl, Integer.class, true),
            new TypeRef<List<Map<String, Integer>>>() {
            }.getType()
        );
        Type tm = new TypeRef<Map<String, List<String>>>() {
        }.getType();
        Assert.assertEquals(
            JieReflect.replaceType(tm, String.class, Integer.class, true),
            new TypeRef<Map<Integer, List<Integer>>>() {
            }.getType()
        );
        Assert.assertEquals(
            JieReflect.replaceType(tm, String.class, Integer.class, false),
            new TypeRef<Map<Integer, List<String>>>() {
            }.getType()
        );
        Assert.assertEquals(
            JieReflect.replaceType(tm, tm, Integer.class, false),
            Integer.class
        );

        Type tw = new TypeRef<Map<String, ? extends List<String>>>() {
        }.getType();
        Assert.assertEquals(
            JieReflect.replaceType(tw, String.class, Integer.class, true),
            new TypeRef<Map<Integer, ? extends List<Integer>>>() {
            }.getType()
        );

        Type tg = new TypeRef<Map<String, ? extends List<String>>[]>() {
        }.getType();
        Assert.assertEquals(
            JieReflect.replaceType(tg, String.class, Integer.class, true),
            new TypeRef<Map<Integer, ? extends List<Integer>>[]>() {
            }.getType()
        );

        Type ts = new TypeRef<Map<String, ? extends List<String>>[]>() {
        }.getType();
        Assert.assertSame(
            JieReflect.replaceType(ts, Integer.class, Integer.class, true),
            ts
        );
    }

    @Test
    public void testGetTypeParameterMapping() throws NoSuchFieldException {
        Map<TypeVariable<?>, Type> map = JieReflect.getTypeParameterMapping(new TypeRef<X<String>>() {
        }.getType());
        // R(1661070039)=V(23805079)
        // K(532385198)=class java.lang.Integer(33524623)
        // U(1028083665)=class java.lang.Double(1703953258)
        // T(261012453)=class java.lang.Float(1367097467)
        // A(844996153)=A(726237730)
        // B(1498084403)=A(844996153)
        // V(23805079)=class java.lang.Long(746023354)
        GekLog.getInstance().info(JieColl.toMap(
            map.entrySet(),
            it -> it.getKey() + "(" + Jie.systemHash(it.getKey()) + ")",
            it -> it.getValue() + "(" + Jie.systemHash(it.getValue()) + ")"
        ));
        Map<TypeVariable<?>, Type> map2 = JieReflect.getTypeParameterMapping(
            T.class.getDeclaredField("x").getGenericType());
        // R(1661070039)=V(23805079)
        // K(532385198)=class java.lang.Integer(33524623)
        // U(1028083665)=class java.lang.Double(1703953258)
        // T(261012453)=class java.lang.Float(1367097467)
        // A(844996153)=A(726237730)
        // B(1498084403)=A(844996153)
        // V(23805079)=class java.lang.Long(746023354)
        GekLog.getInstance().info(JieColl.toMap(
            map2.entrySet(),
            it -> it.getKey() + "(" + Jie.systemHash(it.getKey()) + ")",
            it -> it.getValue() + "(" + Jie.systemHash(it.getValue()) + ")"
        ));
    }

    @Test
    public void testGetGenericSuperType() {
        ParameterizedType generic = JieReflect.getActualTypeArguments(ZS.class, Z.class);
        GekLog.getInstance().info(generic);
        Assert.assertEquals(generic, new TypeRef<Z<String, Integer, Long, Boolean>>() {
        }.getType());
        generic = JieReflect.getActualTypeArguments(new TypeRef<ZB<String>>() {
        }.getType(), Z.class);
        GekLog.getInstance().info(generic);
        Assert.assertEquals(generic, new TypeRef<Z<String, String, Long, Boolean>>() {
        }.getType());
        Assert.assertEquals(
            JieReflect.getActualTypeArguments(new TypeRef<ZB<String>>() {
            }.getType(), ZB.class),
            new TypeRef<ZB<String>>() {
            }.getType()
        );

        Assert.assertNull(JieReflect.getActualTypeArguments(Z.class, ZS.class));
        Assert.assertNull(JieReflect.getActualTypeArguments(ZB.class, ZS.class));

        Assert.assertEquals(
            JieReflect.getActualTypeArguments(Iterable.class, Iterable.class),
            JieType.parameterized(Iterable.class, Arrays.asList(Iterable.class.getTypeParameters()[0]))
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
