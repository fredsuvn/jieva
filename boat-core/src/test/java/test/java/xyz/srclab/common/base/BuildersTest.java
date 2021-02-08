package test.java.xyz.srclab.common.base;

import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.CachingProductBuilder;
import xyz.srclab.common.test.TestLogger;

import java.util.UUID;

public class BuildersTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testBuilder() {

        class TestCachingBuilder extends CachingProductBuilder<String> {

            private String value = "null";

            public void setValue(String value) {
                this.value = value;
                this.commitChange();
            }

            @NotNull
            @Override
            protected String buildNew() {
                return value + UUID.randomUUID().toString();
            }
        }

        TestCachingBuilder testCachingBuilder = new TestCachingBuilder();
        testCachingBuilder.setValue("1");
        String value1 = testCachingBuilder.build();
        String value2 = testCachingBuilder.build();
        testCachingBuilder.setValue("2");
        String value3 = testCachingBuilder.build();
        logger.log("value1: {}", value1);
        logger.log("value2: {}", value2);
        logger.log("value3: {}", value3);
        Assert.assertEquals(value1, value2);
        Assert.assertNotEquals(value2, value3);
    }
}
