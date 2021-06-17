package sample.kotlin.xyz.srclab.egg

import org.testng.annotations.Test
import xyz.srclab.common.egg.BoatEggManager
import xyz.srclab.common.test.TestLogger
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
        val egg = BoatEggManager.pick("O Battle")
        egg.hatchOut("Thank you, Taro.")
        //Or
        //egg.hatchOut("谢谢你，泰罗。")
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}