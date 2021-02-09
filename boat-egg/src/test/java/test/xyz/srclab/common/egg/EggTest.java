package test.xyz.srclab.common.egg;

import org.testng.annotations.Test;
import xyz.srclab.common.egg.Egg;

/**
 * @author sunqian
 */
public class EggTest {

    @Test
    public void testEggV0() {
        Egg egg = Egg.pick("xyz.srclab.common.egg.v0.V0Egg");
        egg.hatchOut("5b+r6LeR77yB6L+Z6YeM5rKh5pyJ5Yqg54+t6LS577yB");
    }
}
