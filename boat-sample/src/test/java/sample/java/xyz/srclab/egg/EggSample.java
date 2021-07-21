package sample.java.xyz.srclab.egg;

import org.testng.annotations.Test;
import xyz.srclab.common.egg.Egg;
import xyz.srclab.common.test.TestLogger;

import java.awt.*;
import java.util.Collections;

/**
 * @Author: TannerHu
 * @Date: 2021/6/10
 * @Version:
 **/
public class EggSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testEgg() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        Egg egg = Egg.pick("xyz.srclab.common.egg.nest.o.OBattle");
        egg.hatchOut("Thank you, Taro.", Collections.emptyMap());
        //Or
        //egg.hatchOut("谢谢你，泰罗。", Collections.emptyMap());
    }
}
