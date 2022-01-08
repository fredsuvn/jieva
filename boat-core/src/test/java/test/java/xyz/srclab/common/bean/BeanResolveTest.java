package test.java.xyz.srclab.common.bean;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.bean.BeanResolveHandler;
import xyz.srclab.common.bean.BeanResolver;
import xyz.srclab.common.bean.BeanType;
import xyz.srclab.common.bean.RecordStyleBeanResolveHandler;
import xyz.srclab.common.reflect.BType;

import java.util.Arrays;
import java.util.List;

public class BeanResolveTest {

    @Test
    public void testBeanResolve() {
        BeanResolver beanStyle = BeanResolver.newBeanResolver(BeanResolveHandler.DEFAULTS);
        BeanType beanType = beanStyle.resolve(BeanStyleBean.class);
        Assert.assertEquals(beanType.getProperties().size(), 5);
        Assert.assertEquals(beanType.getProperty("p1").getType(), String.class);
        Assert.assertEquals(beanType.getProperty("p2").getType(), int.class);
        Assert.assertEquals(beanType.getProperty("p3").getType(), BType.parameterizedType(List.class, String.class));
        Assert.assertEquals(beanType.getProperty("p4").getType(), long[].class);

        BeanResolver namingStyle = BeanResolver.newBeanResolver(Arrays.asList(RecordStyleBeanResolveHandler.INSTANCE));
        BeanType beanType2 = namingStyle.resolve(RecordStyleBean.class);
        Assert.assertEquals(beanType2.getProperties().size(), 4);
        Assert.assertEquals(beanType2.getProperty("p1").getType(), String.class);
        Assert.assertEquals(beanType2.getProperty("p2").getType(), int.class);
        Assert.assertEquals(beanType2.getProperty("p3").getType(), BType.parameterizedType(List.class, String.class));
        Assert.assertEquals(beanType2.getProperty("p4").getType(), long[].class);

        Assert.assertNotSame(beanType, beanType2);
    }

    public static class BeanStyleBean {

        private String p1;
        private int p2;
        private List<String> p3;
        private long[] p4;

        public String getP1() {
            return p1;
        }

        public void setP1(String p1) {
            this.p1 = p1;
        }

        public int getP2() {
            return p2;
        }

        public void setP2(int p2) {
            this.p2 = p2;
        }

        public List<String> getP3() {
            return p3;
        }

        public void setP3(List<String> p3) {
            this.p3 = p3;
        }

        public long[] getP4() {
            return p4;
        }

        public void setP4(long[] p4) {
            this.p4 = p4;
        }

        public enum TestEnum {
            A, B, C
        }
    }

    public static class RecordStyleBean {

        private String p1;
        private int p2;
        private List<String> p3;
        private long[] p4;

        public String p1() {
            return p1;
        }

        public void p1(String p1) {
            this.p1 = p1;
        }

        public int p2() {
            return p2;
        }

        public void p2(int p2) {
            this.p2 = p2;
        }

        public List<String> p3() {
            return p3;
        }

        public void p3(List<String> p3) {
            this.p3 = p3;
        }

        public long[] p4() {
            return p4;
        }

        public void p4(long[] p4) {
            this.p4 = p4;
        }
    }
}
