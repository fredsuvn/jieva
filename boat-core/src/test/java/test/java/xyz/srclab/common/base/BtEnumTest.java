package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BtEnum;
import xyz.srclab.common.base.NoSuchEnumException;

public class BtEnumTest {

    @Test
    public void test() {
        TestEnum t1 = BtEnum.getEnum(TestEnum.class, "T1");
        Assert.assertEquals(t1, TestEnum.T1);
        TestEnum t11 = BtEnum.getEnumOrNull(TestEnum.class, "T1");
        Assert.assertEquals(t11, TestEnum.T1);
        TestEnum t2 = BtEnum.getEnum(TestEnum.class, "t2", true);
        Assert.assertEquals(t2, TestEnum.T2);
        TestEnum t22 = BtEnum.getEnumOrNull(TestEnum.class, "t2", true);
        Assert.assertEquals(t22, TestEnum.T2);
        TestEnum t3 = BtEnum.getEnum(TestEnum.class, 2);
        Assert.assertEquals(t3, TestEnum.T3);
        TestEnum t33 = BtEnum.getEnumOrNull(TestEnum.class, 2);
        Assert.assertEquals(t33, TestEnum.T3);
        Assert.expectThrows(NoSuchEnumException.class, () -> BtEnum.getEnum(TestEnum.class, "T4"));
    }

    public enum TestEnum {
        T1,
        T2,
        T3,
    }
}
