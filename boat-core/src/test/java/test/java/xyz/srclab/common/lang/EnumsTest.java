package test.java.xyz.srclab.common.lang;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.lang.Enums;

public class EnumsTest {

    @Test
    public void test() {
        TestEnum t1 = Enums.value(TestEnum.class, "T1");
        Assert.assertEquals(t1, TestEnum.T1);
        TestEnum t2 = Enums.valueIgnoreCase(TestEnum.class, "t2");
        Assert.assertEquals(t2, TestEnum.T2);
        TestEnum t3 = Enums.value(TestEnum.class, 2);
        Assert.assertEquals(t3, TestEnum.T3);
    }

    public enum TestEnum {
        T1,
        T2,
        T3
    }
}
