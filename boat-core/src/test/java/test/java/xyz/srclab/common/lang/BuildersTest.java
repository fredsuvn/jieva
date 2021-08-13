package test.java.xyz.srclab.common.lang;

import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.lang.CacheableBuilder;
import xyz.srclab.common.test.TestLogger;

public class BuildersTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testCachingProductBuilder() {

        class TestCachingBuilder extends CacheableBuilder<String> {

            private String value = "null";
            private long counter = 0L;

            public void setValue(String value) {
                this.value = value;
                this.commitModification();
            }

            @NotNull
            @Override
            protected String buildNew() {
                return value + counter++;
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
        Assert.assertTrue(value1 == value2);
        Assert.assertNotEquals(value2, value3);
    }
}
