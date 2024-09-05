package test.reflect;

import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.common.reflect.JieReflect;
import xyz.fslabo.common.reflect.JieType;
import xyz.fslabo.common.reflect.TypeRef;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;


public class ReflectTest {

    @Test
    public void testLastName() {
        Assert.assertEquals(JieReflect.getLastName(ReflectTest.class), ReflectTest.class.getSimpleName());
        Assert.assertEquals(
            JieReflect.getLastName(Inner.class),
            ReflectTest.class.getSimpleName() + "$" + Inner.class.getSimpleName()
        );
        System.out.println();
    }

    @Test
    public void testRawName() {
        Assert.assertEquals(JieReflect.getRawType(String.class), String.class);
        Assert.assertEquals(JieReflect.getRawType(JieType.parameterized(List.class, new Type[]{String.class})), List.class);
        Assert.assertNull(JieReflect.getRawType(Inner.class.getTypeParameters()[0]));
    }

    @Test
    public void testBounds() {
        Assert.assertEquals(JieReflect.getUpperBound(JieType.upperBound(String.class)), String.class);
        Assert.assertEquals(JieReflect.getUpperBound(JieType.questionMark()), Object.class);
        Assert.assertEquals(JieReflect.getLowerBound(JieType.lowerBound(String.class)), String.class);
        Assert.assertNull(JieReflect.getLowerBound(JieType.upperBound(String.class)));
        Assert.assertEquals(JieReflect.getFirstBound(Inner.class.getTypeParameters()[0]), Number.class);
        Assert.assertEquals(JieReflect.getUpperBound(new WildcardType() {
            @Override
            public Type[] getUpperBounds() {
                return new Type[0];
            }

            @Override
            public Type[] getLowerBounds() {
                return new Type[0];
            }
        }), Object.class);
        Assert.assertEquals(JieReflect.getFirstBound(new TypeVariable<GenericDeclaration>() {
            @Override
            public Type[] getBounds() {
                return new Type[0];
            }

            @Override
            public GenericDeclaration getGenericDeclaration() {
                return null;
            }

            @Override
            public String getName() {
                return "";
            }

            @Override
            public AnnotatedType[] getAnnotatedBounds() {
                return new AnnotatedType[0];
            }

            @Override
            public <T extends Annotation> T getAnnotation(@NotNull Class<T> annotationClass) {
                return null;
            }

            @Override
            public Annotation[] getAnnotations() {
                return new Annotation[0];
            }

            @Override
            public Annotation[] getDeclaredAnnotations() {
                return new Annotation[0];
            }
        }), Object.class);
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

        Assert.assertNull(JieReflect.getField(JieType.other(), "1"));
        Assert.assertNull(JieReflect.getField(Inner.class, "1"));
        Assert.assertNull(JieReflect.getField(Inner.class, "1", false));
        Assert.assertNull(JieReflect.getMethod(JieType.other(), "1", new Class[]{}));
        Assert.assertNull(JieReflect.getMethod(Inner.class, "1", new Class[]{}));
        Assert.assertNull(JieReflect.getMethod(Inner.class, "1", new Class[]{}, false));
    }

    @Test
    public void testNewInstance() {
        Assert.assertEquals(JieReflect.newInstance(String.class.getName()), "");
        Assert.assertNull(JieReflect.newInstance("123"));
        Assert.assertNull(JieReflect.newInstance(List.class));
    }

    @Test
    public void testArrayClass() {
        Assert.assertEquals(
            JieReflect.arrayClass(Object.class),
            Object[].class
        );
        Assert.assertEquals(
            JieReflect.arrayClass(Object[].class),
            Object[][].class
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
        Assert.expectThrows(IllegalArgumentException.class, () -> JieReflect.arrayClass(void.class));
        Assert.assertEquals(
            JieReflect.arrayClass(JieType.parameterized(List.class, new Type[]{JieType.upperBound(String.class)})),
            List[].class
        );
        Assert.assertEquals(
            JieReflect.arrayClass(JieType.array(JieType.parameterized(List.class, new Type[]{JieType.upperBound(String.class)}))),
            List[][].class
        );
        Assert.assertEquals(
            JieReflect.arrayClass(JieType.array(JieType.array(JieType.parameterized(List.class, new Type[]{JieType.upperBound(String.class)})))),
            List[][][].class
        );
        Assert.assertEquals(
            JieReflect.arrayClass(JieType.array(String.class)),
            String[][].class
        );
        Assert.expectThrows(IllegalArgumentException.class, () -> JieReflect.arrayClass(JieType.array(JieType.other())));
        Assert.expectThrows(IllegalArgumentException.class, () -> JieReflect.arrayClass(Inner.class.getTypeParameters()[0]));
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
        Assert.assertEquals(JieReflect.wrapper(void.class), Void.class);
        Assert.assertEquals(JieReflect.wrapper(Object.class), Object.class);
    }

    @Test
    public void testClassExists() {
        Assert.assertTrue(JieReflect.classExists(String.class.getName()));
        Assert.assertFalse(JieReflect.classExists("123"));
        Assert.assertNull(JieReflect.classForName("123", null));
        Assert.assertNull(JieReflect.classForName("123", Thread.currentThread().getContextClassLoader()));
    }

    @Test
    public void testActualTypeArgs() {
        doTestActualTypeArgs(Inner.class, SuperSuperInter1.class, Short.class, Integer.class);
        doTestActualTypeArgs(
            JieType.parameterized(Inner.class, new Type[]{NumberString1.class}), SuperSuperInter1.class,
            Short.class, Integer.class
        );
        doTestActualTypeArgs(SuperInter1.class, SuperSuperInter1.class, SuperInter1.class.getTypeParameters()[0], Integer.class);
        doTestActualTypeArgs(List.class, ArrayList.class);
        doTestActualTypeArgs(JieType.parameterized(List.class, new Type[]{String.class}), ArrayList.class);
        doTestActualTypeArgs(JieType.other(), ArrayList.class);
        doTestActualTypeArgs(String.class, String.class);
        doTestActualTypeArgs(
            SuperSuperInter1.class, SuperSuperInter1.class,
            SuperSuperInter1.class.getTypeParameters()[0], SuperSuperInter1.class.getTypeParameters()[1]
        );
    }

    private void doTestActualTypeArgs(Type type, Class<?> rawType, Type... args) {
        List<Type> argsList = JieReflect.getActualTypeArguments(type, rawType);
        Assert.assertEquals(argsList.size(), args.length);
        for (int i = 0; i < argsList.size(); i++) {
            Assert.assertEquals(argsList.get(i), args[i]);
        }
    }

    @Test
    public void testTypeParameterMapping() {
        Map<TypeVariable<?>, Type> map = JieReflect.getTypeParameterMapping(new TypeRef<Inner<NumberString1>>() {
        }.getType());
        Assert.assertEquals(map.get(Inner.class.getTypeParameters()[0]), NumberString1.class);
        Assert.assertEquals(map.get(SuperClass1.class.getTypeParameters()[0]), Inner.class.getTypeParameters()[0]);
        Assert.assertEquals(map.get(SuperClass1.class.getTypeParameters()[1]), String.class);
        Assert.assertEquals(map.get(SuperInter1.class.getTypeParameters()[0]), Short.class);
        Assert.assertEquals(map.get(SuperInter1.class.getTypeParameters()[1]), Character.class);
        Assert.assertEquals(map.get(SuperInter2.class.getTypeParameters()[0]), Float.class);
        Assert.assertEquals(map.get(SuperInter2.class.getTypeParameters()[1]), Double.class);
        Assert.assertEquals(map.get(SuperSuperInter1.class.getTypeParameters()[0]), SuperInter1.class.getTypeParameters()[0]);
        Assert.assertEquals(map.get(SuperSuperInter1.class.getTypeParameters()[1]), Integer.class);
        Assert.assertEquals(map.get(SuperSuperInter2.class.getTypeParameters()[0]), Long.class);
        Assert.assertEquals(map.get(SuperSuperInter2.class.getTypeParameters()[1]), SuperInter1.class.getTypeParameters()[1]);
        Assert.assertEquals(JieReflect.getTypeParameterMapping(CharSequence.class), Collections.emptyMap());
        Assert.assertEquals(JieReflect.getTypeParameterMapping(JieType.other()), Collections.emptyMap());
    }

    @Test
    public void testReplaceType() {
        Type t = new TypeRef<List<Map<String, List<String>>>>() {
        }.getType();
        Type tl = new TypeRef<List<String>>() {
        }.getType();
        Assert.assertEquals(
            JieReflect.replaceType(t, tl, Integer.class),
            new TypeRef<List<Map<String, Integer>>>() {
            }.getType()
        );
        Type tm = new TypeRef<Map<String, List<String>>>() {
        }.getType();
        Assert.assertEquals(
            JieReflect.replaceType(tm, String.class, Integer.class),
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
            JieReflect.replaceType(tw, String.class, Integer.class),
            new TypeRef<Map<Integer, ? extends List<Integer>>>() {
            }.getType()
        );

        Type tg = new TypeRef<Map<String, ? extends List<String>>[]>() {
        }.getType();
        Assert.assertEquals(
            JieReflect.replaceType(tg, String.class, Integer.class),
            new TypeRef<Map<Integer, ? extends List<Integer>>[]>() {
            }.getType()
        );

        Type ts = new TypeRef<Map<String, ? extends List<String>>[]>() {
        }.getType();
        Assert.assertSame(
            JieReflect.replaceType(ts, Integer.class, Integer.class),
            ts
        );

        Assert.assertEquals(
            JieReflect.replaceType(new TypeRef<List<List<String>>>() {
            }.getType(), List.class, ArrayList.class),
            new TypeRef<ArrayList<ArrayList<String>>>() {
            }.getType()
        );
        Assert.assertEquals(
            JieReflect.replaceType(new TypeRef<List<ArrayList<String>>>() {
            }.getType(), ArrayList.class, LinkedList.class),
            new TypeRef<List<LinkedList<String>>>() {
            }.getType()
        );
        Assert.assertEquals(
            JieReflect.replaceType(new TypeRef<List<List<? extends String>>>() {
            }.getType(), String.class, Integer.class),
            new TypeRef<List<List<? extends Integer>>>() {
            }.getType()
        );
        Assert.assertEquals(
            JieReflect.replaceType(new TypeRef<List<List<? extends String>>>() {
            }.getType(), String.class, Integer.class, false),
            new TypeRef<List<List<? extends String>>>() {
            }.getType()
        );
        Assert.assertEquals(
            JieReflect.replaceType(new TypeRef<List<List<? super String>>>() {
            }.getType(), String.class, Integer.class),
            new TypeRef<List<List<? super Integer>>>() {
            }.getType()
        );
        Assert.assertEquals(
            JieReflect.replaceType(JieType.array(String.class), String.class, Integer.class),
            JieType.array(Integer.class)
        );
        Assert.assertEquals(
            JieReflect.replaceType(JieType.array(JieType.array(String.class)), String.class, Integer.class, false),
            JieType.array(JieType.array(String.class))
        );
        Assert.assertEquals(
            JieReflect.replaceType(JieType.upperBound(Integer.class), String.class, Integer.class, false),
            JieType.upperBound(Integer.class)
        );
        Assert.assertEquals(
            JieReflect.replaceType(JieType.lowerBound(JieType.lowerBound(String.class)), String.class, Integer.class),
            JieType.lowerBound(JieType.lowerBound(Integer.class))
        );
        Assert.assertEquals(
            JieReflect.replaceType(JieType.lowerBound(JieType.lowerBound(String.class)), String.class, String.class),
            JieType.lowerBound(JieType.lowerBound(String.class))
        );
        Assert.assertEquals(
            JieReflect.replaceType(JieType.lowerBound(JieType.lowerBound(String.class)), String.class, Integer.class, false),
            JieType.lowerBound(JieType.lowerBound(String.class))
        );
        Assert.assertEquals(
            JieReflect.replaceType(JieType.lowerBound(Integer.class), String.class, Integer.class, false),
            JieType.lowerBound(Integer.class)
        );

        Assert.assertEquals(
            JieReflect.replaceType(new TypeRef<Inner<NumberString1>.SubInner<String, String>>() {
            }.getType(), NumberString1.class, NumberString2.class),
            new TypeRef<Inner<NumberString2>.SubInner<String, String>>() {
            }.getType()
        );
        Assert.assertEquals(
            JieReflect.replaceType(new TypeRef<Inner<NumberString1>.SubInner<String, String>>() {
            }.getType(), JieType.parameterized(Inner.class, new Type[]{NumberString1.class}), String.class),
            JieType.parameterized(
                Inner.SubInner.class,
                new Type[]{String.class, String.class},
                String.class
            )
        );
        Assert.assertEquals(
            JieReflect.replaceType(new TypeRef<Inner<NumberString1>.SubInner<String, String>>() {
            }.getType(), NumberString1.class, NumberString2.class, false),
            new TypeRef<Inner<NumberString1>.SubInner<String, String>>() {
            }.getType()
        );
    }

    @Test
    public void testTypeRef() {
        ParameterizedType parameterizedType = new TypeRef<List<String>>() {
        }.getParameterized();
        Assert.assertEquals(parameterizedType, JieType.parameterized(List.class, new Type[]{String.class}));

        class TestRef extends TypeRef<String> {
        }
        TestRef testRef = new TestRef();
        Assert.assertEquals(testRef.getType(), String.class);
        class TestRef2 extends TestRef {
        }
        TestRef2 testRef2 = new TestRef2();
        Assert.assertEquals(testRef2.getType(), String.class);
    }

    @Test
    public void testType() {
        ParameterizedType p1 = JieType.parameterized(Map.class, Arrays.asList(String.class, String.class));
        Type t1 = new TypeRef<Map<String, String>>() {
        }.getType();
        Assert.assertTrue(p1.equals(t1));
        Assert.assertFalse(p1.equals(String.class));
        Assert.assertEquals(p1.hashCode(), t1.hashCode());
        Assert.assertNotEquals(p1, new TypeRef<Map<String, Integer>>() {
        }.getType());
        ParameterizedType p2 = JieType.parameterized(
            Inner.SubInner.class,
            Arrays.asList(String.class, String.class),
            JieType.parameterized(Inner.class, new Type[]{NumberString1.class})
        );
        Assert.assertEquals(p2, new TypeRef<Inner<NumberString1>.SubInner<String, String>>() {
        }.getType());
        Assert.assertTrue(p1.equals(p1));
        Assert.assertFalse(p1.equals(null));
        Assert.assertNotEquals(p1, p2);
        Assert.assertNotEquals(p1, String.class);
        Assert.assertEquals(p1.toString(), new TypeRef<Map<String, String>>() {
        }.getType().toString());
        Assert.assertEquals(p2.toString(), new TypeRef<Inner<NumberString1>.SubInner<String, String>>() {
        }.getType().toString());
        Assert.assertEquals(p1.hashCode(), new TypeRef<Map<String, String>>() {
        }.getType().hashCode());
        Assert.assertEquals(p2.hashCode(), new TypeRef<Inner<NumberString1>.SubInner<String, String>>() {
        }.getType().hashCode());
        Assert.assertEquals(JieType.parameterized(List.class, new Type[]{}).toString(), List.class.getName());

        WildcardType w1 = JieType.upperBound(String.class);
        Assert.assertEquals(w1, new TypeRef<List<? extends String>>() {
        }.getParameterized().getActualTypeArguments()[0]);
        WildcardType w2 = JieType.lowerBound(String.class);
        Assert.assertEquals(w2, new TypeRef<List<? super String>>() {
        }.getParameterized().getActualTypeArguments()[0]);
        Assert.assertTrue(w1.equals(w1));
        Assert.assertFalse(w1.equals(null));
        Assert.assertNotEquals(w1, w2);
        Assert.assertNotEquals(w1, String.class);
        Assert.assertEquals(w1.toString(), new TypeRef<List<? extends String>>() {
        }.getParameterized().getActualTypeArguments()[0].toString());
        Assert.assertEquals(w2.toString(), new TypeRef<List<? super String>>() {
        }.getParameterized().getActualTypeArguments()[0].toString());
        Assert.assertEquals(w1.hashCode(), new TypeRef<List<? extends String>>() {
        }.getParameterized().getActualTypeArguments()[0].hashCode());
        Assert.assertEquals(w2.hashCode(), new TypeRef<List<? super String>>() {
        }.getParameterized().getActualTypeArguments()[0].hashCode());
        WildcardType w3 = JieType.wildcard(new Type[]{}, new Type[]{});
        Assert.assertEquals(w3.toString(), "?");
        WildcardType w4 = JieType.upperBound(Object.class);
        Assert.assertEquals(w4, new TypeRef<List<? extends Object>>() {
        }.getParameterized().getActualTypeArguments()[0]);
        Assert.assertEquals(w4.toString(), new TypeRef<List<? extends Object>>() {
        }.getParameterized().getActualTypeArguments()[0].toString());
        WildcardType w5 = JieType.wildcard(new Type[]{String.class, Integer.class}, new Type[]{});
        Assert.assertEquals(w5.toString(), "? extends " + String.class.getName() + " & " + Integer.class.getName());
        WildcardType w6 = JieType.lowerBound(null);
        Assert.assertEquals(w6.toString(), "? super java.lang.Object");

        GenericArrayType g1 = JieType.array(JieType.parameterized(List.class, new Type[]{String.class}));
        Assert.assertEquals(g1, new TypeRef<List<String>[]>() {
        }.getType());
        Assert.assertTrue(g1.equals(g1));
        Assert.assertFalse(g1.equals(null));
        Assert.assertNotEquals(g1, w2);
        Assert.assertNotEquals(g1, String.class);
        Assert.assertEquals(g1.toString(), new TypeRef<List<String>[]>() {
        }.getType().toString());
        Assert.assertEquals(g1.hashCode(), new TypeRef<List<String>[]>() {
        }.getType().hashCode());
        Assert.assertEquals(JieType.array(String.class).toString(), String.class.getName() + "[]");

        Type other = JieType.other();
        Assert.assertEquals(other.getTypeName(), "Hello, Jieva!");
        Assert.assertTrue(other.equals(other));
        Assert.assertFalse(other.equals(null));
        Assert.assertNotEquals(other, w2);
        Assert.assertNotEquals(other, String.class);
        Assert.assertEquals(other.hashCode(), 1);
    }

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

    public interface SuperInter1<SI11, SI12> extends SuperSuperInter1<SI11, Integer>, SuperSuperInter2<Long, SI12> {
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

        public class SubInner<SI1, SI2> {
        }

        public T[] tArray;
    }

    public static class NumberString1 extends Number implements CharSequence {

        @Override
        public int length() {
            return 0;
        }

        @Override
        public char charAt(int index) {
            return 0;
        }

        @NotNull
        @Override
        public CharSequence subSequence(int start, int end) {
            return null;
        }

        @Override
        public int intValue() {
            return 0;
        }

        @Override
        public long longValue() {
            return 0;
        }

        @Override
        public float floatValue() {
            return 0;
        }

        @Override
        public double doubleValue() {
            return 0;
        }
    }

    public static class NumberString2 extends NumberString1 {
    }
}
