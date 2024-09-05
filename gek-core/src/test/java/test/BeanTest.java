package test;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.bean.*;
import xyz.fslabo.common.bean.handlers.NonGetterPrefixResolverHandler;
import xyz.fslabo.common.bean.handlers.NonPrefixResolverHandler;
import xyz.fslabo.common.coll.JieColl;
import xyz.fslabo.common.reflect.JieType;
import xyz.fslabo.common.reflect.TypeRef;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

public class BeanTest {

    @Test
    public void testProvider() {
        BeanProvider provider = BeanProvider.defaultProvider();
        BeanInfo b1 = provider.getBeanInfo(new TypeRef<Inner<Short, Long>>() {
        }.getType());
        BeanInfo b2 = provider.getBeanInfo(new TypeRef<Inner<Short, Long>>() {
        }.getType());
        Assert.assertSame(b1, b2);
        Assert.assertEquals(b1, b2);
        Assert.assertEquals(b1.toString(), b2.toString());
        Assert.assertEquals(b1.hashCode(), b2.hashCode());
        BeanProvider provider2 = BeanProvider.withResolver(BeanResolver.defaultResolver());
        BeanInfo b3 = provider2.getBeanInfo(new TypeRef<Inner<Short, Long>>() {
        }.getType());
        Assert.assertNotSame(b1, b3);
        Assert.assertEquals(b1, b3);
        Assert.assertEquals(b1.toString(), b3.toString());
        Assert.assertEquals(b1.hashCode(), b3.hashCode());
    }

    @Test
    public void testBeanInfo() {
        BeanInfo b1 = BeanInfo.get(new TypeRef<Inner<Short, Long>>() {
        }.getType());
        BeanInfo b2 = BeanResolver.defaultResolver().resolve(new TypeRef<Inner<Short, Long>>() {
        }.getType());
        Assert.assertEquals(b1.getType(), new TypeRef<Inner<Short, Long>>() {
        }.getType());
        Assert.assertEquals(b1.getRawType(), Inner.class);
        Assert.assertNotSame(b1, b2);
        Assert.assertEquals(b1, b2);
        Assert.assertEquals(b1.toString(), b2.toString());
        Assert.assertEquals(b1.hashCode(), b2.hashCode());
        Assert.assertTrue(b1.equals(b1));
        Assert.assertFalse(b1.equals(null));
        Assert.assertFalse(b1.equals(""));
        Assert.assertEquals(
            JieColl.addAll(new HashMap<>(), b1.getProperties(), k -> k, BasePropertyInfo::getType),
            Jie.hashMap("ffFf1", String.class
                , "ffFf2", Short.class
                , "ffFf3", Long.class
                , "ffFf4", JieType.parameterized(List.class, new Type[]{String.class})
                , "ffFf5", JieType.array(JieType.parameterized(List.class, new Type[]{String.class}))
                , "class", JieType.parameterized(Class.class, new Type[]{JieType.questionMark()})
                , "c1", Short.class
                , "bb", boolean.class)
        );
        List<Method> mList = new ArrayList<>(TestUtil.getMethods(Inner.class));
        mList.removeIf(m ->
            (((m.getName().startsWith("get") || m.getName().startsWith("set")) && m.getName().length() > 3)
                || (m.getName().startsWith("is") && m.getName().length() > 2))
                && (!m.getName().equals("gett") && !m.getName().equals("sett") && !m.getName().equals("iss")));
        Assert.assertEquals(
            JieColl.addAll(new ArrayList<>(), b1.getMethods(), BaseMethodInfo::getMethod),
            mList
        );
        Assert.assertEquals(
            b1.getType(), new TypeRef<Inner<Short, Long>>() {
            }.getType()
        );

        BeanInfo b3 = BeanInfo.get(Inner.class);
        Assert.assertEquals(
            JieColl.addAll(new HashMap<>(), b3.getProperties(), k -> k, BasePropertyInfo::getType),
            Jie.hashMap("ffFf1", String.class
                , "ffFf2", Inner.class.getTypeParameters()[0]
                , "ffFf3", Inner.class.getTypeParameters()[1]
                , "ffFf4", JieType.parameterized(List.class, new Type[]{String.class})
                , "ffFf5", JieType.array(JieType.parameterized(List.class, new Type[]{String.class}))
                , "class", JieType.parameterized(Class.class, new Type[]{JieType.questionMark()})
                , "c1", Inner.class.getTypeParameters()[0]
                , "bb", boolean.class)
        );
    }

    @Test
    public void testMember() throws Exception {
        BeanInfo b1 = BeanInfo.get(new TypeRef<Inner<Short, Long>>() {
        }.getType());
        BeanInfo b3 = BeanResolver.defaultResolver().resolve(new TypeRef<Inner<Short, Long>>() {
        }.getType());
        PropertyInfo p1 = b1.getProperty("ffFf1");
        Assert.assertEquals(p1.getOwner(), b1);
        Assert.assertEquals(b1.toString(), b1.getType().getTypeName());
        Assert.assertEquals(p1.toString(),
            b1.getType().getTypeName() + "." + p1.getName() + "[" + p1.getType().getTypeName() + "]");
        MethodInfo m1 = b1.getMethod("m1");
        Assert.assertEquals(m1.toString(),
            b1.getType().getTypeName() + "." + m1.getName() + "()[" + m1.getMethod().getGenericReturnType() + "]");
        Assert.assertEquals(p1, b3.getProperty("ffFf1"));
        Assert.assertEquals(p1.toString(), b3.getProperty("ffFf1").toString());
        Assert.assertEquals(p1.hashCode(), b3.getProperty("ffFf1").hashCode());
        Assert.assertNotSame(p1, b3.getProperty("ffFf1"));
        Assert.assertSame(p1, p1);
        Assert.assertTrue(p1.equals(p1));
        Assert.assertFalse(p1.equals(null));
        Assert.assertFalse(p1.equals(""));
        Assert.assertEquals(m1, b3.getMethod("m1"));
        Assert.assertEquals(m1.toString(), b3.getMethod("m1").toString());
        Assert.assertEquals(m1.hashCode(), b3.getMethod("m1").hashCode());
        Assert.assertNotSame(m1, b3.getMethod("m1"));
        Assert.assertSame(m1, m1);
        Assert.assertTrue(m1.equals(m1));
        Assert.assertFalse(m1.equals(null));
        Assert.assertFalse(m1.equals(""));

        PropertyInfo p2 = b1.getProperty("ffFf2");
        PropertyInfo p3 = b1.getProperty("ffFf3");
        PropertyInfo p4 = b1.getProperty("ffFf4");
        PropertyInfo p5 = b1.getProperty("ffFf5");
        PropertyInfo c1 = b1.getProperty("c1");
        Assert.assertEquals(p1.getAnnotations().get(0).annotationType(), Nullable.class);
        Assert.assertEquals(p1.getGetterAnnotations().get(0).annotationType(), Nullable.class);
        Assert.assertTrue(p1.getSetterAnnotations().isEmpty());
        Assert.assertTrue(p1.getFieldAnnotations().isEmpty());
        Assert.assertNotNull(p1.getAnnotation(Nullable.class));
        Assert.assertEquals(p4.getAnnotations().get(0).annotationType(), Nullable.class);
        Assert.assertEquals(p4.getSetterAnnotations().get(0).annotationType(), Nullable.class);
        Assert.assertTrue(p4.getGetterAnnotations().isEmpty());
        Assert.assertTrue(p4.getFieldAnnotations().isEmpty());
        Assert.assertNotNull(p4.getAnnotation(Nullable.class));
        Assert.assertEquals(c1.getAnnotations().get(0).annotationType(), Nullable.class);
        Assert.assertEquals(c1.getFieldAnnotations().get(0).annotationType(), Nullable.class);
        Assert.assertTrue(c1.getSetterAnnotations().isEmpty());
        Assert.assertTrue(c1.getGetterAnnotations().isEmpty());
        Assert.assertNotNull(c1.getAnnotation(Nullable.class));
        Assert.assertEquals(m1.getAnnotations().get(0).annotationType(), Nullable.class);
        Assert.assertNull(p2.getAnnotation(Nullable.class));
        Assert.assertNull(p3.getAnnotation(Nullable.class));
        Assert.assertTrue(p1.isReadable());
        Assert.assertFalse(p1.isWriteable());
        Assert.assertTrue(p2.isReadable());
        Assert.assertTrue(p2.isWriteable());
        Assert.assertTrue(p3.isReadable());
        Assert.assertTrue(p3.isWriteable());
        Assert.assertFalse(p4.isReadable());
        Assert.assertTrue(p4.isWriteable());
        Assert.assertTrue(p5.isReadable());
        Assert.assertTrue(p5.isWriteable());
        Assert.assertTrue(c1.isReadable());
        Assert.assertTrue(c1.isWriteable());
        Assert.assertEquals(p4.getRawType(), List.class);
        Assert.assertEquals(p2.getGetter(), Inner.class.getMethod("getFfFf2"));
        Assert.assertEquals(p2.getSetter(), Inner.class.getMethod("setFfFf2", Object.class));
        Assert.assertEquals(p2.getField(), Inner.class.getDeclaredField("ffFf2"));
        Assert.assertEquals(c1.getField(), InnerChild.class.getDeclaredField("c1"));

        Inner<Short, Long> inner = new Inner<>();
        inner.setFfFf2((short) 22);
        Assert.assertEquals(m1.invoke(inner), (short) 22);
        Assert.assertEquals(p2.getValue(inner), (short) 22);
        p2.setValue(inner, (short) 111);
        Assert.assertEquals(p2.getValue(inner), (short) 111);
        Assert.expectThrows(BeanException.class, () -> p4.getValue(inner));
        Assert.expectThrows(BeanException.class, () -> p1.setValue(inner, 111));
        Assert.assertNull(b1.getMethod("m1", Object.class));
        Assert.assertNull(b1.getMethod("m11"));
    }

    @Test
    public void testResolver() {
        TestHandler h1 = new TestHandler();
        TestHandler h2 = new TestHandler();
        TestHandler h3 = new TestHandler();
        BeanResolver r1 = BeanResolver.withHandlers(h1);
        BeanInfo b1 = r1.resolve(Inner.class);
        Assert.assertEquals(b1.getProperties(), Collections.emptyMap());
        Assert.assertEquals(b1.getMethods(), Collections.emptyList());
        Assert.assertEquals(h1.times, 1);
        r1 = BeanResolver.withHandlers(Arrays.asList(h1));
        b1 = r1.resolve(Inner.class);
        Assert.assertEquals(b1.getProperties(), Collections.emptyMap());
        Assert.assertEquals(b1.getMethods(), Collections.emptyList());
        Assert.assertEquals(h1.times, 2);
        r1 = r1.addFirstHandler(h2);
        b1 = r1.resolve(Inner.class);
        Assert.assertEquals(b1.getProperties(), Collections.emptyMap());
        Assert.assertEquals(b1.getMethods(), Collections.emptyList());
        Assert.assertEquals(h1.times, 3);
        Assert.assertEquals(h2.times, 1);
        r1 = r1.addLastHandler(h3);
        b1 = r1.resolve(Inner.class);
        Assert.assertEquals(b1.getProperties(), Collections.emptyMap());
        Assert.assertEquals(b1.getMethods(), Collections.emptyList());
        Assert.assertEquals(h1.times, 4);
        Assert.assertEquals(h2.times, 2);
        Assert.assertEquals(h3.times, 1);
        r1 = r1.replaceFirstHandler(h3);
        r1 = r1.replaceFirstHandler(h3);
        b1 = r1.resolve(Inner.class);
        Assert.assertEquals(b1.getProperties(), Collections.emptyMap());
        Assert.assertEquals(b1.getMethods(), Collections.emptyList());
        Assert.assertEquals(h1.times, 5);
        Assert.assertEquals(h2.times, 2);
        Assert.assertEquals(h3.times, 3);
        r1 = r1.replaceLastHandler(h2);
        r1 = r1.replaceLastHandler(h2);
        b1 = r1.resolve(Inner.class);
        Assert.assertEquals(b1.getProperties(), Collections.emptyMap());
        Assert.assertEquals(b1.getMethods(), Collections.emptyList());
        Assert.assertEquals(h1.times, 6);
        Assert.assertEquals(h2.times, 3);
        Assert.assertEquals(h3.times, 4);
        //h3 -> h1 -> h2
        //h4 = h3 -> h1 -> h2
        BeanResolver.Handler h4 = r1.asHandler();
        //h3 -> h1 -> h2 -> h4
        r1 = r1.addLastHandler(h4);
        b1 = r1.resolve(Inner.class);
        Assert.assertEquals(b1.getProperties(), Collections.emptyMap());
        Assert.assertEquals(b1.getMethods(), Collections.emptyList());
        Assert.assertEquals(h1.times, 8);
        Assert.assertEquals(h2.times, 5);
        Assert.assertEquals(h3.times, 6);
        BreakHandler h5 = new BreakHandler();
        //h5 -> h3 -> h1 -> h2 -> h4
        r1 = r1.addFirstHandler(h5);
        b1 = r1.resolve(Inner.class);
        Assert.assertEquals(b1.getProperties(), Collections.emptyMap());
        Assert.assertEquals(b1.getMethods(), Collections.emptyList());
        Assert.assertEquals(h1.times, 8);
        Assert.assertEquals(h2.times, 5);
        Assert.assertEquals(h3.times, 6);

        BeanResolver r4 = (BeanResolver) h4;
        //h5 -> h4
        r4 = r4.addFirstHandler(h5);
        h4 = r4.asHandler();
        BeanResolver r5 = BeanResolver.withHandlers(h4);
        b1 = r5.resolve(Inner.class);
        Assert.assertEquals(b1.getProperties(), Collections.emptyMap());
        Assert.assertEquals(b1.getMethods(), Collections.emptyList());
        Assert.assertEquals(h1.times, 8);
        Assert.assertEquals(h2.times, 5);
        Assert.assertEquals(h3.times, 6);

        ThrowHandler h6 = new ThrowHandler();
        //h6 -> h5 -> h4
        BeanResolver r6 = r5.addFirstHandler(h6);
        Assert.expectThrows(BeanResolvingException.class, () -> r6.resolve(Inner.class));
    }

    @Test
    public void testHandler() {
        Assert.expectThrows(BeanResolvingException.class, () -> BeanInfo.get(JieType.array(String.class)));
        BeanInfo b1 = BeanResolver.withHandlers(new NonGetterPrefixResolverHandler()).resolve(Simple1.class);
        Assert.assertEquals(b1.getProperties().size(), 1);
        Simple1 s1 = new Simple1();
        PropertyInfo aa1 = b1.getProperty("aa");
        Assert.assertEquals(aa1.getValue(s1), null);
        aa1.setValue(s1, "ss");
        Assert.assertEquals(aa1.getValue(s1), "ss");

        BeanInfo b2 = BeanResolver.withHandlers(new NonPrefixResolverHandler()).resolve(Simple2.class);
        Assert.assertEquals(b2.getProperties().size(), 1);
        Simple2 s2 = new Simple2();
        PropertyInfo aa2 = b2.getProperty("aa");
        Assert.assertEquals(aa2.getValue(s2), null);
        aa2.setValue(s2, "ss");
        Assert.assertEquals(aa2.getValue(s2), "ss");
    }

    public static class TestHandler implements BeanResolver.Handler {

        public int times = 0;

        @Override
        public @Nullable Flag resolve(BeanResolver.Context context) throws BeanResolvingException {
            times++;
            return null;
        }
    }

    public static class BreakHandler implements BeanResolver.Handler {

        @Override
        public @Nullable Flag resolve(BeanResolver.Context context) throws BeanResolvingException {
            return Flag.BREAK;
        }
    }

    public static class ThrowHandler implements BeanResolver.Handler {

        @Override
        public @Nullable Flag resolve(BeanResolver.Context context) throws BeanResolvingException {
            throw new IllegalStateException("");
        }
    }

    public static class Inner<T1, T2> extends InnerChild<T1> {
        private String ffFf1;
        @Getter
        @Setter
        private T1 ffFf2;
        @Getter
        @Setter
        private T2 ffFf3;
        private List<String> ffFf4;
        @Getter
        @Setter
        private List<String>[] ffFf5;
        @Getter
        @Setter
        private boolean bb;

        @Nullable
        public T1 m1() {
            return ffFf2;
        }

        @Nullable
        public String getFfFf1() {
            return ffFf1;
        }

        @Nullable
        public void setFfFf4(List<String> ffFf4) {
            this.ffFf4 = ffFf4;
        }

        public String get() {
            return null;
        }

        public void set() {
        }

        public void set(Object obj) {
        }

        public boolean is() {
            return false;
        }

        public String gett() {
            return null;
        }

        public void sett(Object obj) {
        }

        public boolean iss() {
            return false;
        }
    }

    @Data
    public static class InnerChild<C> {
        @Nullable
        private C c1;
    }

    public static class Simple1 {
        private String aa;

        public String aa() {
            return aa;
        }

        public void setAa(String aa) {
            this.aa = aa;
        }

        public void set(Object a) {
        }

        public void set(Object a, Object b) {
        }

        public void sett(Object obj) {
        }
    }

    public static class Simple2 {
        private String aa;

        public String aa() {
            return aa;
        }

        public void aa(String aa) {
            this.aa = aa;
        }

        public void aa(String aa, String bb) {
            this.aa = aa;
        }
    }
}
