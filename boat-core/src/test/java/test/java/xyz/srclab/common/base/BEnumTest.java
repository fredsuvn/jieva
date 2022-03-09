package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BEnum;
import xyz.srclab.common.base.NoSuchEnumException;

public class BEnumTest {

    @Test
    public void test() {
        TestEnum t1 = BEnum.getValue(TestEnum.class, "T1");
        Assert.assertEquals(t1, TestEnum.T1);
        TestEnum t11 = BEnum.getValueOrNull(TestEnum.class, "T1");
        Assert.assertEquals(t11, TestEnum.T1);
        TestEnum t2 = BEnum.getValue(TestEnum.class, "t2", true);
        Assert.assertEquals(t2, TestEnum.T2);
        TestEnum t22 = BEnum.getValueOrNull(TestEnum.class, "t2", true);
        Assert.assertEquals(t22, TestEnum.T2);
        TestEnum t3 = BEnum.getValue(TestEnum.class, 2);
        Assert.assertEquals(t3, TestEnum.T3);
        TestEnum t33 = BEnum.getValueOrNull(TestEnum.class, 2);
        Assert.assertEquals(t33, TestEnum.T3);
        Assert.expectThrows(NoSuchEnumException.class, () -> BEnum.getValue(TestEnum.class, "T4"));
    }

    public enum TestEnum {
        T1,
        T2,
        T3,
    }
}
