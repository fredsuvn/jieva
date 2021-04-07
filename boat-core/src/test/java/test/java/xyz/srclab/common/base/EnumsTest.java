package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Enums;

public class EnumsTest {

    @Test
    public void test() {
        TestEnum t1 = Enums.valueOf(TestEnum.class, "T1");
        Assert.assertEquals(t1, TestEnum.T1);
        TestEnum t2 = Enums.valueOfIgnoreCase(TestEnum.class, "t2");
        Assert.assertEquals(t2, TestEnum.T2);
    }

    public enum TestEnum {
        T1,
        T2
    }
}
