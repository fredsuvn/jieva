package test.objectx;

import lombok.Data;
import org.testng.Assert;
import org.testng.annotations.Test;
import test.TestUtil;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.bean.*;
import xyz.fslabo.common.coll.JieColl;
import xyz.fslabo.common.reflect.JieType;
import xyz.fslabo.common.reflect.TypeRef;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

public class BeanTest {

    @Test
    public void testBeanInfo() throws Exception {
        BeanProvider provider = BeanProvider.defaultProvider();
        BeanInfo b1 = provider.getBeanInfo(new TypeRef<Inner<Short, Long>>() {
        }.getType());
        BeanInfo b2 = provider.getBeanInfo(new TypeRef<Inner<Short, Long>>() {
        }.getType());
        BeanInfo b3 = BeanResolver.defaultResolver().resolve(new TypeRef<Inner<Short, Long>>() {
        }.getType());
        Assert.assertSame(b1, b2);
        Assert.assertEquals(b1, b2);
        Assert.assertEquals(b1.toString(), b2.toString());
        Assert.assertEquals(b1.hashCode(), b2.hashCode());
        Assert.assertNotSame(b1, b3);
        Assert.assertEquals(b1, b3);
        Assert.assertEquals(b1.toString(), b3.toString());
        Assert.assertEquals(b1.hashCode(), b3.hashCode());
        Assert.assertTrue(b1.equals(b1));
        Assert.assertFalse(b1.equals(null));
        Assert.assertFalse(b1.equals(""));
        Assert.assertEquals(
            JieColl.addAll(new HashMap<>(), b1.getProperties(), k -> k, BasePropertyInfo::getType),
            JieColl.addAll(new HashMap<>(), "ffFf1", String.class
                , "ffFf2", Short.class
                , "ffFf3", Long.class
                , "ffFf4", JieType.parameterized(List.class, new Type[]{String.class})
                , "ffFf5", JieType.array(JieType.parameterized(List.class, new Type[]{String.class}))
                , "class", JieType.parameterized(Class.class, new Type[]{JieType.questionMark()}))
        );
        List<Method> mList = new ArrayList<>(TestUtil.getMethods(Inner.class));
        mList.remove(Object.class.getMethod("getClass"));
        Assert.assertEquals(
            JieColl.addAll(new ArrayList<>(), b1.getMethods(), BaseMethodInfo::getMethod),
            mList
        );
        Assert.assertEquals(
            b1.getType(), new TypeRef<Inner<Short, Long>>() {
            }.getType()
        );
        Assert.assertEquals(
            b1.getProperty("ffFf1").getOwner(), b1
        );
        Assert.assertEquals(
            b1.getProperty("ffFf1"), b3.getProperty("ffFf1")
        );
        Assert.assertEquals(
            b1.getProperty("ffFf1").toString(), b3.getProperty("ffFf1").toString()
        );
        Assert.assertEquals(
            b1.getProperty("ffFf1").hashCode(), b3.getProperty("ffFf1").hashCode()
        );
        Assert.assertNotSame(
            b1.getProperty("ffFf1"), b3.getProperty("ffFf1")
        );
        Assert.assertEquals(
            b1.getProperty("ffFf1").toString(), b1.getProperty("ffFf1").toString()
        );
        Assert.assertSame(
            b1.getProperty("ffFf1"), b1.getProperty("ffFf1")
        );
        Assert.assertTrue(b1.getProperty("ffFf1").equals(b1.getProperty("ffFf1")));
        Assert.assertFalse(b1.getProperty("ffFf1").equals(null));
        Assert.assertFalse(b1.getProperty("ffFf1").equals(""));
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

    @Data
    public static class Inner<T1, T2> {
        private String ffFf1;
        private T1 ffFf2;
        private T2 ffFf3;
        private List<String> ffFf4;
        private List<String>[] ffFf5;

        public T1 m1() {
            return null;
        }
    }
}
