package test.java.xyz.srclab.common.base;

import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.CacheableBuilder;
import xyz.srclab.common.logging.Logger;

public class BuildersTest {

    private static final Logger logger = Logger.simpleLogger();

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
        logger.info("value1: {}", value1);
        logger.info("value2: {}", value2);
        logger.info("value3: {}", value3);
        Assert.assertSame(value1, value2);
        Assert.assertNotEquals(value2, value3);
    }
}
