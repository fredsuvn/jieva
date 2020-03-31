package test.xyz.srclab.common.main;

import org.testng.annotations.Test;

public class MainTest {

    @Test
    public void testArray() {
        Object[] objects = new Object[0];
        Number[] numbers = new Number[0];
        Integer[] integers = new Integer[0];
        System.out.println(objects.getClass());
        System.out.println(numbers.getClass());
        System.out.println(integers.getClass());
        System.out.println(numbers.getClass().isAssignableFrom(integers.getClass()));
    }

    //    @NotNull
    public String testNotNull(String a) {
        return null;
    }

    //    @Nullable
    public String testNullable(String a) {
        return null;
    }

    public String testNull(String a) {
        return null;
    }
}
