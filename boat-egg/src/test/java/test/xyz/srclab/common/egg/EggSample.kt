package test.xyz.srclab.common.egg

import org.testng.annotations.Test
import xyz.srclab.common.egg.Egg
import java.awt.GraphicsEnvironment

/**
 * @Author: TannerHu
 * @Date: 2021/6/10
 * @Version:
 **/
class EggSample {

    @Test
    fun testEgg() {
        if (GraphicsEnvironment.isHeadless()) {
            return
        }
        val egg = Egg.pick("xyz.srclab.common.egg.nest.o.OBattle")
        egg.hatchOut("Thank you, Taro.", emptyMap())
        //Or
        //egg.hatchOut("谢谢你，泰罗。", Collections.emptyMap());
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}