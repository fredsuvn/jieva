package sample.xyz.srclab.annotations;

import org.testng.annotations.Test;
import xyz.srclab.annotations.JavaBean;

public class AnnotationSample {

    @Test
    public void testAnnotations() {

    }


    public CharSequence returnCharSequence(String p1, String p2) {
        return "";
    }

    @JavaBean
    public static class TestBean {
        private String p1;
        private String p2;

        public String getP1() {
            return p1;
        }

        public void setP1(String p1) {
            this.p1 = p1;
        }

        public String getP2() {
            return p2;
        }

        public void setP2(String p2) {
            this.p2 = p2;
        }
    }
}
