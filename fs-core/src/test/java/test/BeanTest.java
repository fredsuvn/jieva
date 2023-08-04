package test;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FsLogger;
import xyz.srclab.common.bean.FsBean;
import xyz.srclab.common.bean.FsBeanProperty;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.annotation.*;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BeanTest {

    @Test
    public void testTypeBean() throws Exception {
        Type ccType = new TypeRef<Cc<Double>>() {
        }.getType();
        FsBean ccBean = FsBean.resolve(ccType);
        FsLogger.system().info("ccBean: ", ccBean);
        FsBeanProperty cc = ccBean.getProperty("cc");
        FsBeanProperty c1 = ccBean.getProperty("c1");
        FsBeanProperty c2 = ccBean.getProperty("c2");
        FsBeanProperty i1 = ccBean.getProperty("i1");
        FsBeanProperty i2 = ccBean.getProperty("i2");
        Assert.assertEquals(cc.getType(), Double.class);
        Assert.assertEquals(c2.getType(), Long.class);
        Assert.assertEquals(i1.getType(), String.class);
        Assert.assertEquals(i2.getType(), Integer.class);
        Assert.assertNull(c1);
        Assert.assertEquals(
            c2.getFieldAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
            Arrays.asList(C2.class.getDeclaredField("c2").getAnnotations()[0].toString())
        );
        Assert.assertEquals(
            c2.getGetterAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
            Arrays.asList(C2.class.getMethod("getC2").getAnnotations()[0].toString())
        );
        Assert.assertEquals(
            c2.getSetterAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
            Arrays.asList(C2.class.getMethod("setC2", Object.class).getAnnotations()[0].toString())
        );
        Assert.assertEquals(
            c2.getAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
            Arrays.asList(
                C2.class.getMethod("getC2").getAnnotations()[0].toString(),
                C2.class.getMethod("setC2", Object.class).getAnnotations()[0].toString(),
                C2.class.getDeclaredField("c2").getAnnotations()[0].toString()
            ));
    }

    @Test
    public void testClassBean() throws Exception {
        Type ccType = Cc.class;
        FsBean ccBean = FsBean.resolve(ccType);
        FsLogger.system().info("ccBean: ", ccBean);
        FsBeanProperty cc = ccBean.getProperty("cc");
        FsBeanProperty c1 = ccBean.getProperty("c1");
        FsBeanProperty c2 = ccBean.getProperty("c2");
        FsBeanProperty i1 = ccBean.getProperty("i1");
        FsBeanProperty i2 = ccBean.getProperty("i2");
        Assert.assertEquals(cc.getType().toString(), "T");
        Assert.assertEquals(c2.getType(), Long.class);
        Assert.assertEquals(i1.getType(), String.class);
        Assert.assertEquals(i2.getType(), Integer.class);
        Assert.assertNull(c1);
        Assert.assertEquals(
            c2.getFieldAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
            Arrays.asList(C2.class.getDeclaredField("c2").getAnnotations()[0].toString())
        );
        Assert.assertEquals(
            c2.getGetterAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
            Arrays.asList(C2.class.getMethod("getC2").getAnnotations()[0].toString())
        );
        Assert.assertEquals(
            c2.getSetterAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
            Arrays.asList(C2.class.getMethod("setC2", Object.class).getAnnotations()[0].toString())
        );
        Assert.assertEquals(
            c2.getAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
            Arrays.asList(
                C2.class.getMethod("getC2").getAnnotations()[0].toString(),
                C2.class.getMethod("setC2", Object.class).getAnnotations()[0].toString(),
                C2.class.getDeclaredField("c2").getAnnotations()[0].toString()
            ));
    }

    @Test
    public void testBeanResolver() {
        Type ccType = new TypeRef<Cc<Double>>() {
        }.getType();
        FsBean ccBean1 = FsBean.resolve(ccType);
        FsBean ccBean2 = FsBean.resolve(ccType);
        Assert.assertSame(ccBean1, ccBean2);
        FsBean.Resolver resolver = FsBean.resolverBuilder().useCache(false).build();
        FsBean ccBean3 = resolver.resolve(ccType);
        Assert.assertNotSame(ccBean1, ccBean3);
        Assert.assertEquals(ccBean1, ccBean3);
        FsBean ccBean4 = resolver.resolve(ccType);
        Assert.assertNotSame(ccBean4, ccBean3);
        Assert.assertEquals(ccBean4, ccBean3);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Cc<T> extends C2<Long> implements I1, I2<Integer> {

        private T cc;
        private Integer i2;

        @Override
        public String getI1() {
            return null;
        }

        @Override
        public void setI1(String i1) {

        }
    }

    public static class C2<T> {

        @Ann("c2")
        private T c2;

        @Ann("getC2")
        public T getC2() {
            return c2;
        }

        @Ann("setC2")
        public void setC2(T c2) {
            this.c2 = c2;
        }
    }

    @Data
    public static class C1 {
        private String c1;
    }

    public interface I2<T> {

        T getI2();

        void setI2(T t);
    }

    public interface I1 {

        String getI1();

        void setI1(String i1);
    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    public @interface Ann {
        String value();
    }
}
