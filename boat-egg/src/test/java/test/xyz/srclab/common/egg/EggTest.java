package test.xyz.srclab.common.egg;

import org.testng.annotations.Test;
import xyz.srclab.common.egg.Egg;

/**
 * @author sunqian
 */
public class EggTest {

    @Test
    public void testEgg() {
        Egg egg = Egg.pickOne("Hello Egg");
        egg.hatchOut("5b+r6LeR77yB6L+Z6YeM5rKh5pyJ5Yqg54+t6LS577yB");

        Egg egg2 = Egg.pickOne("O Space Battle");
        egg2.hatchOut("5b+r6LeR77yB6L+Z6YeM5rKh5pyJ5Yqg54+t6LS577yB");
    }
}
