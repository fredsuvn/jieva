package test.reflect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.common.reflect.JieReflect;
import xyz.fslabo.common.reflect.JieType;
import xyz.fslabo.common.reflect.TypeRef;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;


public class ReflectTest {

    @Test
    public void testLastName() {
        Assert.assertEquals(JieReflect.getLastName(ReflectTest.class), ReflectTest.class.getSimpleName());
        Assert.assertEquals(
            JieReflect.getLastName(Inner.class),
            ReflectTest.class.getSimpleName() + "$" + Inner.class.getSimpleName()
        );
    }

    @Test
    public void testRawName() {
        Assert.assertEquals(JieReflect.getRawType(String.class), String.class);
        Assert.assertEquals(JieReflect.getRawType(JieType.parameterized(List.class, new Type[]{String.class})), List.class);
    }

    @Test
    public void testBounds() {
        Assert.assertEquals(JieReflect.getUpperBound(JieType.upperBound(String.class)), String.class);
        Assert.assertEquals(JieReflect.getUpperBound(JieType.questionMark()), Object.class);
        Assert.assertEquals(JieReflect.getLowerBound(JieType.lowerBound(String.class)), String.class);
        Assert.assertNull(JieReflect.getLowerBound(JieType.upperBound(String.class)));
        Assert.assertEquals(JieReflect.getFirstBound(Inner.class.getTypeParameters()[0]), Number.class);
    }

    @Test
    public void testSearching() throws Exception {
        Field ssif1 = SuperSuperInter1.class.getDeclaredField("ssif1");
        Field ssif2 = SuperSuperInter2.class.getDeclaredField("ssif2");
        Field sif1 = SuperInter1.class.getDeclaredField("sif1");
        Field sif2 = SuperInter2.class.getDeclaredField("sif2");
        Field scf1 = SuperClass1.class.getDeclaredField("scf1");
        Field scf2 = SuperClass1.class.getDeclaredField("scf2");
        Field f1 = Inner.class.getDeclaredField("f1");
        Field f2 = Inner.class.getDeclaredField("f2");
        Assert.assertEquals(JieReflect.getField(Inner.class, "ssif1"), ssif1);
        Assert.assertEquals(JieReflect.getField(Inner.class, "ssif2"), ssif2);
        Assert.assertEquals(JieReflect.getField(Inner.class, "sif1"), sif1);
        Assert.assertEquals(JieReflect.getField(Inner.class, "sif2"), sif2);
        Assert.assertEquals(JieReflect.getField(Inner.class, "scf1"), scf1);
        Assert.assertEquals(JieReflect.getField(Inner.class, "scf2"), scf2);
        Assert.assertEquals(JieReflect.getField(Inner.class, "f1"), f1);
        Assert.assertEquals(JieReflect.getField(Inner.class, "f2"), f2);


        Method ssim1 = SuperSuperInter1.class.getDeclaredMethod("ssim1");
        Method ssim2 = SuperSuperInter2.class.getDeclaredMethod("ssim2");
        Method sim1 = SuperInter1.class.getDeclaredMethod("sim1");
        Method sim2 = SuperInter2.class.getDeclaredMethod("sim2");
        Method scm1 = SuperClass1.class.getDeclaredMethod("scm1");
        Method scm2 = SuperClass1.class.getDeclaredMethod("scm2");
        Method m1 = Inner.class.getDeclaredMethod("m1");
        Method m2 = Inner.class.getDeclaredMethod("m2");
        Assert.assertEquals(JieReflect.getMethod(Inner.class, "ssim1", new Class[]{}), ssim1);
        Assert.assertEquals(JieReflect.getMethod(Inner.class, "ssim2", new Class[]{}), ssim2);
        Assert.assertEquals(JieReflect.getMethod(Inner.class, "sim1", new Class[]{}), sim1);
        Assert.assertEquals(JieReflect.getMethod(Inner.class, "sim2", new Class[]{}), sim2);
        Assert.assertEquals(JieReflect.getMethod(Inner.class, "scm1", new Class[]{}), scm1);
        Assert.assertEquals(JieReflect.getMethod(Inner.class, "scm2", new Class[]{}), scm2);
        Assert.assertEquals(JieReflect.getMethod(Inner.class, "m1", new Class[]{}), m1);
        Assert.assertEquals(JieReflect.getMethod(Inner.class, "m2", new Class[]{}), m2);
    }

    @Test
    public void testNewInstance() {
        Assert.assertEquals(JieReflect.newInstance(String.class.getName()), "");
    }

    @Test
    public void testArrayClass() {
        Assert.assertEquals(
            JieReflect.arrayClass(Object.class),
            Object[].class
        );
        Assert.assertEquals(
            JieReflect.arrayClass(boolean.class),
            boolean[].class
        );
        Assert.assertEquals(
            JieReflect.arrayClass(byte.class),
            byte[].class
        );
        Assert.assertEquals(
            JieReflect.arrayClass(short.class),
            short[].class
        );
        Assert.assertEquals(
            JieReflect.arrayClass(char.class),
            char[].class
        );
        Assert.assertEquals(
            JieReflect.arrayClass(int.class),
            int[].class
        );
        Assert.assertEquals(
            JieReflect.arrayClass(long.class),
            long[].class
        );
        Assert.assertEquals(
            JieReflect.arrayClass(float.class),
            float[].class
        );
        Assert.assertEquals(
            JieReflect.arrayClass(double.class),
            double[].class
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
        Assert.expectThrows(UnsupportedOperationException.class, () -> JieReflect.arrayClass(Inner.class.getTypeParameters()[0]));
    }

    @Test
    public void testWrapper() {
        Assert.assertEquals(JieReflect.wrapper(boolean.class), Boolean.class);
        Assert.assertEquals(JieReflect.wrapper(byte.class), Byte.class);
        Assert.assertEquals(JieReflect.wrapper(short.class), Short.class);
        Assert.assertEquals(JieReflect.wrapper(char.class), Character.class);
        Assert.assertEquals(JieReflect.wrapper(int.class), Integer.class);
        Assert.assertEquals(JieReflect.wrapper(long.class), Long.class);
        Assert.assertEquals(JieReflect.wrapper(float.class), Float.class);
        Assert.assertEquals(JieReflect.wrapper(double.class), Double.class);
        Assert.assertEquals(JieReflect.wrapper(Object.class), Object.class);
    }

    @Test
    public void testClassExists() {
        Assert.assertTrue(JieReflect.classExists(String.class.getName()));
        Assert.assertFalse(JieReflect.classExists("123"));
    }

    @Test
    public void testActualTypeArgs() {
        List<Type> args = JieReflect.getActualTypeArguments(Inner.class, SuperSuperInter1.class);
        Assert.assertEquals(args.size(), 2);
        Assert.assertEquals(args.get(0), Short.class);
        Assert.assertEquals(args.get(1), Integer.class);
    }

    private void doTestActualTypeArgs() {

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

//    @Test
//    public void testGetTypeParameterMapping() throws NoSuchFieldException {
//        Map<TypeVariable<?>, Type> map = JieReflect.getTypeParameterMapping(new TypeRef<X<String>>() {
//        }.getType());
//        // R(1661070039)=V(23805079)
//        // K(532385198)=class java.lang.Integer(33524623)
//        // U(1028083665)=class java.lang.Double(1703953258)
//        // T(261012453)=class java.lang.Float(1367097467)
//        // A(844996153)=A(726237730)
//        // B(1498084403)=A(844996153)
//        // V(23805079)=class java.lang.Long(746023354)
//        JieLog.system().info(JieColl.toMap(
//            map.entrySet(),
//            it -> it.getKey() + "(" + Jie.systemHash(it.getKey()) + ")",
//            it -> it.getValue() + "(" + Jie.systemHash(it.getValue()) + ")"
//        ));
//        Map<TypeVariable<?>, Type> map2 = JieReflect.getTypeParameterMapping(
//            Inner.class.getDeclaredField("x").getGenericType());
//        // R(1661070039)=V(23805079)
//        // K(532385198)=class java.lang.Integer(33524623)
//        // U(1028083665)=class java.lang.Double(1703953258)
//        // T(261012453)=class java.lang.Float(1367097467)
//        // A(844996153)=A(726237730)
//        // B(1498084403)=A(844996153)
//        // V(23805079)=class java.lang.Long(746023354)
//        JieLog.system().info(JieColl.toMap(
//            map2.entrySet(),
//            it -> it.getKey() + "(" + Jie.systemHash(it.getKey()) + ")",
//            it -> it.getValue() + "(" + Jie.systemHash(it.getValue()) + ")"
//        ));
//    }
//
//    @Test
//    public void testGetGenericSuperType() {
//        List<Type> actualArguments = JieReflect.getActualTypeArguments(ZS.class, Z.class);
//        JieLog.system().info(actualArguments);
//        Assert.assertEquals(actualArguments, Arrays.asList(new TypeRef<Z<String, Integer, Long, Boolean>>() {
//        }.getParameterized()));
//        actualArguments = JieReflect.getActualTypeArguments(new TypeRef<ZB<String>>() {
//        }.getType(), Z.class);
//        JieLog.system().info(actualArguments);
//        Assert.assertEquals(actualArguments, Arrays.asList(new TypeRef<Z<String, String, Long, Boolean>>() {
//        }.getParameterized()));
//        Assert.assertEquals(
//            JieReflect.getActualTypeArguments(new TypeRef<ZB<String>>() {
//            }.getType(), ZB.class),
//            new TypeRef<ZB<String>>() {
//            }.getType()
//        );
//
//        Assert.assertNull(JieReflect.getActualTypeArguments(Z.class, ZS.class));
//        Assert.assertNull(JieReflect.getActualTypeArguments(ZB.class, ZS.class));
//
//        Assert.assertEquals(
//            JieReflect.getActualTypeArguments(Iterable.class, Iterable.class),
//            JieType.parameterized(Iterable.class, Arrays.asList(Iterable.class.getTypeParameters()[0]))
//        );
//    }

    public interface SuperSuperInter1<SSI11, SSI12> {
        String ssif1 = null;

        default void ssim1() {
        }
    }

    public interface SuperSuperInter2<SSI21, SSI22> {
        String ssif2 = null;

        default void ssim2() {
        }
    }

    public interface SuperInter1<SI11, SSI12> extends SuperSuperInter1<SI11, Integer>, SuperSuperInter2<Long, SSI12> {
        String sif1 = null;

        default void sim1() {
        }
    }

    public interface SuperInter2<SI21, SSI22> {
        String sif2 = null;

        default void sim2() {
        }
    }

    public static class SuperClass1<SC11, SC12> {
        public static String scf1;
        private static String scf2;

        public static void scm1() {
        }

        private static void scm2() {
        }
    }

    public static class Inner<T extends Number & CharSequence>
        extends SuperClass1<T, String> implements SuperInter1<Short, Character>, SuperInter2<Float, Double> {

        public static String f1;
        private static String f2;

        public static void m1() {
        }

        private static void m2() {
        }
    }
}
