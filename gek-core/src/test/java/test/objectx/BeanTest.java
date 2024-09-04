package test.objectx;

import lombok.Data;
import org.testng.annotations.Test;

import java.util.List;

public class BeanTest {

    @Test
    public void testResolving() {

    }

    @Data
    public static class Inner<T1, T2> {
        private String ffFf1;
        private T1 ffFf2;
        private T2 ffFf3;
        private List<String> ffFf4;
        private List<String>[] ffFf5;
    }
}
