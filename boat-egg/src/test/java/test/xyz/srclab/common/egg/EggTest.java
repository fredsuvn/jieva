package test.xyz.srclab.common.egg;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.egg.BoatEggManager;
import xyz.srclab.common.egg.Egg;
import xyz.srclab.common.egg.EggNotFoundException;
import xyz.srclab.common.test.TestLogger;

/**
 * @author sunqian
 */
public class EggTest {

    private static TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testEgg() {
        BoatEggManager eggManager = BoatEggManager.INSTANCE;
        Egg egg = eggManager.pick("Hello, Boat Egg!");
        egg.hatchOut("Any Spell!");

        Assert.expectThrows(EggNotFoundException.class, () -> {
            eggManager.pick("Hello, Egg!");
        });

        try {
            eggManager.pick("Hello, Egg!");
        } catch (EggNotFoundException e) {
            logger.log(e);
        }
    }

    @Test
    public void testOSpaceBattle() {
        BoatEggManager eggManager = BoatEggManager.INSTANCE;
        Egg egg = eggManager.pick("O Space Battle");
        egg.hatchOut("5b+r6LeR77yB6L+Z6YeM5rKh5pyJ5Yqg54+t6LS577yB");
    }
}
