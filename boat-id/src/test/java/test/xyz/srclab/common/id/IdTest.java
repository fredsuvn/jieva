package test.xyz.srclab.common.id;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.id.StringIdFactoryGenerator;
import xyz.srclab.common.id.StringIdSpec;
import xyz.srclab.common.test.TestLogger;

/**
 * @author sunqian
 */
public class IdTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testId() {
        String spec = "seq-{TimeCount,yyyyMMddHHmmssSSS,1023,%17s%04d}-{Constant,tail}";
        StringIdSpec stringIdSpec = new StringIdSpec(spec);
        for (int i = 0; i < 10; i++) {
            logger.log(stringIdSpec.newId());
        }

        String spec2 = "seq\\{\\}-{TimeCount,yyyyMMddHHmmssSSS,1023,%17s%04d\\\\}-{Constant,tail}";
        StringIdSpec stringIdSpec2 = new StringIdSpec(spec2);
        for (int i = 0; i < 10; i++) {
            logger.log(stringIdSpec2.newId());
        }

        String spec3 = "seq\\{\\}-{TimeCount,yyyyMMddHHmmssSSS,1023,%17s%04d";
        Assert.expectThrows(IllegalArgumentException.class, () -> new StringIdSpec(spec3));
        //new StringIdSpec(spec3);

        String spec4 = "seq\\";
        Assert.expectThrows(IllegalArgumentException.class, () -> new StringIdSpec(spec4));
        // StringIdSpec(spec4);
    }

    @Test
    public void testCustomId() {
        StringIdFactoryGenerator stringIdFactoryGenerator = (name, args) -> {
            if (name.equals("my")) {
                return new MyIdComponentGenerator(args.get(0));
            }
            return StringIdFactoryGenerator.DEFAULT.generate(name, args);
        };
        String spec = "seq-{TimeCount,yyyyMMddHHmmssSSS,1023,%17s%04d}-{Constant,tail}-{my,value}";
        StringIdSpec stringIdSpec = new StringIdSpec(spec, stringIdFactoryGenerator);
        for (int i = 0; i < 10; i++) {
            logger.log(stringIdSpec.newId());
        }
    }
}
