package sample.java.xyz.srclab.common.egg;

import org.testng.annotations.Test;
import xyz.srclab.common.egg.BoatEggManager;
import xyz.srclab.common.egg.Egg;
import xyz.srclab.common.egg.EggManager;

public class EggSample {

    @Test
    public void testEgg() {
        EggManager eggManager = BoatEggManager.INSTANCE;
        Egg egg = eggManager.pick("Hello, Boat Egg!");
        egg.hatchOut("出来吧，神龙！");
    }
}
