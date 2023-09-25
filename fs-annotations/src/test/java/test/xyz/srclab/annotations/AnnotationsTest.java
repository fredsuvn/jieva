package test.xyz.srclab.annotations;

import org.testng.annotations.Test;
import xyz.srclab.annotations.Nullable;

/**
 * @author sunqian
 */
public class AnnotationsTest {

    @Test
    public void testAnnotations() {
        Bean1 bean1 = new Bean1();
        bean1.setP1("123");
        bean1.setP1("p1");
        bean1.setP2("p2");
        @Nullable String p1 = bean1.getP1();
        String p2 = bean1.getP2();
        System.out.println(p1.substring(1));
        System.out.println(p2.substring(1));
    }
}
