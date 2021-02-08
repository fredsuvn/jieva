package sample.xyz.srclab.common.id;

import org.testng.annotations.Test;
import xyz.srclab.common.id.StringIdSpec;
import xyz.srclab.common.test.TestLogger;

public class IdSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testId() {
        String spec = "seq-{TimeCount,yyyyMMddHHmmssSSS,1023,%17s%04d}-{Constant,tail}";
        StringIdSpec stringIdSpec = new StringIdSpec(spec);
        //seq-202102071449568890000-tail
        for (int i = 0; i < 10; i++) {
            logger.log(stringIdSpec.newId());
        }
    }
}
