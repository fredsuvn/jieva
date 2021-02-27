package sample.xyz.srclab.annotations;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.annotations.Accepted;
import xyz.srclab.annotations.JavaBean;
import xyz.srclab.annotations.NonNull;
import xyz.srclab.annotations.Written;

public class AnnotationSample {

    @Test
    public void testAnnotations() {
        TestBean testBean = new TestBean();
        Assert.assertEquals(testBean.getP2().substring(1), "2");
        Assert.expectThrows(NullPointerException.class, () -> testBean.getP1().substring(1));

        StringBuilder buffer = new StringBuilder();
        writeBuffer(buffer, "123");
        Assert.assertEquals(buffer.toString(), "123");
    }

    private void writeBuffer(
            @Written StringBuilder buffer,
            @Accepted(String.class) @Accepted(StringBuffer.class) CharSequence readOnly
    ) {
        buffer.append(readOnly);
    }

    @JavaBean
    public static class TestBean {

        private String p1;
        @NonNull
        private String p2 = "p2";

        public String getP1() {
            return p1;
        }

        public void setP1(String p1) {
            this.p1 = p1;
        }

        @NonNull
        public String getP2() {
            return p2;
        }

        public void setP2(@NonNull String p2) {
            this.p2 = p2;
        }
    }
}
