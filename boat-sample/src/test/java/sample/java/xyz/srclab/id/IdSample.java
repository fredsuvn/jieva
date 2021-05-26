package sample.java.xyz.srclab.id;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.id.IdSpec;
import xyz.srclab.common.test.TestLogger;

public class IdSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testId() {
        //seq-06803239610792857600-tail
        String spec = "seq-{Snowflake, 20, 41, 10, 12}-tail";
        IdSpec stringIdSpec = new IdSpec(spec);
        for (int i = 0; i < 10; i++) {
            logger.log(stringIdSpec.newId());
        }

        //seq{}-06803240106559590400-tail
        String spec2 = "seq\\{}-{Snowflake, 20, 41, 10, 12}-tail";
        IdSpec stringIdSpec2 = new IdSpec(spec2);
        for (int i = 0; i < 10; i++) {
            logger.log(stringIdSpec2.newId());
        }

        String spec3 = "seq\\{\\}-{Snowflake, 20, 41, 10, 12";
        Assert.expectThrows(IllegalArgumentException.class, () -> new IdSpec(spec3));
        //new StringIdSpec(spec3);
    }

    @Test
    public void testCustomId() {
        String spec = "seq-{Snowflake, 20, 41, 10, 12}-{My, 88888}";
        IdSpec stringIdSpec = new IdSpec(spec, type -> {
            if (type.equals(MyIdComponent.TYPE)) {
                return new MyIdComponent();
            }
            return IdSpec.DEFAULT_COMPONENT_SUPPLIER.get(type);
        });
        //seq-06803242693339123712-88888
        for (int i = 0; i < 10; i++) {
            logger.log(stringIdSpec.newId());
        }
    }
}
