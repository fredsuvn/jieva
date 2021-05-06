package test.xyz.srclab.common.id;

import kotlin.jvm.functions.Function1;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.id.IdComponentFactory;
import xyz.srclab.common.id.StringIdSpec;
import xyz.srclab.common.test.TestLogger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sunqian
 */
public class IdTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testId() {
        //seq-202103040448046580000-tail
        String spec = "seq-{timeCount, yyyyMMddHHmmssSSS, 1023, %17s%04d}-tail";
        StringIdSpec stringIdSpec = new StringIdSpec(spec);
        for (int i = 0; i < 10; i++) {
            logger.log(stringIdSpec.create());
        }

        //seq{}-202103040448046750000\\\}-tail
        String spec2 = "seq\\{}-{timeCount, yyyyMMddHHmmssSSS, 1023, %17s%04d\\\\\\\\}}-tail";
        StringIdSpec stringIdSpec2 = new StringIdSpec(spec2);
        for (int i = 0; i < 10; i++) {
            logger.log(stringIdSpec2.create());
        }

        String spec3 = "seq\\{\\}-{timeCount, yyyyMMddHHmmssSSS, 1023, %17s%04d";
        Assert.expectThrows(IllegalArgumentException.class, () -> new StringIdSpec(spec3));
        //new StringIdSpec(spec3);
    }

    @Test
    public void testCustomId() {
        String spec = "seq-{timeCount, yyyyMMddHHmmssSSS, 1023, %17s%04d}-{my, value}";
        Map<String, Function1<String[], IdComponentFactory<?>>> generators =
            new HashMap<>(StringIdSpec.DEFAULT_COMPONENT_FACTORY_PROVIDERS);
        generators.put("my", (args) -> new MyIdComponentFactory(args[0]));
        StringIdSpec stringIdSpec = new StringIdSpec(spec, generators);
        for (int i = 0; i < 10; i++) {
            logger.log(stringIdSpec.create());
        }
    }
}
