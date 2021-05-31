package test.xyz.srclab.common.id;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.id.IdSpec;
import xyz.srclab.common.test.TestLogger;

/**
 * @author sunqian
 */
public class IdTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testId() {
        //seq0-06803239610792857600-tail
        String spec0 = "seq0-{Snowflake, 20, 41, 10, 12}-tail";
        IdSpec stringIdSpec0 = new IdSpec(spec0);
        for (int i = 0; i < 10; i++) {
            logger.log(stringIdSpec0.newId());
        }

        //seq1-00001826267315077279180346359808-tail
        String spec1 = "seq1-{Snowflake, 32, 55, 25, 25}-tail";
        IdSpec stringIdSpec1 = new IdSpec(spec1);
        for (int i = 0; i < 10; i++) {
            logger.log(stringIdSpec1.newId());
        }

        //seq2-29921563690270857976266765631488-tail
        String spec2 = "seq2-{Snowflake, 32, 63, 32, 32}-tail";
        IdSpec stringIdSpec2 = new IdSpec(spec2);
        for (int i = 0; i < 10; i++) {
            logger.log(stringIdSpec2.newId());
        }

        //seq3{}-06803240106559590400-tail
        String spec3 = "seq3\\{}-{Snowflake, 20, 41, 10, 12}-tail";
        IdSpec stringIdSpec3 = new IdSpec(spec3);
        for (int i = 0; i < 10; i++) {
            logger.log(stringIdSpec3.newId());
        }

        //seq4{}-06805124180752646144-tail
        String spec4 = "seq4\\{\\}-{Snowflake, 20, 41, 10, 12}-tail";
        IdSpec stringIdSpec4 = new IdSpec(spec4);
        for (int i = 0; i < 10; i++) {
            logger.log(stringIdSpec4.newId());
        }

        String spec5 = "seq5\\{\\}-{Snowflake, 20, 41, 10, 12";
        Assert.expectThrows(IllegalArgumentException.class, () -> new IdSpec(spec5));
    }

    @Test
    public void testCustomId() {
        String spec = "seq-{Snowflake, 20, 41, 10, 12}-{My, 88888}";
        IdSpec stringIdSpec = new IdSpec(spec, (IdSpec.ComponentSupplier) type -> {
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
